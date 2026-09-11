# Model validation

Validation date: 2026-09-11. Runtime: official Renew 4.1 base distribution, Java 17.0.20.1.

## Outcome

GUI compatibility correction: all RNW files now include the required version `12` header. The original low-level validation bypassed version detection and did not catch its absence. Validation now uses `StorableInputDrawingLoader.readStorableDrawing`, the same version-aware entry point used by Renew's GUI. All eleven corrected drawings were also opened in the actual Renew interface and appeared in its Windows menu. Compilation and the directed smoke test passed again after this correction; the checksums below cover the corrected files.

PASS: all eleven saved `.rnw` drawings reopened with Renew StorableInput and compiled together using its built-in Timed Java Compiler configuration (`JavaNetCompiler(true,true,true)`). The native graph audit matched **76 places, 68 transitions, and 136 ordinary directed arcs** against the structure transcribed from the request. These are static model counts, not simulation statistics.

PASS: the actual Renew engine fired the initial synchronized creation step and the directed successful path in README.md. It verified one token in each of the eight agents' expected final places. See `validation.log` for the executed transitions and checks. The two object nets participated through their synchronous channels.

The first development smoke test exposed mistakenly generated test arcs; the generator was corrected to create ordinary directed arcs explicitly. The final saved files were regenerated, audited for arc type and endpoints, recompiled, and successfully executed. No claim of exhaustive state-space exploration is made.

## Project files

- `CompetencyNet.rnw`
- `CredentialObject.rnw`
- `EvidenceNet.rnw`
- `EvidenceObject.rnw`
- `HEDULedgerNet.rnw`
- `HRAgent.rnw`
- `Launch.ps1`
- `model.tsv`
- `model_manifest.json`
- `MODEL_VALIDATION.md`
- `ProfessorAgent.rnw`
- `README.md`
- `StudentAgent.rnw`
- `SystemNet.rnw`
- `tools/BuildProject.java`
- `UniversityAgent.rnw`
- `Validate.ps1`
- `validation.log`
- `WalletNet.rnw`
- `MODEL_VALIDATION.md` (this report).

The optional Renew runtime is adjacent to this project in `../.validation`; it is not a model dependency beyond having a Renew installation. The project itself is portable.

## Launch and replay

1. Open PowerShell in this directory and run `./Launch.ps1`, or `./Launch.ps1 -RenewHome "C:\path\to\Renew"`. The supplied script opens all eleven drawings. Java must be available on PATH.
2. Confirm **Simulation > Formalisms > Timed Java Compiler**. Under **Simulation > Configure Simulation > Engine**, enable **Sequential mode**, set **Multiplicity = 1**, and apply.
3. Select **SystemNet**, then **Simulation > Simulation Step** (Ctrl+I). This creates the root instance and executes its enabled startup step.
4. Double-click net-reference tokens to open the agent instances. Right-click enabled initiating transitions to replay the ordered sequence in README.md. Channel partners fire together.
5. Alternatively choose **Simulation > Run Simulation** (Ctrl+R) for automatic nondeterministic execution. Use **Halt Simulation** to pause and **Terminate Simulation** before a fresh scenario.
6. Run `./Validate.ps1` with the same optional `-RenewHome` to reopen, audit, and compile the saved drawings without modifying them. This optional tool requires a JDK.

The Timed Java Compiler configuration is required for this chosen initialization design: freshly created instances provide their initial tokens to synchronous initialization steps. No custom compiler or Java model code is required. `currentTime` is a bound integer input, not Renew simulation time.

## Initial markings

| Net | Place | One initial token |
|---|---|---|
| SystemNet | p_StudentPool | `[]` |
| SystemNet | p_ProfessorPool | `[]` |
| SystemNet | p_UniversityPool | `[]` |
| SystemNet | p_EvidencePool | `[]` |
| SystemNet | p_CompetencyPool | `[]` |
| SystemNet | p_WalletPool | `[]` |
| SystemNet | p_LedgerPool | `[]` |
| SystemNet | p_HRPool | `[]` |
| StudentAgent | p_Idle | `[80,50,0,100]` |
| ProfessorAgent | p_Idle | `[]` |
| UniversityAgent | p_Idle | `[]` |
| EvidenceNet | p_Created | `[]` |
| CompetencyNet | p_Submitted | `[]` |
| WalletNet | p_Empty | `[]` |
| HEDULedgerNet | p_Received | `[]` |
| HRAgent | p_Idle | `[]` |
| EvidenceObject | p_Created | `[]` |
| CredentialObject | p_Submitted | `[]` |

