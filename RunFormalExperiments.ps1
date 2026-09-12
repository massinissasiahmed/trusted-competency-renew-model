param([string]$RenewHome = (Join-Path $PSScriptRoot '..\.validation'))
$ErrorActionPreference = 'Stop'
& python -X utf8 (Join-Path $PSScriptRoot 'tools\formal_experiments.py') --renew-home $RenewHome
$experimentExitCode = $LASTEXITCODE
if ($experimentExitCode -eq 2) {
    Write-Host 'STOP: property counterexample found. See NEW_CORRECTNESS_DEFECT.md and results/p7_counterexample.txt.'
}
exit $experimentExitCode
