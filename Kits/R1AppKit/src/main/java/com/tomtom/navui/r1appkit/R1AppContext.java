package com.tomtom.navui.r1appkit;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.tomtom.navui.appkit.AppContext;
import com.tomtom.navui.appkit.AppScreen;
import com.tomtom.navui.r1appkit.controllers.globalobservers.IntentHandlerGlobalObserver;
import com.tomtom.navui.appkit.ExtAppScreenContext;
import com.tomtom.navui.promptkit.PromptContext;
import com.tomtom.navui.systemport.ApplicationConfiguration;
import com.tomtom.navui.systemport.SystemContext;
import com.tomtom.navui.taskkit.TaskContext;
import com.tomtom.navui.viewkit.ViewContext;
import com.tomtom.navui.sigappkit.SigAppContext;
import com.tomtom.navui.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * Implementation of the {@link AppContext} that is able to inject screens. To make it work, just
 * replace the default implementation ({@link SigAppContext}) with this one.
 */
public class R1AppContext extends SigAppContext {

    private static final String TAG = "R1AppContext";

    private static final Class<?>[] CONSTRUCTOR_SIG = {SigAppContext.class};
    private static final Class<?>[] ACTION_CONSTRUCTOR_SIG = {AppContext.class, Uri.class};

    /**
     * Builder class for {@link R1AppContext}, based on the builder for {@link SigAppContext}.
     * Users of this builder must provide mandatory kits in the constructor and optionally other
     * kits in the setters.
     */
    public static class Builder extends SigAppContext.Builder {
        public Builder(@NonNull final ViewContext viewKit,
                       @NonNull final TaskContext taskKit,
                       @NonNull final PromptContext promptKit,
                       @NonNull final SystemContext systemPort,
                       @NonNull final ApplicationConfiguration applicationConfiguration) {
            super(viewKit, taskKit, promptKit, systemPort, applicationConfiguration);
        }
        @Override
        public R1AppContext build() {
            return new R1AppContext(this);
        }
    }
    public R1AppContext(@NonNull final Builder builder) {
        super(builder);
        addGlobalObserver(new IntentHandlerGlobalObserver(this));
    }
    @Override
    public <T extends AppScreen> T newScreen(final CharSequence name) {
        if (Log.ENTRY) {
            Log.entry(TAG, "CustomNewScreen " + name);
        }

        final Collection<ExtAppScreenContext> extContexts = getExts(ExtAppScreenContext.class);
        for (final ExtAppScreenContext ext : extContexts) {
            final T screen = ext.newScreen(name);
            if (screen != null) {
                return screen;
            }
        }

        if (name.toString().indexOf("R1") == 0) {
            return newScreen(name.toString().substring(2));
        }

        final String cname = name.toString();
        try {
            final String implName = getR1ImplementationName(cname);
            final Class<?> implClass = Class.forName(implName);
            final Constructor<?> constructor = implClass.getDeclaredConstructor(CONSTRUCTOR_SIG);
            final Object[] args = {this};
            final Object impl = constructor.newInstance(args);
            @SuppressWarnings("unchecked")
            final T screen = (T) impl;
            return screen;
        } catch (final ClassNotFoundException e) {
            // Trying to find the sig implementation of the class.
            return super.newScreen(name);
        } catch (final NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(cname, e);
        }
    }
    private String getR1ImplementationName(final String base) {
        return "com.tomtom.navui.r1appkit.R1" + base;
    }
}
