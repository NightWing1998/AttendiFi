package edu.somaiya.attendifi;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.io.IOException;
import java.util.StringTokenizer;

public class student extends AppCompatActivity {

    public JSONObject decodeIp(String S) throws Exception {
        StringTokenizer decoded = new StringTokenizer(S, "#");
        String temp = decoded.nextToken(), result = "";
        temp = decoded.nextToken();
        JSONObject obj = new JSONObject();
        for (int i = 0; i < temp.length(); i++) {
            char t = temp.charAt(i);
            if (t == 'A' || t == 'B' || t == 'C') {
                result += '.';
            } else if (t == 'D') {
                i = temp.length() + 2;
            } else {
                result += t;
            }
        }
        obj.put("ip", result);
        obj.put("index", Integer.parseInt(String.valueOf(S.charAt(S.length() - 1))));
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
    static JSONObject intentData = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        Toast.makeText(this, "Scan faculty's barcode to mark yourself attended", Toast.LENGTH_SHORT).show();
        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
        Bundle i = getIntent().getExtras();
        try{
            String str = i.getString("Name");

            intentData.put("name", str);
            intentData.put("roll_no", i.getString("Roll_No"));

            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.i("permission","true");
            } else {
                ActivityCompat.requestPermissions(student.this, new
                        String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
                ActivityCompat.requestPermissions(student.this, new
                        String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
            }
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
            WifiInfo wInfo = wifiManager.getConnectionInfo();
            String macAddress = wInfo.getMacAddress();
            intentData.put("mac_address", macAddress);


            txtBarcodeValue.setText(intentData.toString());

        } catch (Exception e){
            e.printStackTrace();
        }

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
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {


                    txtBarcodeValue.post(new Runnable() {

                        @Override
                        public void run() {

                            JSONObject intentData1;
                            String scanned = barcodes.valueAt(0).displayValue;
                            try {
                                intentData1 = decodeIp(scanned);
//                                index = (char) ;
//                                scanned = (String) intentData1.get("ip");
                                intentData.put("index",intentData1.getInt("index"));
                                intentData.put("ip",intentData1.getString("ip"));
                                Log.i("Intent Data",intentData.toString());
                            } catch (Exception e) {
                                scanned += e.toString();
                            }
                            txtBarcodeValue.setText("YOUR REQUEST FOR ATTENDANCE HAS BEEN PLACED!");

                            if (cameraSource != null)
                                cameraSource.release();

                            ImageView img = new ImageView(getApplicationContext());
                            img.setImageResource(R.drawable.check_mark_2);
                            img.animate().alpha(0).setDuration(0);
                            LinearLayout l = (LinearLayout) findViewById(R.id.linear);
                            l.addView(img, 0);
                            l.removeViewAt(1);

                            img.animate().rotation(720).setDuration(1000);
                            img.animate().alpha(.25f).setDuration(500);
                            img.animate().alpha(0.5f).setDuration(500);
                            img.animate().alpha(0.75f).setDuration(500);
                            img.animate().alpha(1f).setDuration(500);

//                            checked = true;

                            cameraSource = null;

                            try {
                                new studentSocket(getApplicationContext(),txtBarcodeValue).execute(intentData);
                                txtBarcodeValue.setText(intentData.toString());
                            } catch (Exception e) {
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
        if (cameraSource != null)
            cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }
}

class studentSocket extends AsyncTask<JSONObject, Integer, JSONObject> {
    private Socket connect;
    private TextView txt;
    private Context c;
    studentSocket(Context c,TextView txtView ){
        this.txt = txtView;
        this.c = c;
    }

    @Override
    protected JSONObject doInBackground(JSONObject... ip) {
        JSONObject result = new JSONObject();

        try {
            connect = new Socket((String) ip[0].get("ip"), 6666);

            DataOutputStream dout = new DataOutputStream(connect.getOutputStream());

            dout.flush();
            dout.writeUTF(ip[0].toString());

            dout.close();

            DataInputStream din = new DataInputStream(connect.getInputStream());

            result.put("output", din.readInt());

            din.close();

        } catch (Exception e) {
            try {
                result.getInt("output");
            } catch (Exception ejson) {
                try {
                    result.put("output", -1);
                }catch (Exception e1){}
                ejson.printStackTrace();
            }
            Log.i("Socket Error", e.toString());
        } finally {
            if (connect != null) {
                try {
                    Thread.sleep(1000);
                    connect.close();
//                                            Toast.makeText(camera.this, "Socket closed", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    try {
                        result.put("output", -2);
                    } catch (Exception ejson) {
                        ejson.printStackTrace();
                    }
                    e.printStackTrace();
                } catch (InterruptedException ie){
                    ie.printStackTrace();
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
        } catch (Exception ejson) {
            ejson.printStackTrace();
        }
        switch (res) {
            case 0:
                Log.i("Success", "Sending successful!!!");
                Toast.makeText(c.getApplicationContext(), "Attended successfully!!", Toast.LENGTH_SHORT).show();
                txt.setText("You have attended!!");
                break;

            case -1:
                Log.i("Socket Error1", "Error");
                break;

            case -2:
                Log.i("Close error1", "Cannot close connection");
                break;
        }

    }
}