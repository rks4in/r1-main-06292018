package com.tomtom.navui.r1appkit;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tomtom.navui.appkit.AboutScreen;
import com.tomtom.navui.core.Model;
import com.tomtom.navui.r1appkit.util.BuildInfoUtils;
import com.tomtom.navui.r1viewkit.R1AboutView;
import com.tomtom.navui.sigappkit.SigAppContext;
import com.tomtom.navui.sigappkit.SigAppScreen;
import com.tomtom.navui.taskkit.TaskContext;
import com.tomtom.navui.util.Log;

public class R1AboutScreen extends SigAppScreen implements AboutScreen,
        BuildInfoUtils.BuildInfoListener {

    private static final String TAG = "R1AboutScreen";

    private Model<R1AboutView.Attributes> mAboutViewModel;
    private BuildInfoUtils mBuildInfoUtils;

    public R1AboutScreen(final SigAppContext context) {
        super(context);
    }

    @Override
    public void onCreateTasks(final TaskContext taskKit) {
        // Not needed
    }

    @Override
    public void onReleaseTasks() {
        // Not needed
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        if (Log.ENTRY) Log.entry(TAG, "onCreateView");

        final Context context = container.getContext();
        R1AboutView aboutView = getContext().getViewKit().newView(R1AboutView.class, context,
                null);
        mAboutViewModel = aboutView.getModel();

        initialize();
        return aboutView.getView();
    }

    @Override
    public void onDestroyView() {
        if (mAboutViewModel != null) {
            mAboutViewModel = null;
        }

        if (mBuildInfoUtils != null) {
            mBuildInfoUtils.removeListener(this);
            mBuildInfoUtils.release();
            mBuildInfoUtils = null;
        }
    }

    @Override
    public void onBuildInfoAvailable(BuildInfoUtils.BuildInfo buildInfo) {
        fillModel(buildInfo);
    }

    private void initialize(){
        mBuildInfoUtils = new BuildInfoUtils(getContext());
        mBuildInfoUtils.addListener(this);
        mBuildInfoUtils.fetchBuildInfo();

        // Fill with data even if it's not available yet and update later
        fillModel(mBuildInfoUtils.getBuildInfo());
    }

    private void fillModel(BuildInfoUtils.BuildInfo buildInfo) {
        if (mAboutViewModel != null) {
            mAboutViewModel.putCharSequence(R1AboutView.Attributes.APP_VERSION, buildInfo.appVersion);
            mAboutViewModel.putString(R1AboutView.Attributes.MAP_NAME, buildInfo.mapName);
            mAboutViewModel.putString(R1AboutView.Attributes.MAP_VERSION, buildInfo.mapVersion);
            mAboutViewModel.putString(R1AboutView.Attributes.NAVKIT_VERSION, buildInfo.navKitVersion);
            mAboutViewModel.putString(R1AboutView.Attributes.NAVUI_VERSION, buildInfo.navUiVersion);
        }
    }
}
