import java.io.*;
import java.net.*;

public class AltClient {
    // initialize socket and input output streams
    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream out = null;
    private BufferedReader br = null;

    // constructor to put ip address and port
    public AltClient(String address, int port) {
        // establish a connection
        try {
            socket = new Socket(address, port);
            System.out.println("Connected");

            // takes input from terminal
            input = new DataInputStream(socket.getInputStream());

            // sends output to the socket
            out = new DataOutputStream(socket.getOutputStream());

            //
            br = new BufferedReader(new InputStreamReader(System.in));
        }
        catch (UnknownHostException u) {
            System.out.println(u);
            return;
        }
        catch (IOException i) {
            System.out.println(i);
            return;
        }

        // string to read message from input
        String line = "";
        String serverCode = "";
        String dbOutput = "";

        // keep reading until "Over" is input
        while (!line.equals("QUIT")) {
            try {
                line = br.readLine();
                out.writeUTF(line);
                serverCode = input.readUTF();
                System.out.println("S: " + serverCode);

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

                dbOutput = input.readUTF();
                System.out.println("S: " + dbOutput);
            }
            catch (IOException i) {
                System.out.println(i);
            }
        }

    }

    public static void main(String args[])
    {
        AltClient client = new AltClient("127.0.0.1", 3339);
    }
}