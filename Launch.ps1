param([string]$RenewHome = (Join-Path $PSScriptRoot '..\.validation'))
$ErrorActionPreference = 'Stop'
$RenewHome = (Resolve-Path -LiteralPath $RenewHome).Path
if (-not (Test-Path -LiteralPath (Join-Path $RenewHome 'de.renew.loader.jar'))) {
    throw 'RenewHome must point to a Renew 4.1 directory containing de.renew.loader.jar, libs, and plugins.'
}
$nets = @(Get-ChildItem -LiteralPath $PSScriptRoot -Filter '*.rnw' | Sort-Object @{Expression={ $_.Name -eq 'SystemNet.rnw' }},Name | ForEach-Object { $_.Name })
# Renew parses its command arguments again; relative template names avoid its
# backslash escape rules for absolute Windows paths. Resolve them from the project.
Push-Location -LiteralPath $PSScriptRoot
try {
    & java '-Drenew.compiler=Timed Java Compiler' '-Dde.renew.simulatorMode=-1' "-Dde.renew.netPath=$PSScriptRoot" -p "$RenewHome;$RenewHome\libs" -m de.renew.loader gui @nets
    $renewExitCode = $LASTEXITCODE
} finally {
    Pop-Location
}
exit $renewExitCode