All unlisted places start empty. Each marking is per instance; subnets are instantiated only through the creation transitions. `[]` is one native empty-tuple token, not an empty marking. There are eight root seeds and one start token per subordinate instance.

The StudentAgent input tuple is `[grade,passing_threshold,currentTime,expiryDate] = [80,50,0,100]`. These arbitrary scenario inputs are editable, not grades or timing measurements obtained from real data. Integers are bound by its first input arc and passed through channel parameters and native tuple arcs thereafter.

## Synchronous channels

Each line below gives the exact transition inscription endpoint. A leading colon is an uplink; a reference followed by a colon is a downlink. Name and arity define the channel signature. Reference variables are passed from SystemNet or created locally, never resolved as undefined Java objects.

### anchor_request/0

- downlink: `CompetencyNet.t_SBT_Accept`: `l:anchor_request()`.
- uplink: `HEDULedgerNet.t_Execute`: `:anchor_request()`.

### consent/0

- downlink: `StudentAgent.t_ReceiveCredential`: `w:consent()`.
- uplink: `WalletNet.t_RequestConsent`: `:consent()`.

### createc/1

- downlink: `SystemNet.t_CreateStudent`: `this:createc(c)`.
- uplink: `SystemNet.t_CreateCompetency`: `:createc(c)`.

### createe/1

- downlink: `SystemNet.t_CreateStudent`: `this:createe(e)`.
- uplink: `SystemNet.t_CreateEvidence`: `:createe(e)`.

### createh/1

- downlink: `SystemNet.t_CreateStudent`: `this:createh(h)`.
- uplink: `SystemNet.t_CreateHR`: `:createh(h)`.

### createl/1

- downlink: `SystemNet.t_CreateStudent`: `this:createl(l)`.
- uplink: `SystemNet.t_CreateLedger`: `:createl(l)`.

### createp/1

- downlink: `SystemNet.t_CreateStudent`: `this:createp(p)`.
- uplink: `SystemNet.t_CreateProfessor`: `:createp(p)`.

### createu/1

- downlink: `SystemNet.t_CreateStudent`: `this:createu(u)`.
- uplink: `SystemNet.t_CreateUniversity`: `:createu(u)`.

### createw/1

- downlink: `SystemNet.t_CreateStudent`: `this:createw(w)`.
- uplink: `SystemNet.t_CreateWallet`: `:createw(w)`.

### evaluate/0

- downlink: `ProfessorAgent.t_AssessEvidence`: `e:evaluate()`.
- uplink: `EvidenceNet.t_Grade_Assign`: `:evaluate()`.
- downlink: `EvidenceNet.t_Grade_Assign`: `eo:evaluate()`.
- uplink: `EvidenceNet.t_Reject`: `:evaluate()`.
- uplink: `EvidenceObject.t_Grade_Assign`: `:evaluate()`.

### evaluate/4

- downlink: `CompetencyNet.t_Review_Init`: `co:evaluate(grade,passing_threshold,currentTime,expiryDate)`.
- uplink: `CredentialObject.t_StartReview`: `:evaluate(grade,passing_threshold,currentTime,expiryDate)`.

### expire/0

- downlink: `CompetencyNet.t_SBT_Expire`: `co:expire()`.
- uplink: `CredentialObject.t_Expire`: `:expire()`.

### lms/0

- downlink: `EvidenceNet.t_LMS_Submit`: `eo:lms()`.
- uplink: `EvidenceObject.t_LMS_Submit`: `:lms()`.

### sbt_accept/0

- downlink: `StudentAgent.t_AcceptCredential`: `w:sbt_accept()`.
- uplink: `CompetencyNet.t_SBT_Accept`: `:sbt_accept()`.
- downlink: `CompetencyNet.t_SBT_Accept`: `co:sbt_accept()`.
- uplink: `WalletNet.t_AcceptCredential`: `:sbt_accept()`.
- downlink: `WalletNet.t_AcceptCredential`: `c:sbt_accept()`.
- uplink: `CredentialObject.t_Accept`: `:sbt_accept()`.

