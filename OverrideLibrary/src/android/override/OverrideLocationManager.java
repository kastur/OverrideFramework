package android.override;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.*;
import android.util.Log;

import java.util.HashMap;

// The OverrideLocationManager is a bare-bones version of the default Android LocationManager.
// If implemented as part of the Android Framework, we will make this class extend LocationManager directly,
// and when external apps call "Context.getSystemService", they will receive an instance of this class.
// However, here as a demonstration we have implemented it as a class external to the Android Framework.
// Applications have to explicitly initialize this class.
public class OverrideLocationManager {
    private static final String TAG = "OverrideLocationManager";
    private Context mContext;
    private IOverrideLocationService mService;
    private LocationManager mLocationManager;

    public OverrideLocationManager(Context context) {
        mContext = context;
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        Intent serviceIntent = new Intent("android.override.OverrideLocationService");
        mContext.bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void requestLocationUpdates(String provider, long minTime, float minDistance, LocationListener listener) {
        _requestLocationUpdates(provider, false, listener, null);
    }

    public void requestLocationUpdates(String provider, long minTime, float minDistance, LocationListener listener, Looper looper) {
        _requestLocationUpdates(provider, false, listener, looper);
    }

    public void requestLocationUpdates(long minTime, float minDistance, Criteria criteria, LocationListener listener, Looper looper) {
        mLocationManager.requestLocationUpdates(minTime, minDistance, criteria, listener, looper);
    }

    public void requestLocationUpdates(String provider, long minTime, float minDistance, PendingIntent intent) {
        mLocationManager.requestLocationUpdates(provider, minTime, minDistance, intent);
    }

    public void requestLocationUpdates(long minTime, float minDistance, Criteria criteria, PendingIntent intent) {
        mLocationManager.requestLocationUpdates(minTime, minDistance, criteria, intent);
    }

    public void requestSingleUpdate(String provider, LocationListener listener, Looper looper) {
        _requestLocationUpdates(provider, true, listener, looper);
    }

    public void requestSingleUpdate(Criteria criteria, LocationListener listener, Looper looper) {
        mLocationManager.requestSingleUpdate(criteria, listener, looper);
    }

    public void requestSingleUpdate(String provider, PendingIntent intent) {
        mLocationManager.requestSingleUpdate(provider, intent);
    }

    public void requestSingleUpdate(Criteria criteria, PendingIntent intent) {
        mLocationManager.requestSingleUpdate(criteria, intent);
    }

    public void removeUpdates(LocationListener listener) {
        _removeUpdates(listener);
    }

    public void removeUpdates(PendingIntent intent) {
        mLocationManager.removeUpdates(intent);
    }

    public void addProximityAlert(double latitude, double longitude, float radius, long expiration, PendingIntent intent) {
        mLocationManager.addProximityAlert(latitude, longitude, radius, expiration, intent);
    }

    public void removeProximityAlert(PendingIntent intent) {
        mLocationManager.removeProximityAlert(intent);
    }

    public boolean isProviderEnabled(String provider) {
        return mLocationManager.isProviderEnabled(provider);
    }

    public Location getLastKnownLocation(String provider) {
        return mLocationManager.getLastKnownLocation(provider);
    }

    // ------------------------------------------------------------------------

    private final HashMap<LocationListener, ListenerTransport> mListeners =
            new HashMap<LocationListener, ListenerTransport>();

    private void _requestLocationUpdates(String provider, boolean singleShot, LocationListener listener, Looper looper) {
        try {
            synchronized (mListeners) {
                ListenerTransport transport = mListeners.get(listener);
                if (transport == null) {
                    transport = new ListenerTransport(listener, looper);
                }
                mListeners.put(listener, transport);

                if (mService == null) {
                    Log.i(TAG, "Location service is not available.");
                } else {
                    mService.requestLocationUpdates(mContext.getPackageName(), provider, singleShot, transport);
                }
            }
        } catch (RemoteException ex) {
            Log.e(TAG, "requestLocationUpdates: DeadObjectException", ex);
        }
    }

    private void _removeUpdates(LocationListener listener) {
        ListenerTransport transport = mListeners.get(listener);
        try {
            if (transport != null)
                mService.removeUpdates(transport);
        } catch (RemoteException ex) {
            Log.e(TAG, "transport: DeadObjectException", ex);
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            Log.i(TAG, "Connected to service");
            mService = IOverrideLocationService.Stub.asInterface(binder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
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

}
