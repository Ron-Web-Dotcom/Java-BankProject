/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bankproject;
import java.util.Scanner;
/**
 *
 * @author RON TAYLOR
 */
public class BankProject {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String name;
        int acc_num;
        double old_bal, new_bal, mon_dep, mon_wit;

        Scanner input = new Scanner(System.in);
        System.out.println("Please Enter your name");
        name = input.nextLine();
        System.out.println("Please Enter your Account Number");
        acc_num = input.nextInt();
        System.out.println("Please Enter your Old Balance: $");
        old_bal = input.nextDouble();
        System.out.println("Please Enter your Deposit Amount: $");
        mon_dep = input.nextDouble();
        System.out.println("Please Enter your Withdrawl Amount: $");
        mon_wit = input.nextDouble();
        new_bal = (old_bal + mon_dep) - mon_wit;

        System.out.println(name + ", your account number is " + acc_num);
        System.out.println("Your old balance was: $" + old_bal + ", you deposited $" + mon_dep + " this month and withdrew $" + mon_wit + ".");

        if (new_bal < 1000){
            new_bal = new_bal - 50;
            System.out.println("A $50 service charge has been applied.");
        }
        System.out.println("Your new balance is: $" + new_bal);
    }
    
}
