# Frozen RNW byte restoration

Restored files: SystemNet.rnw.

All 11 working RNW byte hashes match the frozen validated source-check copies. All normalized text hashes match. Only SystemNet.rnw differs from the parent Git blob: its 225 CRLF endings were stored as LF. The exact frozen bytes were copied, without text reconstruction. All other RNWs were untouched.

No places, transitions, arcs, inscriptions, guards, channels, initial markings, coordinates, figure IDs, topology or model semantics changed. model.tsv, model_manifest.json and experiment results were untouched.

The existing repository-wide `* -text` rule disables Git text conversion, including RNWs. No attributes change was required. Full pre-restoration hashes and EOL counts are recorded in RNW_BYTE_AUDIT.json and RNW_BYTE_AUDIT.md.
