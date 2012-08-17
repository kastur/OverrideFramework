package android.override;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootUpReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    Intent startIntent = new Intent(context, OverrideCommander.class);
    context.startService(startIntent);
  }
}
