param([string]$RenewHome = (Join-Path $PSScriptRoot '..\.validation'))
$ErrorActionPreference = 'Stop'
$RenewHome = (Resolve-Path -LiteralPath $RenewHome).Path
Push-Location -LiteralPath $PSScriptRoot
try {
    # Preserve all pre-repair evidence; use distinct post-repair output paths.
    # Native stderr can contain harmless Log4j warnings. Retain it separately;
    # use the child exit code instead of PowerShell's NativeCommandError conversion.
    $p7ValidationArgs = @('-NoProfile','-ExecutionPolicy','Bypass','-File',('"' + (Join-Path $PSScriptRoot 'Validate.ps1') + '"'),'-RenewHome',('"' + $RenewHome + '"'),'-Smoke')
    $p7Validation = Start-Process -FilePath powershell.exe -ArgumentList $p7ValidationArgs -WindowStyle Hidden -Wait -PassThru -RedirectStandardOutput results/p7_post_repair_regressions.txt -RedirectStandardError results/p7_post_repair_regressions_stderr.txt
    if ($p7Validation.ExitCode -ne 0) { throw 'P7 regression validation failed; see captured stderr.' }
    $p7Classpath = "tools\bin;$RenewHome\plugins\*;$RenewHome\libs\*;$RenewHome\de.renew.loader.jar"
    $p7ReplayArgs = @('-Djava.awt.headless=true','-cp',('"' + $p7Classpath + '"'),'BuildProject',('"' + $PSScriptRoot + '"'),'smoke','bad_issuer_signature')
    $p7Replay = Start-Process -FilePath (Get-Command java).Source -ArgumentList $p7ReplayArgs -WindowStyle Hidden -Wait -PassThru -RedirectStandardOutput results/p7_post_repair.txt -RedirectStandardError results/p7_post_repair_stderr.txt
    if ($p7Replay.ExitCode -ne 0) { throw 'P7 counterexample replay failed; see captured stderr.' }
    & python -X utf8 tools/p7_results.py
    if ($LASTEXITCODE -ne 0) { throw 'P7 evidence checks failed.' }
} finally {
    Pop-Location
}
