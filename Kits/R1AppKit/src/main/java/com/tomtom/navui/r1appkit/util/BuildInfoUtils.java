package com.tomtom.navui.r1appkit.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.tomtom.navui.appkit.AppContext;
import com.tomtom.navui.r1appkit.BuildConfig;
import com.tomtom.navui.r1appkit.R;
import com.tomtom.navui.taskkit.TaskContext;
import com.tomtom.navui.taskkit.TaskNotReadyException;
import com.tomtom.navui.taskkit.mapselection.MapDetails;
import com.tomtom.navui.taskkit.mapselection.MapSelectionTask;
import com.tomtom.navui.taskkit.property.ComponentDetails;
import com.tomtom.navui.taskkit.property.PropertyTask;
import com.tomtom.navui.util.Log;
import com.tomtom.navui.util.Releasable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BuildInfoUtils implements MapSelectionTask.MapSelectionListener,
                                       PropertyTask.PropertyListener,
                                       TaskContext.ContextStateListener,
                                       Releasable{

    public static class BuildInfo {
        public String appVersion;
        public String mapName;
        public String mapVersion;
        public String navUiVersion;
        public String navKitVersion;

        public BuildInfo(String appVersionValue, String mapNameValue, String mapVersionValue,
                         String navUiVersionValue, String navKitVersionValue) {
            appVersion = appVersionValue;
            mapName = mapNameValue;
            mapVersion = mapVersionValue;
            navUiVersion = navUiVersionValue;
            navKitVersion = navKitVersionValue;
        }
    }

    public interface BuildInfoListener {
        void onBuildInfoAvailable(BuildInfo buildInfo);
    }

    private static final String TAG = "BuildInfoUtils";

    private String mAppVersionLabel;
    private String mMapNameLabel;
    private String mMapVersionLabel;
    private String mNavUiVersionLabel;
    private String mNavKitVersionLabel;

    private String mDefaultValue;

    private String mAppVersion;
    private String mMapName;
    private String mMapVersion;
    private String mNavUiVersion;
    private String mNavKitVersion;

    private MapSelectionTask mMapSelectionTask;
    private PropertyTask mPropertyTask;

    private AppContext mAppContext;

    private Set<BuildInfoListener> mListeners;

    public BuildInfoUtils(AppContext appContext) {
        mAppContext = appContext;

        mListeners = new HashSet<>();

        final Context context = mAppContext.getSystemPort().getApplicationContext();

        mAppVersionLabel = context.getString(R.string.r1_navui_app_version);
        mMapNameLabel = context.getString(R.string.navui_mapname);
        mMapVersionLabel = context.getString(R.string.navui_mapversion);
        mNavKitVersionLabel = context.getString(R.string.navui_navkitversion);
        mNavUiVersionLabel = context.getString(R.string.r1_navui_version);

        mDefaultValue = context.getString(R.string.navui_notavailable);
        mAppVersion = mDefaultValue;
        mMapName = mDefaultValue;
        mMapVersion = mDefaultValue;
        mNavUiVersion = mDefaultValue;
        mNavKitVersion = mDefaultValue;
    }

    public void addListener(BuildInfoListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(BuildInfoListener listener) {
        mListeners.remove(listener);
    }

    public void fetchBuildInfo() {
        if (!mAppContext.getTaskKit().isReady()) {
            mAppContext.getTaskKit().addContextStateListener(this);
        } else {
            onTaskContextReady();
        }
    }

    public boolean isBuildInfoAvailable() {
        return  !mAppVersion.equals(mDefaultValue) &&
                !mMapName.equals(mDefaultValue) &&
                !mMapVersion.equals(mDefaultValue) &&
                !mNavUiVersion.equals(mDefaultValue) &&
                !mNavKitVersion.equals(mDefaultValue);
    }

    public BuildInfo getBuildInfo() {
        return new BuildInfo(getAppVersion(), getMapName(),
                getMapVersion(), getNavUiVersion(), getNavKitVersion());
    }

    @Override
    public void onTaskContextReady() {
        fetchAppVersion(mAppContext.getSystemPort().getApplicationContext());
        fetchMapInfo(mAppContext);
        fetchNavKitVersion(mAppContext);
        fetchNavUiVersion();

        mAppContext.getTaskKit().removeContextStateListener(this);
    }

    @Override
    public void onTaskContextMapStateChange(TaskContext.MapState mapState) {
        // Not needed
    }

    @Override
    public void onTaskContextLost(Boolean recoverable, ErrorCode error) {
        // Not needed
    }

    @Override
    public void onMapListReceived(List<MapDetails> mapList, UpdateType type) {
        // Not needed
    }

    @Override
    public void onMapActivated(MapDetails mapDetails) {
        String mapName;
        String mapVersion;

        if (mapDetails != null) {
            mapName = mapDetails.getName();
            mapVersion = mapDetails.getReleaseNumber() + "." + mapDetails.getBuildNumber();
        } else {
            mapName = mDefaultValue;
            mapVersion = mDefaultValue;
        }

        if (!mapName.equals(mMapName) || !mapVersion.equals(mMapVersion)) {
            mMapName = mapName;
            mMapVersion = mapVersion;

            notifyListenersIfBuildInfoAvailable();
        }
    }

    @Override
    public void onComponentListRecieved(short[] componentIdList) {
        if ((null != componentIdList) && (componentIdList.length > 0)) {
            //NavKit has implemented only one component-property mapping hence the workaround
            mPropertyTask.getProperties((short) /*componentIdList[0]*/1);
        } else {
            if (Log.D) {
                Log.d(TAG,"onComponentListRecieved() - componentList is null or has no elements");
            }
        }
    }

    @Override
    public void onPropertyRecieved(ComponentDetails componentDetails) {
        String navKitVersion;

        if (componentDetails != null) {
            navKitVersion = componentDetails.getVersion();
        } else {
            navKitVersion = mDefaultValue;
        }

        if (!navKitVersion.equals(mNavKitVersion)) {
            mNavKitVersion = navKitVersion;
            notifyListenersIfBuildInfoAvailable();
        }
    }

    @Override
    public void release() {
        if (mPropertyTask != null) {
            mPropertyTask.removeListener(this);
            mPropertyTask.release();
            mPropertyTask = null;
        }

        if (mMapSelectionTask != null) {
            mMapSelectionTask.removeListener(this);
            mMapSelectionTask.release();
            mMapSelectionTask = null;
        }
    }

    private void notifyListenersIfBuildInfoAvailable() {
        if (isBuildInfoAvailable()) {
            for (final BuildInfoListener listener : mListeners) {
                listener.onBuildInfoAvailable(getBuildInfo());
            }
        }
    }

    private String getAppVersion() {
        return mAppVersionLabel + mAppVersion;
    }

    private String getMapName() {
        return mMapNameLabel + mMapName;
    }

    private String getMapVersion() {
        return mMapVersionLabel + mMapVersion;
    }

    private String getNavUiVersion() {
        return mNavUiVersionLabel + mNavUiVersion;
    }

    private String getNavKitVersion() {
        return mNavKitVersionLabel + mNavKitVersion;
    }

    private void fetchAppVersion(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            mAppVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            mAppVersion = mDefaultValue;
        }

        notifyListenersIfBuildInfoAvailable();
    }

    private void fetchNavUiVersion() {
        if (BuildConfig.NAVUI_VERSION.length() > 0) {
            mNavUiVersion = BuildConfig.NAVUI_VERSION;
        } else {
            mNavUiVersion = mDefaultValue;
        }

        notifyListenersIfBuildInfoAvailable();
    }

    private void fetchNavKitVersion(AppContext appContext) {
        try {
            mPropertyTask = appContext.getTaskKit().newTask(PropertyTask.class);
            mPropertyTask.addListener(this);
        } catch (final TaskNotReadyException e) {
            if (Log.E) Log.e(TAG, "Task not ready", e);
        }

        // Get list of components for NavKit - will get the properties in the callback
        mPropertyTask.getComponents();
    }

    private void fetchMapInfo(AppContext appContext) {
        try {
            mMapSelectionTask = appContext.getTaskKit().newTask(MapSelectionTask.class);
            mMapSelectionTask.addListener(this);
        } catch (final TaskNotReadyException e) {
            if (Log.E) Log.e(TAG, "Task not ready", e);
        }
    }
}
