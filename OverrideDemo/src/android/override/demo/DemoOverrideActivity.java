package android.override.demo;

import android.app.Activity;
import android.content.Intent;
import android.location.ILocationManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.OverrideLocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ServiceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DemoOverrideActivity extends Activity {

  private static final String TAG = "DemoOverrideActivity";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    onCreateLocationStuff();
    onCreateOverrideStuff();
  }


  @Override
  protected void onResume() {
    super.onResume();
    onResumeLocationStuff();
  }

  @Override
  protected void onPause() {
    onPauseLocationStuff();
    super.onPause();

  }

  // ----------------------------------------------------------------------------------------------
  //        LOCATION STUFF
  // ----------------------------------------------------------------------------------------------
  private LocationManager mLocationManager;

  private void onCreateLocationStuff() {
    IBinder binder = ServiceManager.getService(LOCATION_SERVICE);
    ILocationManager service = ILocationManager.Stub.asInterface(binder);
    mLocationManager = new OverrideLocationManager(this, service);
  }

  private void onPauseLocationStuff() {
    mLocationManager.removeUpdates(locationListener);
  }

  private void onResumeLocationStuff() {
    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                                            locationListener);
  }


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

  // ----------------------------------------------------------------------------------------------
  //        SUPPRESS / ALLOW LOCATION UPDATES TO THIS APP
  // ----------------------------------------------------------------------------------------------

  private void onCreateOverrideStuff() {
    {
      Button b = (Button)findViewById(R.id.btnSuppress);
      b.setOnClickListener(onSuppressClickListener);
    }

    {
      Button b = (Button)findViewById(R.id.btnReset);
      b.setOnClickListener(onResetClickListener);
    }
  }

  android.view.View.OnClickListener onSuppressClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      Intent suppressIntent = new Intent("android.override.OverrideCommanderService");
      suppressIntent.putExtra("COMMAND", "SUPPRESS");
      suppressIntent.putExtra("PACKAGE", getPackageName());
      startService(suppressIntent);
    }
  };

  android.view.View.OnClickListener onResetClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      Intent releaseIntent = new Intent("android.override.OverrideCommanderService");
      releaseIntent.putExtra("COMMAND", "RELEASE");
      releaseIntent.putExtra("PACKAGE", getPackageName());
      startService(releaseIntent);
    }
  };

  // ----------------------------------------------------------------------------------------------
}
