package bankproject;

/**
 * A checking account that applies a flat monthly service charge whenever the
 * balance falls below the required minimum at the time fees are processed.
 *
 * <p><b>Monthly fee rule:</b> If {@code balance < }{@value #MINIMUM_BALANCE}
 * when {@link #applyMonthlyFees()} is called, a {@value #SERVICE_CHARGE}
 * service charge is deducted and recorded as a SERVICE_CHARGE transaction.
 * No fee is applied when the balance meets or exceeds the minimum.
 *
 * @author RON TAYLOR
 * @see Account
 * @see SavingsAccount
 */
public class CheckingAccount extends Account {

    /** Minimum balance required to avoid the monthly service charge ($1,000.00). */
    private static final double MINIMUM_BALANCE = 1000.00;

    /** Flat fee deducted when balance drops below {@link #MINIMUM_BALANCE} ($50.00). */
    private static final double SERVICE_CHARGE  = 50.00;

    /**
     * Opens a new Checking account.
     *
     * @param ownerName      the full name of the account holder
     * @param accountNumber  the unique account number
     * @param initialBalance the opening balance; must be &gt;= 0
     */
    public CheckingAccount(String ownerName, int accountNumber, double initialBalance) {
        super(ownerName, accountNumber, initialBalance);
    }

    /**
     * Loading constructor — restores a Checking account from persisted data
     * without recording an opening deposit transaction.
     *
     * @param ownerName     the full name of the account holder
     * @param accountNumber the unique account number
     * @param balance       the balance restored from file
     * @param loading       distinguishing flag; always pass {@code true}
     */
    CheckingAccount(String ownerName, int accountNumber, double balance, boolean loading) {
        super(ownerName, accountNumber, balance, loading);
    }

    /**
     * Applies the monthly service charge if the current balance is below
     * {@link #MINIMUM_BALANCE}. A SERVICE_CHARGE transaction is recorded and
     * a message printed; the ending balance is always displayed.
     */
    @Override
    public void applyMonthlyFees() {
        if (balance < MINIMUM_BALANCE) {
            balance -= SERVICE_CHARGE;
            history.add(new Transaction(Transaction.Type.SERVICE_CHARGE, -SERVICE_CHARGE, balance));
            System.out.printf("Service charge applied: -$%.2f  (balance was below $%.2f minimum)%n",
                    SERVICE_CHARGE, MINIMUM_BALANCE);
        } else {
            System.out.println("No service charge this month - balance is above the minimum.");
        }
        System.out.printf("Balance after monthly review: $%.2f%n", balance);
    }

    /**
     * @return {@code "Checking"}
     */
    @Override
    public String getAccountType() {
        return "Checking";
    }
}
