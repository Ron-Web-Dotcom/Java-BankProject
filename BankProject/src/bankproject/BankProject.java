package bankproject;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * @author RON TAYLOR
 */
public class BankProject {

    public static void main(String[] args) {
        try (Scanner input = new Scanner(System.in)) {

            System.out.print("Please enter your name: ");
            String name = input.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Error: Name cannot be empty.");
                return;
            }

            System.out.print("Please enter your account number: ");
            int acc_num = input.nextInt();

            System.out.print("Please enter your old balance: $");
            double old_bal = input.nextDouble();

            System.out.print("Please enter your deposit amount: $");
            double mon_dep = input.nextDouble();
            if (mon_dep < 0) {
                System.out.println("Error: Deposit amount cannot be negative.");
                return;
            }

            System.out.print("Please enter your withdrawal amount: $");
            double mon_wit = input.nextDouble();
            if (mon_wit < 0) {
                System.out.println("Error: Withdrawal amount cannot be negative.");
                return;
            }

            double new_bal = (old_bal + mon_dep) - mon_wit;

            System.out.println("\n--- Account Summary ---");
            System.out.println(name + ", your account number is " + acc_num);
            System.out.printf("Your old balance was: $%.2f, you deposited $%.2f this month and withdrew $%.2f.%n",
                    old_bal, mon_dep, mon_wit);

            if (mon_wit > old_bal + mon_dep) {
                System.out.println("Warning: Withdrawal exceeded available funds. Account is overdrawn.");
            }

            if (new_bal < 1000) {
                new_bal -= 50;
                System.out.println("A $50.00 service charge has been applied.");
            }

            System.out.printf("Your new balance is: $%.2f%n", new_bal);

        } catch (InputMismatchException e) {
            System.out.println("Error: Invalid input. Please enter numeric values for account number and amounts.");
        }
    }

}
