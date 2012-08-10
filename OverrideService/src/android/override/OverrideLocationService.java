package android.override;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class OverrideLocationService extends android.app.Service {

  private static final String TAG = "OverrideLocationService";
  private OverrideLocationServiceImpl mBinder;

  @Override
  public void onCreate() {
    super.onCreate();
    Log.v(TAG, "onCreate");
    mBinder = new OverrideLocationServiceImpl(getApplicationContext());
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
}