### sbt_cancel/0

- downlink: `CompetencyNet.t_SBT_Cancel`: `co:sbt_cancel()`.
- uplink: `CredentialObject.t_Cancel`: `:sbt_cancel()`.

### sbt_mint/0

- downlink: `UniversityAgent.t_IssueCredential`: `c:sbt_mint()`.
- uplink: `CompetencyNet.t_SBT_Mint`: `:sbt_mint()`.
- downlink: `CompetencyNet.t_SBT_Mint`: `co:sbt_mint()`.
- uplink: `CredentialObject.t_Mint`: `:sbt_mint()`.

### sbt_mint/1

- downlink: `CompetencyNet.t_SBT_Mint`: `w:sbt_mint(this)`.
- uplink: `WalletNet.t_ReceiveCredential`: `:sbt_mint(c)`.

### sbt_reject/0

- downlink: `StudentAgent.t_RejectCredential`: `w:sbt_reject()`.
- uplink: `CompetencyNet.t_SBT_Reject`: `:sbt_reject()`.
- downlink: `CompetencyNet.t_SBT_Reject`: `co:sbt_reject()`.
- uplink: `WalletNet.t_RejectCredential`: `:sbt_reject()`.
- downlink: `WalletNet.t_RejectCredential`: `c:sbt_reject()`.
- uplink: `CredentialObject.t_Reject`: `:sbt_reject()`.

### setup/7

- downlink: `SystemNet.t_CreateStudent`: `s:setup(p,u,e,c,w,l,h)`.
- uplink: `StudentAgent.t_PrepareEvidence`: `:setup(p,u,e,c,w,l,h)`.

### submit/0

- downlink: `StudentAgent.t_ShareVP`: `h:submit()`.
- downlink: `EvidenceNet.t_IPFS_Upload`: `eo:submit()`.
- uplink: `HRAgent.t_ReceiveVP`: `:submit()`.
- uplink: `EvidenceObject.t_IPFS_Upload`: `:submit()`.

### submit/2

- downlink: `StudentAgent.t_SubmitEvidence`: `e:submit(grade,passing_threshold)`.
- uplink: `EvidenceNet.t_IPFS_Upload`: `:submit(grade,passing_threshold)`.

### submit/7

- downlink: `UniversityAgent.t_RequestCredential`: `c:submit(w,l,h,grade,passing_threshold,currentTime,expiryDate)`.
- uplink: `CompetencyNet.t_Review_Init`: `:submit(w,l,h,grade,passing_threshold,currentTime,expiryDate)`.

### submit/8

- downlink: `ProfessorAgent.t_ApproveEvidence`: `u:submit(c,w,l,h,grade,passing_threshold,currentTime,expiryDate)`.
- uplink: `UniversityAgent.t_ReceiveEvidence`: `:submit(c,w,l,h,grade,passing_threshold,currentTime,expiryDate)`.

### submit/10

- downlink: `StudentAgent.t_SubmitEvidence`: `p:submit(u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate)`.
- uplink: `ProfessorAgent.t_ReceiveEvidence`: `:submit(u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate)`.

### update_profile/0

- downlink: `CompetencyNet.t_Agg_Ingest`: `co:update_profile()`.
- downlink: `CompetencyNet.t_Agg_Ingest`: `l:update_profile()`.
- downlink: `CompetencyNet.t_Agg_Ingest`: `h:update_profile()`.
- uplink: `HEDULedgerNet.t_Anchor`: `:update_profile()`.
- uplink: `HRAgent.t_BuildProfile`: `:update_profile()`.
- uplink: `CredentialObject.t_Aggregate`: `:update_profile()`.

### validate/0

- downlink: `CompetencyNet.t_Verification_Pass`: `co:validate()`.
- uplink: `CredentialObject.t_Validate`: `:validate()`.

All required names are present with paired endpoints: `submit`, `evaluate`, `sbt_mint`, `sbt_accept`, `sbt_reject`, `sbt_cancel`, `update_profile`. Additional channels perform initialization and lifecycle handoffs without additional graph nodes or arcs.

SystemNet binds `s,p,u,e,c,w,l,h` using the exact eight requested `:new` inscriptions. Its Student creation transition calls the other seven creation transitions via local `this:create…` channels, then configures StudentAgent through `s:setup(...)`. Each pool retains its created reference, so the input seed `[]` cannot be reused for unlimited creation. EvidenceNet creates `eo :new EvidenceObject`; CompetencyNet creates `co :new CredentialObject` on existing transitions.

