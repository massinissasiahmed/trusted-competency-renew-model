# Post-repair final markings

Validated 2026-09-11 on `fix/failure-protocol-completion`, branched from `feat/renew-functional-integration` at `b9f19c2618904646d2dc712a2620eebeb5a4e907`. Renew 4.1, Java 17, Timed Java Compiler with early tokens, sequential engine.

**EXHAUSTIVE STATE-SPACE ANALYSIS NOT PERFORMED**

These are directed native-engine witnesses and endpoint binding checks. No deadlock-freedom, boundedness, general liveness, state-space coverage, formal verification, cryptographic security, or performance claim is made. Signatures and identifiers remain simulation strings; ledger anchoring is the modeled workflow event, not an external blockchain transaction. Logical `currentTime` is a fixed carried integer.

Each table is the **actual quiescent** system configuration, not an intermediate assertion cutoff. Every shown occupied place contains exactly one token; all other places in that instance are empty. SystemNet has one actual reference in each of its eight pools. “Not created” is distinct from an existing empty instance. Outcome tags in the token dump distinguish failure causes even where sink places are shared.

## happy

**EXPECTED_SUCCESS_TERMINATION**; outcome `SUCCESS`.

| Net | Final occupied place(s) | Status |
| --- | --- | --- |
| SystemNet.rnw | p_CompetencyPool; p_EvidencePool; p_HRPool; p_LedgerPool; p_ProfessorPool; p_StudentPool; p_UniversityPool; p_WalletPool | Reference repository |
| StudentAgent.rnw | p_VPShared | Activated and terminated |
| ProfessorAgent.rnw | p_Approved | Activated and terminated |
| UniversityAgent.rnw | p_CredentialIssued | Activated and terminated |
| EvidenceNet.rnw | p_Graded | Activated and terminated |
| CompetencyNet.rnw | p_ProfileAggregated | Activated and terminated |
| WalletNet.rnw | p_VPShared | Activated and terminated |
| HEDULedgerNet.rnw | p_Anchored | Activated and terminated |
| HRAgent.rnw | p_ResultReady | Activated and terminated |
| EvidenceObject.rnw | p_Graded | Activated and terminated |
| CredentialObject.rnw | p_Aggregated | Activated and terminated |

Enabled bindings: **0**. Active unfinished instances: **0**. Retained references: **11/11**. Uninvoked services: `[]`.

```text
CompetencyNet.p_ProfileAggregated = [[StudentAgent[2],CredentialObject[39],WalletNet[8],HEDULedgerNet[6],HRAgent[16],int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001,issuer-signature,did:example:student1,vp-001]]
CredentialObject.p_Aggregated = [[int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001,student-signature,issuer-signature,did:example:student1,vp-001]]
EvidenceNet.p_Graded = [[EvidenceObject[29],did:example:student1,bafyEvidence001,int(50),did:example:assessor1,int(80),https://example.org/criteria]]
EvidenceObject.p_Graded = [[did:example:student1,bafyEvidence001,did:example:assessor1,int(80),https://example.org/criteria]]
HEDULedgerNet.p_Anchored = [[CompetencyNet[12],ANCHORED]]
HRAgent.p_ResultReady = [[did:example:student1,vp-001]]
ProfessorAgent.p_Approved = [[StudentAgent[2],UniversityAgent[14],CompetencyNet[12],WalletNet[8],HEDULedgerNet[6],HRAgent[16],EvidenceNet[4],int(80),int(50),int(0),int(100),did:example:assessor1,https://example.org/criteria]]
StudentAgent.p_VPShared = [[ProfessorAgent[10],UniversityAgent[14],EvidenceNet[4],CompetencyNet[12],WalletNet[8],HEDULedgerNet[6],HRAgent[16],int(80),int(50),int(0),int(100),did:example:student1,bafyEvidence001,student-signature]]
SystemNet.p_CompetencyPool = [CompetencyNet[12]]
SystemNet.p_EvidencePool = [EvidenceNet[4]]
SystemNet.p_HRPool = [HRAgent[16]]
SystemNet.p_LedgerPool = [HEDULedgerNet[6]]
SystemNet.p_ProfessorPool = [ProfessorAgent[10]]
SystemNet.p_StudentPool = [StudentAgent[2]]
SystemNet.p_UniversityPool = [UniversityAgent[14]]
SystemNet.p_WalletPool = [WalletNet[8]]
UniversityAgent.p_CredentialIssued = [[StudentAgent[2],CompetencyNet[12],WalletNet[8],HEDULedgerNet[6],HRAgent[16],int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001]]
WalletNet.p_VPShared = [[CompetencyNet[12],did:example:issuer1,did:example:student1,credential-001]]
```

## reject

**EXPECTED_FAILURE_TERMINATION**; outcome `HOLDER_REJECTED`.

| Net | Final occupied place(s) | Status |
| --- | --- | --- |
| SystemNet.rnw | p_CompetencyPool; p_EvidencePool; p_HRPool; p_LedgerPool; p_ProfessorPool; p_StudentPool; p_UniversityPool; p_WalletPool | Reference repository |
| StudentAgent.rnw | p_Rejected | Activated and terminated |
| ProfessorAgent.rnw | p_Approved | Activated and terminated |
| UniversityAgent.rnw | p_CredentialIssued | Activated and terminated |
| EvidenceNet.rnw | p_Graded | Activated and terminated |
| CompetencyNet.rnw | p_Terminated | Activated and terminated |
| WalletNet.rnw | p_Rejected | Activated and terminated |
| HEDULedgerNet.rnw | p_Received | Uninvoked service |
| HRAgent.rnw | p_Idle | Uninvoked service |
| EvidenceObject.rnw | p_Graded | Activated and terminated |
| CredentialObject.rnw | p_Rejected | Activated and terminated |

Enabled bindings: **0**. Active unfinished instances: **0**. Retained references: **11/11**. Uninvoked services: `[HEDULedgerNet, HRAgent]`.

