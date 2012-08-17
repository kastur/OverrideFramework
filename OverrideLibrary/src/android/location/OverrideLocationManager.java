package android.location;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.override.IOverrideCommandListener;
import android.override.IOverrideCommander;
import android.util.Log;

import java.util.HashMap;

// The OverrideLocationManager is a bare-bones version of the default Android LocationManager.
// If implemented as part of the Android Framework, we will make this class extend LocationManager directly,
// and when external apps call "Context.getSystemService", they will receive an instance of this class.
// However, here as a demonstration we have implemented it as a class external to the Android Framework.
// Applications have to explicitly initialize this class.
public class OverrideLocationManager extends LocationManager {

  private static final String TAG = "OverrideLocationManager";
  private Context mContext;

  private final HashMap<LocationListener, LocationListener> mWrappedListeners;

  public OverrideLocationManager(Context context, ILocationManager service) {
    super(service);
    mContext = context;
    mWrappedListeners = new HashMap<LocationListener, LocationListener>();
    Intent bindIntent = new Intent("android.override.OverrideCommanderService");
    mContext.bindService(bindIntent, mCommanderConnection,
                         Context.BIND_AUTO_CREATE | Context.BIND_DEBUG_UNBIND);
  }

  @Override
  public void finalize() throws Throwable {
    if (mCommander != null) {
      mCommander.removeCommandListener(mContext.getPackageName(), mCommandListener);
      mContext.unbindService(mCommanderConnection);
    }
    super.finalize();
  }

  @Override
  public void requestLocationUpdates(String provider, long minTime, float minDistance,
                                     LocationListener listener) {
    LocationListener wrapped_listener = getWrappedListener(listener);
    super.requestLocationUpdates(provider, minTime, minDistance, wrapped_listener);
  }

  @Override
  public void requestLocationUpdates(String provider, long minTime, float minDistance,
                                     LocationListener listener, Looper looper) {
    LocationListener wrapped_listener = getWrappedListener(listener);
    super.requestLocationUpdates(provider, minTime, minDistance, wrapped_listener, looper);
  }

  @Override
  public void requestLocationUpdates(long minTime, float minDistance, Criteria criteria,
                                     LocationListener listener, Looper looper) {
    LocationListener wrapped_listener = getWrappedListener(listener);
    super.requestLocationUpdates(minTime, minDistance, criteria, wrapped_listener, looper);
  }

  @Override
  public void requestLocationUpdates(String provider, long minTime, float minDistance,
                                     PendingIntent intent) {
    Log.i(TAG,
          "Blocked requestLocationUpdates(String provider, long minTime, float minDistance, PendingIntent intent)");
    //super.requestLocationUpdates(provider, minTime, minDistance, intent);
  }

  @Override
  public void requestLocationUpdates(long minTime, float minDistance, Criteria criteria,
                                     PendingIntent intent) {
    Log.i(TAG,
          "Blocked requestLocationUpdates(long minTime, float minDistance, Criteria criteria, PendingIntent intent)");
    //super.requestLocationUpdates(minTime, minDistance, criteria, intent);
  }

  @Override
  public void requestSingleUpdate(String provider, LocationListener listener, Looper looper) {
    LocationListener wrapped_listener = getWrappedListener(listener);
    super.requestSingleUpdate(provider, wrapped_listener, looper);
  }

  @Override
  public void requestSingleUpdate(Criteria criteria, LocationListener listener, Looper looper) {
    LocationListener wrapped_listener = getWrappedListener(listener);
    super.requestSingleUpdate(criteria, wrapped_listener, looper);
  }

  @Override
  public void requestSingleUpdate(String provider, PendingIntent intent) {
    Log.i(TAG, "Blocked requestSingleUpdate(String provider, PendingIntent intent)");
    //super.requestSingleUpdate(provider, intent);
  }

  @Override
  public void requestSingleUpdate(Criteria criteria, PendingIntent intent) {
    Log.i(TAG, "Blocked requestSingleUpdate(Criteria criteria, PendingIntent intent)");
    //super.requestSingleUpdate(criteria, intent);
  }

  @Override
  public void removeUpdates(LocationListener listener) {
    LocationListener wrapped_listener = getWrappedListener(listener);
    super.removeUpdates(wrapped_listener);
    deleteWrappedListener(wrapped_listener);
  }

