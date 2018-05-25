package com.tomtom.navui.r1viewkit;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.tomtom.navui.controlport.NavLabel;
import com.tomtom.navui.core.BaseModel;
import com.tomtom.navui.core.FilterModel;
import com.tomtom.navui.core.Model;
import com.tomtom.navui.util.ViewUtil;
import com.tomtom.navui.viewkit.ViewContext;

public class R1AboutR1View extends RelativeLayout implements NavAboutR1View {

    private ViewContext mViewContext;
    private Model<Attributes> mModel;

    private NavLabel mHeaderLabel;
    private NavLabel mAppVersionValue;

    public R1AboutR1View(final ViewContext viewContext, final Context context,
                         final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        initialize(viewContext, context);
    }

    protected void initialize(final ViewContext viewContext, final Context context){
        mViewContext = viewContext;
        final View view = inflate(context, R.layout.r1aboutview, this);

        mHeaderLabel = ViewUtil.findInterfaceById(view, R.id.r1_about_headerlabel);

        mAppVersionValue = ViewUtil.findInterfaceById(view, R.id.r1_about_appversionvalue);
    }

    @Override
    public Model<Attributes> getModel() {
        if (mModel == null) {
            setModel(new BaseModel<>(Attributes.class));
        }
        return mModel;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void setModel(Model<Attributes> model) {
        mModel = model;
        if (mModel == null) {
            return;
        }

        final FilterModel<NavLabel.Attributes, Attributes> headerLabelModel =
                new FilterModel<NavLabel.Attributes, Attributes>(model,
                        NavLabel.Attributes.class);
        headerLabelModel.addFilter(NavLabel.Attributes.TEXT, Attributes.HEADER_LABEL_TEXT);
        mHeaderLabel.setModel(headerLabelModel);

        final FilterModel<NavLabel.Attributes, Attributes> appVersionValueModel =
                new FilterModel<NavLabel.Attributes, Attributes>(model,NavLabel.Attributes.class);
        appVersionValueModel.addFilter(NavLabel.Attributes.TEXT, Attributes.APP_VERSION_VALUE_TEXT);
        mAppVersionValue.setModel(appVersionValueModel);
    }

    @Override
    public ViewContext getViewContext() {
        return mViewContext;
    }
}
