import java.net.*;
import java.io.*;
import java.sql.*;

public class Server
{

    public Server(int port) {
        // starts server and waits for a connection
        try {
            ServerSocket server = new ServerSocket(port);
            System.out.println("Server started");
            System.out.println("Waiting for a client ...");

            while (!server.isClosed()) {
                //Listens for client
                //initialize server, socket, input stream, and output stream
                Socket socket = server.accept();
                System.out.println("Client accepted\n");

                // takes input from the client socket
                DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

                // sends output from server to client socket
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                String line;

                // reads message from client until socket is closed
                while (!socket.isClosed()) {

                    try {
                        line = in.readUTF();
                        System.out.println("Received: " + line);
                        //Takes input from client and tokenizes it for individual commands.
                        String[] command = line.split("\\s");

                        switch (command[0]) {
                            case "BUY" -> {
                                if (command[1].length() != 4) {
                                    out.write(2);
                                    out.writeUTF("403 Message Format Error");
                                    out.writeUTF("Improper Stock Symbol Format");
                                }
                                else if (Integer.parseInt(command[4]) != 1) {
                                    out.write(2);
                                    out.writeUTF("403 Message Format Error");
                                    out.writeUTF("User does not exist");
                                }
                                buyStock(out, command[1], Double.parseDouble(command[2]),
                                        Double.parseDouble(command[3]), Integer.parseInt(command[4]));
                            }
                            case "SELL" -> {
                                if (command[1].length() != 4) {
                                    out.write(2);
                                    out.writeUTF("403 Message Format Error");
                                    out.writeUTF("Improper Stock Symbol Format");
                                }
                                else if (Integer.parseInt(command[4]) != 1) {
                                    out.write(2);
                                    out.writeUTF("403 Message Format Error");
                                    out.writeUTF("User does not exist");
                                }
                                sellStock(out, command[1], Double.parseDouble(command[2]),
                                        Double.parseDouble(command[3]), Integer.parseInt(command[4]));
                            }
                            case "LIST" -> //out.writeUTF("200 OK");
                                    printStock(out);
                            case "BALANCE" -> {
                                out.write(2);
                                out.writeUTF("200 OK");
                                double d = findBalance();
                                out.writeUTF("Balance for user John Doe: $" + d);
                            }
                            case "QUIT" -> {
                                out.write(1);
                                out.writeUTF("200 OK");
                                System.out.println("CLIENT QUIT");
                            }
                            case "SHUTDOWN" -> {
                                out.write(2);
                                out.writeUTF("200 OK");
                                out.writeUTF("SERVER SHUTTING DOWN");
                                System.out.println("SHUTTING DOWN");
                                socket.close();
                                in.close();
                                out.close();
                                server.close();
                            }
                            default -> {
                                System.out.println("INVALID COMMAND");
                                out.write(2);
                                out.writeUTF("400 ERROR");
                                out.writeUTF("INVALID COMMAND");
                            }
                        }
                    } catch (IOException i) {
                        System.out.println(i);
                        //System.out.println("Connection lost");
                        socket.close();
                        in.close();
                    }
                }
                System.out.println("Closing connection");

                // close connection
                socket.close();
                in.close();
            }
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
    }

    //Connects to database, if no database exists, one is created
    public static void connect() {
        Connection c = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:stock.db");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully\n");
    }

    //Checks table users, if one does not exist, it is created
    public static void userTable() {
        Connection c;
        Statement stmt;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:stock.db");
            //System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS users " +
                    "(ID INT PRIMARY_KEY NOT_NULL AUTO_INCREMENT, " +
                    " first_name varchar(255), " +
                    " last_name varchar(255), " +
                    " user_name varchar(255) NOT NULL, " +
                    " password varchar(255), " +
                    " usd_balance DOUBLE NOT NULL, " +
                    " PRIMARY KEY (id))";
            stmt.executeUpdate(sql);

            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Users table created successfully\n");
    }

    //Checks table stocks, if one does not exist, it is created
    public static void stockTable() {
        Connection c;
        Statement stmt;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:stock.db");
            //System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS stocks " +
                    "(id int PRIMARY_KEY NOT_NULL AUTO_INCREMENT, " +
                    " stock_symbol varchar(4) NOT NULL, " +
                    " stock_amount DOUBLE, " +
                    " stock_balance DOUBLE, " +
                    " user_id int, " +
                    " PRIMARY KEY (id), " +
                    " FOREIGN KEY (user_id) REFERENCES users (id))";
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Stock table created successfully\n");
    }

