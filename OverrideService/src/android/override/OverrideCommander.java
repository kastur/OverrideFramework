package android.override;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;


public class OverrideCommander extends IOverrideCommander.Stub {

  private static final String TAG = "OverrideCommander";

  private HashMap<String, HashSet<IOverrideCommandListener>> mListenerSets =
      new HashMap<String, HashSet<IOverrideCommandListener>>();
  private HashMap<String, Bundle> mLastCommand = new HashMap<String, Bundle>();

  @Override
  public void onStatusChanged(String packageName, Bundle data) throws RemoteException {

  }

  @Override
  public synchronized void registerCommandListener(String packageName,
                                                   IOverrideCommandListener listener)
      throws RemoteException {
    if (!mListenerSets.containsKey(packageName)) {
      mListenerSets.put(packageName, new HashSet<IOverrideCommandListener>());
    }
    mListenerSets.get(packageName).add(listener);

    Bundle lastCommand = mLastCommand.get(packageName);

    if (lastCommand != null) {
      // TODO: This sends out the last command to all listeners, not just the last registered one.
      relayCommandToListeners(packageName, lastCommand);
    }
  }

  @Override
  public void removeCommandListener(String packageName, IOverrideCommandListener listener)
      throws RemoteException {
    mListenerSets.get(packageName).remove(listener);
  }

  private void handleCommandReceived(String packageName, Bundle command) {
    final Bundle savedCommand = new Bundle(command);
    mLastCommand.put(packageName, savedCommand);
    relayCommandToListeners(packageName, command);
  }

  private void relayCommandToListeners(String packageName, Bundle command) {
    HashSet<IOverrideCommandListener> listeners = mListenerSets.get(packageName);
    if (listeners == null) {
      Log.v(TAG, "No listeners registered for " + packageName);
      return;
    }

    for (IOverrideCommandListener listener : listeners) {
      try {
        Log.d(TAG, "sendCommand: " + packageName);
        Log.d(TAG, "sendCommand: " + command.toString());
        listener.onCommand(command);
      } catch (RemoteException ex) {
        Log.d(TAG, "sendCommand: " + packageName + " RemoteException!");
      }
    }
  }

  public void onReceiveData(Bundle data) {
    handleCommandReceived(data.getString("PACKAGE"), data);
  }
}
