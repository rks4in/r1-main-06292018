package com.tomtom.navui.sigappkit.controller;

import com.tomtom.navui.appkit.AppContext;
import com.tomtom.navui.systemport.SystemContext;
import com.tomtom.navui.systemport.SystemGpsObservable;
import com.tomtom.navui.systemport.SystemPubSubManager;
import com.tomtom.navui.systemport.SystemSettings;
import com.tomtom.navui.taskkit.Location2;
import com.tomtom.navui.taskkit.TaskContext;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test case to indicate if the proper dialog box is open according to different GPS sates
 */
@Config(manifest = "./src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class RouteDisplayerImplTest {

    @Mock
    private SystemSettings mMockedSystemSettings;
    @Rule
    public MockitoRule mMockitoRule = MockitoJUnit.rule();

    @Test
    public void testIsProperDialogOpenWhenNoneGpsAvailalbe() {
        AppContext mckContext = mockAll(SystemGpsObservable.LocationServiceState.NONE);
        RouteDisplayerImplEx testObject = new RouteDisplayerImplEx(mckContext);
        testObject.displayLocation(null);
        assertTrue(testObject.isShowNoGpsDialogCalled());
    }

    @Test
    public void testIsProperDialogOpenWhenGpsOff() {
        AppContext mckContext = mockAll(SystemGpsObservable.LocationServiceState.STATE_OFF);
        RouteDisplayerImplEx testObject = new RouteDisplayerImplEx(mckContext);
        testObject.displayLocation(null);
        assertTrue(testObject.isShowNoGpsDialogCalled());
    }

    @Test
    public void testIsProperDialogOpenWhenPasiveOn() {
        AppContext mckContext = mockAll(SystemGpsObservable.LocationServiceState.STATE_PASSIVE_ON);
        RouteDisplayerImplEx testObject = new RouteDisplayerImplEx(mckContext);
        testObject.displayLocation(null);
        assertTrue(testObject.isShowNoGpsDialogCalled());
    }

    @Test
    public void testIsProperDialogOpenWhenGsmGpsOn() {
        AppContext mckContext = mockAll(SystemGpsObservable.LocationServiceState.STATE_WIFI_GSM_ON);
        RouteDisplayerImplEx testObject = new RouteDisplayerImplEx(mckContext);
        testObject.displayLocation(null);
        assertTrue(testObject.isShowNoGpsDialogCalled());
    }

    @Test
    public void testIsProperDialogOpenWhenGpsOn() {
        AppContext mckContext = mockAll(SystemGpsObservable.LocationServiceState.STATE_GPS_ON);
        RouteDisplayerImplEx testObject = new RouteDisplayerImplEx(mckContext);
        testObject.displayLocation(null);
        assertTrue(testObject.isStartPlanRouteCalled());
    }

    private AppContext mockAll(SystemGpsObservable.LocationServiceState state) {
        AppContext mckAppContext = mock(AppContext.class);
        TaskContext mckTaskKit = mock(TaskContext.class);
        SystemContext mckSysPort = mock(SystemContext.class);
        when(mckAppContext.getTaskKit()).thenReturn(mckTaskKit);
        when(mckAppContext.getSystemPort()).thenReturn(mckSysPort);
        when(mckSysPort.getSystemObservable(SystemGpsObservable.class)).
            thenReturn(new MockSystemGpsObservable(state));

        return mckAppContext;
    }

    private static class RouteDisplayerImplEx extends RouteDisplayerImpl {
        public RouteDisplayerImplEx(AppContext appContext) {
            super(appContext);
        }

        private boolean showNoGpsDialogCalled;
        private boolean startPlanRouteCalled;

        @Override
        protected void showNoGpsDialog(Location2 location2) {
            showNoGpsDialogCalled = true;
        }

        @Override
        protected void startPlanRoute(Location2 location2) {
            startPlanRouteCalled = true;
        }

        public boolean isShowNoGpsDialogCalled() {
            return showNoGpsDialogCalled;
        }

        public boolean isStartPlanRouteCalled() {
            return startPlanRouteCalled;
        }

    }
}
