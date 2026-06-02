package bankproject;

/**
 * A savings account that earns compound interest credited monthly.
 *
 * <p><b>Interest rule:</b> Each call to {@link #applyMonthlyFees()} credits
 * one month's interest calculated as {@code balance * (annualRate / 12)},
 * using an annual rate of {@value #ANNUAL_INTEREST_RATE} (4%). The amount is
 * recorded as an INTEREST transaction. There are no fees or penalties.
 *
 * @author RON TAYLOR
 * @see Account
 * @see CheckingAccount
 */
public class SavingsAccount extends Account {

    /** Annual interest rate (4.0%). */
    private static final double ANNUAL_INTEREST_RATE  = 0.04;

    /** Monthly interest rate derived from the annual rate (annualRate / 12). */
    private static final double MONTHLY_INTEREST_RATE = ANNUAL_INTEREST_RATE / 12.0;

    /**
     * Opens a new Savings account.
     *
     * @param ownerName      the full name of the account holder
     * @param accountNumber  the unique account number
     * @param initialBalance the opening balance; must be &gt;= 0
     */
    public SavingsAccount(String ownerName, int accountNumber, double initialBalance) {
        super(ownerName, accountNumber, initialBalance);
    }

    /**
     * Loading constructor — restores a Savings account from persisted data
     * without recording an opening deposit transaction.
     *
     * @param ownerName     the full name of the account holder
     * @param accountNumber the unique account number
     * @param balance       the balance restored from file
     * @param loading       distinguishing flag; always pass {@code true}
     */
    SavingsAccount(String ownerName, int accountNumber, double balance, boolean loading) {
        super(ownerName, accountNumber, balance, loading);
    }

    /**
     * Credits one month's interest ({@code balance * MONTHLY_INTEREST_RATE})
     * to this account and records an INTEREST transaction. Prints the credited
     * amount and updated balance to the console.
     */
    @Override
    public void applyMonthlyFees() {
        double interest = balance * MONTHLY_INTEREST_RATE;
        balance += interest;
        history.add(new Transaction(Transaction.Type.INTEREST, interest, balance));
        System.out.printf("Interest credited: +$%.2f  (%.1f%% annual rate)%n",
                interest, ANNUAL_INTEREST_RATE * 100);
        System.out.printf("Balance after interest: $%.2f%n", balance);
    }

    /**
     * @return {@code "Savings"}
     */
    @Override
    public String getAccountType() {
        return "Savings";
    }
}
