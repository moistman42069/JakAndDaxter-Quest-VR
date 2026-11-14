# The mean brightness (0-255) and lit share of each PNG in a folder, sampled on a grid: tells black pictures apart
# without opening them.  powershell -File tools/png_levels.ps1 <folder> [filter]
param([string]$Folder, [string]$Filter = "*.png")
Add-Type -AssemblyName System.Drawing
Get-ChildItem -Path $Folder -Filter $Filter | Sort-Object Name | ForEach-Object {
  $bmp = [System.Drawing.Bitmap]::FromFile($_.FullName)
  $sum = 0.0; $n = 0; $lit = 0
  for ($y = 4; $y -lt $bmp.Height; $y += [Math]::Max(1, [int]($bmp.Height / 48))) {
    for ($x = 4; $x -lt $bmp.Width; $x += [Math]::Max(1, [int]($bmp.Width / 48))) {
      $c = $bmp.GetPixel($x, $y)
      $v = ($c.R + $c.G + $c.B) / 3.0
      $sum += $v; $n++
      if ($v -gt 12) { $lit++ }
    }
  }
  $bmp.Dispose()
  "{0,-34} mean {1,6:N1}  lit {2,5:P0}" -f $_.Name, ($sum / $n), ($lit / $n)
}
