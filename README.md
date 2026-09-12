# Trusted Competency Validation and Talent Management — Renew Model

This directory contains the eleven requested native Renew `.rnw` drawings. Existing figures and their IDs are retained; explicit failure and anchor-confirmation paths extend the topology. The model uses native tuples, integers, net references, creation inscriptions, guards, and synchronous channels. No Java domain helpers or external services are required.

## Open and simulate

Validated with **Renew 4.1 and Java 17**. Use **Timed Java Compiler** and **Sequential** simulation mode. This built-in compiler enables initial tokens to participate in synchronization during creation, as required by this model's setup and object-creation channels. Choosing it does not turn the input variable `currentTime` into a running clock.

1. In PowerShell, change to this project directory.
2. Run `./Launch.ps1`. In this workspace it finds the downloaded official Renew distribution in the adjacent `.validation` directory. Elsewhere run `./Launch.ps1 -RenewHome 'C:\path\to\Renew'`, pointing to the directory containing `de.renew.loader.jar`, `libs`, and `plugins`.
3. Alternatively launch Renew normally and open **all eleven `.rnw` files in this directory** through File > Open Drawing. Do not mix these files with older drawings having the same net names.
4. Choose **Simulation > Formalisms > Timed Java Compiler**. Under **Simulation > Configure Simulation > Engine**, select **Sequential mode**, set **Multiplicity** to **1**, and click **OK**. The launch script supplies these settings; verify them before starting.
5. Select the `SystemNet` drawing, then choose **Simulation > Simulation Step** (Ctrl+I) for a controlled start. Renew creates its instance when simulation starts. **Simulation > Run Simulation** (Ctrl+R) runs automatically; **Simulation > Halt Simulation** pauses it.
6. The first enabled creation step is `t_CreateStudent`. It synchronizes with the other seven SystemNet creation transitions and StudentAgent's `t_PrepareEvidence`. Each pool's one `[]` seed is replaced by its corresponding net reference. Open the referenced agent instances by double-clicking the reference tokens in the SystemNet instance.
7. To choose a specific enabled transition in an instance drawing, right-click that transition. Automatic runs choose among enabled alternatives, including cancellation and rejection; they need not follow the successful path.
8. Use **Simulation > Terminate Simulation** before editing initial markings or starting a fresh scenario.

## Inputs and scope

StudentAgent's `p_Idle` holds one token `[80,50,0,100,"did:example:student1","bafyEvidence001","student-signature"]`, interpreted as `[grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]`. These are illustrative input values, **not measured results**. Other role/object initial places bind the issuer, assessor, expected signatures, credential, and profile fixture values. The validation report lists every marking; token counts are unchanged.

Edit initial values before restarting to configure a scenario. A grade below the threshold permits evidence/professor rejection; a currentTime at or after expiryDate enables expiration after minting. EvidenceNet has its own threshold marking, initially 50; update both thresholds consistently when changing grading policy. Matching DID/credential/signature/profile fixtures must also remain consistent across their listed initial places. Logical time remains fixed throughout a run. Acceptance requires `currentTime < expiryDate`; expiry requires `currentTime >= expiryDate`.

The seven requested business signatures are implemented with primitive payloads and real net-reference targets. [CHANNEL_MATRIX.md](CHANNEL_MATRIX.md) records the failure-repair checkpoint. Its CredentialObject channel arities are superseded by [P7_REPAIR_REPORT.md](P7_REPAIR_REPORT.md); use the current RNWs and model.tsv for exact interfaces. Existing internal channel overloads remain for reference transport. Signature placeholders are compared as strings; they are not cryptographic signatures. Profile data is a fixture supplied to the model, not a computed VP.

The supplied graphs are a formal workflow abstraction. Upload, mint, anchoring, presentation, and matching transitions do not contact IPFS, an LMS, a blockchain, or an HR system. The model contains no matching algorithm, cryptography, or computed competency data. The repaired failure protocols carry explicit outcomes to finite endpoints. Fifteen directed runs check nominal success, rejection, cancellation, expiry, invalid signatures and ledger rejection, including pre/post-consent phases. This is not exhaustive verification.

## Directed successful path

After the creation step, manually fire the following initiating transitions, in order. Channel partner transitions fire atomically; do not fire them separately.

