package com.tomtom.navui.r1appkit.controllers.globalobservers;

import com.tomtom.navui.appkit.AppContext;
import com.tomtom.navui.systemport.SystemContext;
import com.tomtom.navui.systemport.systemcomponents.IntentHandlerSystemComponent;
import com.tomtom.navui.util.ActionHandler;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class IntentHandlerGlobalObserverTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private AppContext mAppContext;
    @Mock
    private SystemContext mSystemContext;
    @Mock
    private IntentHandlerSystemComponent mIntentHandlerSystemComponent;

    @Before
    public void setUp() {
        when(mAppContext.getSystemPort()).thenReturn(mSystemContext);
        when(mSystemContext.getComponent(IntentHandlerSystemComponent.class)).thenReturn(mIntentHandlerSystemComponent);
    }

    @Test
    public void testConstructor() {
        // WHEN creating a global observer
        new IntentHandlerGlobalObserver(mAppContext);

        // THEN nothing should happen
        verify(mIntentHandlerSystemComponent, never()).setIntentHandler(any(ActionHandler.class));
    }

    @Test
    public void testOnAppActive() {
        // GIVEN a global observer
        final IntentHandlerGlobalObserver tested = new IntentHandlerGlobalObserver(mAppContext);

        // WHEN the app becomes active
        tested.onAppActive();

        // THEN the system component should be provided with an intent handler
        verify(mIntentHandlerSystemComponent).setIntentHandler(any(ActionHandler.class));
    }

    @Test
    public void testOnAppInactive() {
        // GIVEN that the app is active
        final IntentHandlerGlobalObserver tested = new IntentHandlerGlobalObserver(mAppContext);
        tested.onAppActive();

        // WHEN the app becomes inactive
        tested.onAppInactive();

        // THEN the system component's intent handler should be removed
        verify(mIntentHandlerSystemComponent).setIntentHandler(any(ActionHandler.class));
        verify(mIntentHandlerSystemComponent).setIntentHandler(null);
    }

}
