package com.tomtom.navui.r1appkit;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tomtom.navui.appkit.AboutScreen;
import com.tomtom.navui.appkit.AppContext;
import com.tomtom.navui.core.Model;
import com.tomtom.navui.r1viewkit.NavAboutR1View;
import com.tomtom.navui.sigappkit.SigAppContext;

public class R1AboutScreen extends R1AppScreen implements AboutScreen{

    private Context mContext;

    protected Model<NavAboutR1View.Attributes> mModel;

    private NavAboutR1View mR1AboutView;

    protected R1AboutScreen(AppContext context) {
        super(context);
    }

    public R1AboutScreen(final SigAppContext context) {
        super(context);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        mContext = container.getContext();
        mR1AboutView = getContext().getViewKit().newView(NavAboutR1View.class, mContext, null);
        mModel = mR1AboutView.getModel();
        initialize();
        return mR1AboutView.getView();
    }

    protected void initialize(){
        // Data to show in About Screen
        // TODO: These are dummy values, s/b changed.
        final String headerLabel = "TomTom";
        final String appVersionValue = "1.18";

        mModel.putCharSequence(NavAboutR1View.Attributes.HEADER_LABEL_TEXT, headerLabel);
        mModel.putCharSequence(NavAboutR1View.Attributes.APP_VERSION_VALUE_TEXT, appVersionValue);
    }
}
