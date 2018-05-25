package com.tomtom.navui.r1viewkit;

import com.tomtom.navui.viewkit.NavView;
import com.tomtom.navui.core.Model;

public interface NavAboutR1View extends NavView<NavAboutR1View.Attributes> {
    public enum Attributes implements Model.Attributes {
        //Attributes which are wanted in About Screen can be extended here.
        HEADER_LABEL_TEXT(CharSequence.class),
        APP_VERSION_VALUE_TEXT(CharSequence.class);

        private Attributes(final Class<?> type) {
            mType = type;
        }

        @Override
        public Class<?> getType() {
            return mType;
        }

        private final Class<?> mType;
    }
}