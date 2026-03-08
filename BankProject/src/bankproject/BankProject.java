package bankproject;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * @author RON TAYLOR
 */
public class BankProject {

    public static void main(String[] args) {
        try (Scanner input = new Scanner(System.in)) {
            System.out.println("=========================================");
            System.out.println("         Welcome to Ron's Bank          ");
            System.out.println("=========================================\n");

            Account account = createAccount(input);
            if (account == null) return;

            System.out.printf("%nAccount created! Welcome, %s.%n", account.getOwnerName());

            boolean running = true;
            while (running) {
                printMenu(account);
                System.out.print("Choose an option: ");

                int choice;
                try {
                    choice = input.nextInt();
                    input.nextLine();
                } catch (InputMismatchException e) {
                    input.nextLine();
                    System.out.println("Invalid choice. Please enter a number.\n");
                    continue;
                }

                switch (choice) {
                    case 1:
                        handleDeposit(input, account);
                        break;
                    case 2:
                        handleWithdrawal(input, account);
                        break;
                    case 3:
                        System.out.println();
                        account.applyMonthlyFees();
                        break;
                    case 4:
                        account.printStatement();
                        break;
                    case 5:
                        System.out.printf("%nCurrent balance: $%.2f%n%n", account.getBalance());
                        break;
                    case 6:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please choose 1-6.\n");
                }
            }

            System.out.printf("%nThank you for banking with us, %s. Goodbye!%n",
                    account.getOwnerName());
        }
    }

    // -------------------------------------------------------------------------
    // Account creation
    // -------------------------------------------------------------------------

    private static Account createAccount(Scanner input) {
        System.out.print("Enter your name: ");
        String name = input.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Error: Name cannot be empty.");
            return null;
        }

        int accNum;
        try {
            System.out.print("Enter account number: ");
            accNum = input.nextInt();
            input.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Error: Account number must be numeric.");
            return null;
        }

        System.out.println("\nSelect account type:");
        System.out.println("  1. Checking  (service charge if balance drops below $1,000)");
        System.out.println("  2. Savings   (earns 4% annual interest, credited monthly)");
        System.out.print("Choice: ");

        int type;
        try {
            type = input.nextInt();
            input.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Error: Please enter 1 or 2.");
            return null;
        }

        double initialBalance;
        try {
            System.out.print("Enter opening balance: $");
            initialBalance = input.nextDouble();
            input.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Error: Balance must be a number.");
            return null;
        }

        if (initialBalance < 0) {
            System.out.println("Error: Opening balance cannot be negative.");
            return null;
        }

        if (type == 1) {
            return new CheckingAccount(name, accNum, initialBalance);
        } else if (type == 2) {
            return new SavingsAccount(name, accNum, initialBalance);
        } else {
            System.out.println("Unknown type — defaulting to Checking.");
            return new CheckingAccount(name, accNum, initialBalance);
        }
    }

    // -------------------------------------------------------------------------
    // Menu
    // -------------------------------------------------------------------------

    private static void printMenu(Account account) {
        System.out.printf("%n--- %s Account #%d | Balance: $%.2f ---%n",
                account.getAccountType(),
                account.getAccountNumber(),
                account.getBalance());
        System.out.println("1. Deposit");
        System.out.println("2. Withdraw");
        System.out.println("3. Apply Monthly Fees / Interest");
        System.out.println("4. View Full Statement");
        System.out.println("5. Check Balance");
        System.out.println("6. Exit");
    }

    // -------------------------------------------------------------------------
    // Transaction handlers
    // -------------------------------------------------------------------------

    private static void handleDeposit(Scanner input, Account account) {
        try {
            System.out.print("Enter deposit amount: $");
            double amount = input.nextDouble();
            input.nextLine();
            if (!account.deposit(amount)) {
                System.out.println("Error: Deposit amount must be greater than zero.");
            } else {
                System.out.printf("Deposited $%.2f successfully. New balance: $%.2f%n",
                        amount, account.getBalance());
            }
        } catch (InputMismatchException e) {
            input.nextLine();
            System.out.println("Error: Invalid amount.");
        }
    }

    private static void handleWithdrawal(Scanner input, Account account) {
        try {
            System.out.print("Enter withdrawal amount: $");
            double amount = input.nextDouble();
            input.nextLine();
            if (amount <= 0) {
                System.out.println("Error: Withdrawal amount must be greater than zero.");
            } else if (amount > account.getBalance()) {
                System.out.printf("Error: Insufficient funds. Available balance: $%.2f%n",
                        account.getBalance());
            } else if (!account.withdraw(amount)) {
                System.out.println("Error: Withdrawal failed.");
            } else {
                System.out.printf("Withdrew $%.2f successfully. New balance: $%.2f%n",
                        amount, account.getBalance());
            }
        } catch (InputMismatchException e) {
            input.nextLine();
            System.out.println("Error: Invalid amount.");
        }
    }
}
