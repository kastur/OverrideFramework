package android.override;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.*;
import android.util.Log;
import android.util.PrintWriterPrinter;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * The backend service class that manages LocationProviders and issues location
 * updates and alerts to listeners.
 * <p/>
 * Bare-bones version of the LocationManagerService from the Android Framework.
 * Here, we allow arbitrary, external location providers to call reportLocation.
 * These locations are transported to the appropriate listeners.
 */
public class OverrideLocationServiceImpl extends IOverrideLocationService.Stub {
    private static final String TAG = "OverrideLocationService";
    private static final boolean LOCAL_LOGV = true;

    private final Object mLock = new Object();
    private final Context mContext;
    private HashSet<String> mProviders = new HashSet<String>();

    private HashMap<String, Location> mLastKnownLocation = new HashMap<String, Location>();

    private LocationWorkerHandler mLocationHandler = new LocationWorkerHandler();
    private static final int MESSAGE_LOCATION_CHANGED = 1;

    private final HashMap<Object, Receiver> mReceivers = new HashMap<Object, Receiver>();
    private final HashMap<String, ArrayList<UpdateRecord>> mRecordsByProvider = new HashMap<String, ArrayList<UpdateRecord>>();

    private final class Receiver implements IBinder.DeathRecipient, PendingIntent.OnFinished {
        final IOverrideLocationListener mListener;
        final PendingIntent mPendingIntent;
        final Object mKey;
        final HashMap<String, UpdateRecord> mUpdateRecords = new HashMap<String, UpdateRecord>();
        String requiredPermissions;

        Receiver(IOverrideLocationListener listener) {
            mListener = listener;
            mPendingIntent = null;
            mKey = listener.asBinder();
        }

        Receiver(PendingIntent intent) {
            mPendingIntent = intent;
            mListener = null;
            mKey = intent;
        }

        @Override
        public boolean equals(Object otherObj) {
            return otherObj instanceof Receiver && mKey.equals(((Receiver) otherObj).mKey);
        }

        @Override
        public int hashCode() {
            return mKey.hashCode();
        }

        @Override
        public String toString() {
            String result;
            if (mListener != null) {
                result = "Receiver{"
                        + Integer.toHexString(System.identityHashCode(this))
                        + " Listener " + mKey + "}";
            } else {
                result = "Receiver{"
                        + Integer.toHexString(System.identityHashCode(this))
                        + " Intent " + mKey + "}";
            }
            result += "mUpdateRecords: " + mUpdateRecords;
            return result;
        }

        public boolean isListener() {
            return mListener != null;
        }

        public IOverrideLocationListener getListener() {
            if (mListener != null) {
                return mListener;
            }
            throw new IllegalStateException("Request for non-existent listener");
        }

        public boolean callLocationChangedLocked(Location location) {
            if (mListener != null) {
                try {
                    synchronized (this) {
                        // synchronize to ensure incrementPendingBroadcastsLocked()
                        // is called before decrementPendingBroadcasts()
                        mListener.onLocationChanged(location);
                    }
                } catch (RemoteException e) {
                    return false;
                }
            } else {
                Intent locationChanged = new Intent();
                locationChanged.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
                try {
                    synchronized (this) {
                        // synchronize to ensure incrementPendingBroadcastsLocked()
                        // is called before decrementPendingBroadcasts()
                        mPendingIntent.send(mContext, 0, locationChanged, this, mLocationHandler,
                                requiredPermissions);
                    }
                } catch (PendingIntent.CanceledException e) {
                    return false;
                }
            }
            return true;
        }

        public void binderDied() {
            if (LOCAL_LOGV) {
                Log.v(TAG, "Location listener died");
            }
            synchronized (mLock) {
                removeUpdatesLocked(this);
            }
        }

        public void onSendFinished(PendingIntent pendingIntent, Intent intent,
                                   int resultCode, String resultData, Bundle resultExtras) {
        }
    }

    /**
     * @param context the context that the LocationManagerService runs in
     */
    public OverrideLocationServiceImpl(Context context) {
        super();
        mContext = context;

        if (LOCAL_LOGV) {
            Log.v(TAG, "Constructed LocationManager Service");
        }
    }

    private class UpdateRecord {
        final String mProvider;
        final Receiver mReceiver;
        final boolean mSingleShot;
        final int mUid;
        Location mLastFixBroadcast;
        long mLastStatusBroadcast;

        /**
         * Note: must be constructed with lock held.
         */
        UpdateRecord(String provider, boolean singleShot, Receiver receiver, int uid) {
            mProvider = provider;
            mReceiver = receiver;
            mSingleShot = singleShot;
            mUid = uid;

            ArrayList<UpdateRecord> records = mRecordsByProvider.get(provider);
            if (records == null) {
                records = new ArrayList<UpdateRecord>();
                mRecordsByProvider.put(provider, records);
            }
            if (!records.contains(this)) {
                records.add(this);
            }
        }

        /**
         * Method to be called when a record will no longer be used.  Calling this multiple times
         * must have the same effect as calling it once.
         */
        void disposeLocked() {
            ArrayList<UpdateRecord> records = mRecordsByProvider.get(this.mProvider);
            if (records != null) {
                records.remove(this);
            }
        }

