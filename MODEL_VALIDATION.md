# Model validation — functional integration

Baseline `4c98b05507d7e92ea7223c9e4bf000371297c75e`; branch `feat/renew-functional-integration`. Validated 2026-09-11 with official Renew 4.1 and Java 17.

## Results

GUI regression check: Launch.ps1 successfully opened all eleven integrated drawings, verified in the actual Renew Windows menu. The launcher now passes relative filenames from the project directory, avoiding Renew's secondary interpretation of backslashes in absolute Windows arguments.

PASS: all 11 version-12 RNW drawings load through Renew’s GUI-compatible StorableInputDrawingLoader and compile with the Timed Java Compiler, sequential engine. All names, directed arcs and saved text inscriptions match model.tsv. The files were edited in place; no drawing was regenerated.

PASS: 76 places, 68 transitions, 136 ordinary directed arcs, unchanged from the baseline. All non-text serialization (coordinates, graphic attributes, figure IDs, REF links, connectors and locators) matches the baseline after normalizing line endings. SystemNet is unchanged.

PASS: seven directed scenario checks. See VALIDATION_EVIDENCE.txt for preserved native output, including final payload-bearing markings, and validation.log for the most recent local validation run.

| Scenario | Executed result |
|---|---|
| happy | Evidence submission, evaluation, validation, mint, consent, accept, ledger commit/anchor and aggregation completed; all 8 agent terminal places plus both object terminal places checked. |
| reject | Student and wallet reject; CompetencyNet terminates; CredentialObject p_Rejected marked. |
| cancel | CompetencyNet terminates; CredentialObject p_Cancelled marked. |
| expire | currentTime == expiryDate; acceptance binding absent; CompetencyNet terminates and CredentialObject p_Expired marked. |
| grade_reject | Grade set to 40 in memory; EvidenceNet and ProfessorAgent reject. No claim of global completion. |
| ledger_reject | Ledger p_Rejected marked; aggregation cannot finish. |
| bad_signature | Student signature set to wrong-signature in memory; acceptance has no binding and student remains at p_CredentialReceived. This checks placeholder equality, not cryptography. |

## Reproduce

From the project directory run `powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\Validate.ps1 -Smoke`. This override applies only to the invoked process; it does not change system policy. Add `-RenewHome "C:\path\to\Renew"` if the default adjacent runtime is unavailable. The validator and smoke tests never write RNW files. Each scenario runs in a fresh JVM. Native compilation failures and failed assertions return nonzero and stop the script.

Open using `powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\Launch.ps1`. Select Timed Java Compiler, sequential mode with multiplicity 1; select SystemNet and press Ctrl+I. See README.md for the directed sequence.

## Current channel and binding audit

CHANNEL_MATRIX.md contains every caller, receiver, exact inscription, target instance provenance, positional argument mapping, and an executed witness. All seven requested business signatures are implemented. Existing internal channel names remain; no additional name was invented. No undefined variable or missing template was detected. Runtime tests verify synchronization using live instances, not string names.

## All initial markings

| Net | Place | One token |
|---|---|---|
| SystemNet | p_StudentPool | `[]` |
| SystemNet | p_ProfessorPool | `[]` |
| SystemNet | p_UniversityPool | `[]` |
| SystemNet | p_EvidencePool | `[]` |
| SystemNet | p_CompetencyPool | `[]` |
| SystemNet | p_WalletPool | `[]` |
| SystemNet | p_LedgerPool | `[]` |
| SystemNet | p_HRPool | `[]` |
| StudentAgent | p_Idle | `[80,50,0,100,"did:example:student1","bafyEvidence001","student-signature"]` |
| ProfessorAgent | p_Idle | `["did:example:assessor1","https://example.org/criteria"]` |
| UniversityAgent | p_Idle | `["did:example:issuer1","did:example:student1","credential-001"]` |
| EvidenceNet | p_Created | `50` |
| CompetencyNet | p_Submitted | `["did:example:issuer1","did:example:student1","credential-001","issuer-signature","did:example:student1","vp-001"]` |
| WalletNet | p_Empty | `[]` |
| HEDULedgerNet | p_Received | `[]` |
| HRAgent | p_Idle | `[]` |
| EvidenceObject | p_Created | `[]` |
| CredentialObject | p_Submitted | `["did:example:issuer1","did:example:student1","credential-001","student-signature","issuer-signature","did:example:student1","vp-001"]` |

