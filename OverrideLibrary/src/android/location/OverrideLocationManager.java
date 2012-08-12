package android.location;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.override.IOverrideLocationListener;
import android.override.IOverrideLocationService;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

// The OverrideLocationManager is a bare-bones version of the default Android LocationManager.
// If implemented as part of the Android Framework, we will make this class extend LocationManager directly,
// and when external apps call "Context.getSystemService", they will receive an instance of this class.
// However, here as a demonstration we have implemented it as a class external to the Android Framework.
// Applications have to explicitly initialize this class.
public class OverrideLocationManager extends LocationManager {

  private static final String TAG = "OverrideLocationManager";
  private Context mContext;
  private IOverrideLocationService mService;
  //private LocationManager mLocationManager;

  public OverrideLocationManager(Context context) {
    super(null);
    Log.i(TAG, context.getPackageName() + " constructed a new OverrideLocationManager");
    mContext = context;
    //mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

    Intent serviceIntent = new Intent("android.override.OverrideLocationService");
    mContext.bindService(serviceIntent, mServiceConnection, Context.BIND_DEBUG_UNBIND);
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    if (mContext != null && mServiceConnection != null) {
      mContext.unbindService(mServiceConnection);
    }
  }

  @Override
  public boolean isProviderEnabled(String provider) {
    if (provider == LocationManager.GPS_PROVIDER) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public String getBestProvider(Criteria criteria, boolean i_dont_know_what) {
    return LocationManager.GPS_PROVIDER;
  }

  @Override
  public void requestLocationUpdates(String provider, long minTime, float minDistance,
                                     LocationListener listener) {
    _requestLocationUpdates(provider, false, listener, null);
  }

  @Override
  public void requestLocationUpdates(String provider, long minTime, float minDistance,
                                     LocationListener listener, Looper looper) {
    _requestLocationUpdates(provider, false, listener, looper);
  }

  @Override
  public void requestLocationUpdates(long minTime, float minDistance, Criteria criteria,
                                     LocationListener listener, Looper looper) {
    Log.i(TAG,
          "Blocked requestLocationUpdates(long minTime, float minDistance, Criteria criteria, LocationListener listener, Looper looper)");
    //mLocationManager.requestLocationUpdates(minTime, minDistance, criteria, listener, looper);
  }

  @Override
  public void requestLocationUpdates(String provider, long minTime, float minDistance,
                                     PendingIntent intent) {
    Log.i(TAG,
          "Blocked requestLocationUpdates(String provider, long minTime, float minDistance, PendingIntent intent)");
    //mLocationManager.requestLocationUpdates(provider, minTime, minDistance, intent);
  }

  @Override
  public void requestLocationUpdates(long minTime, float minDistance, Criteria criteria,
                                     PendingIntent intent) {
    Log.i(TAG,
          "Blocked requestLocationUpdates(long minTime, float minDistance, Criteria criteria, PendingIntent intent)");
    //mLocationManager.requestLocationUpdates(minTime, minDistance, criteria, intent);
  }

  @Override
  public void requestSingleUpdate(String provider, LocationListener listener, Looper looper) {
    _requestLocationUpdates(provider, true, listener, looper);
  }

  @Override
  public void requestSingleUpdate(Criteria criteria, LocationListener listener, Looper looper) {
    Log.i(TAG,
          "Blocked requestSingleUpdate(Criteria criteria, LocationListener listener, Looper looper)");
    //mLocationManager.requestSingleUpdate(criteria, listener, looper);
  }

  @Override
  public void requestSingleUpdate(String provider, PendingIntent intent) {
    Log.i(TAG, "Blocked requestSingleUpdate(String provider, PendingIntent intent)");
    //mLocationManager.requestSingleUpdate(provider, intent);
  }

  @Override
  public void requestSingleUpdate(Criteria criteria, PendingIntent intent) {
    Log.i(TAG, "Blocked requestSingleUpdate(Criteria criteria, PendingIntent intent)");
    //mLocationManager.requestSingleUpdate(criteria, intent);
  }

  @Override
  public void removeUpdates(LocationListener listener) {
    _removeUpdates(listener);
  }

  @Override
  public void removeUpdates(PendingIntent intent) {
    Log.i(TAG, "Blocked removeUpdates(PendingIntent intent)");
    //mLocationManager.removeUpdates(intent);
  }

  @Override
  public void addProximityAlert(double latitude, double longitude, float radius, long expiration,
                                PendingIntent intent) {
    Log.i(TAG,
          "Blocked addProximityAlert(double latitude, double longitude, float radius, long expiration, PendingIntent intent)");
    //mLocationManager.addProximityAlert(latitude, longitude, radius, expiration, intent);
  }

  @Override
  public void removeProximityAlert(PendingIntent intent) {
    Log.i(TAG, "Blocked removeProximityAlert(PendingIntent intent)");
    //mLocationManager.removeProximityAlert(intent);
  }

  @Override
  public Location getLastKnownLocation(String provider) {
    if (mService == null) {
      Log.i(TAG,
            "Blocked getLastKnownLocation(String provider). Returned location of empire state!");
      //return mLocationManager.getLastKnownLocation(provider);
      Location empire_state = new Location(provider);
      empire_state.setLatitude(40.748502);
      empire_state.setLongitude(-73.98445);
      empire_state.setProvider(provider);
      empire_state.setTime(System.currentTimeMillis());
      return empire_state;
    } else {
      try {
        return mService.getLastKnownLocation(mContext.getPackageName(), provider);
      } catch (RemoteException ex) {
        Log.e(TAG, "getLastKnownLocation: DeadObjectException", ex);
        return new Location(provider);
      }

    }
  }

  @Override
  public LocationProvider getProvider(String provider) {
    Log.i(TAG, "getProvider: return new LocationProvider();");
    return new DefaultLocationProvider();
  }

  @Override
  public boolean addNmeaListener(GpsStatus.NmeaListener listener) {
    Log.i(TAG, "addNmeaListener: return false;");
    return false;
  }

  @Override
  public void removeNmeaListener(GpsStatus.NmeaListener listener) {
    Log.i(TAG, "removeNmeaListener: return;");
    return;
  }

  @Override
  public List<String> getAllProviders() {
    Log.i(TAG, "getAllProviders: return {\"gps\"}");
    List<String> list = new ArrayList<String>();
    list.add(LocationManager.GPS_PROVIDER);
    return list;
  }

  @Override
  public List<String> getProviders(boolean enabledOnly) {
    Log.v(TAG, "getAllProviders");
    return getAllProviders();
  }

  @Override
  public List<String> getProviders(Criteria criteria, boolean enabledOnly) {
    Log.v(TAG, "getAllProviders");
    return getAllProviders();
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

  @Override
  public void addTestProvider(String name, boolean requiresNetwork, boolean requiresSatellite,
                              boolean requiresCell, boolean hasMonetaryCost,
                              boolean supportsAltitude, boolean supportsSpeed,
                              boolean supportsBearing, int powerRequirement, int accuracy) {
    Log.v(TAG, "addTestProvider");
  }

  @Override
  public void removeTestProvider(String provider) {
    Log.v(TAG, "removeTestProvider");
  }

  @Override
  public void setTestProviderLocation(String provider, Location loc) {
    Log.v(TAG, "setTestProviderLocation");
  }

  @Override
  public void clearTestProviderLocation(String provider) {
    Log.v(TAG, "clearTestProviderLocation");
  }

  @Override
  public void setTestProviderEnabled(String provider, boolean enabled) {
    Log.v(TAG, "setTestProviderEnabled");
  }

  @Override
  public void clearTestProviderEnabled(String provider) {
    Log.v(TAG, "clearTestProviderEnabled");
  }

  @Override
  public void setTestProviderStatus(String provider, int status, Bundle extras, long updateTime) {
    Log.v(TAG, "setTestProviderStatus");
  }

  @Override
  public void clearTestProviderStatus(String provider) {
    Log.v(TAG, "clearTestProviderStatus");
  }

  // ------------------------------------------------------------------------

  private final HashMap<LocationListener, ListenerTransport> mListeners =
      new HashMap<LocationListener, ListenerTransport>();

  private LinkedList<Runnable> mTaskeQueue = new LinkedList<Runnable>();

  private void _requestLocationUpdates(String provider, boolean singleShot,
                                       LocationListener listener, Looper looper) {
    if (mService == null) {
      Log.i(TAG, "Deferring _requestLocationUpdates: " + provider + "," + singleShot);
      final String final_provider = provider;
      final boolean final_singleShot = singleShot;
      final LocationListener final_listener = listener;
      final Looper final_looper = looper;
      Runnable task = new Runnable() {
        @Override
        public void run() {
          _handleRequestLocationUpdates(final_provider, final_singleShot, final_listener,
                                        final_looper);
        }
      };
      mTaskeQueue.add(task);
    } else {
      _handleRequestLocationUpdates(provider, singleShot, listener, looper);
    }
  }

  private void _removeUpdates(LocationListener listener) {
    if (mService == null) {
      Log.i(TAG, "Deferring _removeUpdates " + listener);
      final LocationListener final_listener = listener;
      Runnable task = new Runnable() {
        @Override
        public void run() {
          _handleRemoveUpdates(final_listener);
        }
      };
      mTaskeQueue.add(task);
    } else {
      _handleRemoveUpdates(listener);
    }
  }

  private void _handleRequestLocationUpdates(String provider, boolean singleShot,
                                             LocationListener listener, Looper looper) {
    Log.i(TAG, "_requestLocationUpdates: " + provider + "," + singleShot);
    try {
      synchronized (mListeners) {
        ListenerTransport transport = mListeners.get(listener);
        if (transport == null) {
          transport = new ListenerTransport(listener, looper);
        }
        mListeners.put(listener, transport);

        if (mService == null) {
          Log.i(TAG, "_requestLocationUpdates: Location service is not available.");
        } else {
          mService
              .requestLocationUpdates(mContext.getPackageName(), provider, singleShot, transport);
        }
      }

    } catch (RemoteException ex) {
      Log.e(TAG, "requestLocationUpdates: DeadObjectException", ex);
    }
  }

  private void _handleRemoveUpdates(LocationListener listener) {
    Log.i(TAG, "_removeUpdates: ");
    ListenerTransport transport = mListeners.get(listener);
    try {
      if (transport != null) {
        if (mService == null) {
          Log.i(TAG, "_requestLocationUpdates: Location service is not available.");
        } else {
          mService.removeUpdates(transport);
        }
      }
    } catch (RemoteException ex) {
      Log.e(TAG, "_removeUpdates transport: DeadObjectException", ex);
    }
  }

  private ServiceConnection mServiceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder binder) {
      Log.i(TAG, "onServiceConnected");
      mService = IOverrideLocationService.Stub.asInterface(binder);

      for (Runnable task : mTaskeQueue) {
        task.run();
      }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
      Log.i(TAG, "onServiceDisconnected");
      mService = null;
    }
  };