```text
CompetencyNet.p_Terminated = [[StudentAgent[2],CredentialObject[39],WalletNet[8],HEDULedgerNet[6],HRAgent[10],int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001,issuer-signature,did:example:student1,vp-001,HOLDER_REJECTED]]
CredentialObject.p_Rejected = [[int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001,student-signature,issuer-signature,did:example:student1,vp-001,HOLDER_REJECTED]]
EvidenceNet.p_Graded = [[EvidenceObject[29],did:example:student1,bafyEvidence001,int(50),did:example:assessor1,int(80),https://example.org/criteria]]
EvidenceObject.p_Graded = [[did:example:student1,bafyEvidence001,did:example:assessor1,int(80),https://example.org/criteria]]
HEDULedgerNet.p_Received = [[]]
HRAgent.p_Idle = [[]]
ProfessorAgent.p_Approved = [[StudentAgent[2],UniversityAgent[16],CompetencyNet[12],WalletNet[8],HEDULedgerNet[6],HRAgent[10],EvidenceNet[14],int(80),int(50),int(0),int(100),did:example:assessor1,https://example.org/criteria]]
StudentAgent.p_Rejected = [[ProfessorAgent[4],UniversityAgent[16],EvidenceNet[14],CompetencyNet[12],WalletNet[8],HEDULedgerNet[6],HRAgent[10],int(80),int(50),int(0),int(100),did:example:student1,bafyEvidence001,student-signature,HOLDER_REJECTED]]
SystemNet.p_CompetencyPool = [CompetencyNet[12]]
SystemNet.p_EvidencePool = [EvidenceNet[14]]
SystemNet.p_HRPool = [HRAgent[10]]
SystemNet.p_LedgerPool = [HEDULedgerNet[6]]
SystemNet.p_ProfessorPool = [ProfessorAgent[4]]
SystemNet.p_StudentPool = [StudentAgent[2]]
SystemNet.p_UniversityPool = [UniversityAgent[16]]
SystemNet.p_WalletPool = [WalletNet[8]]
UniversityAgent.p_CredentialIssued = [[StudentAgent[2],CompetencyNet[12],WalletNet[8],HEDULedgerNet[6],HRAgent[10],int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001]]
WalletNet.p_Rejected = [[CompetencyNet[12],did:example:issuer1,did:example:student1,credential-001,HOLDER_REJECTED]]
```

## cancel

**EXPECTED_FAILURE_TERMINATION**; outcome `CANCELLED`.

| Net | Final occupied place(s) | Status |
| --- | --- | --- |
| SystemNet.rnw | p_CompetencyPool; p_EvidencePool; p_HRPool; p_LedgerPool; p_ProfessorPool; p_StudentPool; p_UniversityPool; p_WalletPool | Reference repository |
| StudentAgent.rnw | p_Rejected | Activated and terminated |
| ProfessorAgent.rnw | p_Approved | Activated and terminated |
| UniversityAgent.rnw | p_CredentialIssued | Activated and terminated |
| EvidenceNet.rnw | p_Graded | Activated and terminated |
| CompetencyNet.rnw | p_Terminated | Activated and terminated |
| WalletNet.rnw | p_Rejected | Activated and terminated |
| HEDULedgerNet.rnw | p_Received | Uninvoked service |
| HRAgent.rnw | p_Idle | Uninvoked service |
| EvidenceObject.rnw | p_Graded | Activated and terminated |
| CredentialObject.rnw | p_Cancelled | Activated and terminated |

Enabled bindings: **0**. Active unfinished instances: **0**. Retained references: **11/11**. Uninvoked services: `[HEDULedgerNet, HRAgent]`.

```text
CompetencyNet.p_Terminated = [[StudentAgent[2],CredentialObject[39],WalletNet[8],HEDULedgerNet[6],HRAgent[10],int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001,issuer-signature,did:example:student1,vp-001,CANCELLED]]
CredentialObject.p_Cancelled = [[int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001,student-signature,issuer-signature,did:example:student1,vp-001,CANCELLED]]
EvidenceNet.p_Graded = [[EvidenceObject[29],did:example:student1,bafyEvidence001,int(50),did:example:assessor1,int(80),https://example.org/criteria]]
EvidenceObject.p_Graded = [[did:example:student1,bafyEvidence001,did:example:assessor1,int(80),https://example.org/criteria]]
HEDULedgerNet.p_Received = [[]]
HRAgent.p_Idle = [[]]
ProfessorAgent.p_Approved = [[StudentAgent[2],UniversityAgent[16],CompetencyNet[12],WalletNet[8],HEDULedgerNet[6],HRAgent[10],EvidenceNet[14],int(80),int(50),int(0),int(100),did:example:assessor1,https://example.org/criteria]]
StudentAgent.p_Rejected = [[ProfessorAgent[4],UniversityAgent[16],EvidenceNet[14],CompetencyNet[12],WalletNet[8],HEDULedgerNet[6],HRAgent[10],int(80),int(50),int(0),int(100),did:example:student1,bafyEvidence001,student-signature,CANCELLED]]
SystemNet.p_CompetencyPool = [CompetencyNet[12]]
SystemNet.p_EvidencePool = [EvidenceNet[14]]
SystemNet.p_HRPool = [HRAgent[10]]
SystemNet.p_LedgerPool = [HEDULedgerNet[6]]
SystemNet.p_ProfessorPool = [ProfessorAgent[4]]
SystemNet.p_StudentPool = [StudentAgent[2]]
SystemNet.p_UniversityPool = [UniversityAgent[16]]
SystemNet.p_WalletPool = [WalletNet[8]]
UniversityAgent.p_CredentialIssued = [[StudentAgent[2],CompetencyNet[12],WalletNet[8],HEDULedgerNet[6],HRAgent[10],int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001]]
WalletNet.p_Rejected = [[CompetencyNet[12],did:example:issuer1,did:example:student1,credential-001,CANCELLED]]
```

## expire

**EXPECTED_FAILURE_TERMINATION**; outcome `EXPIRED`.

| Net | Final occupied place(s) | Status |
| --- | --- | --- |
| SystemNet.rnw | p_CompetencyPool; p_EvidencePool; p_HRPool; p_LedgerPool; p_ProfessorPool; p_StudentPool; p_UniversityPool; p_WalletPool | Reference repository |
| StudentAgent.rnw | p_Rejected | Activated and terminated |
| ProfessorAgent.rnw | p_Approved | Activated and terminated |
| UniversityAgent.rnw | p_CredentialIssued | Activated and terminated |
| EvidenceNet.rnw | p_Graded | Activated and terminated |
| CompetencyNet.rnw | p_Terminated | Activated and terminated |
| WalletNet.rnw | p_Rejected | Activated and terminated |
| HEDULedgerNet.rnw | p_Received | Uninvoked service |
| HRAgent.rnw | p_Idle | Uninvoked service |
| EvidenceObject.rnw | p_Graded | Activated and terminated |
| CredentialObject.rnw | p_Expired | Activated and terminated |

Enabled bindings: **0**. Active unfinished instances: **0**. Retained references: **11/11**. Uninvoked services: `[HEDULedgerNet, HRAgent]`.