1. StudentAgent: `t_SubmitEvidence`.
2. EvidenceNet: `t_LMS_Submit`, `t_Review_Request`.
3. ProfessorAgent: `t_StartReview`, `t_AssessEvidence`, `t_ApproveEvidence`.
4. UniversityAgent: `t_VerifyEvidence`, `t_RequestCredential`.
5. CompetencyNet: `t_Verification_Pass`.
6. UniversityAgent: `t_IssueCredential`.
7. StudentAgent: `t_WaitCredential`, `t_ReceiveCredential`, `t_AcceptCredential`.
8. HEDULedgerNet: `t_Order`, `t_Validate`, `t_Commit`, **`t_Anchor`** (atomically confirms anchoring to Competency, CredentialObject, Student and Wallet).
9. StudentAgent: `t_GenerateVP`, `t_ShareVP`.
10. HRAgent: `t_ParseVP`.
11. CompetencyNet: `t_Agg_Ingest` (updates the credential object and builds the HR profile; the ledger is already anchored).
12. HRAgent: `t_SemanticMatch`, `t_GapAnalysis`, `t_GenerateResult`.
13. WalletNet: `t_GenerateVP`, `t_ShareVP`.

This sequence was actually executed by the validation harness with the default input. It establishes a successful execution, not exhaustive correctness or performance.

## Validate again

Run `./Validate.ps1` (or pass `-RenewHome` as above). Add `-Smoke` to execute fifteen directed checks in separate JVMs, including invalid student/issuer signatures and submitted/waiting/received timing alternatives. Every run checks quiescence, exact final markings, failure outcomes, active versus uninvoked services, and reference retention. A JDK is required. Validation checks the version header, nodes, arcs, every stored text inscription, and native compilation. Neither validation nor smoke mode regenerates or modifies drawings; scenario overrides affect only in-memory markings. Native output is saved to `validation.log`, and failures stop the script.

