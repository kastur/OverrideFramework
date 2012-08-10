package android.override;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Periodically reports locations, each randomly perturbed from a starting point.
 */
public class RandomWalkLocationProvider extends LocationProviderService {

  private static final String TAG = "OverrideRandomWalkLocationProvider";
  public static final String RANDOM_WALK_LOCATION_PROVIDER = "RandomWalkLocationProvider";

  // Start out at the Empire State Building
  private static final double START_LATITUDE = 40.748502;
  private static final double START_LONGITUDE = -73.98445;

  private static final int LOCATION_REPORT_DELAY = 500; // half a second.
  private static final int LOCATION_REPORT_PERIOD = 5000; // report new location every 5 seconds.
  private static final double RANDOM_WALK_MAX_MOVE = 0.001; // 1/1000 of a degree

  Location mLocation;
  Timer mTaskTimer = new Timer("PeriodicRandomWalkLocationUpdates");


  @Override
  public void onCreate() {
    super.onCreate();
    mLocation = new Location(RANDOM_WALK_LOCATION_PROVIDER);
    mLocation.setLatitude(START_LATITUDE);
    mLocation.setLongitude(START_LONGITUDE);
    mTaskTimer.scheduleAtFixedRate(
        mPeriodicReportTask, LOCATION_REPORT_DELAY, LOCATION_REPORT_PERIOD);
  }

  @Override
  public void onDestroy() {
    mTaskTimer.cancel();
    super.onDestroy();
  }

  @Override
  protected String getName() {
    return RANDOM_WALK_LOCATION_PROVIDER;
  }

  TimerTask mPeriodicReportTask = new TimerTask() {

    @Override
    public void run() {

      // Report a new location around upto 40 feet away from the previous one.
      mLocation.setLatitude(mLocation.getLatitude() + RANDOM_WALK_MAX_MOVE * (Math.random() - 0.5));
      mLocation
          .setLongitude(mLocation.getLongitude() + RANDOM_WALK_MAX_MOVE * (Math.random() - 0.5));
      mLocation.setProvider(RANDOM_WALK_LOCATION_PROVIDER);
      mLocation.setTime(new java.util.Date().getTime());

      if (getService() == null) {
        return;
      }

      try {
        getService().reportOverrideLocation(RANDOM_WALK_LOCATION_PROVIDER, mLocation);
      } catch (RemoteException e) {
        Log.e(TAG, "reportOverrideLocation: OverrideLocationService is not available.");
      }
    }
  };
}