```text
CompetencyNet.p_Terminated = [[StudentAgent[2],CredentialObject[39],WalletNet[6],HEDULedgerNet[4],HRAgent[8],int(80),int(50),int(100),int(100),did:example:issuer1,did:example:student1,credential-001,issuer-signature,did:example:student1,vp-001,EXPIRED]]
CredentialObject.p_Expired = [[int(80),int(50),int(100),int(100),did:example:issuer1,did:example:student1,credential-001,student-signature,issuer-signature,did:example:student1,vp-001,EXPIRED]]
EvidenceNet.p_Graded = [[EvidenceObject[29],did:example:student1,bafyEvidence001,int(50),did:example:assessor1,int(80),https://example.org/criteria]]
EvidenceObject.p_Graded = [[did:example:student1,bafyEvidence001,did:example:assessor1,int(80),https://example.org/criteria]]
HEDULedgerNet.p_Received = [[]]
HRAgent.p_Idle = [[]]
ProfessorAgent.p_Approved = [[StudentAgent[2],UniversityAgent[14],CompetencyNet[16],WalletNet[6],HEDULedgerNet[4],HRAgent[8],EvidenceNet[10],int(80),int(50),int(100),int(100),did:example:assessor1,https://example.org/criteria]]
StudentAgent.p_Rejected = [[ProfessorAgent[12],UniversityAgent[14],EvidenceNet[10],CompetencyNet[16],WalletNet[6],HEDULedgerNet[4],HRAgent[8],int(80),int(50),int(100),int(100),did:example:student1,bafyEvidence001,student-signature,EXPIRED]]
SystemNet.p_CompetencyPool = [CompetencyNet[16]]
SystemNet.p_EvidencePool = [EvidenceNet[10]]
SystemNet.p_HRPool = [HRAgent[8]]
SystemNet.p_LedgerPool = [HEDULedgerNet[4]]
SystemNet.p_ProfessorPool = [ProfessorAgent[12]]
SystemNet.p_StudentPool = [StudentAgent[2]]
SystemNet.p_UniversityPool = [UniversityAgent[14]]
SystemNet.p_WalletPool = [WalletNet[6]]
UniversityAgent.p_CredentialIssued = [[StudentAgent[2],CompetencyNet[16],WalletNet[6],HEDULedgerNet[4],HRAgent[8],int(80),int(50),int(100),int(100),did:example:issuer1,did:example:student1,credential-001]]
WalletNet.p_Rejected = [[CompetencyNet[16],did:example:issuer1,did:example:student1,credential-001,EXPIRED]]
```

## grade_reject

**EXPECTED_FAILURE_TERMINATION**; outcome `EVIDENCE_REJECTED`.

| Net | Final occupied place(s) | Status |
| --- | --- | --- |
| SystemNet.rnw | p_CompetencyPool; p_EvidencePool; p_HRPool; p_LedgerPool; p_ProfessorPool; p_StudentPool; p_UniversityPool; p_WalletPool | Reference repository |
| StudentAgent.rnw | p_Rejected | Activated and terminated |
| ProfessorAgent.rnw | p_Rejected | Activated and terminated |
| UniversityAgent.rnw | p_Idle | Uninvoked service |
| EvidenceNet.rnw | p_Rejected | Activated and terminated |
| CompetencyNet.rnw | p_Submitted | Uninvoked service |
| WalletNet.rnw | p_Empty | Uninvoked service |
| HEDULedgerNet.rnw | p_Received | Uninvoked service |
| HRAgent.rnw | p_Idle | Uninvoked service |
| EvidenceObject.rnw | p_Graded | Activated and terminated |
| CredentialObject.rnw | NOT CREATED | Not created |

Enabled bindings: **0**. Active unfinished instances: **0**. Retained references: **10/10**. Uninvoked services: `[CompetencyNet, HEDULedgerNet, HRAgent, UniversityAgent, WalletNet]`.

```text
CompetencyNet.p_Submitted = [[did:example:issuer1,did:example:student1,credential-001,issuer-signature,did:example:student1,vp-001]]
EvidenceNet.p_Rejected = [[EvidenceObject[29],did:example:student1,bafyEvidence001,int(50),did:example:assessor1,int(40),https://example.org/criteria]]
EvidenceObject.p_Graded = [[did:example:student1,bafyEvidence001,did:example:assessor1,int(40),https://example.org/criteria]]
HEDULedgerNet.p_Received = [[]]
HRAgent.p_Idle = [[]]
ProfessorAgent.p_Rejected = [[StudentAgent[2],UniversityAgent[8],CompetencyNet[16],WalletNet[12],HEDULedgerNet[10],HRAgent[4],EvidenceNet[14],int(40),int(50),int(0),int(100),did:example:assessor1,https://example.org/criteria]]
StudentAgent.p_Rejected = [[ProfessorAgent[6],UniversityAgent[8],EvidenceNet[14],CompetencyNet[16],WalletNet[12],HEDULedgerNet[10],HRAgent[4],int(40),int(50),int(0),int(100),did:example:student1,bafyEvidence001,student-signature,EVIDENCE_REJECTED]]
SystemNet.p_CompetencyPool = [CompetencyNet[16]]
SystemNet.p_EvidencePool = [EvidenceNet[14]]
SystemNet.p_HRPool = [HRAgent[4]]
SystemNet.p_LedgerPool = [HEDULedgerNet[10]]
SystemNet.p_ProfessorPool = [ProfessorAgent[6]]
SystemNet.p_StudentPool = [StudentAgent[2]]
SystemNet.p_UniversityPool = [UniversityAgent[8]]
SystemNet.p_WalletPool = [WalletNet[12]]
UniversityAgent.p_Idle = [[did:example:issuer1,did:example:student1,credential-001]]
WalletNet.p_Empty = [[]]
```

## ledger_reject

**EXPECTED_FAILURE_TERMINATION**; outcome `LEDGER_REJECTED`.

| Net | Final occupied place(s) | Status |
| --- | --- | --- |
| SystemNet.rnw | p_CompetencyPool; p_EvidencePool; p_HRPool; p_LedgerPool; p_ProfessorPool; p_StudentPool; p_UniversityPool; p_WalletPool | Reference repository |
| StudentAgent.rnw | p_Rejected | Activated and terminated |
| ProfessorAgent.rnw | p_Approved | Activated and terminated |
| UniversityAgent.rnw | p_CredentialIssued | Activated and terminated |
| EvidenceNet.rnw | p_Graded | Activated and terminated |
| CompetencyNet.rnw | p_Terminated | Activated and terminated |
| WalletNet.rnw | p_Rejected | Activated and terminated |
| HEDULedgerNet.rnw | p_Rejected | Activated and terminated |
| HRAgent.rnw | p_Idle | Uninvoked service |
| EvidenceObject.rnw | p_Graded | Activated and terminated |
| CredentialObject.rnw | p_Rejected | Activated and terminated |

Enabled bindings: **0**. Active unfinished instances: **0**. Retained references: **11/11**. Uninvoked services: `[HRAgent]`.

