param([string]$RenewHome = (Join-Path $PSScriptRoot '..\.validation'))
$ErrorActionPreference = 'Stop'
$RenewHome = (Resolve-Path -LiteralPath $RenewHome).Path
if (-not (Test-Path -LiteralPath (Join-Path $RenewHome 'de.renew.loader.jar'))) {
    throw 'RenewHome must point to a Renew 4.1 directory containing de.renew.loader.jar, libs, and plugins.'
}
$nets = @(Get-ChildItem -LiteralPath $PSScriptRoot -Filter '*.rnw' | Sort-Object Name | ForEach-Object { $_.FullName })
& java '-Drenew.compiler=Timed Java Compiler' '-Dde.renew.simulatorMode=-1' "-Dde.renew.netPath=$PSScriptRoot" -p "$RenewHome;$RenewHome\libs" -m de.renew.loader gui @nets
exit $LASTEXITCODE
