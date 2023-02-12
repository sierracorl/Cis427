import java.net.*;
import java.io.*;
import java.util.StringTokenizer;

class Server{
    public static void main(String args[])throws Exception{
        ServerSocket ss=new ServerSocket(3339);
        /*Socket s = ss.accept();
        System.out.println("CLIENT CONNECTED");
        DataInputStream din = new DataInputStream(s.getInputStream());
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());
*/
        //String the holds the client's input.
        String clientInput="";

        //while(!clientInput.equals("SHUTDOWN")){
        while(!ss.isClosed()) {
            Socket s = ss.accept();
            System.out.println("CLIENT CONNECTED");
            DataInputStream din = new DataInputStream(s.getInputStream());
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());

            clientInput = din.readUTF();
            System.out.println("Received: " + clientInput);

            //Takes client string and tokenizes it for specific commands
            String command[] = clientInput.split("\\s");

            if (command[0].equals("BUY")) {
                dout.writeUTF("200 OK");
                dout.flush();
            }
            else if (command[0].equals("SELL")) {
                dout.writeUTF("200 OK");
                dout.flush();
            } else if (command[0].equals("LIST")) {
                dout.writeUTF("200 OK");
                dout.flush();
            } else if (command[0].equals("BALANCE")) {
                dout.writeUTF("200 OK");
                dout.flush();
            } else if (command[0].equals("QUIT")) {
                dout.writeUTF("200 OK");
                dout.flush();
                s.close();
            } else if (command[0].equals("SHUTDOWN")) {
                dout.writeUTF("200 OK");
                dout.flush();
                dout.writeUTF("200 OK");
                dout.flush();
            } else {
               dout.writeUTF("COMMAND NOT FOUND");
               dout.flush();
            }
        }//end while
    }//end main
}//end Server