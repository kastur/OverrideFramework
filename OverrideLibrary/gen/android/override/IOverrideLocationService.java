/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\k\\OverrideFramework\\OverrideLibrary\\src\\android\\override\\IOverrideLocationService.aidl
 */
package android.override;

public interface IOverrideLocationService extends android.os.IInterface {

  /**
   * Local-side IPC implementation stub class.
   */
  public static abstract class Stub extends android.os.Binder
      implements android.override.IOverrideLocationService {

    private static final java.lang.String DESCRIPTOR = "android.override.IOverrideLocationService";

    /**
     * Construct the stub at attach it to the interface.
     */
    public Stub() {
      this.attachInterface(this, DESCRIPTOR);
    }

    /**
     * Cast an IBinder object into an android.override.IOverrideLocationService interface, generating a
     * proxy if needed.
     */
    public static android.override.IOverrideLocationService asInterface(android.os.IBinder obj) {
      if ((obj == null)) {
        return null;
      }
      android.os.IInterface iin = (android.os.IInterface) obj.queryLocalInterface(DESCRIPTOR);
      if (((iin != null) && (iin instanceof android.override.IOverrideLocationService))) {
        return ((android.override.IOverrideLocationService) iin);
      }
      return new android.override.IOverrideLocationService.Stub.Proxy(obj);
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
        case TRANSACTION_requestLocationUpdates: {
          data.enforceInterface(DESCRIPTOR);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _arg1;
          _arg1 = data.readString();
          boolean _arg2;
          _arg2 = (0 != data.readInt());
          android.override.IOverrideLocationListener _arg3;
          _arg3 =
              android.override.IOverrideLocationListener.Stub.asInterface(data.readStrongBinder());
          this.requestLocationUpdates(_arg0, _arg1, _arg2, _arg3);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_removeUpdates: {
          data.enforceInterface(DESCRIPTOR);
          android.override.IOverrideLocationListener _arg0;
          _arg0 =
              android.override.IOverrideLocationListener.Stub.asInterface(data.readStrongBinder());
          this.removeUpdates(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getLastKnownLocation: {
          data.enforceInterface(DESCRIPTOR);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _arg1;
          _arg1 = data.readString();
          android.location.Location _result = this.getLastKnownLocation(_arg0, _arg1);
          reply.writeNoException();
          if ((_result != null)) {
            reply.writeInt(1);
            _result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          } else {
            reply.writeInt(0);
          }
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
        case TRANSACTION_reportOverrideLocation: {
          data.enforceInterface(DESCRIPTOR);
          java.lang.String _arg0;
          _arg0 = data.readString();
          android.location.Location _arg1;
          if ((0 != data.readInt())) {
            _arg1 = android.location.Location.CREATOR.createFromParcel(data);
          } else {
            _arg1 = null;
          }
          this.reportOverrideLocation(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
      }
      return super.onTransact(code, data, reply, flags);
    }

    private static class Proxy implements android.override.IOverrideLocationService {

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
// The following functions are visible to applications.

      public void requestLocationUpdates(java.lang.String packageName, java.lang.String provider,
                                         boolean singleShot,
                                         android.override.IOverrideLocationListener listener)
          throws android.os.RemoteException {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(packageName);
          _data.writeString(provider);
          _data.writeInt(((singleShot) ? (1) : (0)));
          _data.writeStrongBinder((((listener != null)) ? (listener.asBinder()) : (null)));
          mRemote.transact(Stub.TRANSACTION_requestLocationUpdates, _data, _reply, 0);
          _reply.readException();
        } finally {
          _reply.recycle();
          _data.recycle();
        }
      }

      public void removeUpdates(android.override.IOverrideLocationListener listener)
          throws android.os.RemoteException {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongBinder((((listener != null)) ? (listener.asBinder()) : (null)));
          mRemote.transact(Stub.TRANSACTION_removeUpdates, _data, _reply, 0);
          _reply.readException();
        } finally {
          _reply.recycle();
          _data.recycle();
        }
      }

      public android.location.Location getLastKnownLocation(java.lang.String packageName,
                                                            java.lang.String provider)
          throws android.os.RemoteException {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.location.Location _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(packageName);
          _data.writeString(provider);
          mRemote.transact(Stub.TRANSACTION_getLastKnownLocation, _data, _reply, 0);
          _reply.readException();
          if ((0 != _reply.readInt())) {
            _result = android.location.Location.CREATOR.createFromParcel(_reply);
          } else {
            _result = null;
          }
        } finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
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

      public void reportOverrideLocation(java.lang.String overrideProvider,
                                         android.location.Location location)
          throws android.os.RemoteException {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(overrideProvider);
          if ((location != null)) {
            _data.writeInt(1);
            location.writeToParcel(_data, 0);
          } else {
            _data.writeInt(0);
          }
          mRemote.transact(Stub.TRANSACTION_reportOverrideLocation, _data, _reply, 0);
          _reply.readException();
        } finally {
          _reply.recycle();
          _data.recycle();
        }
      }
    }

    static final
    int
        TRANSACTION_requestLocationUpdates =
        (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_removeUpdates = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final
    int
        TRANSACTION_getLastKnownLocation =
        (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
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
        TRANSACTION_reportOverrideLocation =
        (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
  }
// The following functions are visible to applications.

  public void requestLocationUpdates(java.lang.String packageName, java.lang.String provider,
                                     boolean singleShot,
                                     android.override.IOverrideLocationListener listener)
      throws android.os.RemoteException;

  public void removeUpdates(android.override.IOverrideLocationListener listener)
      throws android.os.RemoteException;

  public android.location.Location getLastKnownLocation(java.lang.String packageName,
                                                        java.lang.String provider)
      throws android.os.RemoteException;
// The following functions are used by location providers.

  public void addOverrideProvider(java.lang.String overrideProvider)
      throws android.os.RemoteException;

  public void removeOverrideProvider(java.lang.String overrideProvider)
      throws android.os.RemoteException;

  public void reportOverrideLocation(java.lang.String overrideProvider,
                                     android.location.Location location)
      throws android.os.RemoteException;
}
