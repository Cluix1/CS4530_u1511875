"""Run against one booted emulator: python check_app.py [path/to/adb]."""

from pathlib import Path
import re
import subprocess
import sys
import time
import xml.etree.ElementTree as ET


ADB = sys.argv[1] if len(sys.argv) > 1 else "adb"
PACKAGE = "com.example.buttonexplorer"
SCREENSHOTS = Path(__file__).parent / "screenshots"


def adb(*arguments):
    return subprocess.check_output([ADB, *arguments])


def screen():
    adb("shell", "uiautomator", "dump", "/sdcard/window.xml")
    return ET.fromstring(adb("shell", "cat", "/sdcard/window.xml"))


def find_view(resource_name):
    resource_id = f"{PACKAGE}:id/{resource_name}"
    for node in screen().iter("node"):
        if node.get("resource-id") == resource_id:
            return node
    raise AssertionError(f"View missing: {resource_name}")


def tap(resource_name):
    node = find_view(resource_name)
    left, top, right, bottom = map(int, re.findall(r"\d+", node.attrib["bounds"]))
    adb("shell", "input", "tap", str((left + right) // 2), str((top + bottom) // 2))
    time.sleep(1)


def capture(filename):
    # Write the binary output directly to avoid PowerShell text redirection.
    time.sleep(2)
    (SCREENSHOTS / filename).write_bytes(adb("exec-out", "screencap", "-p"))


def assert_selection(expected):
    assert find_view("selected_text").get("text") == expected


def main():
    SCREENSHOTS.mkdir(exist_ok=True)
    original_rotation = adb("shell", "settings", "get", "system", "user_rotation").decode().strip()
    original_auto = adb("shell", "settings", "get", "system", "accelerometer_rotation").decode().strip()
    try:
        adb("shell", "settings", "put", "system", "accelerometer_rotation", "0")
        adb("shell", "settings", "put", "system", "user_rotation", "0")
        adb("shell", "am", "force-stop", PACKAGE)
        adb("shell", "am", "start", "-n", f"{PACKAGE}/.MainActivity")
        time.sleep(3)
        choices = ["Mountains", "Ocean", "Forest", "Desert", "City"]
        for choice in choices:
            assert find_view(f"{choice.lower()}_button").get("text") == choice
        capture("01-first-fragment.png")

        for index, choice in enumerate(choices):
            tap(f"{choice.lower()}_button")
            assert_selection(choice)
            if index == 0:
                capture("02-second-fragment.png")
            if index % 2 == 0:
                tap("back_button")
            else:
                adb("shell", "input", "keyevent", "KEYCODE_BACK")
                time.sleep(1)
            find_view("mountains_button")
            print(f"PASS: {choice} text and return navigation", flush=True)

        tap("forest_button")
        adb("shell", "settings", "put", "system", "user_rotation", "1")
        time.sleep(3)
        assert_selection("Forest")
        capture("03-second-fragment-landscape.png")
        adb("shell", "settings", "put", "system", "user_rotation", "0")
        time.sleep(3)
        assert_selection("Forest")
        adb("shell", "input", "keyevent", "KEYCODE_BACK")
        time.sleep(1)
        find_view("city_button")
        print("PASS: selection and back stack survive rotation", flush=True)
    finally:
        for key, value in [("user_rotation", original_rotation), ("accelerometer_rotation", original_auto)]:
            if value == "null":
                adb("shell", "settings", "delete", "system", key)
            else:
                adb("shell", "settings", "put", "system", key, value)


if __name__ == "__main__":
    main()
