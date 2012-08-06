package android.override.demo;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.override.OverrideLocationManager;
import android.override.OverrideSensorManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DemoOverrideActivity extends Activity {

  private static final String TAG = "DemoOverrideActivity";

  OverrideLocationManager locMan;
  OverrideSensorManager senMan;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    Button b = (Button) findViewById(R.id.btnStart);
    b.setOnClickListener(onClickListener);

    locMan = new OverrideLocationManager(this.getApplicationContext());
    senMan = new OverrideSensorManager(this.getApplicationContext());

    startService(new Intent("android.override.providers.RandomWalkLocationProvider"));
  }


  android.view.View.OnClickListener onClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

      SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
      Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
      senMan.registerListener(sensorEventListener, accelerometerSensor,
                              SensorManager.SENSOR_DELAY_NORMAL);
    }
  };

  LocationListener locationListener = new LocationListener() {
    @Override
    public void onLocationChanged(Location location) {
      String
          locationText =
          "lat = " + location.getLatitude() + ", lon = " + location.getLongitude();
      Log.d(TAG, locationText);
      TextView textView = (TextView) findViewById(R.id.txtLocation);
      textView.setText(locationText);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
  };

  SensorEventListener sensorEventListener = new SensorEventListener() {
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
      String
          accelText =
          "x = " + sensorEvent.values[0] + ", y = " + sensorEvent.values[1] + ", z = "
          + sensorEvent.values[2];
      Log.d(TAG, accelText);
      TextView textView = (TextView) findViewById(R.id.txtAccel);
      textView.setText(accelText);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
  };
}
