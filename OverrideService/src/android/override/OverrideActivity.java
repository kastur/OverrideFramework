package android.override;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.OverrideLocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class OverrideActivity extends Activity {

  private static final String TAG = "OverrideActivity";
  LocationManager nativeLocationManager;
  LocationManager overrideLocationManager;

  TextView nativeLocationTextView;
  TextView overrideLocationTextView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    nativeLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    overrideLocationManager = new OverrideLocationManager(this);

    nativeLocationTextView = (TextView) findViewById(R.id.nativeLocation);
    overrideLocationTextView = (TextView) findViewById(R.id.overrideLocation);

    Button
        startLocationProviderButton =
        (Button) findViewById(R.id.startRandomWalkLocationProvider);
    startLocationProviderButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent providerIntent = new Intent(OverrideActivity.this, RandomWalkLocationProvider.class);
        startService(providerIntent);
      }
    });

    Button stopLocationProviderButton = (Button) findViewById(R.id.stopRandomWalkLocationProvider);
    stopLocationProviderButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent providerIntent = new Intent(OverrideActivity.this, RandomWalkLocationProvider.class);
        stopService(providerIntent);
      }
    });

    Button startLocationServiceButton = (Button) findViewById(R.id.startOverrideLocationService);
    startLocationServiceButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent serviceIntent = new Intent(OverrideActivity.this, OverrideLocationService.class);
        startService(serviceIntent);
      }
    });

    Button stopLocationServiceButton = (Button) findViewById(R.id.stopOverrideLocationService);
    stopLocationServiceButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent serviceIntent = new Intent(OverrideActivity.this, OverrideLocationService.class);
        stopService(serviceIntent);
      }
    });
  }

  @Override
  protected void onResume() {
    super.onResume();
    nativeLocationManager
        .requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, nativeLocationListener);
    nativeLocationManager
        .requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, nativeLocationListener);

    overrideLocationManager
        .requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, overrideLocationListener);
  }

  @Override
  protected void onPause() {
    super.onPause();
    nativeLocationManager.removeUpdates(nativeLocationListener);
    overrideLocationManager.removeUpdates(overrideLocationListener);
  }

  private void onNativeLocationChanged(Location location) {
    String locationText =
        "lat = " + location.getLatitude() + ", lon = " + location.getLongitude();
    nativeLocationTextView.setText(locationText);
  }

  private void onOverrideLocationChanged(Location location) {
    String locationText =
        "Override lat = " + location.getLatitude() + ", lon = " + location.getLongitude();
    Log.d(TAG, locationText);
    overrideLocationTextView.setText(locationText);
  }

  LocationListener nativeLocationListener = new LocationListener() {
    @Override
    public void onLocationChanged(Location location) {
      onNativeLocationChanged(location);
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

  LocationListener overrideLocationListener = new LocationListener() {
    @Override
    public void onLocationChanged(Location location) {
      onOverrideLocationChanged(location);
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
}
