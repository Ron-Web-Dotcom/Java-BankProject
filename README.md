# Ron's Bank — Java Console Banking Application

A multi-account, interactive console banking system built in Java. Supports Checking and Savings accounts with full transaction history, inter-account transfers, monthly fee/interest processing, and persistent data storage across sessions.

---

## Features

| Feature | Description |
|---|---|
| Multiple accounts | Open and manage any number of Checking or Savings accounts |
| Deposits & Withdrawals | Full validation; rejects zero/negative amounts and overdrafts |
| Inter-account Transfers | Move funds between any two accounts atomically |
| Monthly Processing | Checking: $50 service charge if balance < $1,000 · Savings: 4% annual interest credited monthly |
| Transaction History | Every operation is timestamped and stored in a per-account ledger |
| Full Statement | Print a formatted statement showing all transactions with running balance |
| Data Persistence | Accounts and history are saved to `accounts.dat` on exit and reloaded on startup |
| Input Validation | All numeric fields are guarded against non-numeric input — bad input returns to the menu |

---

## Project Structure

```
Java-BankProject/
├── README.md
└── BankProject/
    ├── src/
    │   └── bankproject/
    │       ├── BankProject.java      # Entry point and console UI
    │       ├── Bank.java             # Account registry, transfers, file I/O
    │       ├── Account.java          # Abstract base class
    │       ├── CheckingAccount.java  # Monthly service charge logic
    │       ├── SavingsAccount.java   # Monthly interest logic
    │       └── Transaction.java      # Immutable transaction record
    ├── build/                        # Compiled classes (git-ignored)
    └── nbproject/                    # NetBeans project metadata
```

---

## Class Hierarchy

```
Account  (abstract)
├── CheckingAccount   — $50/month fee when balance < $1,000
└── SavingsAccount    — 4% annual interest, credited monthly (÷ 12)

Bank                  — registry + transfer engine + file persistence
Transaction           — immutable record: type · amount · balanceAfter · timestamp
BankProject           — main() + all console menu logic
```

---

## Build & Run

### Requirements
- Java 11 or later (compiled with `javac -source 11 -target 11`)

### NetBeans
Open the `BankProject/` folder as a NetBeans project and click **Run**.

### Command line
```bash
# Compile
javac -source 11 -target 11 BankProject/src/bankproject/*.java -d BankProject/build/classes

# Run
java -cp BankProject/build/classes bankproject.BankProject
```

The program reads and writes `accounts.dat` in the **working directory** where it is launched.

---

## Data Persistence — `accounts.dat` format

Accounts are saved to a plain-text, pipe-delimited file in the working directory. The file is written on clean exit and read on startup.

```
ACCOUNT|CHECKING|Ron Taylor|1001|1250.00
TX|DEPOSIT|500.00|500.00|2024-03-14T10:30:00
TX|WITHDRAWAL|-150.00|350.00|2024-03-14T11:00:00
TX|SERVICE_CHARGE|-50.00|300.00|2024-03-15T00:00:00
ACCOUNT|SAVINGS|Jane Doe|1002|5016.67
TX|DEPOSIT|5000.00|5000.00|2024-03-14T09:00:00
TX|INTEREST|16.67|5016.67|2024-03-14T12:00:00
```

- `ACCOUNT|TYPE|name|accountNumber|balance` — one header per account
- `TX|TYPE|amount|balanceAfter|ISO-timestamp` — one line per transaction
- Malformed lines are silently skipped; the rest of the file is still loaded

---

## Account Rules

### Checking Account
- Monthly service charge: **$50.00** deducted when balance is **below $1,000.00** at time of processing
- No fee when balance meets or exceeds $1,000.00
- Charge is recorded as a `SERVICE_CHARGE` transaction

### Savings Account
- Annual interest rate: **4.00%**
- Monthly credit: `balance × (0.04 ÷ 12)` — applied each time "Apply Monthly Fees / Interest" is selected
- Credit is recorded as an `INTEREST` transaction

---

## Sample Session

Below is a complete walkthrough demonstrating every feature.

### Startup (no existing data)