```text
CompetencyNet.p_Terminated = [[StudentAgent[2],CredentialObject[39],WalletNet[8],HEDULedgerNet[6],HRAgent[10],int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001,issuer-signature,did:example:student1,vp-001,LEDGER_REJECTED]]
CredentialObject.p_Rejected = [[int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001,student-signature,issuer-signature,did:example:student1,vp-001,LEDGER_REJECTED]]
EvidenceNet.p_Graded = [[EvidenceObject[29],did:example:student1,bafyEvidence001,int(50),did:example:assessor1,int(80),https://example.org/criteria]]
EvidenceObject.p_Graded = [[did:example:student1,bafyEvidence001,did:example:assessor1,int(80),https://example.org/criteria]]
HEDULedgerNet.p_Rejected = [[CompetencyNet[12],LEDGER_REJECTED]]
HRAgent.p_Idle = [[]]
ProfessorAgent.p_Approved = [[StudentAgent[2],UniversityAgent[16],CompetencyNet[12],WalletNet[8],HEDULedgerNet[6],HRAgent[10],EvidenceNet[14],int(80),int(50),int(0),int(100),did:example:assessor1,https://example.org/criteria]]
StudentAgent.p_Rejected = [[ProfessorAgent[4],UniversityAgent[16],EvidenceNet[14],CompetencyNet[12],WalletNet[8],HEDULedgerNet[6],HRAgent[10],int(80),int(50),int(0),int(100),did:example:student1,bafyEvidence001,student-signature,LEDGER_REJECTED]]
SystemNet.p_CompetencyPool = [CompetencyNet[12]]
SystemNet.p_EvidencePool = [EvidenceNet[14]]
SystemNet.p_HRPool = [HRAgent[10]]
SystemNet.p_LedgerPool = [HEDULedgerNet[6]]
SystemNet.p_ProfessorPool = [ProfessorAgent[4]]
SystemNet.p_StudentPool = [StudentAgent[2]]
SystemNet.p_UniversityPool = [UniversityAgent[16]]
SystemNet.p_WalletPool = [WalletNet[8]]
UniversityAgent.p_CredentialIssued = [[StudentAgent[2],CompetencyNet[12],WalletNet[8],HEDULedgerNet[6],HRAgent[10],int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001]]
WalletNet.p_Rejected = [[CompetencyNet[12],did:example:issuer1,did:example:student1,credential-001,LEDGER_REJECTED]]
```

## bad_signature

**EXPECTED_FAILURE_TERMINATION**; outcome `INVALID_SIGNATURE`.

| Net | Final occupied place(s) | Status |
| --- | --- | --- |
| SystemNet.rnw | p_CompetencyPool; p_EvidencePool; p_HRPool; p_LedgerPool; p_ProfessorPool; p_StudentPool; p_UniversityPool; p_WalletPool | Reference repository |
| StudentAgent.rnw | p_Rejected | Activated and terminated |
| ProfessorAgent.rnw | p_Approved | Activated and terminated |
| UniversityAgent.rnw | p_CredentialIssued | Activated and terminated |
| EvidenceNet.rnw | p_Graded | Activated and terminated |
| CompetencyNet.rnw | p_Terminated | Activated and terminated |
| WalletNet.rnw | p_Rejected | Activated and terminated |
| HEDULedgerNet.rnw | p_Received | Uninvoked service |
| HRAgent.rnw | p_Idle | Uninvoked service |
| EvidenceObject.rnw | p_Graded | Activated and terminated |
| CredentialObject.rnw | p_Rejected | Activated and terminated |

Enabled bindings: **0**. Active unfinished instances: **0**. Retained references: **11/11**. Uninvoked services: `[HEDULedgerNet, HRAgent]`.

```text
CompetencyNet.p_Terminated = [[StudentAgent[2],CredentialObject[39],WalletNet[14],HEDULedgerNet[12],HRAgent[16],int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001,issuer-signature,did:example:student1,vp-001,INVALID_SIGNATURE]]
CredentialObject.p_Rejected = [[int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001,student-signature,issuer-signature,did:example:student1,vp-001,INVALID_SIGNATURE]]
EvidenceNet.p_Graded = [[EvidenceObject[29],did:example:student1,bafyEvidence001,int(50),did:example:assessor1,int(80),https://example.org/criteria]]
EvidenceObject.p_Graded = [[did:example:student1,bafyEvidence001,did:example:assessor1,int(80),https://example.org/criteria]]
HEDULedgerNet.p_Received = [[]]
HRAgent.p_Idle = [[]]
ProfessorAgent.p_Approved = [[StudentAgent[2],UniversityAgent[4],CompetencyNet[8],WalletNet[14],HEDULedgerNet[12],HRAgent[16],EvidenceNet[10],int(80),int(50),int(0),int(100),did:example:assessor1,https://example.org/criteria]]
StudentAgent.p_Rejected = [[ProfessorAgent[6],UniversityAgent[4],EvidenceNet[10],CompetencyNet[8],WalletNet[14],HEDULedgerNet[12],HRAgent[16],int(80),int(50),int(0),int(100),did:example:student1,bafyEvidence001,wrong-signature,INVALID_SIGNATURE]]
SystemNet.p_CompetencyPool = [CompetencyNet[8]]
SystemNet.p_EvidencePool = [EvidenceNet[10]]
SystemNet.p_HRPool = [HRAgent[16]]
SystemNet.p_LedgerPool = [HEDULedgerNet[12]]
SystemNet.p_ProfessorPool = [ProfessorAgent[6]]
SystemNet.p_StudentPool = [StudentAgent[2]]
SystemNet.p_UniversityPool = [UniversityAgent[4]]
SystemNet.p_WalletPool = [WalletNet[14]]
UniversityAgent.p_CredentialIssued = [[StudentAgent[2],CompetencyNet[8],WalletNet[14],HEDULedgerNet[12],HRAgent[16],int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001]]
WalletNet.p_Rejected = [[CompetencyNet[8],did:example:issuer1,did:example:student1,credential-001,INVALID_SIGNATURE]]
```

## bad_issuer_signature

**EXPECTED_FAILURE_TERMINATION**; outcome `INVALID_ISSUER_SIGNATURE`.

| Net | Final occupied place(s) | Status |
| --- | --- | --- |
| SystemNet.rnw | p_CompetencyPool; p_EvidencePool; p_HRPool; p_LedgerPool; p_ProfessorPool; p_StudentPool; p_UniversityPool; p_WalletPool | Reference repository |
| StudentAgent.rnw | p_Rejected | Activated and terminated |
| ProfessorAgent.rnw | p_Approved | Activated and terminated |
| UniversityAgent.rnw | p_CredentialIssued | Activated and terminated |
| EvidenceNet.rnw | p_Graded | Activated and terminated |
| CompetencyNet.rnw | p_Terminated | Activated and terminated |
| WalletNet.rnw | p_Rejected | Activated and terminated |
| HEDULedgerNet.rnw | p_Received | Uninvoked service |
| HRAgent.rnw | p_Idle | Uninvoked service |
| EvidenceObject.rnw | p_Graded | Activated and terminated |
| CredentialObject.rnw | p_Rejected | Activated and terminated |

Enabled bindings: **0**. Active unfinished instances: **0**. Retained references: **11/11**. Uninvoked services: `[HEDULedgerNet, HRAgent]`.

