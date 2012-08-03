package android.override.providers;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.os.RemoteException;
import android.override.IOverrideLocationService;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class RandomWalkLocationProvider extends Service {
    private static final String TAG = "RandomWalkLocationProvider";
    public static final String RANDOM_WALK_LOCATION_PROVIDER = "android.override.demo/gps";

    IOverrideLocationService mService;
    ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            mService = IOverrideLocationService.Stub.asInterface(binder);

            try {
                mService.addOverrideProvider(RANDOM_WALK_LOCATION_PROVIDER);
            } catch (RemoteException e) {
                Log.e(TAG, "addOverrideProvider: OverrideLocationService is not available.");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
        }
    };

    Location mLocation;
    Timer mTaskTimer = new Timer("PeriodicRandomWalkLocationUpdates");
    TimerTask mPeriodicReportTask = new TimerTask() {

        @Override
        public void run() {
            mLocation.setLatitude(mLocation.getLatitude() + Math.random() - 0.5);
            mLocation.setLongitude(mLocation.getLongitude() + Math.random() - 0.5);
            mLocation.setProvider(RANDOM_WALK_LOCATION_PROVIDER);
            mLocation.setTime(new java.util.Date().getTime());

            if (mService == null)
                return;
            try {
                mService.reportOverrideLocation(RANDOM_WALK_LOCATION_PROVIDER, mLocation);
            } catch (RemoteException e) {
                Log.e(TAG, "reportOverrideLocation: OverrideLocationService is not available.");
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent serviceIntent = new Intent("android.override.OverrideLocationService");
        bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        startService(serviceIntent);

        mLocation = new Location(RANDOM_WALK_LOCATION_PROVIDER);
        mLocation.setLatitude(0.0);
        mLocation.setLongitude(0.0);

        mTaskTimer.scheduleAtFixedRate(mPeriodicReportTask, 500, 5000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
