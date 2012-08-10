package android.override;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class OverrideSensorService extends Service {

    private final HashMap<String, IOverrideSensorDataListener> mListeners = new HashMap<String, IOverrideSensorDataListener>();


    @Override
    public void onCreate() {
        super.onCreate();

        // TODO: We send out random accelerometer data to apps that request it. We need to permit external providers!
        mTimer.schedule(mTimerTask, 1000, 10000);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private class ServiceBinder extends IOverrideSensorService.Stub {
        @Override
        public void registerSensorDataListener(String packageName, IOverrideSensorDataListener listener) {
            synchronized (mListeners) {
                mListeners.put(packageName, listener);
            }
        }

        @Override
        public void enable_sensor(String packageName, String sensorName) throws RemoteException {
        }

        @Override
        public void disable_sensor(String packageName, String sensorName) throws RemoteException {
        }

        @Override
        public void addOverrideProvider(String overrideProvider) throws RemoteException {
        }

        @Override
        public void removeOverrideProvider(String overrideProvider) throws RemoteException {
        }

        @Override
        public void reportOverrideSensorData(String overrideProvider, String sensorName, float[] values, long nano_timestamp) {

        }
    }

    private IBinder mBinder = new ServiceBinder();
    private Timer mTimer = new Timer("RandomAccelerometerUpdatesProvider");
    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            synchronized (mListeners) {
                ArrayList<IOverrideSensorDataListener> deadListeners = new ArrayList<IOverrideSensorDataListener>();
                for (IOverrideSensorDataListener listener : mListeners.values()) {
                    try {
                        listener.onSensorDataChanged(Sensor.TYPE_ACCELEROMETER, new float[]{(float) Math.random(), (float) Math.random(), (float) Math.random()}, System.nanoTime());
                    } catch (RemoteException ex) {
                        deadListeners.add(listener);
                    }
                }

                for (IOverrideSensorDataListener deadListener : deadListeners) {
                    //TODO: Remove dead listeners.
                }
            }
        }
    };


}
