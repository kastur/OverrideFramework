package android.override;

interface IOverrideSensorDataListener {
    void onSensorDataChanged(int sensorType, in float[] values, long nano_timestamp);
}