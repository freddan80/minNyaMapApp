package fredriksApp.apk;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements SensorEventListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float last_x, last_y, last_z;
    long lastUpdate = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("FredriksMapApp", "Creating MapsActivity");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Setup sensors
        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //sensorManager.registerListener()

        setUpMapIfNeeded();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        //Log.d("FredriksMapApp","Sensor triggered!");

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //Log.d("FredriksMapApp","Accelerometer triggered!");
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            long curTime = System.currentTimeMillis();
            SimpleDateFormat date = new SimpleDateFormat("dd-mm-yyyy");
            String currentDateTime = date.format(new Date());



            /*mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(17.23062, -80.42178))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                    .title("You moved the x axis" + currentDateTime));*/

            if (Math.abs(curTime - lastUpdate) > 5000) {
                Log.i("FredriksMapApp", "x: " + x + " y: " + y + " curTime: " + curTime + " lastUpdate: " + lastUpdate + " last_x: " + last_x);

                // Plot my location if available
                Location myLocation = mMap.getMyLocation();
                if (myLocation != null) {
                    Log.i("FredriksMapApp", "myLocation.getLatitude(): " + myLocation.getLatitude());
                    Log.i("FredriksMapApp", "myLocation.getLongitude(): " + myLocation.getLongitude());
                    LatLng myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                    mMap.addMarker(new MarkerOptions()
                            .position(myLatLng)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                            .title("You moved the x axis" + currentDateTime));

                    for (int ii=0; ii<10; ii++) {
                        Random r = new Random();
                        LatLng rndLatLng = new LatLng(myLocation.getLatitude() + (r.nextFloat() - 0.5) / 100, myLocation.getLongitude() + (r.nextFloat() - 0.5) / 100);
                        mMap.addMarker(new MarkerOptions()
                                .position(rndLatLng)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_black_dot_transparent)));
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 14.0f));
                } else {
                    Log.w("FredriksMapApp", "myLocation == null");
                }

                /*
                // Plot a dummy location
                if (Math.abs(last_y - y) > 1) {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(47.24062, -80.42278))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            .title("You moved the y axis" + currentDateTime));
                }

                // Plot a dummy location
                if (Math.abs(last_z - z) > 1) {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(57.25062, -80.42378))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                            .title("You moved the z axis" + currentDateTime));
                }
                */

                last_x = x;
                last_y = y;
                last_z = z;
                lastUpdate = curTime;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        Log.i("FredriksMapApp", "setUpMapIfNeeded()");

        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            mMap.setMyLocationEnabled(true);

            if (mMap != null) {
                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location arg0) {

                        // Hmmm this don't seem to execute!
                        Log.i("FredriksMapApp", "onMyLocationChange() arg0: " + arg0);

                        // TODO Auto-generated method stub
                        //mMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
                    }
                });
            }



            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        } else {
            Log.w("FredriksMapApp", "mMap != null!");
        }

    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        Log.i("FredriksMapApp", "setUpMap()");
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).title("Blah"));
        //mMap.addMarker(new MarkerOptions().position(new LatLng(27.229, -80.424)).title("Virginia Tech"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(27.229, -80.424), 14.9f));
    }
}
