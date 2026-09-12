# Release validation — v1.0.0

Final source branch: analysis/post-p7-experiments.

Before commit, the exact repaired model and executable validation tools were copied to an isolated local checkout. This preserves the original publication CSV sample and historical counterexamples because the runners write to fixed result paths. The commands ran in order with an explicit RenewHome pointing to the installed Renew 4.1 distribution:

1. Validate.ps1 -Smoke — exit 0; all eleven drawings compile and fifteen original regressions pass.
2. VerifyP7.ps1 — exit 0; P7-A through P7-F, P6, old-prefix replay and preservation checks pass.
3. RunPostP7Experiments.ps1 — exit 0; all 569 existing campaign executions completed; zero property violations. The generated summary matches the frozen publication summary, including 24 representative, 55 interleaving, 40 concurrency and 450 runtime executions.

The validation itself did not mutate source RNWs or model metadata. The original results/post_p7 files remain byte-identical. Rerun timing observations are retained in the local isolated checkout and do not replace the published sample. Command logs are retained in release_validation/. RELEASE_CHECKPOINT.json identifies model, tool and publication-table hashes.

Failure/P7 repair edits overlap in the same source files and validator, so they form one coherent commit with all historical defect evidence. The post-P7 campaign and final repository documentation form separate commits. No artificial intermediate model was reconstructed. Git attributes preserve original bytes, recognize CRLF and retain intentional native RNW/TSV/evidence whitespace. No caches or manuscript files are part of the release.

Publication remains gated on revalidating the merged main with Validate.ps1 -Smoke and VerifyP7.ps1, checking an unchanged model and a clean working tree, then pushing without force. The final main/tag SHAs are reported after publication. No exhaustive verification or external blockchain/security/AI result is implied.
