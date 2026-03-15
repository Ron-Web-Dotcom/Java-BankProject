package bankproject;

import java.util.ArrayList;
import java.util.List;

public abstract class Account {

    protected final String ownerName;
    protected final int accountNumber;
    protected double balance;
    protected final List<Transaction> history = new ArrayList<>();

    /** Used when opening a brand-new account — records the opening deposit. */
    public Account(String ownerName, int accountNumber, double initialBalance) {
        this.ownerName = ownerName;
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        if (initialBalance > 0) {
            history.add(new Transaction(Transaction.Type.DEPOSIT, initialBalance, balance));
        }
    }

    /** Used when loading an existing account from file — does NOT add an initial transaction. */
    protected Account(String ownerName, int accountNumber, double balance, boolean loading) {
        this.ownerName = ownerName;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public boolean deposit(double amount) {
        if (amount <= 0) return false;
        balance += amount;
        history.add(new Transaction(Transaction.Type.DEPOSIT, amount, balance));
        return true;
    }

    public boolean withdraw(double amount) {
        if (amount <= 0 || amount > balance) return false;
        balance -= amount;
        history.add(new Transaction(Transaction.Type.WITHDRAWAL, -amount, balance));
        return true;
    }

    /** Applies monthly fees or interest specific to the account type. */
    public abstract void applyMonthlyFees();

    public abstract String getAccountType();

    // -------------------------------------------------------------------------
    // File persistence
    // -------------------------------------------------------------------------

    /** Serialize this account's header line for accounts.dat */
    public String toFileLine() {
        return String.format("ACCOUNT|%s|%s|%d|%.2f",
                getAccountType().toUpperCase(), ownerName, accountNumber, balance);
    }

    /** Serialize all transactions to file lines. */
    public List<String> transactionFileLines() {
        List<String> lines = new ArrayList<>();
        for (Transaction tx : history) {
            lines.add(tx.toFileLine());
        }
        return lines;
    }

    /** Add a transaction loaded from file (bypasses balance mutation). */
    public void addHistoricalTransaction(Transaction tx) {
        history.add(tx);
    }

    // -------------------------------------------------------------------------
    // Display
    // -------------------------------------------------------------------------

    public void printStatement() {
        System.out.println("\n========================================");
        System.out.println("           Account Statement            ");
        System.out.println("========================================");
        System.out.printf("  Owner:   %s%n", ownerName);
        System.out.printf("  Account: #%d  (%s)%n", accountNumber, getAccountType());
        System.out.printf("  Balance: $%.2f%n", balance);
        System.out.println("----------------------------------------");
        System.out.printf("%-20s  %-15s  %10s   %s%n",
                "Date/Time", "Type", "Amount", "Balance");
        System.out.println("----------------------------------------");
        if (history.isEmpty()) {
            System.out.println("  No transactions recorded.");
        } else {
            history.forEach(System.out::println);
        }
        System.out.println("========================================\n");
    }

    public double getBalance()    { return balance; }
    public String getOwnerName()  { return ownerName; }
    public int getAccountNumber() { return accountNumber; }
}
