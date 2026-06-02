package bankproject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Entry point and console UI for Ron's Bank.
 *
 * <p>Provides a two-level interactive menu driven entirely from standard input:
 * <ol>
 *   <li><b>Main menu</b> — list all accounts, open a new account, or exit.</li>
 *   <li><b>Account menu</b> — deposit, withdraw, transfer, apply monthly
 *       fees/interest, view full statement, or check balance.</li>
 * </ol>
 *
 * <p><b>Startup:</b> Existing accounts are loaded from {@code accounts.dat}
 * before the first prompt is shown (if the file exists).
 *
 * <p><b>Shutdown:</b> All accounts and transaction histories are saved back to
 * {@code accounts.dat} automatically when the user selects Exit, so no data
 * is lost between sessions.
 *
 * <p><b>Error handling:</b> Every numeric input field is wrapped in an
 * {@link InputMismatchException} guard; bad input returns the user to the
 * current menu rather than crashing.
 *
 * @author RON TAYLOR
 */
public class BankProject {

    /**
     * Application entry point. Loads persisted data, runs the interactive
     * menu loop, then saves all accounts on exit.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        Bank bank = new Bank();
        try {
            bank.load();
            int loaded = bank.getAccounts().size();
            if (loaded > 0) {
                System.out.printf("Loaded %d account%s from file.%n", loaded, loaded == 1 ? "" : "s");
            }
        } catch (IOException e) {
            System.out.println("Note: Could not read saved data - starting fresh.");
        }

        try (Scanner input = new Scanner(System.in)) {
            System.out.println("\n=========================================");
            System.out.println("         Welcome to Ron's Bank          ");
            System.out.println("=========================================");

            boolean running = true;
            while (running) {
                printMainMenu(bank);
                System.out.print("Choose an option: ");

                int choice;
                try {
                    choice = input.nextInt();
                    input.nextLine();
                } catch (InputMismatchException e) {
                    input.nextLine();
                    System.out.println("Invalid input.\n");
                    continue;
                }

                switch (choice) {
                    case 1:
                        selectAndManageAccount(input, bank);
                        break;
                    case 2:
                        Account newAcc = createAccount(input, bank);
                        if (newAcc != null) {
                            bank.addAccount(newAcc);
                            System.out.printf("%nAccount #%d opened for %s. Welcome!%n",
                                    newAcc.getAccountNumber(), newAcc.getOwnerName());
                        }
                        break;
                    case 3:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please choose 1-3.\n");
                }
            }
        }

        // Auto-save on exit
        try {
            bank.save();
            System.out.println("\nAccounts saved. Goodbye!");
        } catch (IOException e) {
            System.out.println("\nWarning: Could not save account data - " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Main menu
    // -------------------------------------------------------------------------

    /**
     * Prints the top-level menu, showing how many accounts are on file.
     *
     * @param bank the bank whose account count is shown in the header
     */
    private static void printMainMenu(Bank bank) {
        int count = bank.getAccounts().size();
        System.out.printf("%n=== Ron's Bank  |  %d account%s on file ===%n",
                count, count == 1 ? "" : "s");
        System.out.println("1. Manage an account");
        System.out.println("2. Open a new account");
        System.out.println("3. Exit");
    }

    // -------------------------------------------------------------------------
    // Account selection
    // -------------------------------------------------------------------------

    /**
     * Lists all accounts with index numbers and prompts the user to pick one,
     * then delegates to {@link #manageAccount}.
     *
     * @param input the active Scanner for reading user input
     * @param bank  the bank registry to retrieve accounts from
     */
    private static void selectAndManageAccount(Scanner input, Bank bank) {
        List<Account> accounts = bank.getAccounts();
        if (accounts.isEmpty()) {
            System.out.println("\nNo accounts on file yet. Please open one first.\n");
            return;
        }

        System.out.println("\nAvailable accounts:");
        for (int i = 0; i < accounts.size(); i++) {
            Account a = accounts.get(i);
            System.out.printf("  %d. %-10s  #%-6d  %-20s  $%.2f%n",
                    i + 1,
                    a.getAccountType(),
                    a.getAccountNumber(),
                    a.getOwnerName(),
                    a.getBalance());
        }
        System.out.print("Select account number (or 0 to go back): ");

        int idx;
        try {
            idx = input.nextInt();
            input.nextLine();
        } catch (InputMismatchException e) {
            input.nextLine();
            System.out.println("Invalid input.\n");
            return;
        }

        if (idx == 0) return;
        if (idx < 1 || idx > accounts.size()) {
            System.out.println("Invalid selection.\n");
            return;
        }

        manageAccount(input, bank, accounts.get(idx - 1));
    }