If Windows blocks scripts, use a process-only override:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\Validate.ps1 -Smoke
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\Launch.ps1
```

`FUNCTIONAL_INTEGRATION_AUDIT.md` records the pre-change audit. `MODEL_VALIDATION.md` documents the failure-repair inventory, markings and limitations; the P7 report and post-P7 results define the final checkpoint. `REGRESSION_REPORT.md` compares against the baseline; `POST_REPAIR_VALIDATION_EVIDENCE.txt` preserves current native evidence; `VALIDATION_EVIDENCE.txt` remains the historical pre-repair output. `model.tsv` and `model_manifest.json` describe the saved annotations. `tools/BuildProject.java` is read-only validation/scenario tooling, **not a Java helper used by any transition**. The original workspace Java classes are not used.

The failure repairs add four places, nineteen transitions and thirty-eight arcs, with four acceptance arcs redirected. The subsequent P7 correction changes channel inscriptions without changing topology. Historical failure and authorization counterexamples remain preserved and are not current failures. See `FAILURE_PROTOCOL_REPAIR.md`, `P7_AUTHORIZATION_CONTRACT.md`, `P7_REPAIR_REPORT.md` and the current `POST_P7_*` reports.

Syntax and operation references: [official Renew 4.1 manual](https://www.informatik.uni-hamburg.de/TGI/renew/4.1/renew4.1.pdf) and [official installation instructions](https://www.informatik.uni-hamburg.de/TGI/renew/4.1/install.html).

## Scientific purpose and architecture

This executable workflow model supports research on coordinating educational evidence, rejectable credential decisions and talent-profile processing for ICMSCT 2026. The three conceptual layers are Evidence → Blockchain Competency → Talent Profile/AI. Nets-within-Nets references represent identity-bearing objects; synchronous channels coordinate their lifecycle with role nets.

SystemNet creates the participating roles. EvidenceNet creates EvidenceObject; CompetencyNet creates CredentialObject. Authorized holder acceptance starts modeled ledger execution, followed by ordering, validation, commit, and anchor with atomic confirmation. Only then can Student/Wallet presentation and HR processing progress. P7 compares carried issuer-signature strings; this is not cryptographic verification.

## Repository structure and templates

- Root coordinator: `SystemNet.rnw`.
- Roles: `StudentAgent.rnw`, `ProfessorAgent.rnw`, `UniversityAgent.rnw`, `HRAgent.rnw`.
- Services: `EvidenceNet.rnw`, `CompetencyNet.rnw`, `WalletNet.rnw`, `HEDULedgerNet.rnw`.
- Dynamic objects: `EvidenceObject.rnw`, `CredentialObject.rnw`.
- Saved graph/inscriptions: `model.tsv`, `model_manifest.json`.
- Validation: `Validate.ps1`, `VerifyP7.ps1`, `RunPostP7Experiments.ps1`, `tools/`.
- Current frozen publication evidence: `results/post_p7/`, `POST_P7_SCIENTIFIC_RESULTS.md`, `POST_P7_PROPERTY_RESULTS.md`.
- Historical evidence: failure audits, `NEW_CORRECTNESS_DEFECT.md`, earlier `PROPERTY_RESULTS.md`/`SCIENTIFIC_RESULTS.md`, and root `results/` counterexample files. Their failing/stopped results describe earlier checkpoints.

## Toolchain and reproduction

Tested on Windows 11 with Renew 4.1, Java/Javac 17 (Temurin 17.0.20.1), Python 3 and PowerShell. Install the official Renew distribution separately; runtime JARs and generated classes are not vendored. Put Java/Javac and Python on PATH.

Run in a **disposable checkout or copy** to preserve the frozen publication evidence: these scripts write logs and result files at fixed relative paths. Rerun runtime values will vary and must not replace the published sample merely to match a new run.

```powershell
.\Validate.ps1 -Smoke
.\VerifyP7.ps1
.\RunPostP7Experiments.ps1
```

All three accept `-RenewHome 'C:\path\to\Renew'`; the default is the adjacent `..\.validation` directory. `Validate.ps1 -Smoke` loads/compiles all drawings and executes the fifteen original regression scenarios. `VerifyP7.ps1` reruns regressions and replays the invalid-issuer counterexample against the repaired protocol. `RunPostP7Experiments.ps1` runs the regression/P7 gates, representative fixtures, prescribed interleavings, controlled multi-root runs and simulator timing repetitions. It is a directed native Renew campaign, not exhaustive verification. See `EXPERIMENT_REPRODUCIBILITY.md` for preservation and interpretation details.

## Final measured checkpoint

The frozen dataset contains **11 drawings, 80 places, 87 transitions and 174 ordinary directed arcs**. All 15 original regressions and the focused P7 check pass.

| Measurement | Recorded result |
| --- | --- |
| Representative fixtures / executions | 16 / 24 |
| Dedicated interleaving runs / distinct fixture-sequence combinations | 55 / 53 |
| Inspected correctness endpoints | 79 (39 expected success, 40 expected failure) |
| Defective global deadlocks observed at those endpoints | 0 |
| Directed transition coverage | 86/87 = 98.85% |
| Controlled roots | 1, 2, 5, 10; 10 repetitions each; 40 runs |
| Renew execution-time observations | 450; 30 repetitions for each of 15 standard scenarios |

No defective global deadlock was observed across the explored executions. P1/P2 have existential witnesses; P3–P9 pass only in the tested domain; P10 reports observed transition coverage. One University rejection transition is structurally excluded in the tested root workflow, not declared globally unreachable.

Concurrent roots advance round-robin in the native sequential engine. No cross-root synchronization, lost references or unfinished workflows were observed in those nominal runs. This is controlled multi-instance simulation, not a production scalability result.

Runtime is **Renew simulation execution time** after loading/compilation, including binding searches, marking/reference checks and trace output. Each repetition uses a fresh JVM, with no warmup exclusion and no isolated host load. `runtime_raw.csv` and `runtime_summary.csv` contain the raw observations and n/mean/median/sample-SD/min/max/nearest-rank-p95 statistics. These are not blockchain latency measurements.

## Formal and implementation limits

No exhaustive Reference-Net state-space exploration, complete reachable-state enumeration, formal boundedness proof or formal liveness proof was performed. Passing directed tests do not prove global deadlock freedom. Time is a fixed carried value. No real Hyperledger Fabric benchmark, IPFS execution, cryptographic verification, AI-quality experiment, Poisson/MMPP workload, batching, TPS or 500-agent scalability test is reported.

## Citation and research context

Research context: “Modeling and Simulation of a Multi-Layer Blockchain-Based Framework for Trusted Competency Validation and Talent Management in Higher Education,” ICMSCT 2026. This repository release concerns the model and reproducibility evidence; it does not assert paper acceptance. Cite this repository, the validated release tag `v1.0.0`, its commit and the relevant evidence files. The final source hashes are recorded in `results/post_p7/checkpoint_hashes.json`.

## License

The repository is distributed under the [MIT License](LICENSE). Renew and any separately installed dependencies retain their own licenses.
