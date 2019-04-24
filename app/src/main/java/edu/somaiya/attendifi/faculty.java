package edu.somaiya.attendifi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.DataInputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.StringTokenizer;

public class faculty extends AppCompatActivity {

    TextView txtView;
    String ip;

    int index = 0;
    String all = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMONPQRSTUVWXYZ0123456789`~!@$%^&*(){}[]:';\",./<>?";
    public String random(int len){
        String res = "";
        for(int i = 0;i < len;i++){
            res += all.charAt( (int) (Math.random()*all.length()) );
        }
        return res;
    };

    public void serve(){
        Toast.makeText(this, "Server started", Toast.LENGTH_SHORT).show();
        ServerSocket ss = null;
        Socket s = null;
        try{
            ss=new ServerSocket(6666);
            s=ss.accept();//establishes connection

            DataInputStream dis=new DataInputStream(s.getInputStream());

            String	str=(String)dis.readUTF();

            Toast.makeText(this, str, Toast.LENGTH_LONG).show();

            dis.close();

        } catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if( s != null){
                    s.close();
                }
                if (ss != null) {
                    ss.close();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public String getLocalIpAddress(){
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
            Log.i("IP Address", ex.toString());
        }
        return "NO.INTERNET.RIGHT.NOW";
    };

    public void genBarcode(String text,ImageView img) throws WriterException{
        String rndm = random(64) + text + random(63 - text.length()) + index;

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        BitMatrix bitMatrix = multiFormatWriter.encode(rndm, BarcodeFormat.QR_CODE,1000,1000);
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        Bitmap myBitmap = barcodeEncoder.createBitmap(bitMatrix);
        img.setImageBitmap( myBitmap );

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty);

        txtView = (TextView) findViewById(R.id.txtContent);

        try {
            ((TextView) findViewById(R.id.ip)).setText(getLocalIpAddress());
        } catch (Exception e){
            Toast.makeText(this, "No Internet Available", Toast.LENGTH_SHORT).show();
        }
        ip = "#";

        StringTokenizer ipformatted = new StringTokenizer(getLocalIpAddress(),".");

        ip+=ipformatted.nextToken()+"A"+ipformatted.nextToken()+"B"+ipformatted.nextToken()+"C"+ipformatted.nextToken()+"D";

        ip+="#";
        final ImageView myImageView = (ImageView) findViewById(R.id.imgview);

//        String text= random(64) + "#192A168B0C1D#" + random(64);     //encoding 192.168.0.1 IP in the string
//        String text = random(64) + ip + random(64-ip.length() );
        try {
            genBarcode(ip,myImageView);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        serve();
    }
}