```text
CompetencyNet.p_Terminated = [[StudentAgent[2],CredentialObject[39],WalletNet[8],HEDULedgerNet[4],HRAgent[6],int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001,wrong-issuer-signature,did:example:student1,vp-001,INVALID_ISSUER_SIGNATURE]]
CredentialObject.p_Rejected = [[int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001,student-signature,issuer-signature,did:example:student1,vp-001,INVALID_ISSUER_SIGNATURE]]
EvidenceNet.p_Graded = [[EvidenceObject[29],did:example:student1,bafyEvidence001,int(50),did:example:assessor1,int(80),https://example.org/criteria]]
EvidenceObject.p_Graded = [[did:example:student1,bafyEvidence001,did:example:assessor1,int(80),https://example.org/criteria]]
HEDULedgerNet.p_Received = [[]]
HRAgent.p_Idle = [[]]
ProfessorAgent.p_Approved = [[StudentAgent[2],UniversityAgent[12],CompetencyNet[16],WalletNet[8],HEDULedgerNet[4],HRAgent[6],EvidenceNet[10],int(80),int(50),int(0),int(100),did:example:assessor1,https://example.org/criteria]]
StudentAgent.p_Rejected = [[ProfessorAgent[14],UniversityAgent[12],EvidenceNet[10],CompetencyNet[16],WalletNet[8],HEDULedgerNet[4],HRAgent[6],int(80),int(50),int(0),int(100),did:example:student1,bafyEvidence001,student-signature,INVALID_ISSUER_SIGNATURE]]
SystemNet.p_CompetencyPool = [CompetencyNet[16]]
SystemNet.p_EvidencePool = [EvidenceNet[10]]
SystemNet.p_HRPool = [HRAgent[6]]
SystemNet.p_LedgerPool = [HEDULedgerNet[4]]
SystemNet.p_ProfessorPool = [ProfessorAgent[14]]
SystemNet.p_StudentPool = [StudentAgent[2]]
SystemNet.p_UniversityPool = [UniversityAgent[12]]
SystemNet.p_WalletPool = [WalletNet[8]]
UniversityAgent.p_CredentialIssued = [[StudentAgent[2],CompetencyNet[16],WalletNet[8],HEDULedgerNet[4],HRAgent[6],int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001]]
WalletNet.p_Rejected = [[CompetencyNet[16],did:example:issuer1,did:example:student1,credential-001,INVALID_ISSUER_SIGNATURE]]
```

## grade_reject_waiting

**EXPECTED_FAILURE_TERMINATION**; outcome `EVIDENCE_REJECTED`.

| Net | Final occupied place(s) | Status |
| --- | --- | --- |
| SystemNet.rnw | p_CompetencyPool; p_EvidencePool; p_HRPool; p_LedgerPool; p_ProfessorPool; p_StudentPool; p_UniversityPool; p_WalletPool | Reference repository |
| StudentAgent.rnw | p_Rejected | Activated and terminated |
| ProfessorAgent.rnw | p_Rejected | Activated and terminated |
| UniversityAgent.rnw | p_Idle | Uninvoked service |
| EvidenceNet.rnw | p_Rejected | Activated and terminated |
| CompetencyNet.rnw | p_Submitted | Uninvoked service |
| WalletNet.rnw | p_Empty | Uninvoked service |
| HEDULedgerNet.rnw | p_Received | Uninvoked service |
| HRAgent.rnw | p_Idle | Uninvoked service |
| EvidenceObject.rnw | p_Graded | Activated and terminated |
| CredentialObject.rnw | NOT CREATED | Not created |

Enabled bindings: **0**. Active unfinished instances: **0**. Retained references: **10/10**. Uninvoked services: `[CompetencyNet, HEDULedgerNet, HRAgent, UniversityAgent, WalletNet]`.

```text
CompetencyNet.p_Submitted = [[did:example:issuer1,did:example:student1,credential-001,issuer-signature,did:example:student1,vp-001]]
EvidenceNet.p_Rejected = [[EvidenceObject[29],did:example:student1,bafyEvidence001,int(50),did:example:assessor1,int(40),https://example.org/criteria]]
EvidenceObject.p_Graded = [[did:example:student1,bafyEvidence001,did:example:assessor1,int(40),https://example.org/criteria]]
HEDULedgerNet.p_Received = [[]]
HRAgent.p_Idle = [[]]
ProfessorAgent.p_Rejected = [[StudentAgent[2],UniversityAgent[4],CompetencyNet[8],WalletNet[14],HEDULedgerNet[12],HRAgent[16],EvidenceNet[10],int(40),int(50),int(0),int(100),did:example:assessor1,https://example.org/criteria]]
StudentAgent.p_Rejected = [[ProfessorAgent[6],UniversityAgent[4],EvidenceNet[10],CompetencyNet[8],WalletNet[14],HEDULedgerNet[12],HRAgent[16],int(40),int(50),int(0),int(100),did:example:student1,bafyEvidence001,student-signature,EVIDENCE_REJECTED]]
SystemNet.p_CompetencyPool = [CompetencyNet[8]]
SystemNet.p_EvidencePool = [EvidenceNet[10]]
SystemNet.p_HRPool = [HRAgent[16]]
SystemNet.p_LedgerPool = [HEDULedgerNet[12]]
SystemNet.p_ProfessorPool = [ProfessorAgent[6]]
SystemNet.p_StudentPool = [StudentAgent[2]]
SystemNet.p_UniversityPool = [UniversityAgent[4]]
SystemNet.p_WalletPool = [WalletNet[14]]
UniversityAgent.p_Idle = [[did:example:issuer1,did:example:student1,credential-001]]
WalletNet.p_Empty = [[]]
```

## cancel_submitted

**EXPECTED_FAILURE_TERMINATION**; outcome `CANCELLED`.

| Net | Final occupied place(s) | Status |
| --- | --- | --- |
| SystemNet.rnw | p_CompetencyPool; p_EvidencePool; p_HRPool; p_LedgerPool; p_ProfessorPool; p_StudentPool; p_UniversityPool; p_WalletPool | Reference repository |
| StudentAgent.rnw | p_Rejected | Activated and terminated |
| ProfessorAgent.rnw | p_Approved | Activated and terminated |
| UniversityAgent.rnw | p_CredentialIssued | Activated and terminated |
| EvidenceNet.rnw | p_Graded | Activated and terminated |
| CompetencyNet.rnw | p_Terminated | Activated and terminated |
| WalletNet.rnw | p_Rejected | Activated and terminated |
| HEDULedgerNet.rnw | p_Received | Uninvoked service |
| HRAgent.rnw | p_Idle | Uninvoked service |
| EvidenceObject.rnw | p_Graded | Activated and terminated |
| CredentialObject.rnw | p_Cancelled | Activated and terminated |

Enabled bindings: **0**. Active unfinished instances: **0**. Retained references: **11/11**. Uninvoked services: `[HEDULedgerNet, HRAgent]`.

