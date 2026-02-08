# Wallet Service (In-Memory)

Simple in-memory wallet service implemented in Java.

## Features
- Create accounts linked to users
- Manage balances (deposit, withdraw)
- Transfer money between accounts
- Input validation and domain exceptions
- **Atomic transfers** (no partial updates)

## Business Rules
- Amounts must be strictly positive
- Accounts must exist
- Balances can never be negative
- Transfers are atomic: debit and credit succeed together or are rolled back

## Atomicity
Transfers are implemented using a `try/catch` rollback mechanism to guarantee
that no money is lost if an error occurs during the operation.

## Tests
JUnit tests cover:
- Valid and invalid inputs
- Non-existing accounts
- Insufficient funds
- Transfer behavior
- Atomicity (rollback on failure)

## Tech Stack
- Java
- JUnit 5
- In-memory storage (HashMap)
