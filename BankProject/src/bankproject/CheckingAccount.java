package bankproject;

public class CheckingAccount extends Account {

    private static final double MINIMUM_BALANCE = 1000.00;
    private static final double SERVICE_CHARGE  = 50.00;

    public CheckingAccount(String ownerName, int accountNumber, double initialBalance) {
        super(ownerName, accountNumber, initialBalance);
    }

    @Override
    public void applyMonthlyFees() {
        if (balance < MINIMUM_BALANCE) {
            balance -= SERVICE_CHARGE;
            history.add(new Transaction(Transaction.Type.SERVICE_CHARGE, -SERVICE_CHARGE, balance));
            System.out.printf("Service charge applied: -$%.2f  (balance was below $%.2f minimum)%n",
                    SERVICE_CHARGE, MINIMUM_BALANCE);
        } else {
            System.out.println("No service charge this month — balance is above the minimum.");
        }
        System.out.printf("Balance after monthly review: $%.2f%n", balance);
    }

    @Override
    public String getAccountType() {
        return "Checking";
    }
}
