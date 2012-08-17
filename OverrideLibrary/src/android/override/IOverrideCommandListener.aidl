package android.override;

import android.os.Bundle;

oneway interface IOverrideCommandListener {
    void onCommand(in Bundle command);
}