  @Override
  public void removeUpdates(PendingIntent intent) {
    Log.i(TAG, "Blocked removeUpdates(PendingIntent intent)");
    //super.removeUpdates(intent);
  }

  @Override
  public void addProximityAlert(double latitude, double longitude, float radius, long expiration,
                                PendingIntent intent) {
    Log.i(TAG,
          "Blocked addProximityAlert(double latitude, double longitude, float radius, long expiration, PendingIntent intent)");
    //super.addProximityAlert(latitude, longitude, radius, expiration, intent);
  }

  @Override
  public void removeProximityAlert(PendingIntent intent) {
    Log.i(TAG, "Blocked removeProximityAlert(PendingIntent intent)");
    //super.removeProximityAlert(intent);
  }

  @Override
  public Location getLastKnownLocation(String provider) {
    return super.getLastKnownLocation(provider);
  }

  @Override
  public boolean addNmeaListener(GpsStatus.NmeaListener listener) {
    Log.i(TAG, "addNmeaListener: return false;");
    return false;
  }

  @Override
  public void removeNmeaListener(GpsStatus.NmeaListener listener) {
    Log.i(TAG, "removeNmeaListener: return;");
  }

  @Override
  public boolean addGpsStatusListener(GpsStatus.Listener listener) {
    Log.v(TAG, "addGpsStatusListener");
    return false;
  }

  @Override
  public void removeGpsStatusListener(GpsStatus.Listener listener) {
    Log.v(TAG, "removeGpsStatusListener");
  }

  @Override
  public GpsStatus getGpsStatus(GpsStatus status) {
    Log.v(TAG, "getGpsStatus");
    return null;
  }

  @Override
  public boolean sendExtraCommand(String provider, String command, Bundle extras) {
    Log.v(TAG, "sendExtraCommand: return false");
    return false;
  }

  private LocationListener getWrappedListener(LocationListener listener) {
    if (mWrappedListeners.containsKey(listener)) {
      return mWrappedListeners.get(listener);
    }

    final LocationListener final_listener = listener;
    LocationListener wrapped_listener = new LocationListener() {
      @Override
      public void onLocationChanged(Location location) {
        if (!mSuppressUpdates) {
          final_listener.onLocationChanged(location);
        } else {
          Log.d(TAG, "SUPPRESSED location: " + mContext.getPackageName());
        }
      }

      @Override
      public void onStatusChanged(String s, int i, Bundle bundle) {
        if (!mSuppressUpdates) {
          final_listener.onStatusChanged(s, i, bundle);
        }
      }

      @Override
      public void onProviderEnabled(String s) {
        if (!mSuppressUpdates) {
          final_listener.onProviderEnabled(s);
        }
      }

      @Override
      public void onProviderDisabled(String s) {
        if (!mSuppressUpdates) {
          final_listener.onProviderDisabled(s);
        }
      }
    };
    mWrappedListeners.put(listener, wrapped_listener);
    return wrapped_listener;
  }

  private void deleteWrappedListener(LocationListener listener) {
    if (mWrappedListeners.containsKey(listener)) {
      mWrappedListeners.remove(listener);
    } else {
      Log.w(TAG, "deleteWrappedListener: listener not registered");
    }
  }

  private IOverrideCommander mCommander = null;
  private boolean mSuppressUpdates = false;

  private IOverrideCommandListener mCommandListener = new IOverrideCommandListener.Stub() {
    @Override
    public void onCommand(Bundle command) throws RemoteException {
      String action = command.getString("COMMAND", "RELEASE");
      if (action.equals("SUPPRESS")) {
        mSuppressUpdates = true;
        Log.d(TAG, "SUPPRESS location updates for " + mContext.getPackageName());
      } else if (action.equals("RELEASE")) {
        mSuppressUpdates = false;
        Log.d(TAG, "RELEASE location updates for " + mContext.getPackageName());
      }

    }
  };

  private ServiceConnection mCommanderConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder binder) {
      mCommander = IOverrideCommander.Stub.asInterface(binder);

      try {
        mCommander.registerCommandListener(mContext.getPackageName(), mCommandListener);
      } catch (RemoteException ex) {
        Log.d(TAG, "ServiceConnection::onServiceConnected RemoteException!");
      }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
      Log.d(TAG, "ServiceConnection::onServiceDisconnected");
      mCommander = null;
    }
  };
}
