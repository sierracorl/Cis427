import java.net.*;
import java.io.*;
class Server{
    public static void main(String args[])throws Exception{
        ServerSocket ss=new ServerSocket(3333);
        Socket s = ss.accept();
        System.out.println("CLIENT CONNECTED");
        DataInputStream din = new DataInputStream(s.getInputStream());
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());

        String str="",str2="";

        while(!str.equals("SHUTDOWN")){

            str = din.readUTF();
            System.out.println("Received: " + str);

            if (str.equals("BUY")) {
                dout.writeUTF("200 OK");
                dout.flush();
            }
            else if (str.equals("SELL")) {
                dout.writeUTF("200 OK");
                dout.flush();
            } else if (str.equals("LIST")) {
                dout.writeUTF("200 OK");
                dout.flush();
            } else if (str.equals("BALANCE")) {
                dout.writeUTF("200 OK");
                dout.flush();
            } else if (str.equals("QUIT")) {
                dout.writeUTF("200 OK");
                dout.flush();
            } else if (str.equals("SHUTDOWN")) {
                dout.writeUTF("200 OK");
                dout.flush();
                dout.writeUTF("200 OK");
                dout.flush();
            } else {
               dout.writeUTF("COMMAND NOT FOUND");
               dout.flush();
            }
        }
    }
}