package edu.somaiya.attendifi;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
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
    facultySocket fs = null;

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
//        Toast.makeText(this, "Server started", Toast.LENGTH_SHORT).show();
        try {
            fs = new facultySocket(new OnEventListener<String>() {
                @Override
                public void onSuccess(String result) {
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            });
//            fs = new facultySocket();
            fs.execute();
        } catch (Exception e){
//            e.printStackTrace();
            Log.i("server error",e.toString());
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

    public void close(View v){
        if(fs != null){
            fs.close();
            fs = null;
        }
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

interface OnEventListener<T> {
    void onSuccess(T object);
    void onFailure(Exception e);
}

class facultySocket extends AsyncTask<String,Integer,Integer> {
    ServerSocket ss = null;
    Socket s = null;
    private OnEventListener<String> callback;
    DataInputStream dis;
    private boolean close = true;
//    TODO: USE GET APPLICATION CONTEXT FOR TOAST
    facultySocket(OnEventListener callback){
        this.callback = callback;
    }
    void close(){
        try {
            close = false;
            if( s != null){
                s.close();
            }
            if (ss != null) {
                ss.close();
            }
            if( dis != null ){
                dis.close();
            }
//            callback.onSuccess("Close successfully");
            Log.i("Closed socket","Socket close successful");
        } catch (Exception e) {
//            result = -2;
            Log.i("Closing error", e.toString());
//            e.printStackTrace();
        }
    }

    @Override
    protected Integer doInBackground(String... ip){
        Integer result = 0;

        try{
            Log.i("Start Socket","Server started successfully");
            callback.onSuccess("Taking attendance");
            ss=new ServerSocket(6666);
            s=ss.accept();//establishes connection

            dis = new DataInputStream(s.getInputStream());
            String str = "";
            while (close){
                str=(String)dis.readUTF();
                Log.i("Received",str);
                callback.onSuccess(str);
            }

//            Toast.makeText(this, str, Toast.LENGTH_LONG).show();
//            dis.close();

            result = 0;
            return result;

        } catch (Exception e){
            result = -1;
            Log.i("Socket error",e.toString());
            callback.onFailure(e);
//            e.printStackTrace();
            return result;
        }

//        return result;
    }

    protected void onProgressUpdate(Integer... progress) {

    }

    protected void onPostExecute(Integer result) {
        switch (result){
            case 0:
                Log.i("Success","Receiving successfull!!!");break;

            case -1:
                Log.i("Socket Error","Error");break;

            case -2:
                Log.i("Close error","Cannot close connection");break;
        }
    }
}