# HEDU Renew project

This directory contains the eleven requested native Renew `.rnw` drawings. All listed places, transitions, and directed arcs are preserved. The model uses native tuples, integers, net references, creation inscriptions, guards, and synchronous channels. No Java domain helpers or external services are required.

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

The seven requested business signatures are implemented with primitive payloads and real net-reference targets. [CHANNEL_MATRIX.md](CHANNEL_MATRIX.md) lists every caller, receiver, argument binding and executed witness. Existing internal channel overloads remain for reference transport. Signature placeholders are compared as strings; they are not cryptographic signatures. Profile data is a fixture supplied to the model, not a computed VP.

The supplied graphs are a formal workflow abstraction. Upload, mint, anchoring, presentation, and matching transitions do not contact IPFS, an LMS, a blockchain, or an HR system. The model contains no matching algorithm, cryptography, or computed competency data. Several rejection/cancellation branches have no recovery or notification path in the prescribed graphs and can leave other agents waiting.

## Directed successful path

After the creation step, manually fire the following initiating transitions, in order. Channel partner transitions fire atomically; do not fire them separately.

1. StudentAgent: `t_SubmitEvidence`.
2. EvidenceNet: `t_LMS_Submit`, `t_Review_Request`.
3. ProfessorAgent: `t_StartReview`, `t_AssessEvidence`, `t_ApproveEvidence`.
4. UniversityAgent: `t_VerifyEvidence`, `t_RequestCredential`.
5. CompetencyNet: `t_Verification_Pass`.
6. UniversityAgent: `t_IssueCredential`.
7. StudentAgent: `t_WaitCredential`, `t_ReceiveCredential`, `t_AcceptCredential`.
8. HEDULedgerNet: `t_Order`, `t_Validate`, `t_Commit`.
9. StudentAgent: `t_GenerateVP`, `t_ShareVP`.
10. HRAgent: `t_ParseVP`.
11. CompetencyNet: `t_Agg_Ingest` (also anchors the ledger and builds the HR profile).
12. HRAgent: `t_SemanticMatch`, `t_GapAnalysis`, `t_GenerateResult`.
13. WalletNet: `t_GenerateVP`, `t_ShareVP`.

This sequence was actually executed by the validation harness with the default input. It establishes a successful execution, not exhaustive correctness or performance.

## Validate again

Run `./Validate.ps1` (or pass `-RenewHome` as above). Add `-Smoke` to execute happy, reject, cancel, expire, grade_reject, ledger_reject, and bad_signature checks in separate JVMs. A JDK is required. Validation checks the version header, nodes, arcs, every stored text inscription, and native compilation. Neither validation nor smoke mode regenerates or modifies drawings; scenario overrides affect only in-memory markings. Native output is saved to `validation.log`, and failures stop the script.

If Windows blocks scripts, use a process-only override:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\Validate.ps1 -Smoke
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\Launch.ps1
```

`FUNCTIONAL_INTEGRATION_AUDIT.md` records the pre-change audit. `MODEL_VALIDATION.md` documents the current inventory, markings, guards, and limitations. `REGRESSION_REPORT.md` compares against the baseline; `VALIDATION_EVIDENCE.txt` preserves the actual native output for the seven executed checks. `model.tsv` and `model_manifest.json` describe the saved annotations. `tools/BuildProject.java` is read-only validation/scenario tooling, **not a Java helper used by any transition**. The original workspace Java classes are not used.

Topology remains unchanged. Failure notification/recovery across every agent is not implemented; some rejection/cancellation/expiry paths leave peers waiting. State-space size, reachable states, deadlock counts, boundedness proof, throughput, latency, TPS, 500-agent, race-condition, Poisson and MMPP results: **NOT MEASURED YET**.

Syntax and operation references: [official Renew 4.1 manual](https://www.informatik.uni-hamburg.de/TGI/renew/4.1/renew4.1.pdf) and [official installation instructions](https://www.informatik.uni-hamburg.de/TGI/renew/4.1/install.html).
