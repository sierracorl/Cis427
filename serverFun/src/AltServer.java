import java.net.*;
import java.io.*;
import java.sql.*;

public class AltServer
{
    //initialize socket and input stream
    private Socket          socket   = null;
    private ServerSocket    server   = null;
    private DataInputStream in       = null;
    private DataOutputStream out     = null;

    // constructor with port
    public AltServer(int port) {
        // starts server and waits for a connection
        try {
            server = new ServerSocket(port);
            System.out.println("Server started");

            System.out.println("Waiting for a client ...");

                while (!server.isClosed()) {


                    socket = server.accept();
                    System.out.println("Client accepted");

                    // takes input from the client socket
                    in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

                    // sends output from server to client socket
                    out = new DataOutputStream(socket.getOutputStream());

                    String line = "";

                    // reads message from client until "Over" is sent
                    while (!socket.isClosed()) {
                        try {
                            line = in.readUTF();
                            System.out.println("Received: " + line);

                            String[] command = line.split("\\s");

                            switch (command[0]) {
                                case "BUY" -> {
                                    out.writeUTF("200 OK");
                                    System.out.println("BUYING");
                                }
                                case "SELL" -> {
                                    out.writeUTF("200 OK");
                                    System.out.println("SELLING");
                                }
                                case "LIST" -> {
                                    out.writeUTF("200 OK");
                                    printStock(out);
                                }
                                case "BALANCE" -> {
                                    out.writeUTF("200 OK");
                                    findBalance(out);
                                }
                                case "QUIT" -> {
                                    out.writeUTF("200 OK");
                                    System.out.println("QUITING");
                                }
                                case "SHUTDOWN" -> {
                                    out.writeUTF("200 OK");
                                    System.out.println("SHUTTING DOWN");
                                    socket.close();
                                    in.close();
                                    out.close();
                                    server.close();
                                }
                                default -> {
                                    System.out.println("400 INVALID COMMAND");
                                    out.writeUTF("400 INVALID COMMAND");
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

    public static void userTable() {
        Connection c = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:stock.db");
            //System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS users " +
                    "(id int NOT_NULL AUTO_INCREMENT, " +
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

    public static void stockTable() {
        Connection c = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:stock.db");
            //System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS stocks " +
                    "(id int NOT_NULL AUTO_INCREMENT, " +
                    " stock_symbol varchar(4) NOT NULL, " +
                    " stock_name varchar(20) NOT NULL, " +
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

    public static void printUsers() {
        Connection c = null;
        Statement stmt = null;

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
                        " VALUES (1, 'John', 'Doe', 'JD', '1234', 100.00 );";
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

    public void printStock(DataOutputStream o) {
        Connection c = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:stock.db");
            c.setAutoCommit(false);
            //System.out.println("Opened database successfully");

            stmt = c.createStatement();
            ResultSet empty = stmt.executeQuery( "SELECT * FROM stocks;" );

            if (!empty.next()){
                System.out.println("No stocks owned");
                o.writeUTF("No stocks owned");
                o.flush();
            }

            ResultSet rs = stmt.executeQuery("SELECT * FROM stocks;");

            while (rs.next()) {
                int id = rs.getInt("id");
                String  symbol = rs.getString("stock_symbol");
                String  name = rs.getString("stock_name");
                double  balance = rs.getDouble("stock_balance");
                int  user_id = rs.getInt("user_id");

                o.writeUTF( id + " " + symbol + " " + name + balance + user_id);
                o.flush();

                System.out.println( "ID = " + id );
                System.out.println( "Stock Symbol = " + symbol );
                System.out.println( "Stock Name = " + name );
                System.out.println( "Stock Balance = " + balance );
                System.out.println( "User ID = " + user_id );
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

    public void findBalance(DataOutputStream o) {
        Connection c = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:stock.db");
            c.setAutoCommit(false);
            //System.out.println("Opened database successfully");

            stmt = c.createStatement();
            ResultSet empty = stmt.executeQuery( "SELECT * FROM users;" );

            ResultSet rs = stmt.executeQuery("SELECT * FROM users;");

            while (rs.next()) {
                double usd  = rs.getInt("usd_balance");

                o.writeUTF("$" + usd);
                o.flush();

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

    public static void main(String[] args)
    {
        connect();
        userTable();
        stockTable();
        printUsers();

        AltServer server = new AltServer(3339);
    }
}