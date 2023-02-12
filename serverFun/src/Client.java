import java.net.*;
import java.io.*;
class Client{
    public static void main(String args[])throws Exception{
        Socket s=new Socket("localhost",3333);
        DataInputStream din=new DataInputStream(s.getInputStream());
        DataOutputStream dout=new DataOutputStream(s.getOutputStream());
        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));

        String str="",str2="";
        int r = 0;
        while(!str.equals("stop")){
            str=br.readLine();
            dout.writeUTF(str);
            dout.flush();

            r = din.read();
            for (int i = 0; i < r; i++) {
                str2 = din.readUTF();
                System.out.println("Server says: " + str2);
            }
        }

        dout.close();
        s.close();
    }}