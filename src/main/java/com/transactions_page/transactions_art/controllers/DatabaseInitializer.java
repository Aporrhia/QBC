package com.transactions_page.transactions_art.controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class DatabaseInitializer {

    public void initializeDatabase() {
        try {
            // Replace with your database connection details
            String url = "jdbc:mysql://localhost:3306/transactions?createDatabaseIfNotExist=true";
            String username = "root";
            String password = "0000";

            // Establish a connection
            Connection conn = DriverManager.getConnection(url, username, password);

            // Create a statement
            Statement stmt = conn.createStatement();

            // Create table SQL query
            String createTableSql = "CREATE TABLE IF NOT EXISTS Country (country_id INT AUTO_INCREMENT PRIMARY KEY, country_name VARCHAR(50), country_code VARCHAR(4), country_phone_len INT, country_phone_code INT)";
            stmt.execute(createTableSql);

            // Create Bank table SQL query
            String createBankTableSql = "CREATE TABLE IF NOT EXISTS Bank (bank_id INT AUTO_INCREMENT PRIMARY KEY, bank_name VARCHAR(50), bank_num VARCHAR(15), country_id INT, FOREIGN KEY (country_id) REFERENCES Country(country_id))";
            stmt.execute(createBankTableSql);
            
            // Create Account table SQL query
            String createAccountTableSql = "CREATE TABLE IF NOT EXISTS Account (acc_id INT AUTO_INCREMENT PRIMARY KEY, acc_fname VARCHAR(50), acc_lname VARCHAR(50), acc_email VARCHAR(100), acc_phone VARCHAR(20), acc_balance DOUBLE, acc_is_active BOOLEAN, acc_iban VARCHAR(40), acc_password VARCHAR(100), accdob DATETIME(6), status ENUM('active', 'inactive'), acc_number BIGINT, bank_id INT, country_id INT, acc_creation_date DATE, acc_updated_date DATE, FOREIGN KEY (bank_id) REFERENCES Bank(bank_id), FOREIGN KEY (country_id) REFERENCES Country(country_id))";
            stmt.execute(createAccountTableSql);
            
            // Create Card table SQL query
            String createCardTableSql = "CREATE TABLE IF NOT EXISTS Card (card_id INT AUTO_INCREMENT PRIMARY KEY, card_branch ENUM('MasterCard','Visa'), expiration_date DATETIME(6), card_number VARCHAR(255), status ENUM('active','inactive'), card_type ENUM('debit','credit'), account_id INT, FOREIGN KEY (account_id) REFERENCES Account(acc_id))";
            stmt.execute(createCardTableSql);

            // Create Transaction table SQL query
            String createTransactionTableSql = "CREATE TABLE IF NOT EXISTS Transaction (tr_id INT AUTO_INCREMENT PRIMARY KEY, amount DOUBLE, tr_descr VARCHAR(255), tr_status ENUM('Pending','Completed','Failed'), tr_date DATETIME(6), recipient_acc_id INT, recipient_card_id INT, sender_acc_id INT, sender_card_id INT, FOREIGN KEY (recipient_acc_id) REFERENCES Account(acc_id), FOREIGN KEY (recipient_card_id) REFERENCES Card(card_id), FOREIGN KEY (sender_acc_id) REFERENCES Account(acc_id), FOREIGN KEY (sender_card_id) REFERENCES Card(card_id))";
            stmt.execute(createTransactionTableSql);

            // Create Notes table SQL query
            String createNotesTableSql = "CREATE TABLE IF NOT EXISTS Notes (note_id INT AUTO_INCREMENT PRIMARY KEY, description VARCHAR(255), account_id INT, FOREIGN KEY (account_id) REFERENCES Account(acc_id))";
            stmt.execute(createNotesTableSql);

            // Check if table is empty
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Country");
            rs.next();
            int rowCount = rs.getInt(1);
            // If table is empty, insert values
            if (rowCount == 0) {
                
                String insertCountrySql = "INSERT INTO Country (country_id, country_name, country_code, country_phone_len, country_phone_code) VALUES " +
                    "(1, 'United States', 'US', 10, 1), " +
                    "(2, 'United Kingdom', 'UK', 11, 44), " +
                    "(3, 'Canada', 'CA', 10, 1), " +
                    "(4, 'Australia', 'AU', 9, 61), " +
                    "(5, 'Germany', 'DE', 11, 49), " +
                    "(6, 'France', 'FR', 10, 33), " +
                    "(7, 'Italy', 'IT', 10, 39), " +
                    "(8, 'Spain', 'ES', 9, 34), " +
                    "(9, 'Japan', 'JP', 11, 81), " +
                    "(10, 'China', 'CN', 11, 86), " +
                    "(11, 'India', 'IN', 10, 91), " +
                    "(12, 'Brazil', 'BR', 11, 55), " +
                    "(13, 'Mexico', 'MX', 10, 52), " +
                    "(14, 'Russia', 'RU', 10, 7), " +
                    "(15, 'South Africa', 'ZA', 9, 27)";
                
                stmt.execute(insertCountrySql);
            }
        
            // Check if Bank table is empty
            rs = stmt.executeQuery("SELECT COUNT(*) FROM Bank");
            rs.next();
            rowCount = rs.getInt(1);

            // If Bank table is empty, insert values
            if (rowCount == 0) {
                String insertBankSql = "INSERT INTO Bank (bank_id, bank_name, bank_num, country_id) VALUES " +
                "(1, 'Bank of America', 'BOFAUS3N', 1), " +
                "(2, 'Chase Bank', 'CHASUS33', 1), " +
                "(3, 'Barclays Bank', 'BARCGB22', 2), " +
                "(4, 'Royal Bank of Canada', 'ROYCCAT2', 3), " +
                "(5, 'Commonwealth Bank of Australia', 'CTBAAU2S', 4), " +
                "(6, 'Deutsche Bank', 'DEUTDEFF', 5), " +
                "(7, 'BNP Paribas', 'BNPAFRPP', 6), " +
                "(8, 'UniCredit', 'UNCRITMM', 7), " +
                "(9, 'Santander Bank', 'BSCHESMM', 8), " +
                "(10, 'Mizuho Bank', 'MHCBJPJT', 9), " +
                "(11, 'Industrial and Commercial Bank of China', 'ICBKCNBJ', 10), " +
                "(12, 'State Bank of India', 'SBININBBFXD', 11), " +
                "(13, 'Banco do Brasil', 'BRASBRRJ', 12), " +
                "(14, 'BBVA', 'BBVAESMM', 13), " +
                "(15, 'Sberbank', 'SABRRUMMXXX', 14), " +
                "(16, 'Standard Bank', 'SBZAZAJJ', 15)";
                stmt.execute(insertBankSql);
            }
                
            // Check if Account table is empty
            rs = stmt.executeQuery("SELECT COUNT(*) FROM Account");
            rs.next();
            rowCount = rs.getInt(1);

            // If Account table is empty, insert default values
            if (rowCount == 0) {
                String insertAccountSql = "INSERT INTO Account (acc_id, acc_fname, acc_lname, acc_email, acc_phone, acc_balance, acc_iban, acc_password, bank_id, country_id, accdob, acc_creation_date, acc_updated_date, status, acc_number) VALUES " +
                "(1, 'Joe', 'Smith', 'j.smith@gmail.com', '+34455234195', 276, 'ES30BNPAFRPP199905151683', 'dneqwu*Yh3n', 7, 8, '1999-02-12 00:00:00.000000', '2024-05-15 11:09:22.706000', '2024-05-15 11:09:22.706000', 'active', 199905151683), " +
                "(2, 'Patrick', 'Green', 'patrick.g24@yahoo.com', '+4934624000972', 1500, 'DE28BARCGB22198405158307', 'fewfHU&yhr32hn', 3, 5, '1984-05-24 00:00:00.000000', '2024-05-15 11:11:19.061000', '2024-05-15 11:11:19.061000', 'active', 198405158307), " +
                "(3, 'Mike', 'Freyl', 'mike8888@gmail.com', '+4444572573431', 91, 'UK62SBZAZAJJ200105158814', ':?nfeh(3jdbyT', 16, 2, '2001-04-07 00:00:00.000000', '2024-05-15 11:12:49.042000', '2024-05-15 11:12:49.042000', 'active', 200105158814)";
                // Print out the SQL statement
                System.out.println(insertAccountSql);

                // Execute the statement and check the return value
                boolean result = stmt.execute(insertAccountSql);
                System.out.println("Statement executed successfully: " + result);
            }
            // Close the connection
            conn.close();
        } catch (Exception e) {
            // Print the stack trace and rethrow the exception
            e.printStackTrace();
        }
    }
}