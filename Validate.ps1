param(
    [string]$RenewHome = (Join-Path $PSScriptRoot '..\.validation'),
    [switch]$Smoke,
    [string[]]$Scenarios = @('happy','reject','cancel','expire','grade_reject','ledger_reject','bad_signature','bad_issuer_signature','grade_reject_waiting','cancel_submitted','cancel_waiting','expire_submitted','expire_waiting','bad_issuer_signature_submitted','bad_issuer_signature_waiting')
)
$ErrorActionPreference = 'Stop'
$validationLog = Join-Path $PSScriptRoot 'validation.log'
Set-Content -LiteralPath $validationLog -Value 'Renew native validation and scenario output' -Encoding Unicode
$RenewHome = (Resolve-Path -LiteralPath $RenewHome).Path
$toolDir = Join-Path $PSScriptRoot 'tools'
$binDir = Join-Path $toolDir 'bin'
New-Item -ItemType Directory -Force -Path $binDir | Out-Null
$cp = "$RenewHome\plugins\*;$RenewHome\libs\*;$RenewHome\de.renew.loader.jar"
& javac -cp $cp -d $binDir (Join-Path $toolDir 'BuildProject.java')
if ($LASTEXITCODE -ne 0) { throw 'Validation-tool compilation failed.' }
& java '-Djava.awt.headless=true' -cp "$binDir;$cp" BuildProject $PSScriptRoot validate | Tee-Object -FilePath $validationLog -Append
if ($LASTEXITCODE -ne 0) { throw 'RNW validation failed.' }
if ($Smoke) {
    foreach ($scenario in $Scenarios) {
        & java '-Djava.awt.headless=true' -cp "$binDir;$cp" BuildProject $PSScriptRoot smoke $scenario | Tee-Object -FilePath $validationLog -Append
        if ($LASTEXITCODE -ne 0) { throw "Renew scenario failed: $scenario" }
    }
}
