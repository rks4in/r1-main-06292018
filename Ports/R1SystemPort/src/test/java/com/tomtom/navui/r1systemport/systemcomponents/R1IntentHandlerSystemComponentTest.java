package com.tomtom.navui.r1systemport.systemcomponents;

import android.content.Intent;

import com.tomtom.navui.systemport.systemcomponents.IntentHandlerSystemComponent;
import com.tomtom.navui.util.ActionHandler;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 19)
public class R1IntentHandlerSystemComponentTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private ActionHandler<Intent> mActionHandler;
    @Mock
    private Intent mIntent;
    @Mock
    private Intent mIntent2;

    @Test
    public void testOnNewIntentBeforeActionHandler() {
        // GIVEN a system component
        final IntentHandlerSystemComponent tested = new R1IntentHandlerSystemComponent();

        // WHEN providing an intent before the action handler
        tested.onNewIntent(mIntent);
        tested.setIntentHandler(mActionHandler);

        // THEN the intent should be handled
        verify(mActionHandler).handle(mIntent);
    }

    @Test
    public void testMultipleOnNewIntentsBeforeActionHandler() {
        // GIVEN a system component
        final IntentHandlerSystemComponent tested = new R1IntentHandlerSystemComponent();

        // WHEN providing two intents before the action handler
        tested.onNewIntent(mIntent);
        tested.onNewIntent(mIntent2);
        tested.setIntentHandler(mActionHandler);

        // THEN only the last intent should be handled
        verify(mActionHandler, never()).handle(mIntent);
        verify(mActionHandler).handle(mIntent2);
    }

    @Test
    public void testOnNewIntentBeforeActionHandlerTwice() {
        // GIVEN an intent has been handled by a later-added action handler
        final IntentHandlerSystemComponent tested = new R1IntentHandlerSystemComponent();
        tested.onNewIntent(mIntent);
        tested.setIntentHandler(mActionHandler);

        // WHEN the action handler goes away and returns
        tested.setIntentHandler(null);
        tested.setIntentHandler(mActionHandler);

        // THEN the intent shouldn't be handled twice
        verify(mActionHandler).handle(mIntent);
    }

    @Test
    public void testOnNewIntentAfterActionHandler() {
        // GIVEN the action handler is set
        final IntentHandlerSystemComponent tested = new R1IntentHandlerSystemComponent();
        tested.setIntentHandler(mActionHandler);

        // WHEN providing a new intent
        tested.onNewIntent(mIntent);

        // THEN the intent should be handled
        verify(mActionHandler).handle(mIntent);
    }

    @Test
    public void testOnNewIntentAfterActionHandlerAndResetActionHandler() {
        // GIVEN an intent has been handled by an already-present action handler
        final IntentHandlerSystemComponent tested = new R1IntentHandlerSystemComponent();
        tested.setIntentHandler(mActionHandler);
        tested.onNewIntent(mIntent);

        // WHEN the action handler goes away and returns
        tested.setIntentHandler(null);
        tested.setIntentHandler(mActionHandler);

        // THEN the intent shouldn't be handled twice
        verify(mActionHandler).handle(mIntent);
    }

}
