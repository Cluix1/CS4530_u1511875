Add-Type -AssemblyName System.Drawing

$output = Join-Path (Split-Path $PSScriptRoot -Parent) 'snapshots'
New-Item -ItemType Directory -Force -Path $output | Out-Null

function Brush([string]$hex) {
    return [System.Drawing.SolidBrush]::new([System.Drawing.ColorTranslator]::FromHtml($hex))
}

function Rect($graphics, $brush, [int]$x, [int]$y, [int]$w, [int]$h) {
    $graphics.FillRectangle($brush, $x, $y, $w, $h)
}

function Label($graphics, [string]$value, [int]$x, [int]$y, [int]$size, [string]$hex, [bool]$bold = $false) {
    $style = if ($bold) { [System.Drawing.FontStyle]::Bold } else { [System.Drawing.FontStyle]::Regular }
    $font = [System.Drawing.Font]::new('Segoe UI', $size, $style)
    $brush = Brush $hex
    $graphics.DrawString($value, $font, $brush, $x, $y)
    $font.Dispose()
    $brush.Dispose()
}

function Render([string]$name, [bool]$dark, [bool]$selected) {
    if ($dark) {
        $background = '#191c1b'; $foreground = '#e0e3e1'; $muted = '#bec9c6'
        $card = '#3f4947'; $primary = '#53dbca'; $onPrimary = '#003732'
        $secondary = '#b1ccc6'; $tertiary = '#adcae6'
    } else {
        $background = '#fafdfb'; $foreground = '#191c1b'; $muted = '#3f4947'
        $card = '#dae5e2'; $primary = '#006a60'; $onPrimary = '#ffffff'
        $secondary = '#4a635f'; $tertiary = '#466179'
    }
    $bitmap = [System.Drawing.Bitmap]::new(390, 844)
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    $graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
    $graphics.TextRenderingHint = [System.Drawing.Text.TextRenderingHint]::AntiAlias
    $bgBrush = Brush $background
    $graphics.Clear([System.Drawing.ColorTranslator]::FromHtml($background))

    Label $graphics '9:41' 25 12 12 $foreground $true
    Label $graphics 'Theme Practice' 24 68 23 $foreground $true
    Label $graphics 'Deep teal / Material 3' 24 113 12 $muted
    Label $graphics 'Dark theme' 24 175 15 $foreground $true
    $switchBrush = Brush $(if ($dark) { $primary } else { '#bec9c6' })
    Rect $graphics $switchBrush 307 178 55 30
    $thumbBrush = Brush $(if ($dark) { $onPrimary } else { '#ffffff' })
    $graphics.FillEllipse($thumbBrush, $(if ($dark) { 336 } else { 310 }), 181, 24, 24)

    $cardBrush = Brush $card
    Rect $graphics $cardBrush 24 246 342 239
    Label $graphics 'A calmer canvas' 43 268 18 $foreground $true
    Label $graphics 'Primary, secondary, and tertiary' 43 314 12 $foreground
    Label $graphics 'colors work together across' 43 338 12 $foreground
    Label $graphics 'components in both light and dark' 43 362 12 $foreground
    Label $graphics 'themes.' 43 386 12 $foreground
    foreach ($dot in @(@(43,$primary),@(103,$secondary),@(163,$tertiary))) {
        $dotBrush = Brush $dot[1]
        $graphics.FillEllipse($dotBrush, [int]$dot[0], 423, 42, 42)
        $dotBrush.Dispose()
    }

    Label $graphics 'Try the controls' 24 525 16 $foreground $true
    $buttonBrush = Brush $primary
    Rect $graphics $buttonBrush 24 577 115 48
    Label $graphics 'Select' 55 589 13 $onPrimary $true
    $outlinePen = [System.Drawing.Pen]::new([System.Drawing.ColorTranslator]::FromHtml($muted), 1)
    $graphics.DrawRectangle($outlinePen, 154, 577, 114, 48)
    Label $graphics 'Reset' 190 589 13 $primary $true
    Label $graphics $(if ($selected) { 'Selected with primary color' } else { 'Nothing selected yet' }) 24 657 13 $(if ($selected) { $primary } else { $muted })
    Label $graphics 'Seed color #006A60' 24 735 11 $muted

    $path = Join-Path $output $name
    $bitmap.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
    $outlinePen.Dispose(); $buttonBrush.Dispose(); $cardBrush.Dispose()
    $thumbBrush.Dispose(); $switchBrush.Dispose(); $bgBrush.Dispose()
    $graphics.Dispose(); $bitmap.Dispose()
}

Render '01-light-preview.png' $false $false
Render '02-dark-preview.png' $true $false
Render '03-selected-preview.png' $false $true
