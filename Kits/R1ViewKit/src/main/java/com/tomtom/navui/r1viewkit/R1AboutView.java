package com.tomtom.navui.r1viewkit;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.tomtom.navui.controlport.NavLabel;
import com.tomtom.navui.core.FilterModel;
import com.tomtom.navui.core.Model;
import com.tomtom.navui.sigviewkit.SigView;
import com.tomtom.navui.viewkit.NavView;
import com.tomtom.navui.viewkit.ViewContext;

public class R1AboutView extends SigView<R1AboutView.Attributes>
                         implements NavView<R1AboutView.Attributes> {

    public enum Attributes implements Model.Attributes {
        //Attributes which are wanted in About Screen can be extended here.
        APP_VERSION(CharSequence.class),
        MAP_NAME(CharSequence.class),
        MAP_VERSION(CharSequence.class),
        NAVUI_VERSION(CharSequence.class),
        NAVKIT_VERSION(CharSequence.class);

        Attributes(final Class<?> type) {
            mType = type;
        }

        @Override
        public Class<?> getType() {
            return mType;
        }

        private final Class<?> mType;
    }

    public R1AboutView(final ViewContext viewContext, final Context context,
                       final AttributeSet attrs, final int style) {
        super(viewContext, context, Attributes.class);
        createView(RelativeLayout.class, attrs, style, 0, R.layout.r1_about_view);
    }

    @Override
    public void setModel(Model<Attributes> model) {
        mModel = model;
        if (mModel == null) {
            return;
        }

        setLabelText(R.id.r1_about_appVersion, Attributes.APP_VERSION);
        setLabelText(R.id.r1_about_mapName, Attributes.MAP_NAME);
        setLabelText(R.id.r1_about_mapVersion, Attributes.MAP_VERSION);
        setLabelText(R.id.r1_about_navUiVersion, Attributes.NAVUI_VERSION);
        setLabelText(R.id.r1_about_navKitVersion, Attributes.NAVKIT_VERSION);
    }

    private void setLabelText(final int labelId, final Attributes attribute) {
        final NavLabel label = findInterfaceById(labelId);

        label.setModel(FilterModel.create(mModel,NavLabel.Attributes.class).
                addFilter(NavLabel.Attributes.TEXT, attribute));
    }
}