```text
CompetencyNet.p_Terminated = [[StudentAgent[2],CredentialObject[39],WalletNet[8],HEDULedgerNet[6],HRAgent[16],int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001,issuer-signature,did:example:student1,vp-001,CANCELLED]]
CredentialObject.p_Cancelled = [[int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001,student-signature,issuer-signature,did:example:student1,vp-001,CANCELLED]]
EvidenceNet.p_Graded = [[EvidenceObject[29],did:example:student1,bafyEvidence001,int(50),did:example:assessor1,int(80),https://example.org/criteria]]
EvidenceObject.p_Graded = [[did:example:student1,bafyEvidence001,did:example:assessor1,int(80),https://example.org/criteria]]
HEDULedgerNet.p_Received = [[]]
HRAgent.p_Idle = [[]]
ProfessorAgent.p_Approved = [[StudentAgent[2],UniversityAgent[14],CompetencyNet[12],WalletNet[8],HEDULedgerNet[6],HRAgent[16],EvidenceNet[4],int(80),int(50),int(0),int(100),did:example:assessor1,https://example.org/criteria]]
StudentAgent.p_Rejected = [[ProfessorAgent[10],UniversityAgent[14],EvidenceNet[4],CompetencyNet[12],WalletNet[8],HEDULedgerNet[6],HRAgent[16],int(80),int(50),int(0),int(100),did:example:student1,bafyEvidence001,student-signature,CANCELLED]]
SystemNet.p_CompetencyPool = [CompetencyNet[12]]
SystemNet.p_EvidencePool = [EvidenceNet[4]]
SystemNet.p_HRPool = [HRAgent[16]]
SystemNet.p_LedgerPool = [HEDULedgerNet[6]]
SystemNet.p_ProfessorPool = [ProfessorAgent[10]]
SystemNet.p_StudentPool = [StudentAgent[2]]
SystemNet.p_UniversityPool = [UniversityAgent[14]]
SystemNet.p_WalletPool = [WalletNet[8]]
UniversityAgent.p_CredentialIssued = [[StudentAgent[2],CompetencyNet[12],WalletNet[8],HEDULedgerNet[6],HRAgent[16],int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001]]
WalletNet.p_Rejected = [[CompetencyNet[12],did:example:issuer1,did:example:student1,credential-001,CANCELLED]]
```

## cancel_waiting

**EXPECTED_FAILURE_TERMINATION**; outcome `CANCELLED`.

| Net | Final occupied place(s) | Status |
| --- | --- | --- |
| SystemNet.rnw | p_CompetencyPool; p_EvidencePool; p_HRPool; p_LedgerPool; p_ProfessorPool; p_StudentPool; p_UniversityPool; p_WalletPool | Reference repository |
| StudentAgent.rnw | p_Rejected | Activated and terminated |
| ProfessorAgent.rnw | p_Approved | Activated and terminated |
| UniversityAgent.rnw | p_CredentialIssued | Activated and terminated |
| EvidenceNet.rnw | p_Graded | Activated and terminated |
| CompetencyNet.rnw | p_Terminated | Activated and terminated |
| WalletNet.rnw | p_Rejected | Activated and terminated |
| HEDULedgerNet.rnw | p_Received | Uninvoked service |
| HRAgent.rnw | p_Idle | Uninvoked service |
| EvidenceObject.rnw | p_Graded | Activated and terminated |
| CredentialObject.rnw | p_Cancelled | Activated and terminated |

Enabled bindings: **0**. Active unfinished instances: **0**. Retained references: **11/11**. Uninvoked services: `[HEDULedgerNet, HRAgent]`.

```text
CompetencyNet.p_Terminated = [[StudentAgent[2],CredentialObject[39],WalletNet[12],HEDULedgerNet[6],HRAgent[14],int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001,issuer-signature,did:example:student1,vp-001,CANCELLED]]
CredentialObject.p_Cancelled = [[int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001,student-signature,issuer-signature,did:example:student1,vp-001,CANCELLED]]
EvidenceNet.p_Graded = [[EvidenceObject[29],did:example:student1,bafyEvidence001,int(50),did:example:assessor1,int(80),https://example.org/criteria]]
EvidenceObject.p_Graded = [[did:example:student1,bafyEvidence001,did:example:assessor1,int(80),https://example.org/criteria]]
HEDULedgerNet.p_Received = [[]]
HRAgent.p_Idle = [[]]
ProfessorAgent.p_Approved = [[StudentAgent[2],UniversityAgent[4],CompetencyNet[10],WalletNet[12],HEDULedgerNet[6],HRAgent[14],EvidenceNet[16],int(80),int(50),int(0),int(100),did:example:assessor1,https://example.org/criteria]]
StudentAgent.p_Rejected = [[ProfessorAgent[8],UniversityAgent[4],EvidenceNet[16],CompetencyNet[10],WalletNet[12],HEDULedgerNet[6],HRAgent[14],int(80),int(50),int(0),int(100),did:example:student1,bafyEvidence001,student-signature,CANCELLED]]
SystemNet.p_CompetencyPool = [CompetencyNet[10]]
SystemNet.p_EvidencePool = [EvidenceNet[16]]
SystemNet.p_HRPool = [HRAgent[14]]
SystemNet.p_LedgerPool = [HEDULedgerNet[6]]
SystemNet.p_ProfessorPool = [ProfessorAgent[8]]
SystemNet.p_StudentPool = [StudentAgent[2]]
SystemNet.p_UniversityPool = [UniversityAgent[4]]
SystemNet.p_WalletPool = [WalletNet[12]]
UniversityAgent.p_CredentialIssued = [[StudentAgent[2],CompetencyNet[10],WalletNet[12],HEDULedgerNet[6],HRAgent[14],int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001]]
WalletNet.p_Rejected = [[CompetencyNet[10],did:example:issuer1,did:example:student1,credential-001,CANCELLED]]
```

## expire_submitted

**EXPECTED_FAILURE_TERMINATION**; outcome `EXPIRED`.

| Net | Final occupied place(s) | Status |
| --- | --- | --- |
| SystemNet.rnw | p_CompetencyPool; p_EvidencePool; p_HRPool; p_LedgerPool; p_ProfessorPool; p_StudentPool; p_UniversityPool; p_WalletPool | Reference repository |
| StudentAgent.rnw | p_Rejected | Activated and terminated |
| ProfessorAgent.rnw | p_Approved | Activated and terminated |
| UniversityAgent.rnw | p_CredentialIssued | Activated and terminated |
| EvidenceNet.rnw | p_Graded | Activated and terminated |
| CompetencyNet.rnw | p_Terminated | Activated and terminated |
| WalletNet.rnw | p_Rejected | Activated and terminated |
| HEDULedgerNet.rnw | p_Received | Uninvoked service |
| HRAgent.rnw | p_Idle | Uninvoked service |
| EvidenceObject.rnw | p_Graded | Activated and terminated |
| CredentialObject.rnw | p_Expired | Activated and terminated |

Enabled bindings: **0**. Active unfinished instances: **0**. Retained references: **11/11**. Uninvoked services: `[HEDULedgerNet, HRAgent]`.