## Guards and bindings

| Net.transition | Guard | Binding source |
|---|---|---|
| ProfessorAgent.t_ApproveEvidence | `guard grade >= passing_threshold` | Input arc token `[u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate]` |
| ProfessorAgent.t_RejectEvidence | `guard grade < passing_threshold` | Input arc token `[u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate]` |
| UniversityAgent.t_RequestCredential | `guard grade >= passing_threshold` | Input arc token `[c,w,l,h,grade,passing_threshold,currentTime,expiryDate]` |
| UniversityAgent.t_RejectEvidence | `guard grade < passing_threshold` | Input arc token `[c,w,l,h,grade,passing_threshold,currentTime,expiryDate]` |
| EvidenceNet.t_Grade_Assign | `guard grade >= passing_threshold` | Input arc token `[eo,grade,passing_threshold]` |
| EvidenceNet.t_Reject | `guard grade < passing_threshold` | Input arc token `[eo,grade,passing_threshold]` |
| CompetencyNet.t_Verification_Pass | `guard grade >= passing_threshold` | Input arc token `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate]` |
| CompetencyNet.t_SBT_Accept | `guard currentTime < expiryDate` | Input arc token `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate]` |
| CompetencyNet.t_SBT_Expire | `guard currentTime >= expiryDate` | Input arc token `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate]` |
| CredentialObject.t_Validate | `guard grade >= passing_threshold` | Input arc token `[grade,passing_threshold,currentTime,expiryDate]` |
| CredentialObject.t_Accept | `guard currentTime < expiryDate` | Input arc token `[grade,passing_threshold,currentTime,expiryDate]` |
| CredentialObject.t_Expire | `guard currentTime >= expiryDate` | Input arc token `[grade,passing_threshold,currentTime,expiryDate]` |

All guard operands are bound on incoming tuple arcs before evaluation. Object-net values originate in `:evaluate(grade,passing_threshold,currentTime,expiryDate)`. Accepted and expired time conditions are complementary. Numeric assignment is unnecessary and no unimplemented functions occur.

## Complete per-file structural inventory

### SystemNet.rnw

Places: `p_StudentPool`, `p_ProfessorPool`, `p_UniversityPool`, `p_EvidencePool`, `p_CompetencyPool`, `p_WalletPool`, `p_LedgerPool`, `p_HRPool`.

Transitions: `t_CreateStudent`, `t_CreateProfessor`, `t_CreateUniversity`, `t_CreateEvidence`, `t_CreateCompetency`, `t_CreateWallet`, `t_CreateLedger`, `t_CreateHR`.

Directed paths:

- `p_StudentPool -> t_CreateStudent -> p_StudentPool`
- `p_ProfessorPool -> t_CreateProfessor -> p_ProfessorPool`
- `p_UniversityPool -> t_CreateUniversity -> p_UniversityPool`
- `p_EvidencePool -> t_CreateEvidence -> p_EvidencePool`
- `p_CompetencyPool -> t_CreateCompetency -> p_CompetencyPool`
- `p_WalletPool -> t_CreateWallet -> p_WalletPool`
- `p_LedgerPool -> t_CreateLedger -> p_LedgerPool`
- `p_HRPool -> t_CreateHR -> p_HRPool`

Transition inscriptions (unlisted transitions have none):

- `t_CreateStudent`: `s :new StudentAgent; this:createp(p); this:createu(u); this:createe(e); this:createc(c); this:createw(w); this:createl(l); this:createh(h); s:setup(p,u,e,c,w,l,h)`
- `t_CreateProfessor`: `p :new ProfessorAgent; :createp(p)`
- `t_CreateUniversity`: `u :new UniversityAgent; :createu(u)`
- `t_CreateEvidence`: `e :new EvidenceNet; :createe(e)`
- `t_CreateCompetency`: `c :new CompetencyNet; :createc(c)`
- `t_CreateWallet`: `w :new WalletNet; :createw(w)`
- `t_CreateLedger`: `l :new HEDULedgerNet; :createl(l)`
- `t_CreateHR`: `h :new HRAgent; :createh(h)`

### StudentAgent.rnw

