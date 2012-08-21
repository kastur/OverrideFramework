package android.override;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;


public class OverrideCommander extends IOverrideCommander.Stub {

  private static final String TAG = "OverrideCommander";

  private HashSet<IOverrideCommandListener> mListeners = new HashSet<IOverrideCommandListener>();
  private HashMap<String, HashSet<IOverrideCommandListener>> mListenersByPackage =
      new HashMap<String, HashSet<IOverrideCommandListener>>();

  @Override
  public void onStatusChanged(String packageName, Bundle data) throws RemoteException {

  }

  @Override
  public synchronized void registerCommandListener(String packageName,
                                                   IOverrideCommandListener listener)
      throws RemoteException {
    mListeners.add(listener);
    if (!mListenersByPackage.containsKey(packageName)) {
      mListenersByPackage.put(packageName, new HashSet<IOverrideCommandListener>());
    }
    mListenersByPackage.get(packageName).add(listener);
  }

  @Override
  public void removeCommandListener(String packageName, IOverrideCommandListener listener)
      throws RemoteException {
    mListeners.remove(listener);
    mListenersByPackage.get(packageName).remove(listener);
  }

  public void onCommand(String packageName, Bundle data) {
    HashSet<IOverrideCommandListener> listeners = mListenersByPackage.get(packageName);
    if (listeners == null) {
      Log.v(TAG, "No listeners registered for " + packageName);
      return;
    }

    for (IOverrideCommandListener listener : listeners) {
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
