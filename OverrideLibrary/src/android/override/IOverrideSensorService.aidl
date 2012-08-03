package android.override;

import android.override.IOverrideSensorDataListener;

interface IOverrideSensorService {
    void registerSensorDataListener(String packageName, IOverrideSensorDataListener listener);
    void enable_sensor(String packageName, String sensorName);
    void disable_sensor(String packageName, String sensorName);

    // The following functions are used by location providers.
    void addOverrideProvider(String overrideProvider);
    void removeOverrideProvider(String overrideProvider);
    void reportOverrideSensorData(String overrideProvider, String sensorName, in float[] values, long nano_timestamp);
}