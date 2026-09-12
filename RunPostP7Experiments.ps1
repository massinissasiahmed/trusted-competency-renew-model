param([string]$RenewHome = (Join-Path $PSScriptRoot '..\.validation'))
$ErrorActionPreference = 'Stop'
& python -X utf8 (Join-Path $PSScriptRoot 'tools\post_p7_experiments.py') --renew-home $RenewHome
exit $LASTEXITCODE