All other places are empty. No initial token count was increased: eight SystemNet seeds plus one starting token per created subordinate instance. Some unit tokens became primitive/tuple records to bind the protocol data. Native [] denotes one empty-tuple token.

## Every guard

| Net.transition | Guard | Input binding |
|---|---|---|
| ProfessorAgent.t_ApproveEvidence | `guard grade >= passing_threshold` | `[u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate,assessorDID,criteriaURL]` |
| ProfessorAgent.t_RejectEvidence | `guard grade < passing_threshold` | `[u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate,assessorDID,criteriaURL]` |
| UniversityAgent.t_RequestCredential | `guard grade >= passing_threshold` | `[c,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData]` |
| UniversityAgent.t_RejectEvidence | `guard grade < passing_threshold` | `[c,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData]` |
| EvidenceNet.t_Grade_Assign | `guard grade >= passing_threshold` | `[eo,studentDID,evidenceCID,passing_threshold]; grade is bound by evaluate(assessorDID,grade,criteriaURL)` |
| EvidenceNet.t_Reject | `guard grade < passing_threshold` | `[eo,studentDID,evidenceCID,passing_threshold]; grade is bound by evaluate(assessorDID,grade,criteriaURL)` |
| CompetencyNet.t_Verification_Pass | `guard grade >= passing_threshold` | `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]` |
| CompetencyNet.t_SBT_Accept | `guard currentTime < expiryDate` | `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]` |
| CompetencyNet.t_SBT_Expire | `guard currentTime >= expiryDate` | `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]` |
| CredentialObject.t_Validate | `guard grade >= passing_threshold` | `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]` |
| CredentialObject.t_Accept | `guard currentTime < expiryDate` | `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]` |
| CredentialObject.t_Expire | `guard currentTime >= expiryDate` | `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]` |

Guard evaluation occurs after the involved variables are bound by input tokens and, for EvidenceNet grade, the synchronous evaluate uplink.

## Current structural inventory and inscriptions

### SystemNet.rnw

Places: p_StudentPool, p_ProfessorPool, p_UniversityPool, p_EvidencePool, p_CompetencyPool, p_WalletPool, p_LedgerPool, p_HRPool.

Transitions and their inscriptions:

- `t_CreateStudent`: `s :new StudentAgent; this:createp(p); this:createu(u); this:createe(e); this:createc(c); this:createw(w); this:createl(l); this:createh(h); s:setup(p,u,e,c,w,l,h)`
- `t_CreateProfessor`: `p :new ProfessorAgent; :createp(p)`
- `t_CreateUniversity`: `u :new UniversityAgent; :createu(u)`
- `t_CreateEvidence`: `e :new EvidenceNet; :createe(e)`
- `t_CreateCompetency`: `c :new CompetencyNet; :createc(c)`
- `t_CreateWallet`: `w :new WalletNet; :createw(w)`
- `t_CreateLedger`: `l :new HEDULedgerNet; :createl(l)`
- `t_CreateHR`: `h :new HRAgent; :createh(h)`

Arcs:

- `p_StudentPool -> t_CreateStudent`: `[]`
- `t_CreateStudent -> p_StudentPool`: `s`
- `p_ProfessorPool -> t_CreateProfessor`: `[]`
- `t_CreateProfessor -> p_ProfessorPool`: `p`
- `p_UniversityPool -> t_CreateUniversity`: `[]`
- `t_CreateUniversity -> p_UniversityPool`: `u`
- `p_EvidencePool -> t_CreateEvidence`: `[]`
- `t_CreateEvidence -> p_EvidencePool`: `e`
- `p_CompetencyPool -> t_CreateCompetency`: `[]`
- `t_CreateCompetency -> p_CompetencyPool`: `c`
- `p_WalletPool -> t_CreateWallet`: `[]`
- `t_CreateWallet -> p_WalletPool`: `w`
- `p_LedgerPool -> t_CreateLedger`: `[]`
- `t_CreateLedger -> p_LedgerPool`: `l`
- `p_HRPool -> t_CreateHR`: `[]`
- `t_CreateHR -> p_HRPool`: `h`

### StudentAgent.rnw

Places: p_Idle, p_EvidenceReady, p_Submitted, p_WaitingCredential, p_CredentialReceived, p_Accepted, p_Rejected, p_PresentationReady, p_VPShared.

Transitions and their inscriptions:

