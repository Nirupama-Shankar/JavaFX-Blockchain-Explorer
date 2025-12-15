#  JavaFX Blockchain Explorer

A modern, Java-based blockchain simulator that demonstrates core blockchain concepts such as proof-of-work, Merkle trees, digital signatures, difficulty adjustment, and tamper detection — all visualized through a JavaFX desktop dashboard.

---

##  Features

-  Blockchain implementation from scratch
-  Digital signatures for transaction authentication (RSA)
-  Merkle Tree for transaction integrity
-  Proof-of-Work mining
-  Dynamic difficulty adjustment with live graph
-  Tamper detection and chain validation
-  Modern JavaFX UI with dark theme and dashboard layout

---

##  Architecture Overview
Transaction (Signed)
↓
Mempool
↓
Merkle Tree → Merkle Root
↓
Block (PoW Mining)
↓
Blockchain Validation
