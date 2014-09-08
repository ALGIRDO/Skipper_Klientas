package skipper_klientas;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SkipperKlientas {
    public static String ServerIP;
    public static Socket socket;
    public static int port = 15123;
    public static String db_vieta;
    public static File db_failas;
    public static DataOutputStream dOut;
    public static byte[] siunciamasFailas;
    public static FileInputStream fin;
    public static BufferedInputStream bin;
    public static OutputStream os;
    public static int issiustibaitai;
    public static int blokas;
            
    public static void sendDatabase(String[] args){
        if(args.length != 0) {
            for(String s: args)
                System.out.println(s);
            if(args.length == 1){
                ServerIP = "127.0.0.1";   // pagal default jungiasi prie localhost
            } else {
                ServerIP = args[1];
            }
            try {
                socket = new Socket(ServerIP, port);
                System.out.println("Prisijungimas pavyko : " + socket);
                if (args.length == 3) {
                    db_vieta = args[2];
                } else {
                    // pagal default
                    db_vieta = System.getProperty("user.home");
                    db_vieta += "\\AppData\\Roaming\\Skype\\";
                    db_vieta += args[0];
                    db_vieta += "\\main.db";
                }
                db_failas = new File(db_vieta); // Failas siunciamas serveriui
                /*          uzklausos siuntimas           */
                dOut = new DataOutputStream(socket.getOutputStream());
                dOut.writeByte(1); // zinutes id
                dOut.writeUTF(args[0]); // skype abonento pavadinimas
                dOut.flush();
                /*          failas nuskaitomas i masyva  */
                siunciamasFailas = new byte[(int) db_failas.length()];
                fin = new FileInputStream(db_failas);
                bin = new BufferedInputStream(fin);
                bin.read(siunciamasFailas, 0, siunciamasFailas.length); // nuskaitomas failas
                System.out.println("Siuntimui paruosta baitu: " + siunciamasFailas.length );
                /*          failas nusiunciamas              */
                System.out.println("Failas siunciamas...");
                os = socket.getOutputStream();
                issiustibaitai = 0;
                blokas = 0;
                while (issiustibaitai < siunciamasFailas.length) {
                    if ((siunciamasFailas.length - issiustibaitai) >= 100) {
                        os.write(siunciamasFailas, (blokas * 100), 100);
                        issiustibaitai += 100;
                    } else {
                        os.write(siunciamasFailas, (blokas * 100), (siunciamasFailas.length - issiustibaitai));
                        issiustibaitai += siunciamasFailas.length - issiustibaitai;
                    }
                    os.flush();
                    blokas++;
                }
                socket.close();
                System.out.println("Failas persiustas");

            } catch (IOException e) {
                System.out.println("Nepavyko prisijungti prie severio.");
                System.out.println(e);
            }
        } else {
            System.out.println("Nenustatytas Skype vartotojas!!!\nRedaguokite Skipper.bat faila");
            System.out.println("Programa baigia darba.");
        }
    }
    public static void main(String[] args){
        sendDatabase(args);
    }
}
