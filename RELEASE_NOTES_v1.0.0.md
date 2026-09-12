# ICMSCT 2026 validated Renew model

This release freezes the repaired executable model and its recorded post-P7 evidence.

- Eleven RNW templates: 80 places, 87 transitions, 174 ordinary directed arcs.
- Explicit failure propagation and atomic anchor confirmation enforce the tested commit-before-share workflow.
- The historical P7 issuer-authorization counterexample and repair remain documented. Authorization uses string unification.
- Fifteen original regressions and focused P7 validation pass in the recorded checkpoint.
- Sixteen representative fixtures produce 24 executions; 55 interleaving runs represent 53 distinct fixture/sequence combinations.
- Seventy-nine inspected correctness endpoints comprise 39 expected successes and 40 expected failures; no defective global deadlock was observed there.
- Directed transition coverage is 86/87 (98.85%).
- Nominal multi-root runs cover 1, 2, 5 and 10 roots, ten repetitions each, with no observed isolation violation.
- The frozen runtime dataset contains 450 instrumented Renew execution times, 30 per original scenario.

No exhaustive state-space exploration, boundedness/liveness proof, real Fabric/IPFS execution, cryptographic verification or AI-quality measurement is claimed. Historical failure reports are not current failing results.

In a disposable checkout with Renew 4.1, Java 17, Python 3 and PowerShell:

```powershell
.\Validate.ps1 -Smoke
.\VerifyP7.ps1
.\RunPostP7Experiments.ps1
```

Use `-RenewHome` as needed. Runners overwrite local output paths; retain the original published CSV sample. See README and EXPERIMENT_REPRODUCIBILITY.md.