    // -------------------------------------------------------------------------
    // Per-account menu
    // -------------------------------------------------------------------------

    /**
     * Runs the per-account action loop until the user chooses to go back.
     *
     * <p>Available actions: Deposit, Withdraw, Transfer, Apply Monthly
     * Fees/Interest, View Statement, Check Balance, Back.
     *
     * @param input   the active Scanner for reading user input
     * @param bank    the bank registry (needed for transfer target lookup)
     * @param account the account to operate on
     */
    private static void manageAccount(Scanner input, Bank bank, Account account) {
        boolean running = true;
        while (running) {
            System.out.printf("%n--- %s Account #%d  |  %s  |  Balance: $%.2f ---%n",
                    account.getAccountType(),
                    account.getAccountNumber(),
                    account.getOwnerName(),
                    account.getBalance());
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Transfer to another account");
            System.out.println("4. Apply Monthly Fees / Interest");
            System.out.println("5. View Full Statement");
            System.out.println("6. Check Balance");
            System.out.println("7. Back to main menu");
            System.out.print("Choose an option: ");

            int choice;
            try {
                choice = input.nextInt();
                input.nextLine();
            } catch (InputMismatchException e) {
                input.nextLine();
                System.out.println("Invalid input.\n");
                continue;
            }

            switch (choice) {
                case 1: handleDeposit(input, account);            break;
                case 2: handleWithdrawal(input, account);         break;
                case 3: handleTransfer(input, bank, account);     break;
                case 4:
                    System.out.println();
                    account.applyMonthlyFees();
                    break;
                case 5: account.printStatement();                 break;
                case 6:
                    System.out.printf("%nCurrent balance: $%.2f%n", account.getBalance());
                    break;
                case 7: running = false;                          break;
                default:
                    System.out.println("Invalid option. Please choose 1-7.\n");
            }
        }
    }

    // -------------------------------------------------------------------------
    // Account creation
    // -------------------------------------------------------------------------

    /**
     * Guides the user through the account-opening wizard.
     *
     * <p>Validated fields: non-blank name, numeric and unique account number,
     * account type 1 or 2, non-negative opening balance.
     *
     * @param input the active Scanner for reading user input
     * @param bank  the registry used to check for duplicate account numbers
     * @return the new Account, or {@code null} if any validation step fails
     */
    private static Account createAccount(Scanner input, Bank bank) {
        System.out.print("\nEnter your name: ");
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
            input.nextLine();
            System.out.println("Error: Account number must be numeric.");
            return null;
        }

        if (bank.isAccountNumberTaken(accNum)) {
            System.out.println("Error: Account number already in use.");
            return null;
        }

        System.out.println("Select account type:");
        System.out.println("  1. Checking  (service charge if balance < $1,000)");
        System.out.println("  2. Savings   (earns 4% annual interest, credited monthly)");
        System.out.print("Choice: ");

        int type;
        try {
            type = input.nextInt();
            input.nextLine();
        } catch (InputMismatchException e) {
            input.nextLine();
            System.out.println("Error: Please enter 1 or 2.");
            return null;
        }

        double initialBalance;
        try {
            System.out.print("Opening balance: $");
            initialBalance = input.nextDouble();
            input.nextLine();
        } catch (InputMismatchException e) {
            input.nextLine();
            System.out.println("Error: Balance must be a number.");
            return null;
        }

        if (initialBalance < 0) {
            System.out.println("Error: Opening balance cannot be negative.");
            return null;
        }

