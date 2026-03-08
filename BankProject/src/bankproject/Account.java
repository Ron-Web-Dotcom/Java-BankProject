package bankproject;

import java.util.ArrayList;
import java.util.List;

public abstract class Account {

    protected final String ownerName;
    protected final int accountNumber;
    protected double balance;
    protected final List<Transaction> history = new ArrayList<>();

    public Account(String ownerName, int accountNumber, double initialBalance) {
        this.ownerName = ownerName;
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        if (initialBalance > 0) {
            history.add(new Transaction(Transaction.Type.DEPOSIT, initialBalance, balance));
        }
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

    /**
     * Applies monthly fees or interest specific to the account type.
     */
    public abstract void applyMonthlyFees();

    public abstract String getAccountType();

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

    public double getBalance()      { return balance; }
    public String getOwnerName()    { return ownerName; }
    public int getAccountNumber()   { return accountNumber; }
}