- `t_PrepareEvidence`: `:setup(p,u,e,c,w,l,h)`
- `t_SubmitEvidence`: `p:submit(u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate); e:submit(studentDID,evidenceCID)`
- `t_WaitCredential`: `(none)`
- `t_ReceiveCredential`: `w:consent()`
- `t_AcceptCredential`: `w:sbt_accept(studentSignature)`
- `t_RejectCredential`: `w:sbt_reject(studentSignature)`
- `t_GenerateVP`: `(none)`
- `t_ShareVP`: `h:submit()`

Arcs:

- `p_Idle -> t_PrepareEvidence`: `[grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]`
- `t_PrepareEvidence -> p_EvidenceReady`: `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]`
- `p_EvidenceReady -> t_SubmitEvidence`: `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]`
- `t_SubmitEvidence -> p_Submitted`: `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]`
- `p_Submitted -> t_WaitCredential`: `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]`
- `t_WaitCredential -> p_WaitingCredential`: `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]`
- `p_WaitingCredential -> t_ReceiveCredential`: `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]`
- `t_ReceiveCredential -> p_CredentialReceived`: `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]`
- `p_CredentialReceived -> t_AcceptCredential`: `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]`
- `t_AcceptCredential -> p_Accepted`: `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]`
- `p_CredentialReceived -> t_RejectCredential`: `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]`
- `t_RejectCredential -> p_Rejected`: `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]`
- `p_Accepted -> t_GenerateVP`: `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]`
- `t_GenerateVP -> p_PresentationReady`: `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]`
- `p_PresentationReady -> t_ShareVP`: `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]`
- `t_ShareVP -> p_VPShared`: `[p,u,e,c,w,l,h,grade,passing_threshold,currentTime,expiryDate,studentDID,evidenceCID,studentSignature]`

### ProfessorAgent.rnw

Places: p_Idle, p_EvidenceReceived, p_UnderReview, p_Evaluated, p_Approved, p_Rejected.

Transitions and their inscriptions:

- `t_ReceiveEvidence`: `:submit(u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate)`
- `t_StartReview`: `(none)`
- `t_AssessEvidence`: `e:evaluate(assessorDID,grade,criteriaURL)`
- `t_ApproveEvidence`: `guard grade >= passing_threshold; u:submit(c,w,l,h,grade,passing_threshold,currentTime,expiryDate)`
- `t_RejectEvidence`: `guard grade < passing_threshold`

Arcs:

- `p_Idle -> t_ReceiveEvidence`: `[assessorDID,criteriaURL]`
- `t_ReceiveEvidence -> p_EvidenceReceived`: `[u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate,assessorDID,criteriaURL]`
- `p_EvidenceReceived -> t_StartReview`: `[u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate,assessorDID,criteriaURL]`
- `t_StartReview -> p_UnderReview`: `[u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate,assessorDID,criteriaURL]`
- `p_UnderReview -> t_AssessEvidence`: `[u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate,assessorDID,criteriaURL]`
- `t_AssessEvidence -> p_Evaluated`: `[u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate,assessorDID,criteriaURL]`
- `p_Evaluated -> t_ApproveEvidence`: `[u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate,assessorDID,criteriaURL]`
- `t_ApproveEvidence -> p_Approved`: `[u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate,assessorDID,criteriaURL]`
- `p_Evaluated -> t_RejectEvidence`: `[u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate,assessorDID,criteriaURL]`
- `t_RejectEvidence -> p_Rejected`: `[u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate,assessorDID,criteriaURL]`

### UniversityAgent.rnw

Places: p_Idle, p_EvidenceReceived, p_Verifying, p_CredentialRequested, p_CredentialIssued, p_Rejected.

Transitions and their inscriptions:

- `t_ReceiveEvidence`: `:submit(c,w,l,h,grade,passing_threshold,currentTime,expiryDate)`
- `t_VerifyEvidence`: `(none)`
- `t_RequestCredential`: `guard grade >= passing_threshold; c:submit(w,l,h,grade,passing_threshold,currentTime,expiryDate)`
- `t_IssueCredential`: `c:sbt_mint(issuerDID,studentDID,credentialData)`
- `t_RejectEvidence`: `guard grade < passing_threshold`

Arcs:

