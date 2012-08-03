package android.override;

import android.override.IOverrideLocationListener;
import android.location.Location;
import android.os.Bundle;

interface IOverrideLocationService
{
    // The following functions are visible to applications.
    void requestLocationUpdates(String packageName, String provider, boolean singleShot, in IOverrideLocationListener listener);
    void removeUpdates(in IOverrideLocationListener listener);
    Location getLastKnownLocation(String packageName, String provider);

    // The following functions are used by location providers.
    void addOverrideProvider(String overrideProvider);
    void removeOverrideProvider(String overrideProvider);
    void reportOverrideLocation(String overrideProvider, in Location location);
}
