param([string]$RenewHome = (Join-Path $PSScriptRoot '..\.validation'))
$ErrorActionPreference = 'Stop'
$RenewHome = (Resolve-Path -LiteralPath $RenewHome).Path
$toolDir = Join-Path $PSScriptRoot 'tools'
$binDir = Join-Path $toolDir 'bin'
New-Item -ItemType Directory -Force -Path $binDir | Out-Null
$cp = "$RenewHome\plugins\*;$RenewHome\libs\*;$RenewHome\de.renew.loader.jar"
& javac -cp $cp -d $binDir (Join-Path $toolDir 'BuildProject.java')
if ($LASTEXITCODE -ne 0) { throw 'Validation-tool compilation failed.' }
& java '-Djava.awt.headless=true' -cp "$binDir;$cp" BuildProject $PSScriptRoot validate
if ($LASTEXITCODE -ne 0) { throw 'RNW validation failed.' }
