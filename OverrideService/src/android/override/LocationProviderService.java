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


public abstract class LocationProviderService extends Service {

  private static final String TAG = "LocationProviderService";

  protected abstract String getName();

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.v(TAG, "onStartCommand");
    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  public void onCreate() {
    super.onCreate();
    Log.v(TAG, "onCreate");
    bindToOverrideLocationManager();
  }

  @Override
  public void onDestroy() {
    Log.v(TAG, "onDestroy");
    unregisterAsProvider();
    unbindService(mServiceConnection);
    super.onDestroy();
  }

  private void registerAsProvider() {
    try {
      mService.addOverrideProvider(getName());
    } catch (RemoteException e) {
      Log.e(TAG, "addOverrideProvider: OverrideLocationService is not available.");
    }
  }

  private void unregisterAsProvider() {
    try {
      mService.removeOverrideProvider(getName());
    } catch (RemoteException e) {
      Log.e(TAG, "removeOverrideProvider: OverrideLocationService is not available.");
    }
  }

  private void bindToOverrideLocationManager() {
    Intent serviceIntent = new Intent("android.override.OverrideLocationService");
    bindService(serviceIntent, mServiceConnection, BIND_DEBUG_UNBIND);
  }

  public IOverrideLocationService getService() {
    return mService;
  }

  private ServiceConnection mServiceConnection = new ServiceConnection() {

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder binder) {
      mService = IOverrideLocationService.Stub.asInterface(binder);
      registerAsProvider();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
      mService = null;
    }
  };

  private IOverrideLocationService mService;

}
