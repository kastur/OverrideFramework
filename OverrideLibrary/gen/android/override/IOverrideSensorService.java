/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\k\\OverrideFramework\\OverrideLibrary\\src\\android\\override\\IOverrideSensorService.aidl
 */
package android.override;

public interface IOverrideSensorService extends android.os.IInterface {

  /**
   * Local-side IPC implementation stub class.
   */
  public static abstract class Stub extends android.os.Binder
      implements android.override.IOverrideSensorService {

    private static final java.lang.String DESCRIPTOR = "android.override.IOverrideSensorService";

    /**
     * Construct the stub at attach it to the interface.
     */
    public Stub() {
      this.attachInterface(this, DESCRIPTOR);
    }

    /**
     * Cast an IBinder object into an android.override.IOverrideSensorService interface, generating a
     * proxy if needed.
     */
    public static android.override.IOverrideSensorService asInterface(android.os.IBinder obj) {
      if ((obj == null)) {
        return null;
      }
      android.os.IInterface iin = (android.os.IInterface) obj.queryLocalInterface(DESCRIPTOR);
      if (((iin != null) && (iin instanceof android.override.IOverrideSensorService))) {
        return ((android.override.IOverrideSensorService) iin);
      }
      return new android.override.IOverrideSensorService.Stub.Proxy(obj);
    }

    public android.os.IBinder asBinder() {
      return this;
    }

    @Override
    public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags)
        throws android.os.RemoteException {
      switch (code) {
        case INTERFACE_TRANSACTION: {
          reply.writeString(DESCRIPTOR);
          return true;
        }
        case TRANSACTION_registerSensorDataListener: {
          data.enforceInterface(DESCRIPTOR);
          java.lang.String _arg0;
          _arg0 = data.readString();
          android.override.IOverrideSensorDataListener _arg1;
          _arg1 =
              android.override.IOverrideSensorDataListener.Stub
                  .asInterface(data.readStrongBinder());
          this.registerSensorDataListener(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_enable_sensor: {
          data.enforceInterface(DESCRIPTOR);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _arg1;
          _arg1 = data.readString();
          this.enable_sensor(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_disable_sensor: {
          data.enforceInterface(DESCRIPTOR);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _arg1;
          _arg1 = data.readString();
          this.disable_sensor(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_addOverrideProvider: {
          data.enforceInterface(DESCRIPTOR);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.addOverrideProvider(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_removeOverrideProvider: {
          data.enforceInterface(DESCRIPTOR);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.removeOverrideProvider(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_reportOverrideSensorData: {
          data.enforceInterface(DESCRIPTOR);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _arg1;
          _arg1 = data.readString();
          float[] _arg2;
          _arg2 = data.createFloatArray();
          long _arg3;
          _arg3 = data.readLong();
          this.reportOverrideSensorData(_arg0, _arg1, _arg2, _arg3);
          reply.writeNoException();
          return true;
        }
      }
      return super.onTransact(code, data, reply, flags);
    }

    private static class Proxy implements android.override.IOverrideSensorService {

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

      public void registerSensorDataListener(java.lang.String packageName,
                                             android.override.IOverrideSensorDataListener listener)
          throws android.os.RemoteException {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(packageName);
          _data.writeStrongBinder((((listener != null)) ? (listener.asBinder()) : (null)));
          mRemote.transact(Stub.TRANSACTION_registerSensorDataListener, _data, _reply, 0);
          _reply.readException();
        } finally {
          _reply.recycle();
          _data.recycle();
        }
      }

      public void enable_sensor(java.lang.String packageName, java.lang.String sensorName)
          throws android.os.RemoteException {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(packageName);
          _data.writeString(sensorName);
          mRemote.transact(Stub.TRANSACTION_enable_sensor, _data, _reply, 0);
          _reply.readException();
        } finally {
          _reply.recycle();
          _data.recycle();
        }
      }

      public void disable_sensor(java.lang.String packageName, java.lang.String sensorName)
          throws android.os.RemoteException {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(packageName);
          _data.writeString(sensorName);
          mRemote.transact(Stub.TRANSACTION_disable_sensor, _data, _reply, 0);
          _reply.readException();
        } finally {
          _reply.recycle();
          _data.recycle();
        }
      }
// The following functions are used by location providers.

      public void addOverrideProvider(java.lang.String overrideProvider)
          throws android.os.RemoteException {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(overrideProvider);
          mRemote.transact(Stub.TRANSACTION_addOverrideProvider, _data, _reply, 0);
          _reply.readException();
        } finally {
          _reply.recycle();
          _data.recycle();
        }
      }

      public void removeOverrideProvider(java.lang.String overrideProvider)
          throws android.os.RemoteException {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(overrideProvider);
          mRemote.transact(Stub.TRANSACTION_removeOverrideProvider, _data, _reply, 0);
          _reply.readException();
        } finally {
          _reply.recycle();
          _data.recycle();
        }
      }

      public void reportOverrideSensorData(java.lang.String overrideProvider,
                                           java.lang.String sensorName, float[] values,
                                           long nano_timestamp) throws android.os.RemoteException {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(overrideProvider);
          _data.writeString(sensorName);
          _data.writeFloatArray(values);
          _data.writeLong(nano_timestamp);
          mRemote.transact(Stub.TRANSACTION_reportOverrideSensorData, _data, _reply, 0);
          _reply.readException();
        } finally {
          _reply.recycle();
          _data.recycle();
        }
      }
    }

    static final
    int
        TRANSACTION_registerSensorDataListener =
        (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_enable_sensor = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_disable_sensor = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    static final
    int
        TRANSACTION_addOverrideProvider =
        (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    static final
    int
        TRANSACTION_removeOverrideProvider =
        (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
    static final
    int
        TRANSACTION_reportOverrideSensorData =
        (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
  }

  public void registerSensorDataListener(java.lang.String packageName,
                                         android.override.IOverrideSensorDataListener listener)
      throws android.os.RemoteException;

  public void enable_sensor(java.lang.String packageName, java.lang.String sensorName)
      throws android.os.RemoteException;

  public void disable_sensor(java.lang.String packageName, java.lang.String sensorName)
      throws android.os.RemoteException;
// The following functions are used by location providers.

  public void addOverrideProvider(java.lang.String overrideProvider)
      throws android.os.RemoteException;

  public void removeOverrideProvider(java.lang.String overrideProvider)
      throws android.os.RemoteException;

  public void reportOverrideSensorData(java.lang.String overrideProvider,
                                       java.lang.String sensorName, float[] values,
                                       long nano_timestamp) throws android.os.RemoteException;
}
