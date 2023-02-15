import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    public Client(String address, int port) {

        Socket socket;
        DataInputStream input;
        DataOutputStream out;
        BufferedReader br;

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
        int numMessages;

        //String to receive response code
        String serverOut;

        while (!line.equals("QUIT")) {
            try {
                line = br.readLine();
                out.writeUTF(line);

                numMessages = input.read();

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
        Client client = new Client(ip, port);
    }
}