```
=========================================
         Welcome to Ron's Bank
=========================================

=== Ron's Bank  |  0 accounts on file ===
1. Manage an account
2. Open a new account
3. Exit
Choose an option:
```

---

### Open a Checking account (account #1001)

```
Choose an option: 2

Enter your name: Ron Taylor
Enter account number: 1001
Select account type:
  1. Checking  (service charge if balance < $1,000)
  2. Savings   (earns 4% annual interest, credited monthly)
Choice: 1
Opening balance: $1500

Account #1001 opened for Ron Taylor. Welcome!
```

---

### Open a Savings account (account #2001)

```
Choose an option: 2

Enter your name: Jane Doe
Enter account number: 2001
Select account type:
  1. Checking  (service charge if balance < $1,000)
  2. Savings   (earns 4% annual interest, credited monthly)
Choice: 2
Opening balance: $5000

Account #2001 opened for Jane Doe. Welcome!
```

---

### Manage an account — account list

```
Choose an option: 1

Available accounts:
  1. Checking    #1001    Ron Taylor            $1500.00
  2. Savings     #2001    Jane Doe              $5000.00
Select account number (or 0 to go back): 1
```

---

### Deposit

```
--- Checking Account #1001  |  Ron Taylor  |  Balance: $1500.00 ---
1. Deposit
...
Choose an option: 1
Deposit amount: $200
Deposited $200.00. New balance: $1700.00
```

---

### Withdrawal

```
Choose an option: 2
Withdrawal amount: $500
Withdrew $500.00. New balance: $1200.00
```

---

### Transfer to another account

```
Choose an option: 3

Transfer to:
  1. Savings     #2001    Jane Doe  ($5000.00)
Select destination (or 0 to cancel): 1
Amount to transfer (available: $1200.00): $300
Transferred $300.00 to Jane Doe (#2001). Your new balance: $900.00
```

---

### Apply Monthly Fees — Checking (balance below $1,000)

```
Choose an option: 4

Service charge applied: -$50.00  (balance was below $1000.00 minimum)
Balance after monthly review: $850.00
```

---

### Apply Monthly Fees — Checking (balance above $1,000)

```
No service charge this month - balance is above the minimum.
Balance after monthly review: $1200.00
```

---

### Apply Monthly Interest — Savings

```
--- Savings Account #2001  |  Jane Doe  |  Balance: $5300.00 ---
Choose an option: 4

Interest credited: +$17.67  (4.0% annual rate)
Balance after interest: $5317.67
```

---

### View Full Statement

```
Choose an option: 5

========================================
           Account Statement
========================================
  Owner:   Ron Taylor
  Account: #1001  (Checking)
  Balance: $850.00
----------------------------------------
Date/Time             Type               Amount      Balance
----------------------------------------
2024-03-14 10:30:00   DEPOSIT         +1500.00   Balance: $1500.00
2024-03-14 10:35:00   DEPOSIT          +200.00   Balance: $1700.00
2024-03-14 10:40:00   WITHDRAWAL       -500.00   Balance: $1200.00
2024-03-14 10:45:00   WITHDRAWAL       -300.00   Balance: $900.00
2024-03-14 10:50:00   SERVICE_CHARGE    -50.00   Balance: $850.00
========================================
```

---

### Check Balance

```
Choose an option: 6

Current balance: $850.00
```

---

### Exit and auto-save

```
Choose an option: 3  (Back to main menu)

Choose an option: 3  (Exit)

Accounts saved. Goodbye!
```

On the next launch:

```
Loaded 2 accounts from file.

=========================================
         Welcome to Ron's Bank
=========================================
```

---

### Error Handling Examples

**Non-numeric input:**
```
Choose an option: abc
Invalid input.
```

**Blank name:**
```
Enter your name:
Error: Name cannot be empty.
```

**Duplicate account number:**
```
Enter account number: 1001
Error: Account number already in use.
```

**Overdraft attempt:**
```
Withdrawal amount: $9999
Error: Insufficient funds. Available: $850.00
```

**Transfer with zero amount:**
```
Amount to transfer (available: $850.00): $0
Transfer failed. Amount must be > $0.00 and <= $850.00.
```

---

## Author

RON TAYLOR
