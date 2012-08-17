package android.override;

import android.override.IOverrideCommandListener;

interface IOverrideCommander {
    void registerCommandListener(String packageName, IOverrideCommandListener listener);
    void removeCommandListener(String packageName, IOverrideCommandListener listener);
    void onStatusChanged(String packageName, in Bundle data);
}