    //Prints a list of users and there key information, if there are none, a user is generated
    public static void printUsers() {
        Connection c;
        Statement stmt;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:stock.db");
            c.setAutoCommit(false);
            //System.out.println("Opened database successfully");

            stmt = c.createStatement();
            ResultSet empty = stmt.executeQuery( "SELECT * FROM users;" );

            if (!empty.next()){
                System.out.println("Generating User");
                String sql = "INSERT INTO users (id, first_name,last_name,user_name,password,usd_balance) " +
                        " VALUES (+1, 'John', 'Doe', 'JD', '1234', 100.00 );";
                stmt.executeUpdate(sql);
                c.commit();
                empty.close();
            }

            System.out.println("Printing Current Users");
            ResultSet rs = stmt.executeQuery("SELECT * FROM users;");

            while (rs.next()) {
                int id = rs.getInt("id");
                String  fName = rs.getString("first_name");
                String  lName = rs.getString("last_name");
                String  uName = rs.getString("user_name");
                String  pWord = rs.getString("password");
                double usd  = rs.getInt("usd_balance");

                System.out.println( "ID = " + id );
                System.out.println( "First name = " + fName );
                System.out.println( "Last name = " + lName );
                System.out.println( "User name = " + uName );
                System.out.println( "PASSWORD = " + pWord );
                System.out.println( "BALANCE = " + usd );
                System.out.println();
            }
            rs.close();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Operation done successfully\n");

    }