        @Override
        public String toString() {
            return "UpdateRecord{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " mProvider: " + mProvider + " mUid: " + mUid + "}";
        }

        void dump(PrintWriter pw, String prefix) {
            pw.println(prefix + this);
            pw.println(prefix + "mProvider=" + mProvider + " mReceiver=" + mReceiver);
            pw.println(prefix + "mSingleShot=" + mSingleShot);
            pw.println(prefix + "mUid=" + mUid);
            pw.println(prefix + "mLastFixBroadcast:");
            if (mLastFixBroadcast != null) {
                mLastFixBroadcast.dump(new PrintWriterPrinter(pw), prefix + "  ");
            }
            pw.println(prefix + "mLastStatusBroadcast=" + mLastStatusBroadcast);
        }
    }

    private Receiver getReceiver(IOverrideLocationListener listener) {
        IBinder binder = listener.asBinder();
        Receiver receiver = mReceivers.get(binder);
        if (receiver == null) {
            receiver = new Receiver(listener);
            mReceivers.put(binder, receiver);

            try {
                if (receiver.isListener()) {
                    receiver.getListener().asBinder().linkToDeath(receiver, 0);
                }
            } catch (RemoteException e) {
                Log.e(TAG, "linkToDeath failed:", e);
                return null;
            }
        }
        return receiver;
    }

    private boolean providerHasListener(String provider, int uid, Receiver excludedReceiver) {
        ArrayList<UpdateRecord> records = mRecordsByProvider.get(provider);
        if (records != null) {
            for (int i = records.size() - 1; i >= 0; i--) {
                UpdateRecord record = records.get(i);
                if (record.mUid == uid && record.mReceiver != excludedReceiver) {
                    return true;
                }
            }
        }
        return false;
    }

    public void requestLocationUpdates(String packageName, String provider, boolean singleShot, IOverrideLocationListener listener) {
        try {
            synchronized (mLock) {
                requestLocationUpdatesLocked(packageName, provider, singleShot, getReceiver(listener));
            }
        } catch (SecurityException se) {
            throw se;
        } catch (IllegalArgumentException iae) {
            throw iae;
        } catch (Exception e) {
            Log.e(TAG, "requestUpdates got exception:", e);
        }
    }

    private void requestLocationUpdatesLocked(String packageName, String native_provider, boolean singleShot, Receiver receiver) {

        // TODO: Need to use the packageName + native_provider to get the actual Override-processed location provider.
        String provider = packageName + "/" + native_provider;

        if (LOCAL_LOGV)
            Log.i(TAG, "requestLocationUpdatesLocked: registering a location updates for: " + "\"" + provider + "\"");
        final int callingUid = Binder.getCallingUid();
        long identity = Binder.clearCallingIdentity();
        try {
            UpdateRecord r = new UpdateRecord(provider, singleShot, receiver, callingUid);
            UpdateRecord oldRecord = receiver.mUpdateRecords.put(provider, r);
            if (oldRecord != null) {
                oldRecord.disposeLocked();
            }

            if (LOCAL_LOGV) {
                Log.v(TAG, "_requestLocationUpdates: provider = " + provider + " listener = " + receiver);
            }
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    public void removeUpdates(IOverrideLocationListener listener) {
        try {
            synchronized (mLock) {
                removeUpdatesLocked(getReceiver(listener));
            }
        } catch (SecurityException se) {
            throw se;
        } catch (IllegalArgumentException iae) {
            throw iae;
        } catch (Exception e) {
            Log.e(TAG, "removeUpdates got exception:", e);
        }
    }

    private void removeUpdatesLocked(Receiver receiver) {
        if (LOCAL_LOGV) {
            Log.v(TAG, "_removeUpdates: listener = " + receiver);
        }

        // so wakelock calls will succeed
        long identity = Binder.clearCallingIdentity();
        try {
            if (mReceivers.remove(receiver.mKey) != null && receiver.isListener()) {
                receiver.getListener().asBinder().unlinkToDeath(receiver, 0);
            }

            HashMap<String, UpdateRecord> oldRecords = receiver.mUpdateRecords;
            if (oldRecords != null) {
                // Call dispose() on the obsolete update records.
                for (UpdateRecord record : oldRecords.values()) {
                    record.disposeLocked();
                }
            }
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    public void addOverrideProvider(String provider) {
        synchronized (mLock) {
            mProviders.add(provider);
        }
    }

    public void removeOverrideProvider(String provider) {
        synchronized (mLock) {
            mProviders.remove(provider);
        }
    }

    public void reportOverrideLocation(String provider, Location location) {
        mLocationHandler.removeMessages(MESSAGE_LOCATION_CHANGED, location);
        Message m = Message.obtain(mLocationHandler, MESSAGE_LOCATION_CHANGED, location);
        mLocationHandler.sendMessageAtFrontOfQueue(m);
    }

    public Location getLastKnownLocation(String packageName, String native_provider) {
        // TODO: Need to user packageName + provider to get the last known location to this app.
        String provider = packageName + "/" + native_provider;
        if (LOCAL_LOGV) {
            Log.v(TAG, "getLastKnownLocation: " + provider);
        }
        try {
            synchronized (mLock) {
                return _getLastKnownLocationLocked(provider);
            }
        } catch (SecurityException se) {
            throw se;
        } catch (Exception e) {
            Log.e(TAG, "getLastKnownLocation got exception:", e);
            return null;
        }
    }

    private Location _getLastKnownLocationLocked(String provider) {

        if (!mProviders.contains(provider)) {
            return null;
        }

        return mLastKnownLocation.get(provider);
    }

    private static boolean shouldBroadcastSafe(Location loc, Location lastLoc, UpdateRecord record) {
        // Always broadcast the first update
        if (lastLoc == null) {
            return true;
        }

        // Don't broadcast same location again regardless of condition
        // TODO - we should probably still rebroadcast if user explicitly sets a minTime > 0
        if (loc.getTime() == lastLoc.getTime()) {
            return false;
        }

        // Check whether sufficient distance has been traveled
        double minDistance = 0.0;
        if (minDistance > 0.0) {
            if (loc.distanceTo(lastLoc) <= minDistance) {
                return false;
            }
        }

        return true;
    }

    private void handleLocationChangedLocked(Location location) {
        String provider = location.getProvider();
        ArrayList<UpdateRecord> records = mRecordsByProvider.get(provider);
        if (records == null || records.size() == 0) {
            return;
        }

        if (!mProviders.contains(provider)) {
            return;
        }

        // Update last known location for provider
        Location lastLocation = mLastKnownLocation.get(provider);
        if (lastLocation == null) {
            mLastKnownLocation.put(provider, new Location(location));
        } else {
            lastLocation.set(location);
        }

        ArrayList<Receiver> deadReceivers = null;

        // Broadcast location or status to all listeners
        for (UpdateRecord r : records) {
            Receiver receiver = r.mReceiver;
            boolean receiverDead = false;

            Location lastLoc = r.mLastFixBroadcast;
            if ((lastLoc == null) || shouldBroadcastSafe(location, lastLoc, r)) {
                if (lastLoc == null) {
                    lastLoc = new Location(location);
                    r.mLastFixBroadcast = lastLoc;
                } else {
                    lastLoc.set(location);
                }
                if (!receiver.callLocationChangedLocked(location)) {
                    Log.w(TAG, "RemoteException calling onLocationChanged on " + receiver);
                    receiverDead = true;
                }
            }

            // remove receiver if it is dead or we just processed a single shot request
            if (receiverDead || r.mSingleShot) {
                if (deadReceivers == null) {
                    deadReceivers = new ArrayList<Receiver>();
                }
                if (!deadReceivers.contains(receiver)) {
                    deadReceivers.add(receiver);
                }
            }
        }

        if (deadReceivers != null) {
            for (int i = deadReceivers.size() - 1; i >= 0; i--) {
                removeUpdatesLocked(deadReceivers.get(i));
            }
        }
    }

    private class LocationWorkerHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            try {
                if (msg.what == MESSAGE_LOCATION_CHANGED) {
                    Log.d(TAG, "LocationWorkerHandler: MESSAGE_LOCATION_CHANGED!");

                    synchronized (mLock) {
                        Location location = (Location) msg.obj;
                        handleLocationChangedLocked(location);
                    }
                }
            } catch (Exception e) {
                // Log, don't crash!
                Log.e(TAG, "Exception in LocationWorkerHandler.handleMessage:", e);
            }
        }
    }

    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (mContext.checkCallingOrSelfPermission(android.Manifest.permission.DUMP)
                != PackageManager.PERMISSION_GRANTED) {
            pw.println("Permission Denial: can't dump LocationManagerService from from pid="
                    + Binder.getCallingPid()
                    + ", uid=" + Binder.getCallingUid());
            return;
        }

        synchronized (mLock) {
            pw.println("Current Location Manager state:");
            pw.println("  Listeners:");
            int N = mReceivers.size();
            for (int i = 0; i < N; i++) {
                pw.println("    " + mReceivers.get(i));
            }
            pw.println("  Location Listeners:");
            for (Receiver i : mReceivers.values()) {
                pw.println("    " + i + ":");
                for (Map.Entry<String, UpdateRecord> j : i.mUpdateRecords.entrySet()) {
                    pw.println("      " + j.getKey() + ":");
                    j.getValue().dump(pw, "        ");
                }
            }
            pw.println("  Records by Provider:");
            for (Map.Entry<String, ArrayList<UpdateRecord>> i
                    : mRecordsByProvider.entrySet()) {
                pw.println("    " + i.getKey() + ":");
                for (UpdateRecord j : i.getValue()) {
                    pw.println("      " + j + ":");
                    j.dump(pw, "        ");
                }
            }
            pw.println("  Last Known Locations:");
            for (Map.Entry<String, Location> i
                    : mLastKnownLocation.entrySet()) {
                pw.println("    " + i.getKey() + ":");
                i.getValue().dump(new PrintWriterPrinter(pw), "      ");
            }

            for (String provider : mProviders) {
                pw.println(provider);
            }
        }
    }
}
