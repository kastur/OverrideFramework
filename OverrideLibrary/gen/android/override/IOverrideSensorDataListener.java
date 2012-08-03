/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\k\\OverrideFramework\\OverrideLibrary\\src\\android\\override\\IOverrideSensorDataListener.aidl
 */
package android.override;

public interface IOverrideSensorDataListener extends android.os.IInterface {
    /**
     * Local-side IPC implementation stub class.
     */
    public static abstract class Stub extends android.os.Binder implements android.override.IOverrideSensorDataListener {
        private static final java.lang.String DESCRIPTOR = "android.override.IOverrideSensorDataListener";

        /**
         * Construct the stub at attach it to the interface.
         */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an android.override.IOverrideSensorDataListener interface,
         * generating a proxy if needed.
         */
        public static android.override.IOverrideSensorDataListener asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            android.os.IInterface iin = (android.os.IInterface) obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof android.override.IOverrideSensorDataListener))) {
                return ((android.override.IOverrideSensorDataListener) iin);
            }
            return new android.override.IOverrideSensorDataListener.Stub.Proxy(obj);
        }

        public android.os.IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_onSensorDataChanged: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0;
                    _arg0 = data.readInt();
                    float[] _arg1;
                    _arg1 = data.createFloatArray();
                    long _arg2;
                    _arg2 = data.readLong();
                    this.onSensorDataChanged(_arg0, _arg1, _arg2);
                    reply.writeNoException();
                    return true;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }

        private static class Proxy implements android.override.IOverrideSensorDataListener {
            private android.os.IBinder mRemote;

            Proxy(android.os.IBinder remote) {
                mRemote = remote;
            }

            public android.os.IBinder asBinder() {
                return mRemote;
            }

            public java.lang.String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            public void onSensorDataChanged(int sensorType, float[] values, long nano_timestamp) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(sensorType);
                    _data.writeFloatArray(values);
                    _data.writeLong(nano_timestamp);
                    mRemote.transact(Stub.TRANSACTION_onSensorDataChanged, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        static final int TRANSACTION_onSensorDataChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    }

    public void onSensorDataChanged(int sensorType, float[] values, long nano_timestamp) throws android.os.RemoteException;
}
