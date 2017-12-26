package com.example.kevingermain.sosfind;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lize.oledcomm.camera_lifisdk_android.ILiFiPosition;
import com.lize.oledcomm.camera_lifisdk_android.LiFiSdkManager;
import com.lize.oledcomm.camera_lifisdk_android.V1.LiFiCamera;

import static com.example.kevingermain.sosfind.SmsListener.wantedActive;

public class MainActivity extends AppCompatActivity {

    // Declare the application elements like TextView or EditText
    TextView valueTextView;
    TextView textViewReceivedSMS;

    // Declare an instance of LiFiSdkManager
    private LiFiSdkManager liFiSdkManager;
    String[] PERMISSIONS = {Manifest.permission.CAMERA};

    // Declare some important elements for the app
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    public TextView resultGPS = null;
    final SmsManager smsManager = SmsManager.getDefault();
    Button buttonMap;
    boolean mobileFound = false;

    // For the message sending function
    Button sendSMSButton;
    EditText editTextPhoneNumber;
    String phoneNumber = "5554";
    String message;

    // For the location service
    Button getLocationButton;
    TextView locationText;
    LocationManager locationManager;
    LocationListener locationListener;

    // Method that will be first executed when the app starts, we included in this method all permissions that should be checked for some functions like sending a message
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if the permission is enabled for sending a message in the AndroidManifest.xml
        int permissionCheckSendSms = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.SEND_SMS);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }

        // Check the Camera permissions
        int permissionCheckCamera = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }

        // Check the location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }

    }

    protected void onResume() {

        super.onResume();

        // Get the value of the TextView element
        valueTextView = (TextView) findViewById(R.id.textView);

        // Initialize lifiSdkManager with Camera_Lib_Version
        liFiSdkManager = new LiFiSdkManager(this, LiFiSdkManager.CAMERA_LIB_VERSION_0_1,
                "token", "user", new ILiFiPosition() {
            @Override
            public void onLiFiPositionUpdate(String lamp) {
                //lamp = "Lamp detected";
                //valueTextView.setText(lamp);

                if (wantedActive == true) {
                    lamp = "11000011";
                }
                if (lamp.equals("00111100") ){
                    if (mobileFound == false)
                    {
                        mobileFound = true;
                        message = "48.345,1.344" ; // GPS data test
                        vibrate();
                        flash();
                        sendSMSMessage("5554", message);
                    }
                    mobileFound = true;

                }
                else if (lamp.equals("10101010")) {
                    if (mobileFound == false) {
                        mobileFound = true;
                        message = "48.345,1.344";
                        vibrate();
                        flash();
                    }
                    mobileFound = true;
                }
                else if (lamp.equals("11000011")) {
                    if (mobileFound == false)
                    {
                        mobileFound = true;
                        message = "48.345,1.344" ;
                        vibrate();
                        flash();
                    }
                    mobileFound = true;
                }
            }
        });

        // Set the location request mode
        liFiSdkManager.setLocationRequestMode(LiFiSdkManager.LOCATION_REQUEST_OFFLINE_MODE);
        // Tell to LiFiSdkManager which camera you want to use and the layout for this camera
        liFiSdkManager.init(R.id.content_main, LiFiCamera.FRONT_CAMERA);
        // Start your LiFiSdkManager instance:
        liFiSdkManager.start();

        // Code that check if the SMS sending function is working, you can use the field in the app to enter a phone number
        /*sendSMSButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                phoneNumber = editTextPhoneNumber.getText().toString();
                sendSMSButton = (Button) findViewById(R.id.sendSMSButton);
                editTextPhoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
                sendSMSMessage(phoneNumber, "Test envoi de message");
            }
        });*/

        // Call to the method that will get the location
        getLocation();

        // Go to the map
        buttonMap = (Button) findViewById(R.id.buttonMap);
        buttonMap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intentMap = new Intent(MainActivity.this, map.class);
                startActivity(intentMap);
            }
        });


    }

    // Send a message with two parameters: the phoneNumber whom sending a message, and the message body
    protected void sendSMSMessage(String phoneNumber, String message) {

        try {
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS Sent!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS faild, please try again later!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    /* ___ Methods used to get the mobile GPS location ___ */
     public void getLocation() {
        locationText = (TextView) findViewById(R.id.locationText);
        getLocationButton = (Button) findViewById(R.id.getLocationButton);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // The location will be updated as soon as a new mobile location is detected
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locationText.append("\n " + location.getLongitude() + "\n " + location.getLatitude());
                String locationCoo = "Longitude : " + location.getLongitude() + "\nLatitude : " + location.getLatitude();
                sendSMSMessage("5554", locationCoo);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        configure_button();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }

    void configure_button(){

        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // noinspection MissingPermission
                locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
            }
        });
    }
    /* ___________________________________________________ */

    public void vibrate()
    {

        long[] vibration = {500,500,2000}; // Parameters are corresponding to frequency
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(vibration,0); // Infinite

    }

    public void flash()
    {
        Camera cam;
        cam = Camera.open();
        Camera.Parameters p = cam.getParameters();
        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        cam.setParameters(p);
        cam.startPreview();
        Log.d("flash","flash");
    }

}