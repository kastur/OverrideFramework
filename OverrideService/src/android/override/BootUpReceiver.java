package android.override;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created with IntelliJ IDEA. User: k Date: 8/9/12 Time: 10:33 PM To change this template use File
 * | Settings | File Templates.
 */
public class BootUpReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    Intent locationStartIntent = new Intent(context, OverrideLocationService.class);
    context.startService(locationStartIntent);
  }
}
