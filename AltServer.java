import java.net.*;
import java.io.*;

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

                            String command[] = line.split("\\s");

                            switch (command[0]) {
                                case "BUY":
                                    out.writeUTF("200 OK");
                                    System.out.println("BUYING");
                                    break;
                                case "SELL":
                                    out.writeUTF("200 OK");
                                    System.out.println("SELLING");
                                    break;
                                case "LIST":
                                    out.writeUTF("200 OK");
                                    System.out.println("LISTING");
                                    break;
                                case "BALANCE":
                                    out.writeUTF("200 OK");
                                    System.out.println("BALANCE");
                                    break;
                                case "QUIT":
                                    out.writeUTF("200 OK");
                                    System.out.println("QUITING");
                                    break;
                                case "SHUTDOWN":
                                    out.writeUTF("200 OK");
                                    System.out.println("SHUTTING DOWN");
                                    break;
                                default:
                                    System.out.println("400 INVALID COMMAND");
                            }

                        } catch (IOException i) {
                            System.out.println(i);
                            System.out.println("Connection lost");
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

    public static void main(String args[])
    {
        AltServer server = new AltServer(3339);
    }
}