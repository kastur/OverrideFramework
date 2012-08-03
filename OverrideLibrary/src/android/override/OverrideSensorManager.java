package android.override;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.*;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class OverrideSensorManager {
    private static final String TAG = "OverrideSensorManager";
    private final ArrayList<ListenerDelegate> sListeners = new ArrayList<ListenerDelegate>();
    private Context mContext;
    private Looper mMainLooper;
    private IOverrideSensorService mService;
    private SensorEventPool sPool;

    public OverrideSensorManager(Context context) {
        mContext = context;
        mMainLooper = context.getMainLooper();
        sPool = new SensorEventPool(100);

        Intent serviceIntent = new Intent("android.override.OverrideSensorService");
        context.bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public Sensor getSensor(int sensorType) {
        SensorManager sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        return sensorManager.getDefaultSensor(sensorType);
    }

    public boolean registerListener(SensorEventListener listener, Sensor sensor, int rate) {
        return registerListener(listener, sensor, rate, null);
    }

    public boolean registerListener(SensorEventListener listener, Sensor sensor, int rate, Handler handler) {
        if (listener == null || sensor == null) {
            return false;
        }
        boolean result = true;
        int delay = -1;

        synchronized (sListeners) {
            // look for this listener in our list
            ListenerDelegate l = null;
            for (ListenerDelegate i : sListeners) {
                if (i.getListener() == listener) {
                    l = i;
                    break;
                }
            }

            // if we don't find it, add it to the list
            if (l == null) {
                l = new ListenerDelegate(listener, sensor, handler);
                sListeners.add(l);
                // if the list is not empty, start our main thread
                if (sListeners.isEmpty()) {
                    // weird, we couldn't add the listener
                    result = false;
                }
            } else {
                l.addSensor(sensor);
                if (!enableSensorLocked(sensor, delay)) {
                    // oops. there was an error
                    l.removeSensor(sensor);
                    result = false;
                }
            }
        }

        return result;
    }

    private void unregisterListener(SensorEventListener listener, Sensor sensor) {
        if (listener == null || sensor == null) {
            return;
        }

        synchronized (sListeners) {
            final int size = sListeners.size();
            for (int i = 0; i < size; i++) {
                ListenerDelegate l = sListeners.get(i);
                if (l.getListener() == listener) {
                    if (l.removeSensor(sensor) == 0) {
                        // if we have no more sensors enabled on this listener,
                        // take it off the list.
                        sListeners.remove(i);
                    }
                    break;
                }
            }
            disableSensorLocked(sensor);
        }
    }

    private void unregisterListener(SensorEventListener listener) {
        if (listener == null) {
            return;
        }

        synchronized (sListeners) {
            final int size = sListeners.size();
            for (int i = 0; i < size; i++) {
                ListenerDelegate l = sListeners.get(i);
                if (l.getListener() == listener) {
                    sListeners.remove(i);
                    // disable all sensors for this listener
                    for (Sensor sensor : l.getSensors()) {
                        disableSensorLocked(sensor);
                    }
                    break;
                }
            }
        }
    }

    private boolean enableSensorLocked(Sensor sensor, int delay) {
        for (ListenerDelegate i : sListeners) {
            if (i.hasSensor(sensor)) {
                try {
                    if (mService == null)
                        return false;
                    mService.enable_sensor(mContext.getPackageName(), sensor.getName());
                    return true;
                } catch (RemoteException ex) {
                    Log.e(TAG, "disableSensorLocked : RemoteException");
                    return false;
                }
            }
        }
        return false;
    }

    private boolean disableSensorLocked(Sensor sensor) {
        for (ListenerDelegate i : sListeners) {
            if (i.hasSensor(sensor)) {
                // not an error, it's just that this sensor is still in use
                return true;
            }
        }
        try {
            if (mService == null)
                return false;
            mService.disable_sensor(mContext.getPackageName(), sensor.getName());
            return true;
        } catch (RemoteException ex) {
            Log.e(TAG, "disableSensorLocked : RemoteException");
            return false;
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            mService = IOverrideSensorService.Stub.asInterface(binder);
            try {
                mService.registerSensorDataListener(mContext.getPackageName(), mSensorDataListener);
            } catch (RemoteException ex) {
                Log.e(TAG, "onServiceConnected : RemoteException");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
        }
    };

    private IOverrideSensorDataListener.Stub mSensorDataListener = new IOverrideSensorDataListener.Stub() {

        @Override
        public void onSensorDataChanged(int sensorType, float[] values, long nano_timestamp) {
            synchronized (sListeners) {
                for (ListenerDelegate listener : sListeners) {
                    listener.onSensorChangedLocked(getSensor(sensorType), values, new long[]{nano_timestamp}, 0);
                }
            }
        }
    };

    private class SensorEventPool {
        private final int mPoolSize;
        private final SensorEvent mPool[];
        private int mNumItemsInPool;

        private SensorEvent createSensorEvent() {
            // Hack to construct SensorEvents
            Class<SensorEvent> clazz = SensorEvent.class;
            Constructor<SensorEvent> c;
            try {
                c = clazz.getDeclaredConstructor(int.class);

            } catch (NoSuchMethodException ex) {
                ex.printStackTrace();
                return null;
            }
            c.setAccessible(true);

            SensorEvent event;
            try {
                event = c.newInstance(3);
            } catch (InstantiationException ex) {
                ex.printStackTrace();
                return null;
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
                return null;
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
                return null;
            }

            return event;
        }

        SensorEventPool(int poolSize) {
            mPoolSize = poolSize;
            mNumItemsInPool = poolSize;
            mPool = new SensorEvent[poolSize];
        }

        SensorEvent getFromPool() {
            SensorEvent t = null;
            synchronized (this) {
                if (mNumItemsInPool > 0) {
                    // remove the "top" item from the pool
                    final int index = mPoolSize - mNumItemsInPool;
                    t = mPool[index];
                    mPool[index] = null;
                    mNumItemsInPool--;
                }
            }
            if (t == null) {
                // the pool was empty or this item was removed from the pool for
                // the first time. In any case, we need to create a new item.
                t = createSensorEvent();
            }
            return t;
        }

        void returnToPool(SensorEvent t) {
            synchronized (this) {
                // is there space left in the pool?
                if (mNumItemsInPool < mPoolSize) {
                    // if so, return the item to the pool
                    mNumItemsInPool++;
                    final int index = mPoolSize - mNumItemsInPool;
                    mPool[index] = t;
                }
            }
        }
    }

    private class ListenerDelegate {
        private final SensorEventListener mSensorEventListener;
        private final HashMap<String, Sensor> mSensorList = new HashMap<String, Sensor>();
        private final Handler mHandler;

        ListenerDelegate(SensorEventListener listener, Sensor sensor, Handler handler) {
            mSensorEventListener = listener;

            Looper looper = (handler != null) ? handler.getLooper() : mMainLooper;
            mHandler = new Handler(looper) {
                @Override
                public void handleMessage(Message msg) {
                    final SensorEvent t = (SensorEvent) msg.obj;
                    mSensorEventListener.onSensorChanged(t);
                    sPool.returnToPool(t);
                }
            };
            addSensor(sensor);
        }

        Object getListener() {
            return mSensorEventListener;
        }

        void addSensor(Sensor sensor) {
            mSensorList.put(sensor.getName(), sensor);
        }

        int removeSensor(Sensor sensor) {
            mSensorList.remove(sensor.getName());
            return mSensorList.size();
        }

        boolean hasSensor(Sensor sensor) {
            return mSensorList.values().contains(sensor);
        }

        boolean hasSensor(String sensorName) {
            return mSensorList.containsKey(sensorName);
        }

        public Collection<Sensor> getSensors() {
            return mSensorList.values();
        }

        void onSensorChangedLocked(Sensor sensor, float[] values, long[] timestamp, int accuracy) {
            SensorEvent t = sPool.getFromPool();
            final float[] v = t.values;
            v[0] = values[0];
            v[1] = values[1];
            v[2] = values[2];
            t.sensor = sensor;
            t.timestamp = timestamp[0];
            t.accuracy = accuracy;
            Message msg = Message.obtain();
            msg.what = 0;
            msg.obj = t;
            mHandler.sendMessage(msg);
        }
    }

}