```text
CompetencyNet.p_Terminated = [[StudentAgent[2],CredentialObject[39],WalletNet[14],HEDULedgerNet[16],HRAgent[8],int(80),int(50),int(100),int(100),did:example:issuer1,did:example:student1,credential-001,issuer-signature,did:example:student1,vp-001,EXPIRED]]
CredentialObject.p_Expired = [[int(80),int(50),int(100),int(100),did:example:issuer1,did:example:student1,credential-001,student-signature,issuer-signature,did:example:student1,vp-001,EXPIRED]]
EvidenceNet.p_Graded = [[EvidenceObject[29],did:example:student1,bafyEvidence001,int(50),did:example:assessor1,int(80),https://example.org/criteria]]
EvidenceObject.p_Graded = [[did:example:student1,bafyEvidence001,did:example:assessor1,int(80),https://example.org/criteria]]
HEDULedgerNet.p_Received = [[]]
HRAgent.p_Idle = [[]]
ProfessorAgent.p_Approved = [[StudentAgent[2],UniversityAgent[12],CompetencyNet[4],WalletNet[14],HEDULedgerNet[16],HRAgent[8],EvidenceNet[10],int(80),int(50),int(100),int(100),did:example:assessor1,https://example.org/criteria]]
StudentAgent.p_Rejected = [[ProfessorAgent[6],UniversityAgent[12],EvidenceNet[10],CompetencyNet[4],WalletNet[14],HEDULedgerNet[16],HRAgent[8],int(80),int(50),int(100),int(100),did:example:student1,bafyEvidence001,student-signature,EXPIRED]]
SystemNet.p_CompetencyPool = [CompetencyNet[4]]
SystemNet.p_EvidencePool = [EvidenceNet[10]]
SystemNet.p_HRPool = [HRAgent[8]]
SystemNet.p_LedgerPool = [HEDULedgerNet[16]]
SystemNet.p_ProfessorPool = [ProfessorAgent[6]]
SystemNet.p_StudentPool = [StudentAgent[2]]
SystemNet.p_UniversityPool = [UniversityAgent[12]]
SystemNet.p_WalletPool = [WalletNet[14]]
UniversityAgent.p_CredentialIssued = [[StudentAgent[2],CompetencyNet[4],WalletNet[14],HEDULedgerNet[16],HRAgent[8],int(80),int(50),int(100),int(100),did:example:issuer1,did:example:student1,credential-001]]
WalletNet.p_Rejected = [[CompetencyNet[4],did:example:issuer1,did:example:student1,credential-001,EXPIRED]]
```

## expire_waiting

**EXPECTED_FAILURE_TERMINATION**; outcome `EXPIRED`.

| Net | Final occupied place(s) | Status |
| --- | --- | --- |
| SystemNet.rnw | p_CompetencyPool; p_EvidencePool; p_HRPool; p_LedgerPool; p_ProfessorPool; p_StudentPool; p_UniversityPool; p_WalletPool | Reference repository |
| StudentAgent.rnw | p_Rejected | Activated and terminated |
| ProfessorAgent.rnw | p_Approved | Activated and terminated |
| UniversityAgent.rnw | p_CredentialIssued | Activated and terminated |
| EvidenceNet.rnw | p_Graded | Activated and terminated |
| CompetencyNet.rnw | p_Terminated | Activated and terminated |
| WalletNet.rnw | p_Rejected | Activated and terminated |
| HEDULedgerNet.rnw | p_Received | Uninvoked service |
| HRAgent.rnw | p_Idle | Uninvoked service |
| EvidenceObject.rnw | p_Graded | Activated and terminated |
| CredentialObject.rnw | p_Expired | Activated and terminated |

Enabled bindings: **0**. Active unfinished instances: **0**. Retained references: **11/11**. Uninvoked services: `[HEDULedgerNet, HRAgent]`.

```text
CompetencyNet.p_Terminated = [[StudentAgent[2],CredentialObject[39],WalletNet[14],HEDULedgerNet[16],HRAgent[8],int(80),int(50),int(100),int(100),did:example:issuer1,did:example:student1,credential-001,issuer-signature,did:example:student1,vp-001,EXPIRED]]
CredentialObject.p_Expired = [[int(80),int(50),int(100),int(100),did:example:issuer1,did:example:student1,credential-001,student-signature,issuer-signature,did:example:student1,vp-001,EXPIRED]]
EvidenceNet.p_Graded = [[EvidenceObject[29],did:example:student1,bafyEvidence001,int(50),did:example:assessor1,int(80),https://example.org/criteria]]
EvidenceObject.p_Graded = [[did:example:student1,bafyEvidence001,did:example:assessor1,int(80),https://example.org/criteria]]
HEDULedgerNet.p_Received = [[]]
HRAgent.p_Idle = [[]]
ProfessorAgent.p_Approved = [[StudentAgent[2],UniversityAgent[12],CompetencyNet[4],WalletNet[14],HEDULedgerNet[16],HRAgent[8],EvidenceNet[10],int(80),int(50),int(100),int(100),did:example:assessor1,https://example.org/criteria]]
StudentAgent.p_Rejected = [[ProfessorAgent[6],UniversityAgent[12],EvidenceNet[10],CompetencyNet[4],WalletNet[14],HEDULedgerNet[16],HRAgent[8],int(80),int(50),int(100),int(100),did:example:student1,bafyEvidence001,student-signature,EXPIRED]]
SystemNet.p_CompetencyPool = [CompetencyNet[4]]
SystemNet.p_EvidencePool = [EvidenceNet[10]]
SystemNet.p_HRPool = [HRAgent[8]]
SystemNet.p_LedgerPool = [HEDULedgerNet[16]]
SystemNet.p_ProfessorPool = [ProfessorAgent[6]]
SystemNet.p_StudentPool = [StudentAgent[2]]
SystemNet.p_UniversityPool = [UniversityAgent[12]]
SystemNet.p_WalletPool = [WalletNet[14]]
UniversityAgent.p_CredentialIssued = [[StudentAgent[2],CompetencyNet[4],WalletNet[14],HEDULedgerNet[16],HRAgent[8],int(80),int(50),int(100),int(100),did:example:issuer1,did:example:student1,credential-001]]
WalletNet.p_Rejected = [[CompetencyNet[4],did:example:issuer1,did:example:student1,credential-001,EXPIRED]]
```

## bad_issuer_signature_submitted

**EXPECTED_FAILURE_TERMINATION**; outcome `INVALID_ISSUER_SIGNATURE`.

| Net | Final occupied place(s) | Status |
| --- | --- | --- |
| SystemNet.rnw | p_CompetencyPool; p_EvidencePool; p_HRPool; p_LedgerPool; p_ProfessorPool; p_StudentPool; p_UniversityPool; p_WalletPool | Reference repository |
| StudentAgent.rnw | p_Rejected | Activated and terminated |
| ProfessorAgent.rnw | p_Approved | Activated and terminated |
| UniversityAgent.rnw | p_CredentialIssued | Activated and terminated |
| EvidenceNet.rnw | p_Graded | Activated and terminated |
| CompetencyNet.rnw | p_Terminated | Activated and terminated |
| WalletNet.rnw | p_Rejected | Activated and terminated |
| HEDULedgerNet.rnw | p_Received | Uninvoked service |
| HRAgent.rnw | p_Idle | Uninvoked service |
| EvidenceObject.rnw | p_Graded | Activated and terminated |
| CredentialObject.rnw | p_Rejected | Activated and terminated |

Enabled bindings: **0**. Active unfinished instances: **0**. Retained references: **11/11**. Uninvoked services: `[HEDULedgerNet, HRAgent]`.

