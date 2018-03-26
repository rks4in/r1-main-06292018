package com.tomtom.navui.input.errhandler;

import android.content.Intent;
import android.net.Uri;

import com.google.common.collect.Sets;
import com.tomtom.navui.appkit.AppContext;
import com.tomtom.navui.appkit.AppDialog;
import com.tomtom.navui.appkit.ExternalDataNotSupportedDialog;
import com.tomtom.navui.appkit.ExternalLocationNotFoundDialog;
import com.tomtom.navui.input.intent.IntentActionHandler;
import com.tomtom.navui.input.parser.GeoUnitHelper;
import com.tomtom.navui.input.parser.ParseException;
import com.tomtom.navui.input.parser.data.location.TextLocationData;
import com.tomtom.navui.systemport.SystemContext;
import com.tomtom.navui.taskkit.TaskContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class InputDataErrorHandlerTests {

    private AppContext mAppContextMock;
    private SystemContext mSystemContextMock;

    @Before
    public void setUp() throws Exception {
        mAppContextMock = mock(AppContext.class);
        mSystemContextMock = mock(SystemContext.class);

        when(mAppContextMock.getSystemPort()).thenReturn(mSystemContextMock);
    }

    @Test
    public void testShouldDisplayCriticalDialogWhenIllegalException() {
        //GIVEN Unhappy flow handler 
        final InputDataErrorDialogHandler dialogHandler = new InputDataErrorDialogHandler(mAppContextMock);
        final Intent dataNotSupportedDialog = new Intent(ExternalDataNotSupportedDialog.class.getSimpleName());
        dataNotSupportedDialog.addCategory(AppDialog.DIALOG_CATEGORY);

        //WHEN we have unexpected situation
        dialogHandler.handle(new IllegalArgumentException());

        //THEN we need show critical dialog
        verify(mSystemContextMock).startScreen(equalsIntent(dataNotSupportedDialog));
    }

    @Test
    public void testShouldShowSearchQuestionDialogWhenParseException() {
        //GIVEN Unhappy flow handler 
        final InputDataErrorDialogHandler handler = new InputDataErrorDialogHandler(mAppContextMock);
        final Intent externalLocationDialog = new Intent(ExternalLocationNotFoundDialog.class.getSimpleName());
        externalLocationDialog.addCategory(AppDialog.DIALOG_CATEGORY);

        //WHEN we have unexpected situation
        handler.handle(new ParseException("test", "query"));

        //THEN we need show critical dialog
        verify(mSystemContextMock).startScreen(equalsIntent(externalLocationDialog));
    }

    @Test
    public void testGoogleMapParserHandleExceptionZeroLengthAddress() {

        //GIVEN google map uri with wrong coordinates and addr.length() == 0
        final String geoMapUriString = "http://maps.google.com/maps?hl=pl&q=&panel=1&f=d&fb=1&dirflg=d&geocode=4.884262&cid=0,0,14080343995521171618";
        final Intent intent = createIntent(geoMapUriString);
        final AppContext mockContext = mock(AppContext.class);
        final InputDataErrorDialogHandlerEx inputErrorHandler = mockAll(mockContext, null);
        final IntentActionHandler intentHandler = new IntentActionHandler(mockContext);
        intentHandler.setDefaultErrorHandler(inputErrorHandler);

        //WHEN handle intent
        intentHandler.handle(intent);

        //THEN dialog to correct should be displayed
        assertTrue(inputErrorHandler.isUnableToFindShowed());
    }

    @Test
    public void testGoogleMapParserHandleExceptionNoAddress() {

        //GIVEN google map uri with wrong coordinates and no addr parameter
        final String geoMapUriString = "http://maps.google.com/maps?hl=pl&adbaddr=&panel=1&f=d&fb=1&dirflg=d&geocode=4.884262&cid=0,0,14080343995521171618";
        final Intent intent = createIntent(geoMapUriString);
        final AppContext mockContext = mock(AppContext.class);
        final InputDataErrorDialogHandlerEx inputErrorHandler = mockAll(mockContext, null);
        final IntentActionHandler intentHandler = createIntenthandler(mockContext, inputErrorHandler);

        //WHEN handle intent
        intentHandler.handle(intent);

        //THEN error dialog should be displayed
        assertTrue(inputErrorHandler.isCriticalShowed());
    }

    @Test
    public void testGoogleMapParserHandleExceptionWithAddress() {

        //GIVEN google map uri with wrong coordinates and addr parameter
        final String geoMapUriString = "http://maps.google.com/maps?hl=pl&q=Ferdinand+Bolstraat+180,+1072+LV+Amsterdam,+Holandia+&panel=1&f=d&fb=1&dirflg=d&geocode=4.884262&cid=0,0,14080343995521171618";
        final Intent intent = createIntent(geoMapUriString);
        final AppContext mockContext = mock(AppContext.class);
        final com.tomtom.navui.appkit.action.Action mockAction = mock(com.tomtom.navui.appkit.action.Action.class);
        final InputDataErrorDialogHandlerEx inputErrorHandler = mockAll(mockContext, mockAction);
        final IntentActionHandler intentHandler = createIntenthandler(mockContext, inputErrorHandler);

        //WHEN handle intent
        intentHandler.handle(intent);

        //THEN verify if the address was found and pin action called
        verify(mockAction, atLeast(1)).addParameter(any(TextLocationData.class));
    }

    @Test
    public void testGoogleMapParserConfirmHandledAddress() {

        //GIVEN google map uri with wrong coordinates and addr parameter
        final String geoMapUriString = "http://maps.google.com/maps?hl=pl&q=Ferdinand+Bolstraat+180,+1072+LV+Amsterdam,+Holandia+&panel=1&f=d&fb=1&dirflg=d&geocode=4.884262&cid=0,0,14080343995521171618";
        final Intent intent = createIntent(geoMapUriString);
        final AppContext mockContext = mock(AppContext.class);
        final com.tomtom.navui.appkit.action.Action mockAction = mock(com.tomtom.navui.appkit.action.Action.class);
        final InputDataErrorDialogHandlerEx inputErrorHandler = mockAll(mockContext, mockAction);
        final IntentActionHandler intentHandler = createIntenthandler(mockContext, inputErrorHandler);
        final String expected = "Ferdinand Bolstraat 180, 1072 LV Amsterdam, Holandia";
        final TextLocationData textLocationData = TextLocationData.create(expected);

        //WHEN handle intent
        intentHandler.handle(intent);

        //THEN verify if the address was found and pin action called
        verify(mockAction).addParameter(textLocationData);
    }

    @Test
    public void testNavigationParserHandleExceptionZeroLengthAddress() {

        //GIVEN navigation uri with q.length() == 0
        final String dataToParse = "google.navigation:///?ll=C4.893381&q==d&opt=4%3A0%2C5%3A0";
        final Intent intent = createIntent(dataToParse);
        final AppContext mockContext = mock(AppContext.class);
        final InputDataErrorDialogHandlerEx inputErrorHandler = mockAll(mockContext, null);
        final IntentActionHandler intentHandler = new IntentActionHandler(mockContext);
        intentHandler.setDefaultErrorHandler(inputErrorHandler);

        //WHEN handle intent
        intentHandler.handle(intent);

        //THEN dialog to correct should be displayed
        assertTrue(inputErrorHandler.isUnableToFindShowed());
    }

    @Test
    public void testNavigationParserHandleExceptionNoAddress() {

        //GIVEN navigation uri with wrong coordinates and wrong address parameter
        final String dataToParse = "google.navigation:///?ll=C4.893381&wqw==d&opt=4%3A0%2C5%3A0";
        final Intent intent = createIntent(dataToParse);
        final AppContext mockContext = mock(AppContext.class);
        final InputDataErrorDialogHandlerEx inputErrorHandler = mockAll(mockContext, null);
        final IntentActionHandler intentHandler = new IntentActionHandler(mockContext);
        intentHandler.setDefaultErrorHandler(inputErrorHandler);

        //WHEN handle intent
        intentHandler.handle(intent);

        ///THEN error dialog should be displayed
        assertTrue(inputErrorHandler.isCriticalShowed());
    }

    @Test
    public void testNavigationParserHandleExceptionWithAddress() {

        //GIVEN navigation uri with wrong coordinates and address
        final String dataToParse = "google.navigation:///?ll=78317&q=Rokin%20174%2C%201012%20LE%20Amsterdam%2C%20The%20Netherlands&entry=d&opt=4%3A0%2C5%3A0";
        final Intent intent = createIntent(dataToParse);
        final AppContext mockContext = mock(AppContext.class);
        final com.tomtom.navui.appkit.action.Action mockAction = mock(com.tomtom.navui.appkit.action.Action.class);
        final InputDataErrorDialogHandlerEx inputErrorHandler = mockAll(mockContext, mockAction);
        final IntentActionHandler intentHandler = createIntenthandler(mockContext, inputErrorHandler);

        //WHEN handle intent
        intentHandler.handle(intent);

        //THEN verify if the address was found and pin action called
        verify(mockAction, atLeast(1)).addParameter(any(TextLocationData.class));
    }

    @Test
    public void testNavigationParserConfirmHandledAddress() {

        //GIVEN navigation uri with wrong coordinates and address
        final String dataToParse = "google.navigation:///?ll=93381&q=Rokin%20174%2C%201012%20LE%20Amsterdam%2C%20The%20Netherlands&entry=d&opt=4%3A0%2C5%3A0";
        final Intent intent = createIntent(dataToParse);
        final AppContext mockContext = mock(AppContext.class);
        final com.tomtom.navui.appkit.action.Action mockAction = mock(com.tomtom.navui.appkit.action.Action.class);
        final InputDataErrorDialogHandlerEx inputErrorHandler = mockAll(mockContext, mockAction);
        final IntentActionHandler intentHandler = createIntenthandler(mockContext, inputErrorHandler);
        final String expected = "Rokin 174, 1012 LE Amsterdam, The Netherlands";
        final TextLocationData textLocationData = TextLocationData.create(expected);

        //WHEN handle intent
        intentHandler.handle(intent);

        //THEN error dialog should be displayed
        verify(mockAction).addParameter(textLocationData);
    }

    @Test
    public void testGeoParserHandleExceptionZeroLengthAddress() {

        //GIVEN geo uri with wrong coordinates and addr.length() == 0
        final String geoUriString = "geo:,0?q=";
        final Intent intent = createIntent(geoUriString);
        final AppContext mockContext = mock(AppContext.class);
        final InputDataErrorDialogHandlerEx inputErrorHandler = mockAll(mockContext, null);
        final IntentActionHandler intentHandler = new IntentActionHandler(mockContext);
        intentHandler.setDefaultErrorHandler(inputErrorHandler);

        //WHEN handle intent
        intentHandler.handle(intent);

        //THEN dialog to correct should be displayed
        assertTrue(inputErrorHandler.isUnableToFindShowed());
    }

    @Test
    public void testGeoParserHandleExceptionNoAddress() {

        //GIVEN geo uri with wrong coordinates and no addr parameter
        final String geoUriString = "geo:0,?qfr=1053%20Oud-West%2C%20The%20Netherlands%208.1%20m";
        final Intent intent = createIntent(geoUriString);
        final AppContext mockContext = mock(AppContext.class);
        final InputDataErrorDialogHandlerEx inputErrorHandler = mockAll(mockContext, null);
        final IntentActionHandler intentHandler = createIntenthandler(mockContext, inputErrorHandler);

        //WHEN handle intent
        intentHandler.handle(intent);

        //THEN error dialog should be displayed
        assertTrue(inputErrorHandler.isCriticalShowed());
    }

    @Test
    public void testGeoParserHandleExceptionWithAddress() {

        //GIVEN geo uri with wrong coordinates and address parameter
        final String geoUriString = "geo:0,?q=1053%20Oud-West%2C%20The%20Netherlands%208.1%20m";
        final Intent intent = createIntent(geoUriString);
        final AppContext mockContext = mock(AppContext.class);
        final com.tomtom.navui.appkit.action.Action mockAction = mock(com.tomtom.navui.appkit.action.Action.class);
        final InputDataErrorDialogHandlerEx inputErrorHandler = mockAll(mockContext, mockAction);
        final IntentActionHandler intentHandler = createIntenthandler(mockContext, inputErrorHandler);

        //WHEN handle intent
        intentHandler.handle(intent);

        //THEN verify if the address was found and pin action called
        verify(mockAction, atLeast(1)).addParameter(any(TextLocationData.class));
    }

    @Test
    public void testGeoParserConfirmHandledAddress() {

        //GIVEN geo uri with wrong coordinates and address parameter
        final String geoUriString = "geo:0,?q=1053%20Oud-West%2C%20The%20Netherlands%208.1%20m";
        final Intent intent = createIntent(geoUriString);
        final AppContext mockContext = mock(AppContext.class);
        final com.tomtom.navui.appkit.action.Action mockAction = mock(com.tomtom.navui.appkit.action.Action.class);
        final InputDataErrorDialogHandlerEx inputErrorHandler = mockAll(mockContext, mockAction);
        final IntentActionHandler intentHandler = createIntenthandler(mockContext, inputErrorHandler);
        final String expected = "1053 Oud-West, The Netherlands 8.1 m";
        final TextLocationData textLocationData = TextLocationData.create(expected);

        //WHEN handle intent
        intentHandler.handle(intent);

        //THEN verify if the address was found and pin action called
        verify(mockAction).addParameter(textLocationData);
    }

    private InputDataErrorDialogHandlerEx mockAll(final AppContext mockContext, com.tomtom.navui.appkit.action.Action mockAction) {
        final TaskContext mockTaskKit = mock(TaskContext.class);
        if (mockAction == null) {
            mockAction = mock(com.tomtom.navui.appkit.action.Action.class);
        }
        final InputDataErrorDialogHandlerEx inputErrorHandler = new InputDataErrorDialogHandlerEx(mockContext);
        when(mockContext.getTaskKit()).thenReturn(mockTaskKit);
        when(mockTaskKit.isReady()).thenReturn(true);
        when(mockContext.newAction(any(Uri.class))).thenReturn(mockAction);
        return inputErrorHandler;
    }

    private IntentActionHandler createIntenthandler(final AppContext context, final InputDataErrorDialogHandlerEx handler) {
        final IntentActionHandler intentHandler = new IntentActionHandler(context);
        intentHandler.setDefaultErrorHandler(handler);
        return intentHandler;
    }

    private Intent createIntent(final String data) {
        final Intent intent = GeoUnitHelper.createIntentfromUrl(data);
        intent.setAction("com.dummy.ACTION");
        return intent;
    }

    /**
     * Helper class to determine if the proper method was called in {@link InputDataErrorHandler}
     */
    private static class InputDataErrorDialogHandlerEx extends InputDataErrorDialogHandler {

        private boolean mCriticalShowed = false;
        private boolean mUnableToFindShowed = false;

        public boolean isCriticalShowed() {
            return mCriticalShowed;
        }

        public boolean isUnableToFindShowed() {
            return mUnableToFindShowed;
        }

        public InputDataErrorDialogHandlerEx(final AppContext appContext) {
            super(appContext);
        }

        @Override
        void showCriticalErrorDialog() {
            mCriticalShowed = true;
        }

        @Override
        void showUnableToFindDialog(final String query) {
            mUnableToFindShowed = true;
        }
    }

    private static Intent equalsIntent(final Intent intent) {
        return argThat(new ArgumentMatcher<Intent>() {
            @Override
            public boolean matches(final Intent argIntent) {
                return intent.getAction().equals(argIntent.getAction())
                        &&
                        ((intent.getCategories() != null && Sets.symmetricDifference(intent.getCategories(), argIntent.getCategories()).isEmpty()) || intent
                            .getCategories() == argIntent.getCategories());
            }
        });
    }
}
