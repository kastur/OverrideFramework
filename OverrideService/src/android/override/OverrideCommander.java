package android.override;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.util.PrintWriterPrinter;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class OverrideCommander extends IOverrideCommander.Stub {

  private static final String TAG = "OverrideCommander";

  private HashSet<IOverrideCommandListener> mListeners = new HashSet<IOverrideCommandListener>();
  private HashMap<String, HashSet<IOverrideCommandListener>> mListenersByPackage =
      new HashMap<String, HashSet<IOverrideCommandListener>>();


  @Override
  public void onStatusChanged(String packageName, Bundle data) throws RemoteException {

  }

  @Override
  public synchronized void registerCommandListener(String packageName, IOverrideCommandListener listener) throws RemoteException {
    mListeners.add(listener);
    if (!mListenersByPackage.containsKey(packageName))
      mListenersByPackage.put(packageName, new HashSet<IOverrideCommandListener>());
    mListenersByPackage.get(packageName).add(listener);
  }

  @Override
  public void removeCommandListener(String packageName, IOverrideCommandListener listener) throws RemoteException {
    mListeners.remove(listener);
    mListenersByPackage.get(packageName).remove(listener);
  }

  public void onCommand(String packageName, Bundle data) {
    for (IOverrideCommandListener listener : mListenersByPackage.get(packageName)) {
      try {
        listener.onCommand(data);
      } catch (RemoteException ex) {
        Log.d(TAG, "onCommand: " + packageName + " RemoteException!");
      }
    }
  }

  public void onReceiveData(Bundle data) {
    onCommand(data.getString("PACKAGE"), data);
  }
}
