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

StudentAgent's `p_Idle` holds one token `[80,50,0,100]`, interpreted as `[grade,passing_threshold,currentTime,expiryDate]`. These are illustrative input values, **not measured results**. Other initial places hold one unit tuple `[]`; all remaining places start empty. The validation report lists every marking.

Edit this one StudentAgent marking before restarting to configure a scenario. For example, a grade below the threshold permits evidence/professor rejection; a currentTime at or after expiryDate enables expiration after minting. Logical time remains fixed throughout a run. Acceptance requires `currentTime < expiryDate`; expiry requires `currentTime >= expiryDate`.

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

Run `./Validate.ps1` (or pass `-RenewHome` as above). A JDK is required for this optional check. It compiles the build-time validation tool, reopens the existing RNW files using Renew's native reader, checks nodes and directed arcs against `model.tsv`, and compiles the entire net system. It does not regenerate or modify the drawings.

`MODEL_VALIDATION.md` provides the full inventory, channels, guards, limitations, and evidence. `validation.log` records the native compilation and directed smoke test. `model.tsv` and `model_manifest.json` preserve the expected structure and annotations. `tools/BuildProject.java` is optional build/validation tooling, **not a Java helper used by any transition**. The original workspace Java classes are not used.

Syntax and operation references: [official Renew 4.1 manual](https://www.informatik.uni-hamburg.de/TGI/renew/4.1/renew4.1.pdf) and [official installation instructions](https://www.informatik.uni-hamburg.de/TGI/renew/4.1/install.html).