- `p_Idle -> t_ReceiveEvidence`: `[issuerDID,studentDID,credentialData]`
- `t_ReceiveEvidence -> p_EvidenceReceived`: `[c,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData]`
- `p_EvidenceReceived -> t_VerifyEvidence`: `[c,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData]`
- `t_VerifyEvidence -> p_Verifying`: `[c,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData]`
- `p_Verifying -> t_RequestCredential`: `[c,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData]`
- `t_RequestCredential -> p_CredentialRequested`: `[c,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData]`
- `p_Verifying -> t_RejectEvidence`: `[c,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData]`
- `t_RejectEvidence -> p_Rejected`: `[c,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData]`
- `p_CredentialRequested -> t_IssueCredential`: `[c,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData]`
- `t_IssueCredential -> p_CredentialIssued`: `[c,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData]`

### EvidenceNet.rnw

Places: p_Created, p_UploadedIPFS, p_SubmittedLMS, p_UnderReview, p_Graded, p_Rejected.

Transitions and their inscriptions:

- `t_IPFS_Upload`: `:submit(studentDID,evidenceCID); eo :new EvidenceObject; eo:submit(studentDID,evidenceCID)`
- `t_LMS_Submit`: `eo:lms()`
- `t_Review_Request`: `(none)`
- `t_Grade_Assign`: `:evaluate(assessorDID,grade,criteriaURL); guard grade >= passing_threshold; eo:evaluate(assessorDID,grade,criteriaURL)`
- `t_Reject`: `:evaluate(assessorDID,grade,criteriaURL); guard grade < passing_threshold`

Arcs:

- `p_Created -> t_IPFS_Upload`: `passing_threshold`
- `t_IPFS_Upload -> p_UploadedIPFS`: `[eo,studentDID,evidenceCID,passing_threshold]`
- `p_UploadedIPFS -> t_LMS_Submit`: `[eo,studentDID,evidenceCID,passing_threshold]`
- `t_LMS_Submit -> p_SubmittedLMS`: `[eo,studentDID,evidenceCID,passing_threshold]`
- `p_SubmittedLMS -> t_Review_Request`: `[eo,studentDID,evidenceCID,passing_threshold]`
- `t_Review_Request -> p_UnderReview`: `[eo,studentDID,evidenceCID,passing_threshold]`
- `p_UnderReview -> t_Grade_Assign`: `[eo,studentDID,evidenceCID,passing_threshold]`
- `t_Grade_Assign -> p_Graded`: `[eo,studentDID,evidenceCID,passing_threshold,assessorDID,grade,criteriaURL]`
- `p_UnderReview -> t_Reject`: `[eo,studentDID,evidenceCID,passing_threshold]`
- `t_Reject -> p_Rejected`: `[eo,studentDID,evidenceCID,passing_threshold,assessorDID,grade,criteriaURL]`

### CompetencyNet.rnw

Places: p_Submitted, p_UnderReview, p_Validated, p_PendingAccept, p_BlockchainAnchored, p_ProfileAggregated, p_Terminated.

Transitions and their inscriptions:

- `t_Review_Init`: `:submit(w,l,h,grade,passing_threshold,currentTime,expiryDate); co :new CredentialObject; co:evaluate(grade,passing_threshold,currentTime,expiryDate)`
- `t_Verification_Pass`: `guard grade >= passing_threshold; co:validate()`
- `t_SBT_Mint`: `:sbt_mint(issuerDID,studentDID,credentialData); co:sbt_mint(issuerDID,studentDID,credentialData); w:sbt_mint(this,issuerDID,studentDID,credentialData)`
- `t_SBT_Accept`: `:sbt_accept(studentSignature); guard currentTime < expiryDate; co:sbt_accept(studentSignature); l:anchor_request()`
- `t_SBT_Reject`: `:sbt_reject(studentSignature); co:sbt_reject(studentSignature)`
- `t_SBT_Cancel`: `co:sbt_cancel(issuerSignature)`
- `t_SBT_Expire`: `guard currentTime >= expiryDate; co:expire()`
- `t_Agg_Ingest`: `co:update_profile(holderDID,vpData); l:update_profile(holderDID,vpData); h:update_profile(holderDID,vpData)`

Arcs:

