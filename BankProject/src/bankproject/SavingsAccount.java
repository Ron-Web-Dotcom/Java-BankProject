package bankproject;

public class SavingsAccount extends Account {

    private static final double ANNUAL_INTEREST_RATE  = 0.04; // 4% per year
    private static final double MONTHLY_INTEREST_RATE = ANNUAL_INTEREST_RATE / 12.0;

    public SavingsAccount(String ownerName, int accountNumber, double initialBalance) {
        super(ownerName, accountNumber, initialBalance);
    }

    @Override
    public void applyMonthlyFees() {
        double interest = balance * MONTHLY_INTEREST_RATE;
        balance += interest;
        history.add(new Transaction(Transaction.Type.INTEREST, interest, balance));
        System.out.printf("Interest credited: +$%.2f  (%.1f%% annual rate)%n",
                interest, ANNUAL_INTEREST_RATE * 100);
        System.out.printf("Balance after interest: $%.2f%n", balance);
    }

    @Override
    public String getAccountType() {
        return "Savings";
    }
}
