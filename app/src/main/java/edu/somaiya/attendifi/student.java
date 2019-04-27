package edu.somaiya.attendifi;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import org.json.*;

import java.io.DataOutputStream;
import java.net.Socket;
import java.io.IOException;
import java.util.StringTokenizer;

// TODO : http://androidsrc.net/android-client-server-using-sockets-client-implementation/
// TODO : https://developer.android.com/reference/java/net/Socket ---> socket programming

public class student extends AppCompatActivity {

    public JSONObject decodeIp(String S) throws Exception{
        StringTokenizer decoded = new StringTokenizer(S,"#");
        String temp = decoded.nextToken(),result="";
        temp = decoded.nextToken();
        JSONObject obj = new JSONObject();
        for(int i = 0;i < temp.length();i++){
            char t = temp.charAt(i);
            if( t == 'A' || t == 'B' || t == 'C' ){
                result+='.';
            } else if (t == 'D') {
                i = temp.length() + 2;
            } else {
                result+=t;
            }
        }
        obj.put("ip",result);
        obj.put("index",Integer.parseInt(String.valueOf(S.charAt(S.length()-1))) );
        return obj;
//        return end;
    }

    SurfaceView surfaceView;
    //    boolean checked = false;
    char index;
    TextView txtBarcodeValue;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    JSONObject intentData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        Toast.makeText(this, "Scan faculty's barcode to mark yourself attended", Toast.LENGTH_SHORT).show();

        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
        surfaceView = findViewById(R.id.surfaceView);

    }

    private void initialiseDetectorsAndSources() {

//        if(checked)
//            return;

//        Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                try {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(student.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
//                Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections){
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {


                    txtBarcodeValue.post(new Runnable() {

                        @Override
                        public void run() {

                            String scanned = barcodes.valueAt(0).displayValue;
                            try{
                                intentData = decodeIp(scanned);
                                index = (char)intentData.get("index");
                                scanned = (String)intentData.get("ip");
                            } catch (Exception e){
                                scanned+=e.toString();
                            }
                            txtBarcodeValue.setText(intentData.toString());

                            if(cameraSource != null)
                                cameraSource.release();

                            ImageView img = new ImageView(getApplicationContext());
                            img.setImageResource(R.drawable.check_mark_2);
                            img.animate().alpha(0).setDuration(0);
                            LinearLayout l = (LinearLayout) findViewById(R.id.linear);
                            l.addView(img,0);
                            l.removeViewAt(1);

                            img.animate().rotation(720).setDuration(1000);
                            img.animate().alpha(.25f).setDuration(500);
                            img.animate().alpha(0.5f).setDuration(500);
                            img.animate().alpha(0.75f).setDuration(500);
                            img.animate().alpha(1f).setDuration(500);

//                            checked = true;

                            cameraSource = null;

                            // Add sockets logic here
//                            Socket connect = null;
//                            try{
//                                connect = new Socket((String)intentData.get("ip"),6666);
//
//                                DataOutputStream dout=new DataOutputStream(connect.getOutputStream());
//
//                                dout.flush();
//                                dout.writeUTF(intentData.toString());
//
//                                Toast.makeText(student.this, intentData.toString(), Toast.LENGTH_SHORT).show();
//                                dout.close();
//                            } catch ( Exception e){
//                                Toast.makeText(student.this, "Socket unknown host error", Toast.LENGTH_SHORT).show();
//                                Log.i("Socket Error",e.toString());
//                            } finally {
//                                if(connect != null){
//                                    try{
//                                        connect.close();
////                                            Toast.makeText(camera.this, "Socket closed", Toast.LENGTH_SHORT).show();
//                                    } catch (IOException e){
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
                            try {
                                new studentSocket().execute(intentData);
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });

                }
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(cameraSource != null)
            cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }
}

class studentSocket extends AsyncTask<JSONObject,Integer,JSONObject>{
    Socket connect;

    @Override
    protected JSONObject doInBackground(JSONObject... ip){
        JSONObject result = new JSONObject();

        try{
            connect = new Socket((String)ip[0].get("ip"),6666);

            DataOutputStream dout=new DataOutputStream(connect.getOutputStream());

            dout.flush();
            dout.writeUTF(ip[0].toString());

            result.put("output",0);
            dout.close();
        } catch ( Exception e){
            try {
                result.put("output", -1);
            }catch (Exception ejson){
                ejson.printStackTrace();
            }
            Log.i("Socket Error",e.toString());
        } finally {
            if(connect != null){
                try{
                    connect.close();
//                                            Toast.makeText(camera.this, "Socket closed", Toast.LENGTH_SHORT).show();
                } catch (IOException e){
                    try {
                        result.put("output", -2);
                    }catch (Exception ejson){
                        ejson.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    protected void onProgressUpdate(Integer... progress) {

    }

    protected void onPostExecute(JSONObject result) {
        int res = -2;
        try {
            res = result.getInt("output");
        }catch (Exception ejson){
            ejson.printStackTrace();
        }
        switch (res){
            case 0:
                Log.i("Success","Sending successful!!!");
                break;

            case -1:
                Log.i("Socket Error","Error");
                break;

            case -2:
                Log.i("Close error","Cannot close connection");
                break;
        }

    }
}