- `p_Submitted -> t_Review_Init`: `[issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]`
- `t_Review_Init -> p_UnderReview`: `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]`
- `p_UnderReview -> t_Verification_Pass`: `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]`
- `t_Verification_Pass -> p_Validated`: `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]`
- `p_Validated -> t_SBT_Mint`: `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]`
- `t_SBT_Mint -> p_PendingAccept`: `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]`
- `p_PendingAccept -> t_SBT_Accept`: `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]`
- `t_SBT_Accept -> p_BlockchainAnchored`: `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]`
- `p_PendingAccept -> t_SBT_Reject`: `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]`
- `t_SBT_Reject -> p_Terminated`: `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]`
- `p_PendingAccept -> t_SBT_Cancel`: `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]`
- `t_SBT_Cancel -> p_Terminated`: `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]`
- `p_PendingAccept -> t_SBT_Expire`: `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]`
- `t_SBT_Expire -> p_Terminated`: `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]`
- `p_BlockchainAnchored -> t_Agg_Ingest`: `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]`
- `t_Agg_Ingest -> p_ProfileAggregated`: `[co,w,l,h,grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,issuerSignature,holderDID,vpData]`

### WalletNet.rnw

Places: p_Empty, p_CredentialReceived, p_PendingConsent, p_Accepted, p_Rejected, p_VPReady, p_VPShared.

Transitions and their inscriptions:

- `t_ReceiveCredential`: `:sbt_mint(c,issuerDID,studentDID,credentialData)`
- `t_RequestConsent`: `:consent()`
- `t_AcceptCredential`: `:sbt_accept(studentSignature); c:sbt_accept(studentSignature)`
- `t_RejectCredential`: `:sbt_reject(studentSignature); c:sbt_reject(studentSignature)`
- `t_GenerateVP`: `(none)`
- `t_ShareVP`: `(none)`

Arcs:

- `p_Empty -> t_ReceiveCredential`: `[]`
- `t_ReceiveCredential -> p_CredentialReceived`: `[c,issuerDID,studentDID,credentialData]`
- `p_CredentialReceived -> t_RequestConsent`: `[c,issuerDID,studentDID,credentialData]`
- `t_RequestConsent -> p_PendingConsent`: `[c,issuerDID,studentDID,credentialData]`
- `p_PendingConsent -> t_AcceptCredential`: `[c,issuerDID,studentDID,credentialData]`
- `t_AcceptCredential -> p_Accepted`: `[c,issuerDID,studentDID,credentialData]`
- `p_PendingConsent -> t_RejectCredential`: `[c,issuerDID,studentDID,credentialData]`
- `t_RejectCredential -> p_Rejected`: `[c,issuerDID,studentDID,credentialData]`
- `p_Accepted -> t_GenerateVP`: `[c,issuerDID,studentDID,credentialData]`
- `t_GenerateVP -> p_VPReady`: `[c,issuerDID,studentDID,credentialData]`
- `p_VPReady -> t_ShareVP`: `[c,issuerDID,studentDID,credentialData]`
- `t_ShareVP -> p_VPShared`: `[c,issuerDID,studentDID,credentialData]`

### HEDULedgerNet.rnw

Places: p_Received, p_Executed, p_Ordered, p_Validated, p_Committed, p_Anchored, p_Rejected.

Transitions and their inscriptions:

- `t_Execute`: `:anchor_request()`
- `t_Order`: `(none)`
- `t_Validate`: `(none)`
- `t_Commit`: `(none)`
- `t_Anchor`: `:update_profile(holderDID,vpData)`
- `t_Reject`: `(none)`

Arcs:

- `p_Received -> t_Execute`: `[]`
- `t_Execute -> p_Executed`: `[]`
- `p_Executed -> t_Order`: `[]`
- `t_Order -> p_Ordered`: `[]`
- `p_Ordered -> t_Validate`: `[]`
- `t_Validate -> p_Validated`: `[]`
- `p_Ordered -> t_Reject`: `[]`
- `t_Reject -> p_Rejected`: `[]`
- `p_Validated -> t_Commit`: `[]`
- `t_Commit -> p_Committed`: `[]`
- `p_Committed -> t_Anchor`: `[]`
- `t_Anchor -> p_Anchored`: `[holderDID,vpData]`

### HRAgent.rnw

Places: p_Idle, p_VPReceived, p_Parsed, p_CompetencyProfile, p_Matching, p_GapAnalysis, p_ResultReady.

Transitions and their inscriptions:

- `t_ReceiveVP`: `:submit()`
- `t_ParseVP`: `(none)`
- `t_BuildProfile`: `:update_profile(holderDID,vpData)`
- `t_SemanticMatch`: `(none)`
- `t_GapAnalysis`: `(none)`
- `t_GenerateResult`: `(none)`

