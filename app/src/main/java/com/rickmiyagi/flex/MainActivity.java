package com.rickmiyagi.flex;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.scanner.ScanActivity;

import org.json.JSONObject;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import java.io.IOException;
import org.json.JSONException;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Hub hub = Hub.getInstance();
        if (!hub.init(this)) {
            Log.e(TAG, "Could not initialize the Hub.");
            finish();
            return;
        }

//        Hub.getInstance().attachToAdjacentMyo();

        Intent intent = new Intent(this, ScanActivity.class);
        this.startActivity(intent);



        Hub.getInstance().setLockingPolicy(Hub.LockingPolicy.NONE);
        Hub.getInstance().addListener(mListener);

    }

    private DeviceListener mListener = new AbstractDeviceListener() {
        @Override
        public void onConnect(Myo myo, long timestamp) {
            Log.d("connect", "connected");
            Toast.makeText(MainActivity.this, "Myo Connected!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDisconnect(Myo myo, long timestamp) {
            Toast.makeText(MainActivity.this, "Myo Disconnected!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPose(Myo myo, long timestamp, Pose pose) {

            if (pose == Pose.WAVE_IN) {
                //TODO: Call Uber!
                Toast.makeText(MainActivity.this, "CALL UBER", Toast.LENGTH_SHORT).show();
                POST("37.334381", "-121.89432", "a1111c8c-c720-46c3-8534-2fcdd730040d");
            }
        }
    };

    public void callPhoneNumber(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(callIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public static void POST(String latitude, String longitude, String productId) {
        HttpURLConnection urlConn = null;
        try {
            URL url = new URL("https://sandbox-api.uber.com/v1/requests");
            urlConn = (HttpURLConnection) url.openConnection();
            DataOutputStream printout;
            DataInputStream input;
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setUseCaches(false);
            urlConn.setRequestProperty("Content-Type", "application/json");
            urlConn.connect();

            //Create JSONObject here
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("start_latitude", latitude);
            jsonParam.put("start_longitude", longitude);
            jsonParam.put("product_id", productId);

            // Send POST output.
            printout = new DataOutputStream(urlConn.getOutputStream());
            //printout.write(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
            printout.flush();
            printout.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            if (urlConn != null)
                urlConn.disconnect();
        }
    }
}