    //Prints a list of all stocks in the database
    public void printStock(DataOutputStream o) {
        Connection c;
        Statement stmt;
        int count = 0;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:stock.db");
            c.setAutoCommit(false);
            //System.out.println("Opened database successfully");
            stmt = c.createStatement();
            ResultSet empty = stmt.executeQuery( "SELECT * FROM stocks;" );

            if (!empty.next()){
                System.out.println("No stocks owned");
                o.write(2);
                o.writeUTF("200 OK");
                o.writeUTF("No stocks owned");
                empty.close();
                //o.flush();
            } else {

                ResultSet total = stmt.executeQuery("SELECT * FROM stocks;");

                while(total.next())
                    count++;

                ResultSet rs = stmt.executeQuery("SELECT * FROM stocks;");

                o.write(count + 1);
                o.writeUTF("The list of records in the Stocks database for user 1:");

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String symbol = rs.getString("stock_symbol");
                    double amount = rs.getDouble("stock_amount");
                    double balance = rs.getDouble("stock_balance");
                    int user_id = rs.getInt("user_id");

                    o.writeUTF(id + " " + symbol + " " + amount + " " + balance + " " + user_id);

                    System.out.println("ID = " + id);
                    System.out.println("Stock Symbol = " + symbol);
                    System.out.println("Stock Amount = " + amount);
                    System.out.println("Stock Balance = " + balance);
                    System.out.println("User ID = " + user_id);
                    System.out.println();

                }
                total.close();
                rs.close();
            }
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Operation done successfully\n");
    }

    //Finds the balance of a user
    public double findBalance() {
        Connection c;
        Statement stmt;
        double usd = 0;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:stock.db");
            c.setAutoCommit(false);
            System.out.println("Finding Balance");

            stmt = c.createStatement();
            ResultSet empty = stmt.executeQuery( "SELECT * FROM users;" );

            ResultSet rs = stmt.executeQuery("SELECT * FROM users;");

            while (rs.next()) {
                usd  = rs.getInt("usd_balance");
            }
            rs.close();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        //System.out.println("Operation done successfully\n");
        return usd;
    }

    //Adds a stock to the database
    public void buyStock(DataOutputStream o, String symbol, double amount, double price, int id) throws IOException {
        Connection c;
        PreparedStatement stmt;
        Statement search;
        double balance = findBalance();
        double cost = amount * price;

        if (cost <= balance) {
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection("jdbc:sqlite:stock.db");
                c.setAutoCommit(false);
                System.out.println("Preparing Buy Statement");

                search = c.createStatement();
                ResultSet rs = search.executeQuery( "SELECT * FROM stocks;" );

                while (rs.next()) {
                    String  sS = rs.getString("stock_symbol");
                    double  sA = rs.getDouble("stock_amount");
                    double  sB = rs.getDouble("stock_balance");

                    //Checks to see if stock exists, if it does, it is modified with new values
                    if (sS.equals(symbol)) {
                        String sql = "UPDATE stocks SET id, stock_amount = ?, stock_balance = ? WHERE stock_symbol = ?";
                        stmt = c.prepareStatement(sql);

                        stmt.setDouble(1, +1);
                        stmt.setDouble(2, sA + amount);
                        stmt.setDouble(3, price);
                        stmt.setString(4, symbol);
                        stmt.executeUpdate();

                        String update = "UPDATE users set usd_balance = ? where ID = 1;";
                        stmt = c.prepareStatement(update);

                        stmt.setDouble(1, balance - cost);

                        stmt.executeUpdate();

                        stmt.close();
                        c.commit();
                        c.close();

                        System.out.println("Bought existing Stock");
                        o.write(2);
                        o.writeUTF("200 OK");
                        o.writeUTF("BOUGHT: New balance: " + amount + " " + symbol + ". USD balance " + (balance - cost));
                        return;
                    }

                }

                //If stock does not exist, then a new stock is added to the stock table
                String sql = "INSERT INTO stocks (stock_symbol,stock_amount,stock_balance,user_id) " +
                        "VALUES (?, ?, ?, ?);";
                stmt = c.prepareStatement(sql);

                stmt.setString(1, symbol);
                stmt.setDouble(2, amount);
                stmt.setDouble(3, price);
                stmt.setInt(4, id);
                stmt.executeUpdate();

                String update = "UPDATE users set usd_balance = ? where ID = 1;";
                stmt = c.prepareStatement(update);

                stmt.setDouble(1, balance - cost);

                stmt.executeUpdate();

                stmt.close();
                c.commit();
                c.close();

            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
            System.out.println("Bought new Stock");
            o.write(2);
            o.writeUTF("200 OK");
            o.writeUTF("BOUGHT: New balance: " + amount + " " + symbol + ". USD balance " + (balance - cost));
        } else {
            System.out.println("Not Enough Balance");
            o.write(2);
            o.writeUTF("400 ERROR");
            o.writeUTF("Note Enough Balance");
        }
    }

    //Sells stock from
    public void sellStock(DataOutputStream o, String symbol, double amount, double price, int id) throws IOException {
        Connection c;
        PreparedStatement stmt;
        Statement search;
        Statement findTotal;
        double balance = findBalance();
        double cost = amount * price;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:stock.db");
            c.setAutoCommit(false);
            System.out.println("Preparing sell statement");

            int count = 0;
            int numStocks = 0;

            findTotal = c.createStatement();
            ResultSet getCount = findTotal.executeQuery("SELECT * FROM stocks;");

            while (getCount.next())
                count++;
            getCount.close();

            search = c.createStatement();
            ResultSet rs = search.executeQuery( "SELECT * FROM stocks;" );

            while (rs.next()) {
                String sS = rs.getString("stock_symbol");
                double sA = rs.getDouble("stock_amount");
                double sB = rs.getDouble("stock_balance");

                if (sS.equals(symbol)) {
                    System.out.println("Found Match");
                    if (sA > amount) {

                        System.out.println("Less than owned amount");
                        String sql = "UPDATE stocks SET stock_amount = ?, stock_balance = ? WHERE stock_symbol = ?";
                        stmt = c.prepareStatement(sql);

                        stmt.setDouble(1, (sA - amount));
                        stmt.setDouble(2, cost);
                        stmt.setString(3, symbol);
                        stmt.executeUpdate();
                        c.commit();

                        String update = "UPDATE users set usd_balance = ? where ID = 1;";
                        stmt = c.prepareStatement(update);

                        stmt.setDouble(1, balance + cost);
                        stmt.executeUpdate();
                        c.commit();

                        System.out.println("Sell Transaction Complete");
                        o.write(2);
                        o.writeUTF("200 OK");
                        o.writeUTF("SOLD: New balance: " + (sA - amount) + " " + symbol + ". USD balance " + (balance + cost));

                    } else if (sA == amount) {
                        System.out.println("Same as owned amount");
                        String sql = "DELETE FROM stocks WHERE stock_symbol = ?";
                        stmt = c.prepareStatement(sql);

                        stmt.setString(1, symbol);
                        stmt.executeUpdate();
                        c.commit();

                        String update = "UPDATE users set usd_balance = ? where ID = 1;";
                        stmt = c.prepareStatement(update);

                        stmt.setDouble(1, balance + cost);
                        stmt.executeUpdate();
                        c.commit();

                        System.out.println("Sell Transaction Complete");
                        o.write(2);
                        o.writeUTF("200 OK");
                        o.writeUTF("SOLD: New balance: " + (sA - amount) + " " + symbol + ". USD balance " + (balance + cost));

                    } else {
                        System.out.println("Exceeds Amount");
                        o.write(2);
                        o.writeUTF("400 ERROR");
                        o.writeUTF("EXCEED STOCK AMOUNT");

                    }
                } else
                    numStocks++;
            }

            rs.close();
            c.close();

            if (numStocks == count) {
                System.out.println("No match");
                o.write(2);
                o.writeUTF("400 ERROR");
                o.writeUTF("STOCK DOES NOT EXIST");
                c.close();
            }

        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    //main
    public static void main(String[] args)
    {
        connect();
        userTable();
        stockTable();
        printUsers();

        Server server = new Server(3339);
    }
}