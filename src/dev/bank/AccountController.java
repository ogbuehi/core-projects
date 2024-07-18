package dev.bank;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dev.bank.Database.updateDeposit;

public class AccountController {
    public static BankAccount bankAccount = new BankAccount();
//    public static ArrayList<BankAccount> bankAccounts = new ArrayList<>();

    public static void createAccount(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Enter Firstname: ");
        String first = scanner.next();
        System.out.println("Enter Lastname: ");
        String last = scanner.next();
        System.out.println("Enter phone number: ");
        String phone = scanner.next();
        if (!isValid(phone)) {
            System.out.println("Invalid");
            return;
        }
        System.out.println("Enter email: ");
        String mail = scanner.next();
        if (!validateEmail(mail)) {
            System.out.println("Invalid Input");
            return;
        }
        System.out.println("Enter Account Number(10-digit)");
        String num = scanner.nextLine();
        System.out.println("Enter 6-digit Pin");
        int pin = scanner.nextInt();


//        BankAccount bankAccount = new BankAccount();
        bankAccount.setFirstName(first);
        bankAccount.setLastName(last);
        bankAccount.setAccountNumber(num);
        bankAccount.setPhoneNumber(phone);
        bankAccount.setEmail(mail);
        bankAccount.setPin(pin);
        bankAccount.setAccountBalance(bankAccount.getAccountBalance());
        Database.addCustomer(bankAccount, conn);
        System.out.println("Account Activated Successfully!!");
        Database.getCustomer(num, pin, conn);

    }

    public static boolean isValid(String phoneNumber) {
        // Define a regular expression pattern for a valid Indian phone number
        // The pattern matches exactly 10 digits from start to end
        Pattern pattern = Pattern.compile("^\\d{11}$");
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    public static boolean validateEmail(String emailAddress) {
        String regexPattern = "^(.+)@(\\S+)$";
        return Pattern.compile(regexPattern).matcher(emailAddress).matches();
    }

    public static void viewBalance(Connection connection, Scanner scanner)
            throws SQLException {
        System.out.println("Enter Account Number to Check Balance");
        String accNum = scanner.nextLine();
        int pin = scanner.nextInt();
        Database.getCustomer(accNum, pin, connection);
    }

    public static void depositFunds(Connection connection, Scanner scanner)
            throws SQLException {
        System.out.println("Enter Account Number and Pin to Login to Account");
        String accNum = scanner.nextLine();
        int pin = scanner.nextInt();
        Database.getCustomer(accNum, pin,  connection);
        System.out.println("Enter Deposit Amount");
        double depositAmount = scanner.nextDouble();
        scanner.nextLine();
        updateDeposit( accNum,pin, depositAmount, connection);

        }

    public static void withdrawFunds(Connection connection, Scanner scanner)
            throws SQLException {
        System.out.println("Enter Account Number and Pin to Login to Account");
        String accNum = scanner.nextLine();
        int pin = scanner.nextInt();
        Database.getCustomer(accNum,pin, connection);
        System.out.println("Enter Withdrawal Amount");
        int withdrawAmount = scanner.nextInt();
        scanner.nextLine();
        System.out.println("You have made a withdrawal of "+withdrawAmount);
        Database.updateWithdraw(accNum,pin, withdrawAmount, connection);
    }
    public static void transferFunds(Connection connection, Scanner scanner)
            throws SQLException {
        System.out.println("Enter Account Number and Pin to Login to Account");
        String senderAccNum = scanner.nextLine();
        int pin = scanner.nextInt();
        Database.getCustomer(senderAccNum, pin, connection);
        System.out.println("Enter recipient's Account Number");
        String receiverAccNum = scanner.nextLine();
        System.out.println("Enter Transfer Amount");
        int amount = scanner.nextInt();
        scanner.nextLine();
        Database.updateTransfer(senderAccNum,pin, amount, connection);
        updateDeposit(receiverAccNum, pin, amount,connection);
        System.out.println("you have made a transfer of #"+amount+" to "+receiverAccNum);
    }
    public static void transactionHistory(Connection connection, Scanner scanner){
         System.out.println("Enter Account Number and Pin to Login to Account");
        String accNum = scanner.nextLine();
        int pin = scanner.nextInt();
        Database.getCustomer(accNum, pin, connection);
        scanner.nextLine();
        Database.getHistory(accNum, pin, connection);
    }
    }