        if (type == 1) return new CheckingAccount(name, accNum, initialBalance);
        if (type == 2) return new SavingsAccount(name, accNum, initialBalance);
        System.out.println("Unknown type - defaulting to Checking.");
        return new CheckingAccount(name, accNum, initialBalance);
    }

    // -------------------------------------------------------------------------
    // Transaction handlers
    // -------------------------------------------------------------------------

    /**
     * Prompts for a deposit amount, applies it to the account, and prints a
     * confirmation or error message.
     *
     * @param input   the active Scanner for reading user input
     * @param account the account to credit
     */
    private static void handleDeposit(Scanner input, Account account) {
        try {
            System.out.print("Deposit amount: $");
            double amount = input.nextDouble();
            input.nextLine();
            if (!account.deposit(amount)) {
                System.out.println("Error: Amount must be greater than zero.");
            } else {
                System.out.printf("Deposited $%.2f. New balance: $%.2f%n",
                        amount, account.getBalance());
            }
        } catch (InputMismatchException e) {
            input.nextLine();
            System.out.println("Error: Invalid amount.");
        }
    }

    /**
     * Prompts for a withdrawal amount and debits the account. Guards against
     * zero/negative amounts and insufficient funds.
     *
     * @param input   the active Scanner for reading user input
     * @param account the account to debit
     */
    private static void handleWithdrawal(Scanner input, Account account) {
        try {
            System.out.print("Withdrawal amount: $");
            double amount = input.nextDouble();
            input.nextLine();
            if (amount <= 0) {
                System.out.println("Error: Amount must be greater than zero.");
            } else if (amount > account.getBalance()) {
                System.out.printf("Error: Insufficient funds. Available: $%.2f%n",
                        account.getBalance());
            } else {
                account.withdraw(amount);
                System.out.printf("Withdrew $%.2f. New balance: $%.2f%n",
                        amount, account.getBalance());
            }
        } catch (InputMismatchException e) {
            input.nextLine();
            System.out.println("Error: Invalid amount.");
        }
    }

    /**
     * Lists all accounts other than {@code from} as transfer targets and
     * executes the transfer via {@link Bank#transfer}.
     *
     * <p>The transfer is recorded as a WITHDRAWAL on the source account and
     * a DEPOSIT on the destination account.
     *
     * @param input the active Scanner for reading user input
     * @param bank  the registry used to discover transfer target accounts
     * @param from  the source account to transfer funds out of
     */
    private static void handleTransfer(Scanner input, Bank bank, Account from) {
        List<Account> targets = new ArrayList<>();
        for (Account a : bank.getAccounts()) {
            if (a.getAccountNumber() != from.getAccountNumber()) {
                targets.add(a);
            }
        }

        if (targets.isEmpty()) {
            System.out.println("No other accounts available to transfer to.\n");
            return;
        }

        System.out.println("\nTransfer to:");
        for (int i = 0; i < targets.size(); i++) {
            Account a = targets.get(i);
            System.out.printf("  %d. %-10s  #%-6d  %s  ($%.2f)%n",
                    i + 1,
                    a.getAccountType(),
                    a.getAccountNumber(),
                    a.getOwnerName(),
                    a.getBalance());
        }
        System.out.print("Select destination (or 0 to cancel): ");

        int idx;
        try {
            idx = input.nextInt();
            input.nextLine();
        } catch (InputMismatchException e) {
            input.nextLine();
            System.out.println("Invalid input.\n");
            return;
        }

        if (idx == 0) return;
        if (idx < 1 || idx > targets.size()) {
            System.out.println("Invalid selection.\n");
            return;
        }

        Account to = targets.get(idx - 1);

        double amount;
        try {
            System.out.printf("Amount to transfer (available: $%.2f): $", from.getBalance());
            amount = input.nextDouble();
            input.nextLine();
        } catch (InputMismatchException e) {
            input.nextLine();
            System.out.println("Error: Invalid amount.");
            return;
        }

        if (!bank.transfer(from, to, amount)) {
            System.out.printf("Transfer failed. Amount must be > $0.00 and <= $%.2f.%n",
                    from.getBalance());
        } else {
            System.out.printf("Transferred $%.2f to %s (#%d). Your new balance: $%.2f%n",
                    amount, to.getOwnerName(), to.getAccountNumber(), from.getBalance());
        }
    }
}