Arcs:

- `p_Idle -> t_ReceiveVP`: `[]`
- `t_ReceiveVP -> p_VPReceived`: `[]`
- `p_VPReceived -> t_ParseVP`: `[]`
- `t_ParseVP -> p_Parsed`: `[]`
- `p_Parsed -> t_BuildProfile`: `[]`
- `t_BuildProfile -> p_CompetencyProfile`: `[holderDID,vpData]`
- `p_CompetencyProfile -> t_SemanticMatch`: `[holderDID,vpData]`
- `t_SemanticMatch -> p_Matching`: `[holderDID,vpData]`
- `p_Matching -> t_GapAnalysis`: `[holderDID,vpData]`
- `t_GapAnalysis -> p_GapAnalysis`: `[holderDID,vpData]`
- `p_GapAnalysis -> t_GenerateResult`: `[holderDID,vpData]`
- `t_GenerateResult -> p_ResultReady`: `[holderDID,vpData]`

### EvidenceObject.rnw

Places: p_Created, p_UploadedIPFS, p_SubmittedLMS, p_Graded.

Transitions and their inscriptions:

- `t_IPFS_Upload`: `:submit(studentDID,evidenceCID)`
- `t_LMS_Submit`: `:lms()`
- `t_Grade_Assign`: `:evaluate(assessorDID,grade,criteriaURL)`

Arcs:

- `p_Created -> t_IPFS_Upload`: `[]`
- `t_IPFS_Upload -> p_UploadedIPFS`: `[studentDID,evidenceCID]`
- `p_UploadedIPFS -> t_LMS_Submit`: `[studentDID,evidenceCID]`
- `t_LMS_Submit -> p_SubmittedLMS`: `[studentDID,evidenceCID]`
- `p_SubmittedLMS -> t_Grade_Assign`: `[studentDID,evidenceCID]`
- `t_Grade_Assign -> p_Graded`: `[studentDID,evidenceCID,assessorDID,grade,criteriaURL]`

### CredentialObject.rnw

Places: p_Submitted, p_UnderReview, p_Validated, p_PendingAccept, p_Anchored, p_Rejected, p_Cancelled, p_Expired, p_Aggregated.

Transitions and their inscriptions:

- `t_StartReview`: `:evaluate(grade,passing_threshold,currentTime,expiryDate)`
- `t_Validate`: `:validate(); guard grade >= passing_threshold`
- `t_Mint`: `:sbt_mint(issuerDID,studentDID,credentialData)`
- `t_Accept`: `:sbt_accept(studentSignature); guard currentTime < expiryDate`
- `t_Reject`: `:sbt_reject(studentSignature)`
- `t_Cancel`: `:sbt_cancel(issuerSignature)`
- `t_Expire`: `:expire(); guard currentTime >= expiryDate`
- `t_Aggregate`: `:update_profile(holderDID,vpData)`

Arcs:

- `p_Submitted -> t_StartReview`: `[issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]`
- `t_StartReview -> p_UnderReview`: `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]`
- `p_UnderReview -> t_Validate`: `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]`
- `t_Validate -> p_Validated`: `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]`
- `p_Validated -> t_Mint`: `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]`
- `t_Mint -> p_PendingAccept`: `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]`
- `p_PendingAccept -> t_Accept`: `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]`
- `t_Accept -> p_Anchored`: `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]`
- `p_PendingAccept -> t_Reject`: `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]`
- `t_Reject -> p_Rejected`: `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]`
- `p_PendingAccept -> t_Cancel`: `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]`
- `t_Cancel -> p_Cancelled`: `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]`
- `p_PendingAccept -> t_Expire`: `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]`
- `t_Expire -> p_Expired`: `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]`
- `p_Anchored -> t_Aggregate`: `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]`
- `t_Aggregate -> p_Aggregated`: `[grade,passing_threshold,currentTime,expiryDate,issuerDID,studentDID,credentialData,studentSignature,issuerSignature,holderDID,vpData]`

## Java helpers

No Java model/helper class is required by any inscription. tools/BuildProject.java is read-only test tooling using Renew’s own reader/compiler/simulator APIs. Its regeneration path was removed. No domain helper methods were added.

## Known limitations

