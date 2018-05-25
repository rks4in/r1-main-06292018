package com.tomtom.navui.r1viewkit;

import android.content.Context;
import android.util.AttributeSet;

import com.tomtom.navui.controlport.ControlContext;
import com.tomtom.navui.sigviewkit.SigViewContext;
import com.tomtom.navui.viewkit.NavView;
import com.tomtom.navui.viewkit.ViewContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class R1ViewContext extends SigViewContext {

    private static final Class<?>[] CONSTRUCTOR_SIG_3PARAM = { ViewContext.class, Context.class,
            AttributeSet.class };

    private static final Class<?>[] CONSTRUCTOR_SIG_4PARAM = { ViewContext.class, Context.class,
            AttributeSet.class, int.class };

    public R1ViewContext(ControlContext controlContext) {
        super(controlContext);
    }

    @Override
    protected <T extends NavView<?>> T newView(String name, Context context, AttributeSet attrs, int style) {
        String cname = name.startsWith("Nav") ? name.substring("Nav".length()) : name;
        try {
            final String implName = getR1ImplementationName(cname);
            return createView(context, attrs, style, implName);
        } catch (ClassNotFoundException e) {
            return super.newView(name, context, attrs, style);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(cname, e);
        } catch (InstantiationException e) {
            throw new RuntimeException(cname, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(cname, e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(cname, e);
        }
    }

    protected final <T extends NavView<?>> T createView(Context context, AttributeSet attrs, int style, String implName)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {

        Class<?> implClass = Class.forName(implName);
        Constructor<?> constructor;
        Object impl = null;
        if (style == 0) {
            // no style requested so use the 3 param constructor to get default style
            try {
                constructor = implClass.getConstructor(CONSTRUCTOR_SIG_3PARAM);
                Object[] args = { this, context, attrs };
                impl = constructor.newInstance(args);
            } catch (NoSuchMethodException e) {

            }
        }
        // no 3 param constructor available
        if (impl == null) {
            constructor = implClass.getConstructor(CONSTRUCTOR_SIG_4PARAM);
            Object[] args = { this, context, attrs, style };
            impl = constructor.newInstance(args);
        }

        @SuppressWarnings("unchecked")
        T view = (T) impl;
        view.getView().setTag(R.id.navui_view_interface_key, impl);
        return view;
    }

    private String getR1ImplementationName(final String base) {
        final String r1viewName = "com.tomtom.navui.r1viewkit.R1" + base;
        return r1viewName;
    }
}
