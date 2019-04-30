package edu.somaiya.attendifi;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

public class faculty extends AppCompatActivity {

    TextView txtView;
    static String ip;
    facultySocket fs = null;

    JSONObject initialInfo = new JSONObject();

    static int index = 0;
    static String all = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMONPQRSTUVWXYZ0123456789`~!@$%^&*(){}[]:';\",./<>?";
    public static String random(int len){
        String res = "";
        for(int i = 0;i < len;i++){
            res += all.charAt( (int) (Math.random()*all.length()) );
        }
        return res;
    }

    public void serve(){
//        Toast.makeText(this, "Server started", Toast.LENGTH_SHORT).show();
        ImageView myImageView = (ImageView) findViewById(R.id.imgview);
        try {
            fs = new facultySocket(this.getApplicationContext(),ip,myImageView,txtView,initialInfo,getExternalFilesDir(null));
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

    public static void genBarcode(String text,ImageView img) throws WriterException{
        String rndm = random(64) + text + random(63 - text.length()) + index;

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        BitMatrix bitMatrix = multiFormatWriter.encode(rndm, BarcodeFormat.QR_CODE,1000,1000);
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        Bitmap myBitmap = barcodeEncoder.createBitmap(bitMatrix);
        img.setImageBitmap( myBitmap );

        index++;

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

        Bundle i = getIntent().getExtras();
        try {
            initialInfo.put("name", i.getString("Name"));
            initialInfo.put("department", i.getString("Department"));
            initialInfo.put("year", i.getString("Year"));
            initialInfo.put("division", i.getString("Division"));
            initialInfo.put("subject", i.getString("Subject"));
            initialInfo.put("topic", i.getString("Topic"));
            initialInfo.put("timing", i.getString("Timing"));
        } catch(JSONException je){
            Log.i("JSON Exception",je.toString());
        }
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

class facultySocket extends AsyncTask<String,String,Integer> {
    private ServerSocket ss = null;
    private Socket s = null;
    private Context c;
    DataInputStream dis;
    DataOutputStream dout;
    private String ip = "";
    private ImageView img;
    private boolean close = true;
    private TextView txt;
    private static int count = 0;
    private JSONObject facultyInfo;
    private Map<String,JSONObject> studentsInfo = new HashMap<String, JSONObject>();
    private Vector<JSONObject> proxy = new Vector<JSONObject>();
    private File path;
    facultySocket(Context context,String ip,ImageView img,TextView txtView,JSONObject info,File p){
        this.c = context;
        this.ip = ip;
        this.img = img;
        this.facultyInfo = info;
        this.txt = txtView;
        this.path = p;
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
            if( dout != null )
                dout.close();
//            callback.onSuccess("Close successfully");
            Log.i("Closed socket","Socket close successful");
        } catch (Exception e) {
//            result = -2;
            Log.i("Closing error", e.toString());
//            e.printStackTrace();
        }
    }
    @Override
    protected void onPreExecute(){
        Toast.makeText(c.getApplicationContext(), "Server started successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Integer doInBackground(String... ip){
        Integer result = 0;

        try{
            Log.i("Start Socket","Server started successfully");
            ss=new ServerSocket(6666);
            String str = "";
            while (close){
                s=ss.accept();//establishes connection

                dis = new DataInputStream(s.getInputStream());

                str=(String)dis.readUTF();
                publishProgress(str);
                Log.i("Received",str);

                dout = new DataOutputStream(s.getOutputStream());

                dout.flush();
                dout.writeInt(0);

                Thread.sleep(1000);

                dout.close();

                dout = null;
                dis = null;

                s.close();
            }

            result = 0;
            return result;

        } catch (Exception e){
            result = -1;
            Log.i("Socket error",e.toString());
//            e.printStackTrace();
            return result;
        }

//        return result;
    }

    @Override
    protected void onProgressUpdate(String... progress) {

        JSONObject student;

        try {
            student = new JSONObject(progress[0]);

            Toast.makeText(c.getApplicationContext(), "Last Student : " + student.getString("name"), Toast.LENGTH_SHORT).show();

            String str = "Last Student: "+student.getString("name") + " Roll No: " + student.getString("roll_no");
            txt.setText(str);

            int index = student.getInt("index");
            if(index == studentsInfo.size()){
                String macAddress = student.getString("mac_address");

                if( !studentsInfo.containsKey(macAddress) ){
                    studentsInfo.put(macAddress,student);
                }else if( macAddress == "02:00:00:00:00:00" ){
                    macAddress += String.valueOf(count);
                    studentsInfo.put(macAddress,student);
                }else {
                    proxy.add(student);
                    Toast.makeText(c.getApplicationContext(), "Proxy detected : " + student.getString("name"), Toast.LENGTH_SHORT).show();
                }
            } else if(index < studentsInfo.size()){
                proxy.add(student);
            }

            faculty.genBarcode(this.ip,this.img);

        } catch (WriterException we){
            Log.i("Barcode error",we.toString());
        } catch (JSONException je){
            Log.i("JSON onprogress",je.toString());
        }
    }

    protected void onPostExecute(Integer result) {
        Toast.makeText(c.getApplicationContext(), "Server closed finally in post execute", Toast.LENGTH_SHORT).show();

        Vector<JSONObject> attendance = new Vector<JSONObject>(studentsInfo.values());
//        TODO : Write to file here attendance vector and facultyInfo json object

        FileWriter fw = null;
        BufferedWriter bw = null;
        try{
            File t = new File(path.getPath(),"hello.csv");
            if(!t.exists())
                t.createNewFile();
            fw = new FileWriter(t);
            bw = new BufferedWriter(fw);

            bw.write("K. J.,Somaiya,College Of Engineering");
            bw.newLine();
            bw.write(facultyInfo.getString("department")+","+facultyInfo.getString("year")+","+facultyInfo.getString("division"));
            bw.newLine();
            bw.write(facultyInfo.getString("subject")+","+facultyInfo.getString("topic")+",");
            bw.newLine();
            bw.write(new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + "," + facultyInfo.getString("timing"));
            bw.newLine();

            for(JSONObject temp:attendance){
                try{
                    bw.write( (temp.getInt("index")/2+1) + "," +temp.getString("roll_no")+ ","+temp.getString("name") );
                    bw.newLine();
                } catch (JSONException je){
                    je.printStackTrace();
                }
            }

            if(bw != null){
                bw.close();
            }
            if(fw != null){
                fw.close();
            }
        } catch (IOException ioe){
            ioe.printStackTrace();
        } catch (JSONException je){
            je.printStackTrace();
        }
        switch (result){
            case 0:
                Log.i("Success","Receiving successful!!!");break;

            case -1:
                Log.i("Socket Error","Error");break;

            case -2:
                Log.i("Close error","Cannot close connection");break;
        }
    }
}