Places: `p_Idle`, `p_EvidenceReady`, `p_Submitted`, `p_WaitingCredential`, `p_CredentialReceived`, `p_Accepted`, `p_Rejected`, `p_PresentationReady`, `p_VPShared`.

Transitions: `t_PrepareEvidence`, `t_SubmitEvidence`, `t_WaitCredential`, `t_ReceiveCredential`, `t_AcceptCredential`, `t_RejectCredential`, `t_GenerateVP`, `t_ShareVP`.

Directed paths:

- `p_Idle -> t_PrepareEvidence -> p_EvidenceReady`
- `p_EvidenceReady -> t_SubmitEvidence -> p_Submitted`
- `p_Submitted -> t_WaitCredential -> p_WaitingCredential`
- `p_WaitingCredential -> t_ReceiveCredential -> p_CredentialReceived`
- `p_CredentialReceived -> t_AcceptCredential -> p_Accepted`
- `p_CredentialReceived -> t_RejectCredential -> p_Rejected`
- `p_Accepted -> t_GenerateVP -> p_PresentationReady`
- `p_PresentationReady -> t_ShareVP -> p_VPShared`

Transition inscriptions (unlisted transitions have none):

- `t_PrepareEvidence`: `:setup(p,u,e,c,w,l,h)`
- `t_SubmitEvidence`: `p:submit(u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate); e:submit(grade,passing_threshold)`
- `t_ReceiveCredential`: `w:consent()`
- `t_AcceptCredential`: `w:sbt_accept()`
- `t_RejectCredential`: `w:sbt_reject()`
- `t_ShareVP`: `h:submit()`

### ProfessorAgent.rnw

Places: `p_Idle`, `p_EvidenceReceived`, `p_UnderReview`, `p_Evaluated`, `p_Approved`, `p_Rejected`.

Transitions: `t_ReceiveEvidence`, `t_StartReview`, `t_AssessEvidence`, `t_ApproveEvidence`, `t_RejectEvidence`.

Directed paths:

- `p_Idle -> t_ReceiveEvidence -> p_EvidenceReceived`
- `p_EvidenceReceived -> t_StartReview -> p_UnderReview`
- `p_UnderReview -> t_AssessEvidence -> p_Evaluated`
- `p_Evaluated -> t_ApproveEvidence -> p_Approved`
- `p_Evaluated -> t_RejectEvidence -> p_Rejected`

Transition inscriptions (unlisted transitions have none):

- `t_ReceiveEvidence`: `:submit(u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate)`
- `t_AssessEvidence`: `e:evaluate()`
- `t_ApproveEvidence`: `guard grade >= passing_threshold; u:submit(c,w,l,h,grade,passing_threshold,currentTime,expiryDate)`
- `t_RejectEvidence`: `guard grade < passing_threshold`

### UniversityAgent.rnw

Places: `p_Idle`, `p_EvidenceReceived`, `p_Verifying`, `p_CredentialRequested`, `p_CredentialIssued`, `p_Rejected`.

Transitions: `t_ReceiveEvidence`, `t_VerifyEvidence`, `t_RequestCredential`, `t_IssueCredential`, `t_RejectEvidence`.

Directed paths:

- `p_Idle -> t_ReceiveEvidence -> p_EvidenceReceived`
- `p_EvidenceReceived -> t_VerifyEvidence -> p_Verifying`
- `p_Verifying -> t_RequestCredential -> p_CredentialRequested`
- `p_Verifying -> t_RejectEvidence -> p_Rejected`
- `p_CredentialRequested -> t_IssueCredential -> p_CredentialIssued`

Transition inscriptions (unlisted transitions have none):

- `t_ReceiveEvidence`: `:submit(c,w,l,h,grade,passing_threshold,currentTime,expiryDate)`
- `t_RequestCredential`: `guard grade >= passing_threshold; c:submit(w,l,h,grade,passing_threshold,currentTime,expiryDate)`
- `t_IssueCredential`: `c:sbt_mint()`
- `t_RejectEvidence`: `guard grade < passing_threshold`

### EvidenceNet.rnw

Places: `p_Created`, `p_UploadedIPFS`, `p_SubmittedLMS`, `p_UnderReview`, `p_Graded`, `p_Rejected`.

