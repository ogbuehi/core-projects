package dev.bank;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Database {
//    private static final ArrayList<BankAccount> bankAccounts = new ArrayList<>();
    private static final  BankAccount account = new BankAccount();

    private static boolean printRecords(ResultSet resultSet) throws SQLException {


        boolean foundData = false;
        var meta = resultSet.getMetaData();

        System.out.println("===================");

        for (int i = 1; i <= meta.getColumnCount() - 1; i++) {
            System.out.printf("%-20s", meta.getColumnName(i).toUpperCase());
        }
        System.out.println();

        while (resultSet.next()) {
            for (int i = 1; i <= meta.getColumnCount() - 1; i++) {

                account.setFirstName(resultSet.getString("firstname"));
                account.setLastName(resultSet.getString("lastname"));
                account.setPhoneNumber(resultSet.getString("phone"));
                account.setEmail(resultSet.getString("email"));
                account.setAccountNumber(resultSet.getString("account_number"));
//                account.setPin(Long.parseLong(resultSet.getString("pin")));
                account.setAccountBalance(Double.parseDouble(resultSet.getString("account_balance")));
                System.out.printf("%-20s", resultSet.getString(i));
            }
            System.out.println();
            foundData = true;
        }
        return foundData;
    }

    public static void addCustomer(BankAccount account, Connection conn)
            throws SQLException {
        String insert = "INSERT INTO bank.user_info(firstname, lastname, pin," +
                " account_number, phone, email, account_balance) VALUES (?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement = conn.prepareStatement(insert);
        preparedStatement.setString(1, account.getFirstName());
        preparedStatement.setString(2, account.getLastName());
        preparedStatement.setLong(3, account.getPin());
        preparedStatement.setString(4, account.getAccountNumber());
        preparedStatement.setString(5, account.getPhoneNumber());
        preparedStatement.setString(6, account.getEmail());
        preparedStatement.setDouble(7, account.getAccountBalance());
        int insertedCount = preparedStatement.executeUpdate();
//        bankAccounts.add(account);
        if (insertedCount > 0) {
            System.out.println("success");
        } else {
            System.out.println("failed");
        }
    }

    public static void getCustomer(String accNum, int pin, Connection conn) throws SQLException {
        String get = "SELECT * FROM bank.user_info WHERE account_number = ? AND pin = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(get);
        preparedStatement.setString(1, accNum);
        preparedStatement.setInt(2,pin);
        ResultSet resultSet = preparedStatement.executeQuery();
        printRecords(resultSet);
    }

    public static boolean updateDeposit(String accNum, int pin, double amount,
                                        Connection connection) throws SQLException {
        String update = "UPDATE bank.user_info SET account_balance = ? WHERE account_number = ?";
        String get = "SELECT account_balance FROM bank.user_info WHERE account_number = ?";
        try (PreparedStatement checkAccountExists = connection.prepareStatement(get)) {
            checkAccountExists.setString(1, accNum);

            try (ResultSet RS = checkAccountExists.executeQuery()) {
                if (RS.next()) {
                    double currentBal = RS.getDouble("account_balance");
                    amount += currentBal;

                    try (PreparedStatement stmt = connection.prepareStatement(update)){

                        stmt.setDouble(1, amount);
                        stmt.setString(2, accNum);

                         stmt.executeUpdate();
                        getCustomer(accNum,pin, connection);
                        System.out.println("success!!");
                        insertTransactions(accNum,pin,amount,connection);
                    }
                } else {
                    System.out.println("Invalid Account Number");
                }
            }
        }
        return true;
    }

    public static boolean updateWithdraw(String accNum, int pin, double amount, Connection connection)
            throws SQLException {
        String update = "UPDATE bank.user_info SET account_balance = ? WHERE account_number = ?";
        String get = "SELECT account_balance FROM bank.user_info WHERE account_number = ?";
        try (PreparedStatement checkAccountExists = connection.prepareStatement(get)) {
            checkAccountExists.setString(1, accNum);

            try (ResultSet RS = checkAccountExists.executeQuery()) {
                if (RS.next()) {
                    double currentBal = RS.getDouble("account_balance");
                    if(currentBal - amount < 0){
                        System.out.println("Insufficient Funds! You only have #" +
                        currentBal + " in your account");
                    }
                    amount = currentBal - amount;

                    try (PreparedStatement stmt = connection.prepareStatement(update)){

                        stmt.setDouble(1, amount);
                        stmt.setString(2, accNum);
                        stmt.executeUpdate();
                        getCustomer(accNum,pin, connection);
                        System.out.println("success!!");
                        System.out.println("New balance is #"+amount);
                        insertTransactions(accNum,pin,amount,connection);
                    }
                } else {
                    System.out.println("Invalid Account Number");
                }
            }
        }

        return true;
    }
    public static void updateTransfer(String senderAccNum,int pin,
                                      double amount, Connection connection) throws SQLException {
        String update = "UPDATE bank.user_info SET account_balance = ? WHERE account_number = ?";
        String get = "SELECT account_balance FROM bank.user_info WHERE account_number = ?";
        try (PreparedStatement checkAccountExists = connection.prepareStatement(get)) {
            checkAccountExists.setString(1, senderAccNum);

            try (ResultSet RS = checkAccountExists.executeQuery()) {
                if (RS.next()) {
                    double currentBal = RS.getDouble("account_balance");
                    if(currentBal - amount < 0){
                        System.out.println("Insufficient Funds! You only have #" +
                                currentBal + " in your account");
                        return;
                    }

                    amount = currentBal - amount;
                    try (PreparedStatement stmt = connection.prepareStatement(update)){
                        stmt.setDouble(1, amount);
                        stmt.setString(2, senderAccNum);
                        stmt.executeUpdate();
                        getCustomer(senderAccNum,pin, connection);
                        System.out.println("success!!");
                        insertTransactions(senderAccNum,pin,amount,connection);
                    }
                } else {
                    System.out.println("Invalid Account Number");
                }
            }
        }
    }
    public static void insertTransactions(String accNum,int pin, double amount, Connection conn)
            throws SQLException {
        String insert = "INSERT INTO bank.transactions(id, operation," +
                " transaction_time, amount, transaction_status) VALUES (?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(insert)) {
            stmt.setString(1, accNum);
            stmt.setString(2, updateWithdraw(accNum, pin, amount, conn) ? Operation.WITHDRAW.name()
                    : updateDeposit(accNum, pin, amount, conn) ? Operation.DEPOSIT.name()
                    : Operation.TRANSFER.name());
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setDouble(4, amount);
        }
    }
    public static void getHistory(String accNum, int pin, Connection connection) throws SQLException {
        String get = "SELECT * FROM bank.transactions WHERE accNum = ? AND pin = ?";
        PreparedStatement statement = connection.prepareStatement(get);
        statement.setString(1,accNum);
        statement.setInt(2,pin);
        ResultSet rs = statement.executeQuery();
        printRecords(rs);

    }
    public static boolean printHistory(ResultSet resultSet) throws SQLException {
        boolean foundData = false;
        var meta = resultSet.getMetaData();

        System.out.println("===================");

        for (int i = 1; i <= meta.getColumnCount() - 1; i++) {
            System.out.printf("%-20s", meta.getColumnName(i).toUpperCase());
        }
        System.out.println();

        while (resultSet.next()) {
            for (int i = 1; i <= meta.getColumnCount() - 1; i++) {
                resultSet.getString("operation");
                resultSet.getString("transaction_time");
                resultSet.getString("amount");
                resultSet.getString("transaction_status");
                System.out.printf("%-20s", resultSet.getString(i));
            }
        }
        return foundData;
    }

    }






