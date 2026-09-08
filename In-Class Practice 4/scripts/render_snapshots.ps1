Add-Type -AssemblyName System.Drawing

$projectRoot = Split-Path -Parent $PSScriptRoot
$snapshotDir = Join-Path $projectRoot "snapshots"
New-Item -ItemType Directory -Force -Path $snapshotDir | Out-Null

function New-AppSnapshot {
    param(
        [string]$FileName,
        [string]$FirstValue,
        [string]$SecondValue,
        [string]$Result
    )

    $bitmap = New-Object System.Drawing.Bitmap 432, 768
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    $graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
    $graphics.TextRenderingHint = [System.Drawing.Text.TextRenderingHint]::AntiAliasGridFit
    $graphics.Clear([System.Drawing.ColorTranslator]::FromHtml("#F8FAF7"))

    $dark = New-Object System.Drawing.SolidBrush ([System.Drawing.ColorTranslator]::FromHtml("#191C1A"))
    $muted = New-Object System.Drawing.SolidBrush ([System.Drawing.ColorTranslator]::FromHtml("#414944"))
    $field = New-Object System.Drawing.SolidBrush ([System.Drawing.ColorTranslator]::FromHtml("#E1EAE4"))
    $accent = New-Object System.Drawing.SolidBrush ([System.Drawing.ColorTranslator]::FromHtml("#006C4C"))
    $white = New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::White)
    $linePen = New-Object System.Drawing.Pen ([System.Drawing.ColorTranslator]::FromHtml("#59615C")), 1
    $titleFont = New-Object System.Drawing.Font "Segoe UI", 25, ([System.Drawing.FontStyle]::Regular)
    $bodyFont = New-Object System.Drawing.Font "Segoe UI", 17, ([System.Drawing.FontStyle]::Regular)
    $labelFont = New-Object System.Drawing.Font "Segoe UI", 12, ([System.Drawing.FontStyle]::Regular)
    $buttonFont = New-Object System.Drawing.Font "Segoe UI Semibold", 14, ([System.Drawing.FontStyle]::Regular)

    $graphics.DrawString("12:00", $labelFont, $dark, 18, 10)
    $graphics.DrawString("Text Concatenator", $titleFont, $dark, 91, 205)

    $fields = @(
        @{ Y = 274; Label = "First text"; Value = $FirstValue },
        @{ Y = 356; Label = "Second text"; Value = $SecondValue }
    )
    foreach ($item in $fields) {
        $graphics.FillRectangle($field, 32, $item.Y, 368, 64)
        $graphics.DrawLine($linePen, 32, ($item.Y + 63), 400, ($item.Y + 63))
        $graphics.DrawString($item.Label, $labelFont, $muted, 48, ($item.Y + 7))
        if ($item.Value) {
            $graphics.DrawString($item.Value, $bodyFont, $dark, 48, ($item.Y + 29))
        }
    }

    $graphics.FillRectangle($accent, 139, 452, 154, 50)
    $graphics.DrawString("Concatenate", $buttonFont, $white, 164, 466)
    $graphics.DrawString("Result: $Result", $bodyFont, $dark, 32, 532)
    $graphics.FillRectangle($dark, 166, 745, 100, 4)

    $bitmap.Save((Join-Path $snapshotDir $FileName), [System.Drawing.Imaging.ImageFormat]::Png)
    $graphics.Dispose()
    $bitmap.Dispose()
}

function New-CodeSnapshot {
    $sourcePath = Join-Path $projectRoot "app\src\main\java\com\example\textconcatenator\MainActivity.kt"
    $lines = Get-Content $sourcePath
    $start = [Array]::IndexOf($lines, "fun ConcatenatorScreen() {")
    $snippet = $lines[$start..([Math]::Min($start + 42, $lines.Count - 1))] -join "`n"

    $bitmap = New-Object System.Drawing.Bitmap 1200, 900
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    $graphics.TextRenderingHint = [System.Drawing.Text.TextRenderingHint]::AntiAliasGridFit
    $graphics.Clear([System.Drawing.ColorTranslator]::FromHtml("#1E1F22"))
    $header = New-Object System.Drawing.SolidBrush ([System.Drawing.ColorTranslator]::FromHtml("#2B2D30"))
    $text = New-Object System.Drawing.SolidBrush ([System.Drawing.ColorTranslator]::FromHtml("#DFE1E5"))
    $muted = New-Object System.Drawing.SolidBrush ([System.Drawing.ColorTranslator]::FromHtml("#9DA0A8"))
    $titleFont = New-Object System.Drawing.Font "Consolas", 16, ([System.Drawing.FontStyle]::Bold)
    $codeFont = New-Object System.Drawing.Font "Consolas", 14, ([System.Drawing.FontStyle]::Regular)

    $graphics.FillRectangle($header, 0, 0, 1200, 52)
    $graphics.DrawString("MainActivity.kt", $titleFont, $text, 24, 15)
    $y = 72
    $lineNumber = $start + 1
    foreach ($line in ($snippet -split "`n")) {
        $graphics.DrawString($lineNumber.ToString().PadLeft(3), $codeFont, $muted, 20, $y)
        $graphics.DrawString($line, $codeFont, $text, 75, $y)
        $lineNumber++
        $y += 19
    }

    $bitmap.Save((Join-Path $snapshotDir "code-main-activity.png"), [System.Drawing.Imaging.ImageFormat]::Png)
    $graphics.Dispose()
    $bitmap.Dispose()
}

New-AppSnapshot -FileName "app-empty.png" -FirstValue "" -SecondValue "" -Result ""
New-AppSnapshot -FileName "app-result.png" -FirstValue "Hello, " -SecondValue "World!" -Result "Hello, World!"
New-CodeSnapshot
