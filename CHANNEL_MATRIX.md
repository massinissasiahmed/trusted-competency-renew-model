# Channel matrix

All rows below resolve the target **template and live reference**, not merely a matching name somewhere in the repository. All listed pairs were exercised in the named directed scenario using the actual Renew engine. The two evaluate/3 receiver alternatives are tested separately. Positional argument equality is enforced by synchronous unification; variables already bound by input markings must match.

| Channel | Caller Net | Caller Transition | Downlink | Target Reference | Receiver Net | Receiver Transition | Uplink | Arguments | Status |
|---|---|---|---|---|---|---|---|---|---|
| createp/1 | SystemNet | t_CreateStudent | `this:createp(p)` | `this` | SystemNet | t_CreateProfessor | `:createp(p)` | `(p)` unifies with `(p)` | PASS — happy |
| createu/1 | SystemNet | t_CreateStudent | `this:createu(u)` | `this` | SystemNet | t_CreateUniversity | `:createu(u)` | `(u)` unifies with `(u)` | PASS — happy |
| createe/1 | SystemNet | t_CreateStudent | `this:createe(e)` | `this` | SystemNet | t_CreateEvidence | `:createe(e)` | `(e)` unifies with `(e)` | PASS — happy |
| createc/1 | SystemNet | t_CreateStudent | `this:createc(c)` | `this` | SystemNet | t_CreateCompetency | `:createc(c)` | `(c)` unifies with `(c)` | PASS — happy |
| createw/1 | SystemNet | t_CreateStudent | `this:createw(w)` | `this` | SystemNet | t_CreateWallet | `:createw(w)` | `(w)` unifies with `(w)` | PASS — happy |
| createl/1 | SystemNet | t_CreateStudent | `this:createl(l)` | `this` | SystemNet | t_CreateLedger | `:createl(l)` | `(l)` unifies with `(l)` | PASS — happy |
| createh/1 | SystemNet | t_CreateStudent | `this:createh(h)` | `this` | SystemNet | t_CreateHR | `:createh(h)` | `(h)` unifies with `(h)` | PASS — happy |
| setup/7 | SystemNet | t_CreateStudent | `s:setup(p,u,e,c,w,l,h)` | `s` | StudentAgent | t_PrepareEvidence | `:setup(p,u,e,c,w,l,h)` | `(p,u,e,c,w,l,h)` unifies with `(p,u,e,c,w,l,h)` | PASS — happy |
| submit/10 | StudentAgent | t_SubmitEvidence | `p:submit(u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate)` | `p` | ProfessorAgent | t_ReceiveEvidence | `:submit(u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate)` | `(u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate)` unifies with `(u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate)` | PASS — happy |
| submit/2 | StudentAgent | t_SubmitEvidence | `e:submit(studentDID,evidenceCID)` | `e` | EvidenceNet | t_IPFS_Upload | `:submit(studentDID,evidenceCID)` | `(studentDID,evidenceCID)` unifies with `(studentDID,evidenceCID)` | PASS — happy |
| consent/0 | StudentAgent | t_ReceiveCredential | `w:consent()` | `w` | WalletNet | t_RequestConsent | `:consent()` | `()` unifies with `()` | PASS — happy |
| sbt_accept/1 | StudentAgent | t_AcceptCredential | `w:sbt_accept(studentSignature)` | `w` | WalletNet | t_AcceptCredential | `:sbt_accept(studentSignature)` | `(studentSignature)` unifies with `(studentSignature)` | PASS — happy |
| sbt_reject/1 | StudentAgent | t_RejectCredential | `w:sbt_reject(studentSignature)` | `w` | WalletNet | t_RejectCredential | `:sbt_reject(studentSignature)` | `(studentSignature)` unifies with `(studentSignature)` | PASS — reject |
| submit/0 | StudentAgent | t_ShareVP | `h:submit()` | `h` | HRAgent | t_ReceiveVP | `:submit()` | `()` unifies with `()` | PASS — happy |
| evaluate/3 | ProfessorAgent | t_AssessEvidence | `e:evaluate(assessorDID,grade,criteriaURL)` | `e` | EvidenceNet | t_Grade_Assign | `:evaluate(assessorDID,grade,criteriaURL)` | `(assessorDID,grade,criteriaURL)` unifies with `(assessorDID,grade,criteriaURL)` | PASS — happy |
| evaluate/3 | ProfessorAgent | t_AssessEvidence | `e:evaluate(assessorDID,grade,criteriaURL)` | `e` | EvidenceNet | t_Reject | `:evaluate(assessorDID,grade,criteriaURL)` | `(assessorDID,grade,criteriaURL)` unifies with `(assessorDID,grade,criteriaURL)` | PASS — grade_reject |
| submit/8 | ProfessorAgent | t_ApproveEvidence | `u:submit(c,w,l,h,grade,passing_threshold,currentTime,expiryDate)` | `u` | UniversityAgent | t_ReceiveEvidence | `:submit(c,w,l,h,grade,passing_threshold,currentTime,expiryDate)` | `(c,w,l,h,grade,passing_threshold,currentTime,expiryDate)` unifies with `(c,w,l,h,grade,passing_threshold,currentTime,expiryDate)` | PASS — happy |
| submit/7 | UniversityAgent | t_RequestCredential | `c:submit(w,l,h,grade,passing_threshold,currentTime,expiryDate)` | `c` | CompetencyNet | t_Review_Init | `:submit(w,l,h,grade,passing_threshold,currentTime,expiryDate)` | `(w,l,h,grade,passing_threshold,currentTime,expiryDate)` unifies with `(w,l,h,grade,passing_threshold,currentTime,expiryDate)` | PASS — happy |
| sbt_mint/3 | UniversityAgent | t_IssueCredential | `c:sbt_mint(issuerDID,studentDID,credentialData)` | `c` | CompetencyNet | t_SBT_Mint | `:sbt_mint(issuerDID,studentDID,credentialData)` | `(issuerDID,studentDID,credentialData)` unifies with `(issuerDID,studentDID,credentialData)` | PASS — happy |
| submit/2 | EvidenceNet | t_IPFS_Upload | `eo:submit(studentDID,evidenceCID)` | `eo` | EvidenceObject | t_IPFS_Upload | `:submit(studentDID,evidenceCID)` | `(studentDID,evidenceCID)` unifies with `(studentDID,evidenceCID)` | PASS — happy |
| lms/0 | EvidenceNet | t_LMS_Submit | `eo:lms()` | `eo` | EvidenceObject | t_LMS_Submit | `:lms()` | `()` unifies with `()` | PASS — happy |
| evaluate/3 | EvidenceNet | t_Grade_Assign | `eo:evaluate(assessorDID,grade,criteriaURL)` | `eo` | EvidenceObject | t_Grade_Assign | `:evaluate(assessorDID,grade,criteriaURL)` | `(assessorDID,grade,criteriaURL)` unifies with `(assessorDID,grade,criteriaURL)` | PASS — happy |
| evaluate/4 | CompetencyNet | t_Review_Init | `co:evaluate(grade,passing_threshold,currentTime,expiryDate)` | `co` | CredentialObject | t_StartReview | `:evaluate(grade,passing_threshold,currentTime,expiryDate)` | `(grade,passing_threshold,currentTime,expiryDate)` unifies with `(grade,passing_threshold,currentTime,expiryDate)` | PASS — happy |
| validate/0 | CompetencyNet | t_Verification_Pass | `co:validate()` | `co` | CredentialObject | t_Validate | `:validate()` | `()` unifies with `()` | PASS — happy |
| sbt_mint/3 | CompetencyNet | t_SBT_Mint | `co:sbt_mint(issuerDID,studentDID,credentialData)` | `co` | CredentialObject | t_Mint | `:sbt_mint(issuerDID,studentDID,credentialData)` | `(issuerDID,studentDID,credentialData)` unifies with `(issuerDID,studentDID,credentialData)` | PASS — happy |
| sbt_mint/4 | CompetencyNet | t_SBT_Mint | `w:sbt_mint(this,issuerDID,studentDID,credentialData)` | `w` | WalletNet | t_ReceiveCredential | `:sbt_mint(c,issuerDID,studentDID,credentialData)` | `(this,issuerDID,studentDID,credentialData)` unifies with `(c,issuerDID,studentDID,credentialData)` | PASS — happy |
| sbt_accept/1 | CompetencyNet | t_SBT_Accept | `co:sbt_accept(studentSignature)` | `co` | CredentialObject | t_Accept | `:sbt_accept(studentSignature)` | `(studentSignature)` unifies with `(studentSignature)` | PASS — happy |
| anchor_request/0 | CompetencyNet | t_SBT_Accept | `l:anchor_request()` | `l` | HEDULedgerNet | t_Execute | `:anchor_request()` | `()` unifies with `()` | PASS — happy |
| sbt_reject/1 | CompetencyNet | t_SBT_Reject | `co:sbt_reject(studentSignature)` | `co` | CredentialObject | t_Reject | `:sbt_reject(studentSignature)` | `(studentSignature)` unifies with `(studentSignature)` | PASS — reject |
| sbt_cancel/1 | CompetencyNet | t_SBT_Cancel | `co:sbt_cancel(issuerSignature)` | `co` | CredentialObject | t_Cancel | `:sbt_cancel(issuerSignature)` | `(issuerSignature)` unifies with `(issuerSignature)` | PASS — cancel |
| expire/0 | CompetencyNet | t_SBT_Expire | `co:expire()` | `co` | CredentialObject | t_Expire | `:expire()` | `()` unifies with `()` | PASS — expire |
| update_profile/2 | CompetencyNet | t_Agg_Ingest | `co:update_profile(holderDID,vpData)` | `co` | CredentialObject | t_Aggregate | `:update_profile(holderDID,vpData)` | `(holderDID,vpData)` unifies with `(holderDID,vpData)` | PASS — happy |
| update_profile/2 | CompetencyNet | t_Agg_Ingest | `l:update_profile(holderDID,vpData)` | `l` | HEDULedgerNet | t_Anchor | `:update_profile(holderDID,vpData)` | `(holderDID,vpData)` unifies with `(holderDID,vpData)` | PASS — happy |
| update_profile/2 | CompetencyNet | t_Agg_Ingest | `h:update_profile(holderDID,vpData)` | `h` | HRAgent | t_BuildProfile | `:update_profile(holderDID,vpData)` | `(holderDID,vpData)` unifies with `(holderDID,vpData)` | PASS — happy |
| sbt_accept/1 | WalletNet | t_AcceptCredential | `c:sbt_accept(studentSignature)` | `c` | CompetencyNet | t_SBT_Accept | `:sbt_accept(studentSignature)` | `(studentSignature)` unifies with `(studentSignature)` | PASS — happy |
| sbt_reject/1 | WalletNet | t_RejectCredential | `c:sbt_reject(studentSignature)` | `c` | CompetencyNet | t_SBT_Reject | `:sbt_reject(studentSignature)` | `(studentSignature)` unifies with `(studentSignature)` | PASS — reject |