Transitions: `t_IPFS_Upload`, `t_LMS_Submit`, `t_Review_Request`, `t_Grade_Assign`, `t_Reject`.

Directed paths:

- `p_Created -> t_IPFS_Upload -> p_UploadedIPFS`
- `p_UploadedIPFS -> t_LMS_Submit -> p_SubmittedLMS`
- `p_SubmittedLMS -> t_Review_Request -> p_UnderReview`
- `p_UnderReview -> t_Grade_Assign -> p_Graded`
- `p_UnderReview -> t_Reject -> p_Rejected`

Transition inscriptions (unlisted transitions have none):

- `t_IPFS_Upload`: `:submit(grade,passing_threshold); eo :new EvidenceObject; eo:submit()`
- `t_LMS_Submit`: `eo:lms()`
- `t_Grade_Assign`: `:evaluate(); guard grade >= passing_threshold; eo:evaluate()`
- `t_Reject`: `:evaluate(); guard grade < passing_threshold`

### CompetencyNet.rnw

Places: `p_Submitted`, `p_UnderReview`, `p_Validated`, `p_PendingAccept`, `p_BlockchainAnchored`, `p_ProfileAggregated`, `p_Terminated`.

Transitions: `t_Review_Init`, `t_Verification_Pass`, `t_SBT_Mint`, `t_SBT_Accept`, `t_SBT_Reject`, `t_SBT_Cancel`, `t_SBT_Expire`, `t_Agg_Ingest`.

Directed paths:

- `p_Submitted -> t_Review_Init -> p_UnderReview`
- `p_UnderReview -> t_Verification_Pass -> p_Validated`
- `p_Validated -> t_SBT_Mint -> p_PendingAccept`
- `p_PendingAccept -> t_SBT_Accept -> p_BlockchainAnchored`
- `p_PendingAccept -> t_SBT_Reject -> p_Terminated`
- `p_PendingAccept -> t_SBT_Cancel -> p_Terminated`
- `p_PendingAccept -> t_SBT_Expire -> p_Terminated`
- `p_BlockchainAnchored -> t_Agg_Ingest -> p_ProfileAggregated`

Transition inscriptions (unlisted transitions have none):

- `t_Review_Init`: `:submit(w,l,h,grade,passing_threshold,currentTime,expiryDate); co :new CredentialObject; co:evaluate(grade,passing_threshold,currentTime,expiryDate)`
- `t_Verification_Pass`: `guard grade >= passing_threshold; co:validate()`
- `t_SBT_Mint`: `:sbt_mint(); co:sbt_mint(); w:sbt_mint(this)`
- `t_SBT_Accept`: `:sbt_accept(); guard currentTime < expiryDate; co:sbt_accept(); l:anchor_request()`
- `t_SBT_Reject`: `:sbt_reject(); co:sbt_reject()`
- `t_SBT_Cancel`: `co:sbt_cancel()`
- `t_SBT_Expire`: `guard currentTime >= expiryDate; co:expire()`
- `t_Agg_Ingest`: `co:update_profile(); l:update_profile(); h:update_profile()`

### WalletNet.rnw

Places: `p_Empty`, `p_CredentialReceived`, `p_PendingConsent`, `p_Accepted`, `p_Rejected`, `p_VPReady`, `p_VPShared`.

Transitions: `t_ReceiveCredential`, `t_RequestConsent`, `t_AcceptCredential`, `t_RejectCredential`, `t_GenerateVP`, `t_ShareVP`.

Directed paths:

- `p_Empty -> t_ReceiveCredential -> p_CredentialReceived`
- `p_CredentialReceived -> t_RequestConsent -> p_PendingConsent`
- `p_PendingConsent -> t_AcceptCredential -> p_Accepted`
- `p_PendingConsent -> t_RejectCredential -> p_Rejected`
- `p_Accepted -> t_GenerateVP -> p_VPReady`
- `p_VPReady -> t_ShareVP -> p_VPShared`

Transition inscriptions (unlisted transitions have none):

- `t_ReceiveCredential`: `:sbt_mint(c)`
- `t_RequestConsent`: `:consent()`
- `t_AcceptCredential`: `:sbt_accept(); c:sbt_accept()`
- `t_RejectCredential`: `:sbt_reject(); c:sbt_reject()`

### HEDULedgerNet.rnw