```text
CompetencyNet.p_Terminated = [[StudentAgent[2],CredentialObject[39],WalletNet[16],HEDULedgerNet[14],HRAgent[10],int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001,wrong-issuer-signature,did:example:student1,vp-001,INVALID_ISSUER_SIGNATURE]]
CredentialObject.p_Rejected = [[int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001,student-signature,issuer-signature,did:example:student1,vp-001,INVALID_ISSUER_SIGNATURE]]
EvidenceNet.p_Graded = [[EvidenceObject[29],did:example:student1,bafyEvidence001,int(50),did:example:assessor1,int(80),https://example.org/criteria]]
EvidenceObject.p_Graded = [[did:example:student1,bafyEvidence001,did:example:assessor1,int(80),https://example.org/criteria]]
HEDULedgerNet.p_Received = [[]]
HRAgent.p_Idle = [[]]
ProfessorAgent.p_Approved = [[StudentAgent[2],UniversityAgent[6],CompetencyNet[4],WalletNet[16],HEDULedgerNet[14],HRAgent[10],EvidenceNet[8],int(80),int(50),int(0),int(100),did:example:assessor1,https://example.org/criteria]]
StudentAgent.p_Rejected = [[ProfessorAgent[12],UniversityAgent[6],EvidenceNet[8],CompetencyNet[4],WalletNet[16],HEDULedgerNet[14],HRAgent[10],int(80),int(50),int(0),int(100),did:example:student1,bafyEvidence001,student-signature,INVALID_ISSUER_SIGNATURE]]
SystemNet.p_CompetencyPool = [CompetencyNet[4]]
SystemNet.p_EvidencePool = [EvidenceNet[8]]
SystemNet.p_HRPool = [HRAgent[10]]
SystemNet.p_LedgerPool = [HEDULedgerNet[14]]
SystemNet.p_ProfessorPool = [ProfessorAgent[12]]
SystemNet.p_StudentPool = [StudentAgent[2]]
SystemNet.p_UniversityPool = [UniversityAgent[6]]
SystemNet.p_WalletPool = [WalletNet[16]]
UniversityAgent.p_CredentialIssued = [[StudentAgent[2],CompetencyNet[4],WalletNet[16],HEDULedgerNet[14],HRAgent[10],int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001]]
WalletNet.p_Rejected = [[CompetencyNet[4],did:example:issuer1,did:example:student1,credential-001,INVALID_ISSUER_SIGNATURE]]
```

## bad_issuer_signature_waiting

**EXPECTED_FAILURE_TERMINATION**; outcome `INVALID_ISSUER_SIGNATURE`.

| Net | Final occupied place(s) | Status |
| --- | --- | --- |
| SystemNet.rnw | p_CompetencyPool; p_EvidencePool; p_HRPool; p_LedgerPool; p_ProfessorPool; p_StudentPool; p_UniversityPool; p_WalletPool | Reference repository |
| StudentAgent.rnw | p_Rejected | Activated and terminated |
| ProfessorAgent.rnw | p_Approved | Activated and terminated |
| UniversityAgent.rnw | p_CredentialIssued | Activated and terminated |
| EvidenceNet.rnw | p_Graded | Activated and terminated |
| CompetencyNet.rnw | p_Terminated | Activated and terminated |
| WalletNet.rnw | p_Rejected | Activated and terminated |
| HEDULedgerNet.rnw | p_Received | Uninvoked service |
| HRAgent.rnw | p_Idle | Uninvoked service |
| EvidenceObject.rnw | p_Graded | Activated and terminated |
| CredentialObject.rnw | p_Rejected | Activated and terminated |

Enabled bindings: **0**. Active unfinished instances: **0**. Retained references: **11/11**. Uninvoked services: `[HEDULedgerNet, HRAgent]`.

```text
CompetencyNet.p_Terminated = [[StudentAgent[2],CredentialObject[39],WalletNet[16],HEDULedgerNet[14],HRAgent[10],int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001,wrong-issuer-signature,did:example:student1,vp-001,INVALID_ISSUER_SIGNATURE]]
CredentialObject.p_Rejected = [[int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001,student-signature,issuer-signature,did:example:student1,vp-001,INVALID_ISSUER_SIGNATURE]]
EvidenceNet.p_Graded = [[EvidenceObject[29],did:example:student1,bafyEvidence001,int(50),did:example:assessor1,int(80),https://example.org/criteria]]
EvidenceObject.p_Graded = [[did:example:student1,bafyEvidence001,did:example:assessor1,int(80),https://example.org/criteria]]
HEDULedgerNet.p_Received = [[]]
HRAgent.p_Idle = [[]]
ProfessorAgent.p_Approved = [[StudentAgent[2],UniversityAgent[8],CompetencyNet[4],WalletNet[16],HEDULedgerNet[14],HRAgent[10],EvidenceNet[6],int(80),int(50),int(0),int(100),did:example:assessor1,https://example.org/criteria]]
StudentAgent.p_Rejected = [[ProfessorAgent[12],UniversityAgent[8],EvidenceNet[6],CompetencyNet[4],WalletNet[16],HEDULedgerNet[14],HRAgent[10],int(80),int(50),int(0),int(100),did:example:student1,bafyEvidence001,student-signature,INVALID_ISSUER_SIGNATURE]]
SystemNet.p_CompetencyPool = [CompetencyNet[4]]
SystemNet.p_EvidencePool = [EvidenceNet[6]]
SystemNet.p_HRPool = [HRAgent[10]]
SystemNet.p_LedgerPool = [HEDULedgerNet[14]]
SystemNet.p_ProfessorPool = [ProfessorAgent[12]]
SystemNet.p_StudentPool = [StudentAgent[2]]
SystemNet.p_UniversityPool = [UniversityAgent[8]]
SystemNet.p_WalletPool = [WalletNet[16]]
UniversityAgent.p_CredentialIssued = [[StudentAgent[2],CompetencyNet[4],WalletNet[16],HEDULedgerNet[14],HRAgent[10],int(80),int(50),int(0),int(100),did:example:issuer1,did:example:student1,credential-001]]
WalletNet.p_Rejected = [[CompetencyNet[4],did:example:issuer1,did:example:student1,credential-001,INVALID_ISSUER_SIGNATURE]]
```

## Interpreting the changed paths

`grade_reject` fires professor notification while Student is still submitted; `grade_reject_waiting` first fires its local wait transition. The cancel/expire/invalid-issuer suffix `_submitted` fires immediately after mint; `_waiting` first advances the Student wait; the unsuffixed variants first complete consent. All variants terminate through their explicit phase receiver, with no subsequent recovery suffix needed.

`bad_signature` ends through explicit denial, not a cancellation continuation. `bad_issuer_signature` ends as INVALID_ISSUER_SIGNATURE, not CANCELLED. `ledger_reject` ends before any VP generation; HR is uninvoked. Happy explicitly fires ledger t_Anchor after t_Commit, before any VP stage. [Native evidence](POST_REPAIR_VALIDATION_EVIDENCE.txt) contains the initiating transitions and complete binding participants.