## Requested business signatures

- `submit(studentDID,evidenceCID)`: implemented with matching enabled caller/receiver pairs above.
- `evaluate(assessorDID,grade,criteriaURL)`: implemented with matching enabled caller/receiver pairs above.
- `sbt_mint(issuerDID,studentDID,credentialData)`: implemented with matching enabled caller/receiver pairs above.
- `sbt_accept(studentSignature)`: implemented with matching enabled caller/receiver pairs above.
- `sbt_reject(studentSignature)`: implemented with matching enabled caller/receiver pairs above.
- `sbt_cancel(issuerSignature)`: implemented with matching enabled caller/receiver pairs above.
- `update_profile(holderDID,vpData)`: implemented with matching enabled caller/receiver pairs above.

## Reference provenance

- **SystemNet:** this = root instance; all other targets are bound by :new during synchronized creation.
- **StudentAgent:** p,e,w,h are stored by t_PrepareEvidence from SystemNet setup, then bound by incoming tuple arcs.
- **ProfessorAgent:** u,e are received via submit/10 and stored/bound by incoming tuple arcs.
- **UniversityAgent:** c is received via submit/8 and stored/bound by incoming tuple arcs.
- **EvidenceNet:** eo is created on t_IPFS_Upload; subsequent arcs store and bind eo.
- **CompetencyNet:** co is created on t_Review_Init; w,l,h arrive through submit/7; subsequent incoming arcs bind all targets.
- **WalletNet:** c is the actual CompetencyNet this reference received via sbt_mint/4 and stored on incoming tuple arcs.

