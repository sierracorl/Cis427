import java.io.*;
import java.net.*;
import java.util.Scanner;

public class AltClient {
    // initialize socket and input output streams
    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream out = null;
    private BufferedReader br = null;

    // constructor to put ip address and port
    public AltClient(String address, int port) {
        // Establish connection
        try {
            socket = new Socket(address, port);
            System.out.println("Connected");
            //Takes input from server
            input = new DataInputStream(socket.getInputStream());
            //Sends output to the socket
            out = new DataOutputStream(socket.getOutputStream());
            //Takes client input
            br = new BufferedReader(new InputStreamReader(System.in));
        } catch (IOException u) {
            System.out.println(u);
            return;
        }

        //String to read message from input
        String line = "";
        //int to hold number of messages server will send
        int numMessages = 0;
        //String to receive response code
        String serverOut = "";

        while (!line.equals("QUIT")) {
            try {
                line = br.readLine();
                out.writeUTF(line);


                numMessages = input.read();
                //System.out.println("#" + numMessages);

                for (int i = 0; i < numMessages; i++) {
                    serverOut = input.readUTF();
                    System.out.println("S: " + serverOut);
                }

                if (line.equals("QUIT")) {
                    try {
                        input.close();
                        out.close();
                        socket.close();
                        System.out.println("closed");
                    }
                    catch (IOException i) {
                        System.out.println(i);
                    }
                }
            }
            catch (IOException i) {
                System.out.println(i);
            }
        }
    }

    public static void main(String[] args)
    {
        Scanner in = new Scanner(System.in);
        String ip;
        int port;

        System.out.print("Enter IP: ");
        ip = in.nextLine();
        
        System.out.print("Enter port: ");
        port = Integer.parseInt(in.nextLine());

        //IP: 127.0.0.1 port: 3339
        AltClient client = new AltClient(ip, port);
    }
}