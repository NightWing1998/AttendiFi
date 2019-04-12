package edu.somaiya.attendifi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    TextView txtView;
    Frame frame;
    SparseArray<Barcode> barcodes;
    Barcode thisCode;
    String ip;
    BarcodeDetector detector;
    String all = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMONPQRSTUVWXYZ0123456789`~!@$%^&*(){}[]:';\",./<>?";
    public String random(int len){
        String res = "";
        for(int i = 0;i < len;i++){
            res += all.charAt( (int) (Math.random()*all.length()) );
        }
        return res;
    };

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
        return null;
    };

    public void genBarcode(String text,ImageView img) throws WriterException{
        String rndm = random(64) + text + random(64 - text.length());

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        BitMatrix bitMatrix = multiFormatWriter.encode(rndm, BarcodeFormat.QR_CODE,1000,1000);
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        Bitmap myBitmap = barcodeEncoder.createBitmap(bitMatrix);
        img.setImageBitmap( myBitmap );

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtView = (TextView) findViewById(R.id.txtContent);

        ( (TextView) findViewById(R.id.ip) ).setText(getLocalIpAddress());

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

        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detector =
                        new BarcodeDetector.Builder(getApplicationContext())
                                .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                                .build();
                if(!detector.isOperational()){
                    txtView.setText(R.string.scanner_fail);
                    return;
                }
                Bitmap myBitmap = ((BitmapDrawable)myImageView.getDrawable()).getBitmap();
                frame = new Frame.Builder().setBitmap(myBitmap).build();
                barcodes = detector.detect(frame);
                thisCode = barcodes.valueAt(0);
                txtView.setText(thisCode.rawValue);

                // Generate new barcode
                try {
                    genBarcode(ip, myImageView);
                } catch (WriterException e){
                    e.printStackTrace();
                }
            }
        });
    }
}