## Primitive argument provenance

- StudentAgent p_Idle supplies studentDID, evidenceCID and studentSignature along with the original grade/time fields. submit/2 propagates DID/CID through EvidenceNet to EvidenceObject.
- ProfessorAgent p_Idle supplies assessorDID and criteriaURL. The grade comes from StudentAgent through the existing submit/10 reference handoff. evaluate/3 transfers these into EvidenceNet and EvidenceObject.
- UniversityAgent p_Idle supplies issuerDID, studentDID, credentialData. CompetencyNet and CredentialObject hold matching expected fixture values; mint unifies them. WalletNet receives these values along with the real CompetencyNet reference.
- StudentSignature flows StudentAgent → WalletNet → CompetencyNet → CredentialObject on accept/reject. CredentialObject holds the expected placeholder string; bad_signature execution proves a different value prevents the synchronized acceptance.
- CompetencyNet holds issuerSignature and calls CredentialObject cancellation, where the expected placeholder must match. This models a cancellation decision at CompetencyNet; it is not an authenticated UniversityAgent action.
- CompetencyNet holds holderDID and vpData. update_profile/2 sends these to CredentialObject, HEDULedgerNet and HRAgent. The ledger and HR terminal markings retain the received tuple.

## Retained internal handoffs

The existing setup/create*, submit/10, submit/8, submit/7, submit/0, evaluate/4, consent/0, lms/0, validate/0, expire/0 and anchor_request/0 protocols remain. Wallet reference delivery now uses sbt_mint/4: (c,issuerDID,studentDID,credentialData). These are internal overloads, not replacements for the exact business signatures. No new channel **name** was introduced. Removing these handoffs would discard the baseline reference-routing or synchronization logic.

## Limits

A successful witness proves that the selected caller and receiver can become enabled together, not that all schedules reach them. No cryptographic signature validation, real credential issuance, external storage, or external profile computation is performed.
