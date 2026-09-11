# Regression report

Compared with baseline `4c98b05507d7e92ea7223c9e4bf000371297c75e` on branch `feat/renew-functional-integration`.

- Topology changed? **NO**.
- Places added: **0**; removed: **0**.
- Transitions added: **0**; removed: **0**.
- Arcs added: **0**; removed: **0**.
- Graphical/text figures added or removed: **0**.
- All non-text serialized content is unchanged (line-ending normalization only for comparison). Coordinates, layout, fonts, colors, IDs, connectors, and REF associations are preserved. Longer inscriptions can occupy more screen width; no layout movement was performed.
- Initial token count added: **0**. Existing initial token values were replaced where needed to bind primitive fixture data.
- Validation: **PASS**. Renew compilation: **PASS**. Seven directed scenario checks: **PASS**.

## Files modified

Ten RNW drawings have inscription-only replacements; SystemNet.rnw is unchanged. model.tsv and model_manifest.json reflect those exact replacements. README.md and MODEL_VALIDATION.md describe the integrated state. Validate.ps1 now runs optional read-only scenarios and saves native output. tools/BuildProject.java no longer regenerates drawings and checks all inscriptions. Launch.ps1 opens SystemNet last and supplies template filenames relative to the project directory to avoid Renew’s Windows path-escape parsing.

New files: FUNCTIONAL_INTEGRATION_AUDIT.md, CHANNEL_MATRIX.md, REGRESSION_REPORT.md, VALIDATION_EVIDENCE.txt. The existing .gitignore was independently changed during this task; that change was preserved and is not attributed to this integration.

## Inscription changes

| Drawing | Existing text values replaced |
|---|---|
| SystemNet.rnw | 0 |
| StudentAgent.rnw | 20 |
| ProfessorAgent.rnw | 12 |
| UniversityAgent.rnw | 12 |
| EvidenceNet.rnw | 14 |
| CompetencyNet.rnw | 22 |
| WalletNet.rnw | 14 |
| HEDULedgerNet.rnw | 2 |
| HRAgent.rnw | 8 |
| EvidenceObject.rnw | 7 |
| CredentialObject.rnw | 22 |

No text figure was added or removed. Each replacement removes its old inscription value and inserts the new value in the same serialized slot. The exact transition replacements are listed below; model.tsv diff covers every arc/marking replacement.

| Net.transition | Before | After |
|---|---|---|
| StudentAgent.t_SubmitEvidence | `p:submit(u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate); e:submit(grade,passing_threshold)` | `p:submit(u,c,w,l,h,e,grade,passing_threshold,currentTime,expiryDate); e:submit(studentDID,evidenceCID)` |
| StudentAgent.t_AcceptCredential | `w:sbt_accept()` | `w:sbt_accept(studentSignature)` |
| StudentAgent.t_RejectCredential | `w:sbt_reject()` | `w:sbt_reject(studentSignature)` |
| ProfessorAgent.t_AssessEvidence | `e:evaluate()` | `e:evaluate(assessorDID,grade,criteriaURL)` |
| UniversityAgent.t_IssueCredential | `c:sbt_mint()` | `c:sbt_mint(issuerDID,studentDID,credentialData)` |
| EvidenceNet.t_IPFS_Upload | `:submit(grade,passing_threshold); eo :new EvidenceObject; eo:submit()` | `:submit(studentDID,evidenceCID); eo :new EvidenceObject; eo:submit(studentDID,evidenceCID)` |
| EvidenceNet.t_Grade_Assign | `:evaluate(); guard grade >= passing_threshold; eo:evaluate()` | `:evaluate(assessorDID,grade,criteriaURL); guard grade >= passing_threshold; eo:evaluate(assessorDID,grade,criteriaURL)` |
| EvidenceNet.t_Reject | `:evaluate(); guard grade < passing_threshold` | `:evaluate(assessorDID,grade,criteriaURL); guard grade < passing_threshold` |
| CompetencyNet.t_SBT_Mint | `:sbt_mint(); co:sbt_mint(); w:sbt_mint(this)` | `:sbt_mint(issuerDID,studentDID,credentialData); co:sbt_mint(issuerDID,studentDID,credentialData); w:sbt_mint(this,issuerDID,studentDID,credentialData)` |
| CompetencyNet.t_SBT_Accept | `:sbt_accept(); guard currentTime < expiryDate; co:sbt_accept(); l:anchor_request()` | `:sbt_accept(studentSignature); guard currentTime < expiryDate; co:sbt_accept(studentSignature); l:anchor_request()` |
| CompetencyNet.t_SBT_Reject | `:sbt_reject(); co:sbt_reject()` | `:sbt_reject(studentSignature); co:sbt_reject(studentSignature)` |
| CompetencyNet.t_SBT_Cancel | `co:sbt_cancel()` | `co:sbt_cancel(issuerSignature)` |
| CompetencyNet.t_Agg_Ingest | `co:update_profile(); l:update_profile(); h:update_profile()` | `co:update_profile(holderDID,vpData); l:update_profile(holderDID,vpData); h:update_profile(holderDID,vpData)` |
| WalletNet.t_ReceiveCredential | `:sbt_mint(c)` | `:sbt_mint(c,issuerDID,studentDID,credentialData)` |
| WalletNet.t_AcceptCredential | `:sbt_accept(); c:sbt_accept()` | `:sbt_accept(studentSignature); c:sbt_accept(studentSignature)` |
| WalletNet.t_RejectCredential | `:sbt_reject(); c:sbt_reject()` | `:sbt_reject(studentSignature); c:sbt_reject(studentSignature)` |
| HEDULedgerNet.t_Anchor | `:update_profile()` | `:update_profile(holderDID,vpData)` |
| HRAgent.t_BuildProfile | `:update_profile()` | `:update_profile(holderDID,vpData)` |
| EvidenceObject.t_IPFS_Upload | `:submit()` | `:submit(studentDID,evidenceCID)` |
| EvidenceObject.t_Grade_Assign | `:evaluate()` | `:evaluate(assessorDID,grade,criteriaURL)` |
| CredentialObject.t_Mint | `:sbt_mint()` | `:sbt_mint(issuerDID,studentDID,credentialData)` |
| CredentialObject.t_Accept | `:sbt_accept(); guard currentTime < expiryDate` | `:sbt_accept(studentSignature); guard currentTime < expiryDate` |
| CredentialObject.t_Reject | `:sbt_reject()` | `:sbt_reject(studentSignature)` |
| CredentialObject.t_Cancel | `:sbt_cancel()` | `:sbt_cancel(issuerSignature)` |
| CredentialObject.t_Aggregate | `:update_profile()` | `:update_profile(holderDID,vpData)` |