Places: `p_Received`, `p_Executed`, `p_Ordered`, `p_Validated`, `p_Committed`, `p_Anchored`, `p_Rejected`.

Transitions: `t_Execute`, `t_Order`, `t_Validate`, `t_Commit`, `t_Anchor`, `t_Reject`.

Directed paths:

- `p_Received -> t_Execute -> p_Executed`
- `p_Executed -> t_Order -> p_Ordered`
- `p_Ordered -> t_Validate -> p_Validated`
- `p_Ordered -> t_Reject -> p_Rejected`
- `p_Validated -> t_Commit -> p_Committed`
- `p_Committed -> t_Anchor -> p_Anchored`

Transition inscriptions (unlisted transitions have none):

- `t_Execute`: `:anchor_request()`
- `t_Anchor`: `:update_profile()`

### HRAgent.rnw

Places: `p_Idle`, `p_VPReceived`, `p_Parsed`, `p_CompetencyProfile`, `p_Matching`, `p_GapAnalysis`, `p_ResultReady`.

Transitions: `t_ReceiveVP`, `t_ParseVP`, `t_BuildProfile`, `t_SemanticMatch`, `t_GapAnalysis`, `t_GenerateResult`.

Directed paths:

- `p_Idle -> t_ReceiveVP -> p_VPReceived`
- `p_VPReceived -> t_ParseVP -> p_Parsed`
- `p_Parsed -> t_BuildProfile -> p_CompetencyProfile`
- `p_CompetencyProfile -> t_SemanticMatch -> p_Matching`
- `p_Matching -> t_GapAnalysis -> p_GapAnalysis`
- `p_GapAnalysis -> t_GenerateResult -> p_ResultReady`

Transition inscriptions (unlisted transitions have none):

- `t_ReceiveVP`: `:submit()`
- `t_BuildProfile`: `:update_profile()`

### EvidenceObject.rnw

Places: `p_Created`, `p_UploadedIPFS`, `p_SubmittedLMS`, `p_Graded`.

Transitions: `t_IPFS_Upload`, `t_LMS_Submit`, `t_Grade_Assign`.

Directed paths:

- `p_Created -> t_IPFS_Upload -> p_UploadedIPFS`
- `p_UploadedIPFS -> t_LMS_Submit -> p_SubmittedLMS`
- `p_SubmittedLMS -> t_Grade_Assign -> p_Graded`

Transition inscriptions (unlisted transitions have none):

- `t_IPFS_Upload`: `:submit()`
- `t_LMS_Submit`: `:lms()`
- `t_Grade_Assign`: `:evaluate()`

### CredentialObject.rnw

Places: `p_Submitted`, `p_UnderReview`, `p_Validated`, `p_PendingAccept`, `p_Anchored`, `p_Rejected`, `p_Cancelled`, `p_Expired`, `p_Aggregated`.

Transitions: `t_StartReview`, `t_Validate`, `t_Mint`, `t_Accept`, `t_Reject`, `t_Cancel`, `t_Expire`, `t_Aggregate`.

Directed paths:

- `p_Submitted -> t_StartReview -> p_UnderReview`
- `p_UnderReview -> t_Validate -> p_Validated`
- `p_Validated -> t_Mint -> p_PendingAccept`
- `p_PendingAccept -> t_Accept -> p_Anchored`
- `p_PendingAccept -> t_Reject -> p_Rejected`
- `p_PendingAccept -> t_Cancel -> p_Cancelled`
- `p_PendingAccept -> t_Expire -> p_Expired`
- `p_Anchored -> t_Aggregate -> p_Aggregated`

Transition inscriptions (unlisted transitions have none):

- `t_StartReview`: `:evaluate(grade,passing_threshold,currentTime,expiryDate)`
- `t_Validate`: `:validate(); guard grade >= passing_threshold`
- `t_Mint`: `:sbt_mint()`
- `t_Accept`: `:sbt_accept(); guard currentTime < expiryDate`
- `t_Reject`: `:sbt_reject()`
- `t_Cancel`: `:sbt_cancel()`
- `t_Expire`: `:expire(); guard currentTime >= expiryDate`
- `t_Aggregate`: `:update_profile()`

## Java code

No Java domain helper code is added or required. No calls to uploadToIPFS(), generateVP(), semanticMatch(), buildProfile(), or the workspace's pre-existing Java classes are used. `tools/BuildProject.java` is separate build-time tooling: it uses Renew's own drawing writer, file reader, compiler, and simulation APIs. Nothing in the RNW files references that class.