  private class ListenerTransport extends IOverrideLocationListener.Stub {

    private static final int TYPE_LOCATION_CHANGED = 1;
    private static final int TYPE_STATUS_CHANGED = 2;
    private static final int TYPE_PROVIDER_ENABLED = 3;
    private static final int TYPE_PROVIDER_DISABLED = 4;

    private LocationListener mListener;
    private final Handler mListenerHandler;

    ListenerTransport(LocationListener listener, Looper looper) {
      mListener = listener;

      if (looper == null) {
        mListenerHandler = new Handler() {
          @Override
          public void handleMessage(Message msg) {
            _handleMessage(msg);
          }
        };
      } else {
        mListenerHandler = new Handler(looper) {
          @Override
          public void handleMessage(Message msg) {
            _handleMessage(msg);
          }
        };
      }
    }

    public void onLocationChanged(Location location) {
      Message msg = Message.obtain();
      msg.what = TYPE_LOCATION_CHANGED;
      msg.obj = location;
      mListenerHandler.sendMessage(msg);
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
      Message msg = Message.obtain();
      msg.what = TYPE_STATUS_CHANGED;
      Bundle b = new Bundle();
      b.putString("provider", provider);
      b.putInt("status", status);
      if (extras != null) {
        b.putBundle("extras", extras);
      }
      msg.obj = b;
      mListenerHandler.sendMessage(msg);
    }