## Initial value changes

| Net.place | Before | After |
|---|---|---|
| StudentAgent.p_Idle | `[80,50,0,100]` | `[80,50,0,100,"did:example:student1","bafyEvidence001","student-signature"]` |
| ProfessorAgent.p_Idle | `[]` | `["did:example:assessor1","https://example.org/criteria"]` |
| UniversityAgent.p_Idle | `[]` | `["did:example:issuer1","did:example:student1","credential-001"]` |
| EvidenceNet.p_Created | `[]` | `50` |
| CompetencyNet.p_Submitted | `[]` | `["did:example:issuer1","did:example:student1","credential-001","issuer-signature","did:example:student1","vp-001"]` |
| CredentialObject.p_Submitted | `[]` | `["did:example:issuer1","did:example:student1","credential-001","student-signature","issuer-signature","did:example:student1","vp-001"]` |

## Executed checks

Baseline: happy, reject, cancel, expire, grade_reject and ledger_reject were replayed before model changes with identical RNW hashes before/after. Integrated: those six plus bad_signature passed through Validate.ps1 -Smoke. Smoke arguments change scenario inputs only in memory; all template files are read-only during validation. Final tokens demonstrate DID/CID/assessor/grade/criteria in EvidenceObject, issuer/student/credential data in WalletNet, and holderDID/vpData in ledger and HR.

Full native evidence: VALIDATION_EVIDENCE.txt. Launch.ps1 was executed and all eleven integrated drawings appeared in Renew's Windows menu. The displayed WalletNet showed the new sbt_mint reference/payload inscription. The GUI-compatible native loader also opened every drawing during final validation. The most recent validation.log records the final compile-only check; the seven scenario traces remain preserved in VALIDATION_EVIDENCE.txt.

Whitespace check: Markdown, PowerShell, Java and JSON changes pass git diff --check. RNW changed lines retain the baseline serializer's trailing whitespace intentionally; it was not stripped because non-text serialization is frozen.

## Remaining boundaries and approvals

No blocker requiring structural approval for the scoped base workflow or local alternatives. Global completion after evidence rejection, cancellation, expiry, or ledger rejection remains outside the frozen graphs, as detailed in MODEL_VALIDATION.md. No structural changes were made. No push or merge was performed; main remains at the baseline commit.

## Unmeasured metrics

- Reachable states: **NOT MEASURED YET**.
- State-space transitions: **NOT MEASURED YET**.
- Deadlock counts: **NOT MEASURED YET**.
- Boundedness proof: **NOT MEASURED YET**.
- Throughput: **NOT MEASURED YET**.
- Latency: **NOT MEASURED YET**.
- TPS: **NOT MEASURED YET**.
- 500 concurrent agents: **NOT MEASURED YET**.
- Race-condition results: **NOT MEASURED YET**.
- Poisson workloads: **NOT MEASURED YET**.
- MMPP workloads: **NOT MEASURED YET**.
