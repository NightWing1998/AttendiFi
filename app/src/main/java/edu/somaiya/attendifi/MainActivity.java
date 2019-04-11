package edu.somaiya.attendifi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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

public class MainActivity extends AppCompatActivity {

    TextView txtView;
    Frame frame;
    SparseArray<Barcode> barcodes;
    Barcode thisCode;
    BarcodeDetector detector;
    Bitmap myBitmap;
    String all = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMONPQRSTUVWXYZ0123456789`~!@#$%^&*(){}[]:';\",./<>?";
    public String random(int len){
        String res = "";
        for(int i = 0;i < len;i++){
            res += all.charAt( (int) (Math.random()*all.length()) );
        }
        return res;
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtView = (TextView) findViewById(R.id.txtContent);

        final ImageView myImageView = (ImageView) findViewById(R.id.imgview);

        String text= random(20) + "#192.168.0.1#" + random(15);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,1000,1000);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            myBitmap = barcodeEncoder.createBitmap(bitMatrix);
            myImageView.setImageBitmap( myBitmap );
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
                myBitmap = ((BitmapDrawable)myImageView.getDrawable()).getBitmap();
                frame = new Frame.Builder().setBitmap(myBitmap).build();
                barcodes = detector.detect(frame);
                thisCode = barcodes.valueAt(0);
                txtView.setText(thisCode.rawValue);
            }
        });
    }
}