- Only one fixture workflow is instantiated per SystemNet root. All identifiers, credentials, signatures and VP values are simulation placeholders.
- No IPFS/LMS/blockchain calls, hashing, signatures, real SBTs, persistent wallet, semantic matching, or computed gap analysis are implemented. Successful termination is a control-flow result.
- currentTime and expiryDate are integers; currentTime does not advance. The expiry scenario changes currentTime to expiryDate only in memory before compilation. Equality prevents acceptance and enables expiration.
- EvidenceNet has its own passing_threshold marking (50), matching the default StudentAgent threshold. If changing grading policy, edit both initial markings consistently; this is an explicit local policy value, not a hidden global.
- Issuer/student/credential/expected-signature/profile fixture values are stored in the role/object initial markings listed below. Edit matching fixture values consistently; mismatches can intentionally disable synchronization.
- Evidence rejection leaves StudentAgent waiting for a credential and EvidenceObject at p_SubmittedLMS, because those graphs lack rejection notification/terminal steps for that event.
- Cancellation and expiry terminate CompetencyNet and CredentialObject; StudentAgent and WalletNet retain pending states. Their graphs have no corresponding cancellation/expiry notification transitions.
- UniversityAgent evidence rejection is still structurally present but unreachable after professor approval of the unchanged grade in the base fixture. Ledger rejection prevents the later synchronized aggregation.
- Automatic cancellation and other alternatives compete with acceptance; a successful directed scenario is not a claim that every automatic run succeeds.
- p_BlockchainAnchored / p_Anchored in competency/object nets are abstract lifecycle labels reached at acceptance. Actual ledger workflow anchoring is synchronized with profile aggregation after ledger commit.
- The VP string originates in CompetencyNet initial data. Student/Wallet VP-generation transitions remain abstract control events, not a generation algorithm. Student-to-HR submit/0 still gates when HR starts; update_profile/2 carries the actual fixture profile payload.
- Only Renew 4.1 / Java 17 was executed. No exhaustive state exploration, soundness/liveness proof, fairness proof, security proof, or performance experiment was performed.

## Experimental metrics

| Metric | Status |
|---|---|
| Reachable states | NOT MEASURED YET |
| State-space transitions | NOT MEASURED YET |
| Deadlock counts | NOT MEASURED YET |
| Boundedness proof | NOT MEASURED YET |
| Throughput | NOT MEASURED YET |
| Latency | NOT MEASURED YET |
| TPS | NOT MEASURED YET |
| 500 concurrent agents | NOT MEASURED YET |
| Race-condition results | NOT MEASURED YET |
| Poisson workloads | NOT MEASURED YET |
| MMPP workloads | NOT MEASURED YET |

## Saved RNW SHA-256

| Drawing | SHA-256 |
|---|---|
| SystemNet.rnw | `04bc84a59bff43a5ec27947f14635163f472e1e28d433d0a3ce4c9382a7553c3` |
| StudentAgent.rnw | `133532724e32604450742a28f3ae8d1b6c5b327de8df0d06d201ab8109eaf3a5` |
| ProfessorAgent.rnw | `df197490580639c655631b3d299513ed1bcab92e46a82e5788a5aa0b647fbae5` |
| UniversityAgent.rnw | `a3b0acd2e1239ea339c100816c46378c236afe2ce89ad0559a86dce7d98ef724` |
| EvidenceNet.rnw | `9870d408906cbf2ec3f8587ce406423bd30926efe5b3d72ef782e0581794734a` |
| CompetencyNet.rnw | `a5eb0638e67693ca36d09ccfc1ff62dad4a152f613b63f04dec154c6f849c42f` |
| WalletNet.rnw | `4ee79412f4d8d59cbf1c89fa23ae6d18565206a6e2bebc4ec53b8ef3347dd5cb` |
| HEDULedgerNet.rnw | `d64298af1fb12da370d1fc7946cde44333dca5c8f01d4d1b18712f2e5049c110` |
| HRAgent.rnw | `1e68fa0ad1ac2851276cdd79ff8038bc31f01f8b15a5c872eeec26b7f9a72fab` |
| EvidenceObject.rnw | `80d7e066372fbc8ebec486a486082362f7e13abe99c5c9dc47f4c5997bbba08a` |
| CredentialObject.rnw | `c5b15e6fc8fce1c88eddb30854b0f7fafa7886cc352462df4e06f585a54fd444` |

Reference: [official Renew 4.1 manual](https://www.informatik.uni-hamburg.de/TGI/renew/4.1/renew4.1.pdf). Compiler and channel claims above are supported by the installed runtime and saved native execution output.
