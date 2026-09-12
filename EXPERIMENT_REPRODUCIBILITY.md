# Current post-P7 reproduction

Use Renew 4.1, Java/Javac 17, Python 3 and PowerShell (Windows tested). From a disposable checkout, run in order:

```powershell
.\Validate.ps1 -Smoke
.\VerifyP7.ps1
.\RunPostP7Experiments.ps1
```

Supply `-RenewHome 'C:\path\to\Renew'` to each command when Renew is not in `..\.validation`. The first compiles all eleven RNWs and runs fifteen regressions; the second adds focused issuer-authorization replay; the third executes the existing finite representative/interleaving/concurrency/runtime campaign. Native failures stop validation. No command performs exhaustive Reference-Net analysis.

The runners overwrite fixed log/result paths. Preserve the published `results/post_p7` dataset and historical root results by rerunning in an isolated copy containing the same model, scripts and historical inputs. Compare model hashes, scenario outcomes, property violations and counts; do not expect elapsed times, instance IDs or scheduling order to be byte-identical. Do not manually edit CSV values. Release revalidation uses this isolation to retain the validated publication sample unchanged.

`results/post_p7/environment.json`, `checkpoint_hashes.json` and `driver_identity.json` describe the original measured run. Current model file hashes must match the checkpoint; the old uncommitted base SHA alone is not sufficient. Runtime measures instrumented simulator execution, not blockchain performance. See README and POST_P7_SCIENTIFIC_RESULTS.md for exact measured scope.

## Historical pre-P7 procedure — preserved context

The following original procedure describes the counterexample-discovery checkpoint, not the expected behavior of the final repaired model. Do not run it over preserved historical evidence expecting the original failure to remain.

# Experiment reproducibility

Prerequisites: Python 3, Java/Javac/Javap 17 on PATH, and the official Renew 4.1 base installation with plugins, libs and de.renew.loader.jar. No network, third-party Python module or domain helper is needed. Tool identities and OS are captured in results/environment.json; plugin identities/hashes in results/plugin_inventory.json.

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\RunFormalExperiments.ps1
# Different local runtime location:
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\RunFormalExperiments.ps1 -RenewHome 'C:\path\to\Renew'
```

The entry point defines capabilities/properties/domain before tests, records the uncommitted working-tree hashes, runs Validate.ps1 -Smoke (all original regressions), then instruments a temporary copy of the current validator. It uses the existing in-memory invalid-issuer fixture, chooses the originally valid native success path, and verifies both competing binding candidates. The copied harness keeps actual Renew execution and complete endpoint checks. No custom simulator or synthetic reachability graph is implemented. Source replacement checks fail if the expected validator structure has changed. Native command/validation errors stop the run; they are not converted into passing properties.

Expected exit is **2** on the documented counterexample. Other errors exit nonzero. On exit 2 no further experiments execute; remaining reports are stopped/not-measured records. All results are regenerated from captured native output; no statistics are manually supplied. results/.build contains generated Java/classes and is ignored; all evidence CSV/JSON/TXT files are eligible for version control. Re-running overwrites this task's generated results/reports, not the RNWs or original validation/helper files. The failed property must be addressed separately before expanding this entry point into a broader campaign.

Files for each result: baseline_validation.txt and baseline_regressions.csv; p7_counterexample.txt, probe_instrumentation.txt, interleaving_results.csv and p7_final_marking.csv; transition_coverage.csv and experiment_summary.json; capability API/plugin/manual records; property_definitions.json and analysis_domain.json; baseline preservation hashes; workload_claim_occurrences.csv. Statistical/concurrency/state-space placeholders explicitly say NOT MEASURED. Existing historical reports are inputs, not rewritten as current successes.
