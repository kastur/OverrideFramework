package android.override;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class OverrideCommanderService extends android.app.Service {

  private static final String TAG = "OverrideCommanderService";
  private OverrideCommander mBinder;

  @Override
  public void onCreate() {
    super.onCreate();
    Log.v(TAG, "onCreate");
    mBinder = new OverrideCommander();
  }

  @Override
  public void onDestroy() {
    Log.v(TAG, "onDestroy");
    super.onDestroy();
  }

  @Override
  public IBinder onBind(Intent intent) {
    return mBinder;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    mBinder.onReceiveData(intent.getExtras());
    return super.onStartCommand(intent, flags, startId);
  }
}