    public void onProviderEnabled(String provider) {
      Message msg = Message.obtain();
      msg.what = TYPE_PROVIDER_ENABLED;
      msg.obj = provider;
      mListenerHandler.sendMessage(msg);
    }

    public void onProviderDisabled(String provider) {
      Message msg = Message.obtain();
      msg.what = TYPE_PROVIDER_DISABLED;
      msg.obj = provider;
      mListenerHandler.sendMessage(msg);
    }

    private void _handleMessage(Message msg) {
      switch (msg.what) {
        case TYPE_LOCATION_CHANGED:
          Location location = new Location((Location) msg.obj);
          mListener.onLocationChanged(location);
          break;
        case TYPE_STATUS_CHANGED:
          Bundle b = (Bundle) msg.obj;
          String provider = b.getString("provider");
          int status = b.getInt("status");
          Bundle extras = b.getBundle("extras");
          mListener.onStatusChanged(provider, status, extras);
          break;
        case TYPE_PROVIDER_ENABLED:
          mListener.onProviderEnabled((String) msg.obj);
          break;
        case TYPE_PROVIDER_DISABLED:
          mListener.onProviderDisabled((String) msg.obj);
          break;
      }

      /* Was needed before to release wakelocks.
      try {
          mService.locationCallbackFinished(this);
      } catch (RemoteException e) {
          Log.e(TAG, "locationCallbackFinished: RemoteException", e);
      }
      */
    }
  }

  private class DefaultLocationProvider extends LocationProvider {

    public DefaultLocationProvider() {
      Log.i(TAG, "Constructing DefaultLocationProvider");
    }

    @Override
    public boolean requiresNetwork() {
      return false;
    }

    @Override
    public boolean requiresSatellite() {
      return false;
    }

    @Override
    public boolean requiresCell() {
      return false;
    }

    @Override
    public boolean hasMonetaryCost() {
      return false;
    }

    @Override
    public boolean supportsAltitude() {
      return false;
    }

    @Override
    public boolean supportsSpeed() {
      return false;
    }

    @Override
    public boolean supportsBearing() {
      return false;
    }

    @Override
    public int getPowerRequirement() {
      return Criteria.POWER_LOW;
    }

    @Override
    public int getAccuracy() {
      return Criteria.ACCURACY_FINE;
    }
  }

  ;

}