## Known limitations and untested properties

- One workflow instance per SystemNet start; no 500-agent load model or batch configuration is implied.
- Grade and logical time are fixed scenario inputs. Time does not advance and expiry does not occur through a timer; change the input and restart for a different time scenario.
- IPFS, LMS, SBT minting, wallet consent, ledger execution/validation, VP creation, semantic matching, gap analysis, and profile aggregation are abstract control-flow events. No cryptography, network calls, persistent storage, real grades, or computed matching results are implemented.
- Automatic cancellation can compete with acceptance or rejection at p_PendingAccept. Acceptance, rejection, cancellation, and expiry branches were compiled and their channels statically paired. Only the directed successful path was executed in the supplied smoke test; alternate branches have not been exhaustively tested.
- A rejected evidence branch leaves StudentAgent waiting because the fixed StudentAgent graph has no evidence-rejection reception transition. Competency cancellation or expiry likewise cannot notify a corresponding wallet/student termination state under the listed structure. These limitations are preserved rather than adding transitions.
- University rejection is structurally present but cannot be reached from the supplied SystemNet scenario after the professor has accepted the same unchanged grade. Ledger validation/rejection remain nondeterministic abstractions; choosing ledger rejection prevents later synchronized aggregation.
- If evidence is rejected, EvidenceObject remains at p_SubmittedLMS because its prescribed graph has no rejection state. Wallet and student presentation steps are separate abstractions; no VP payload is transported.
- Agent and object drawings with uplinks are intended to run under SystemNet. Opening them as standalone root instances will generally wait for external channel partners.
- Compilation and one successful execution do not establish liveness, deadlock freedom, fairness, soundness, exhaustive channel reachability, or security. No state-space size, reachable-marking count, throughput, latency, deadlock count, or 500-agent results have been calculated or fabricated.
- Renew 4.1 / Java 17 was tested. Other versions were not executed. Headless validation emitted only missing-log4j-appender warnings; these are runtime logging configuration messages, not model syntax errors.

## Saved RNW checksums

| File | SHA-256 |
|---|---|
| CompetencyNet.rnw | `ae18d9594edfd79bfc237d17e5abcf71bbb7ee469479b20a8c4dd182bd802700` |
| CredentialObject.rnw | `b384376cf22d7a09a01b26e6a2a5dfa7911ab0f85a08d7c469375929b97890dd` |
| EvidenceNet.rnw | `66f4cd9d2a7989acd35888e5e4bf15d07a62d8b2c48c26827f943f840befc99a` |
| EvidenceObject.rnw | `55f3c3a152055b5b45fae0bc82919558e5dadd367d2959f6ffae8745a77b6744` |
| HEDULedgerNet.rnw | `b8df9ba7b5f1ec0040e7e784bf881291df83de98cad0a6b61222362b51992d56` |
| HRAgent.rnw | `3d0dce9225e196a535f673fb17787ee338b8043342f2a030cff52a89e0208199` |
| ProfessorAgent.rnw | `780394fb61fbe8fab94891a1266a13c4931e447041215aa67e446d6aec27dd29` |
| StudentAgent.rnw | `ccf87fb267a918d425ce8ce61dc62a2227ea62c6f72099038fe24f15e1bc93ad` |
| SystemNet.rnw | `04bc84a59bff43a5ec27947f14635163f472e1e28d433d0a3ce4c9382a7553c3` |
| UniversityAgent.rnw | `a70770208e77b7c16d76c1ad5f9a6d08e77c16a229f693dc95263921340c7add` |
| WalletNet.rnw | `866721863b7d11f94898a057e170ea2b16b7640c44dc05a22b8fb6d1bbfcd4f6` |

## Sources

- [Official Renew 4.1 user guide](https://www.informatik.uni-hamburg.de/TGI/renew/4.1/renew4.1.pdf): native tuples and lists, net creation, synchronous channels, guards, and Simulation menu operation.
- [Official Renew 4.1 installation instructions](https://www.informatik.uni-hamburg.de/TGI/renew/4.1/install.html): modular Windows launcher.
- Installed official runtime APIs and successful native execution are the direct evidence for file-format and compiler compatibility.
