import java.net.*;
import java.io.*;
class Client{
    public static void main(String args[])throws Exception{
        Socket s=new Socket("localhost",3333);
        DataInputStream din=new DataInputStream(s.getInputStream());
        DataOutputStream dout=new DataOutputStream(s.getOutputStream());
        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));

        if (s.isConnected())
            System.out.println("CONNECTED TO SERVER");

        String str="",str2="";
        while(!str.equals("QUIT")){
            str=br.readLine();
            dout.writeUTF(str);
            dout.flush();
            str2=din.readUTF();
            System.out.println("Server says: "+str2);
        }

        dout.close();
        s.close();
    }}