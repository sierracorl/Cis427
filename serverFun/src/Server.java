import java.net.*;
import java.io.*;
class Server{
    public static void main(String args[])throws Exception{
        ServerSocket ss=new ServerSocket(3333);
        Socket s=ss.accept();
        DataInputStream din=new DataInputStream(s.getInputStream());
        DataOutputStream dout=new DataOutputStream(s.getOutputStream());
        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));

        String str="",str2="";
        while(!str.equals("stop")){
            str=din.readUTF();
            System.out.println("client says: "+str);
            str2=br.readLine();
            dout.write(3);
            dout.writeUTF("You");
            dout.writeUTF("SUck");
            dout.writeUTF("DICK");
            dout.flush();
        }
        din.close();
        s.close();
        ss.close();
    }}