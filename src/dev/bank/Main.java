package dev.bank;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) throws SQLException {
        var dataSource = new MysqlDataSource();
        dataSource.setServerName("localhost");
        dataSource.setPort(3306);
        dataSource.setDatabaseName("bank");
        dataSource.setUser(System.getenv("MYSQL_USER"));
        dataSource.setPassword(System.getenv("MYSQL_PASS"));
        Connection connection = dataSource.getConnection();
        char option;
        do {
            Scanner scanner = new Scanner(System.in);
            System.out.println("****WELCOME TO FIDELITY BANK APPLICATION****");
            System.out.println("\n SELECT OPERATION");
            System.out.println("(a) Create Account");
            System.out.println("(b) Deposit Funds");
            System.out.println("(c) View Balance");
            System.out.println("(d) Withdraw Funds");
            System.out.println("(e) Transfer Funds");
            System.out.println("(f) Transaction History")
            System.out.println("(g) Exit");

            option = scanner.next().charAt(0);
            switch (option) {
                case 'a':
                    System.out.println("-".repeat(50));
                    AccountController.createAccount(connection,scanner);
                    System.out.println("-".repeat(50));
                    break;
                case 'b':
                    System.out.println("-".repeat(50));
                    AccountController.depositFunds(connection,scanner);
                    System.out.println("-".repeat(50));
                    break;
                case 'c':
                    System.out.println("-".repeat(50));
                    AccountController.viewBalance(connection,scanner);
                    System.out.println("-".repeat(50));
                    break;
                case 'd':
                    System.out.println("-".repeat(50));
                    AccountController.withdrawFunds(connection,scanner);
                    System.out.println("-".repeat(50));
                    break;
                case 'e':
                    System.out.println("-".repeat(50));
                    AccountController.transferFunds(connection,scanner);
                    System.out.println("-".repeat(50));
                    break;
                case 'f':
                    System.out.println("-".repeat(50));
                    AccountController.transactionHistory(connection,scanner);
                    System.out.println("-".repeat(50));
                    break;
            }
        } while (option != 'g');
        System.out.println("Thanks for Banking with us");
    }
}
