package com.tomtom.navui.sigappkit.controller;

import android.net.Uri;

import com.tomtom.navui.appkit.AppContext;
import com.tomtom.navui.appkit.action.Action;
import com.tomtom.navui.systemport.SystemContext;
import com.tomtom.navui.systemport.SystemGpsObservable;
import com.tomtom.navui.systemport.SystemGpsObservable.LocationServiceState;
import com.tomtom.navui.systemport.SystemSettings;
import com.tomtom.navui.taskkit.Location2;
import com.tomtom.navui.taskkit.TaskContext;
import com.tomtom.navui.taskkit.route.Route;
import com.tomtom.navui.taskkit.route.RouteGuidanceTask;
import com.tomtom.navui.taskkit.route.RoutePlanningTask;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Config(manifest = "./src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class RouteDisplayerImplActionTest {

    @Test
    public void testIfRoutePlaingIsCalledWhenNoRoute() {
        //GIVEN test object
        AppContext mockAppContext = mockAll(false);
        RouteDisplayerImplActEx testObject = new RouteDisplayerImplActEx(mockAppContext);
        //WHEN display new location action
        testObject.displayLocation(null);
        //THEN should be displayed route planning
        assertTrue(testObject.isPlanRoute());
    }

    @Test
    public void testIfRoutePlaningIsNotCalledWhenRouteExists() {
        //GIVEN test object
        AppContext mockAppContext = mockAll(true);
        RouteDisplayerImplActEx testObject = new RouteDisplayerImplActEx(mockAppContext);
        boolean expected = false;
        //WHEN display new location action
        testObject.displayLocation(null);
        //THEN should not be displayed route planning
        assertEquals(testObject.isPlanRoute(), expected);
    }

    @Test
    public void testIfAddWaypointIsCalledWhenRouteExists() {
        //GIVEN test object
        AppContext mockAppContext = mockAll(true);
        RouteDisplayerImplActEx testObject = new RouteDisplayerImplActEx(mockAppContext);
        //WHEN display new location action
        testObject.displayLocation(null);
        //THEN should be displayed add way point
        assertTrue(testObject.isShowAddWayPoint());
    }

    @Test
    public void testIfAddWaypointIsNotCalledWhenRouteDotExists() {
        //GIVEN test object
        AppContext mockAppContext = mockAll(false);
        RouteDisplayerImplActEx testObject = new RouteDisplayerImplActEx(mockAppContext);
        boolean expected = false;
        //WHEN display new location action
        testObject.displayLocation(null);
        //THEN should not be displayed add way point
        assertEquals(testObject.isShowAddWayPoint(), expected);
    }

    @Test
    public void testIsProperActionIsCalledWhenUriIsProper() {
        //GIVEN test object
        AppContext mockAppContext = mockAll(true);
        Action mockAction = mock(Action.class);
        RouteDisplayerImplActEx testObject = new RouteDisplayerImplActEx(mockAppContext);
        Location2 mockLocation2 = mock(Location2.class);
        when(mockAppContext.newAction(Uri.parse("action://AddWayPoint"))).thenReturn(mockAction);
        //WHEN display new location action
        testObject.testAction(mockLocation2);
        //THEN check if the action proper action was run
        assertThat(testObject.getAction(), is(notNullValue()));
    }

    @Test
    public void testIsProperActionIsNotCalledWhenWrongUri() {
        //GIVEN test object
        AppContext mockAppContext = mockAll(true);
        Action mockAction = mock(Action.class);
        RouteDisplayerImplActEx testObject = new RouteDisplayerImplActEx(mockAppContext);
        Location2 mockLocation2 = mock(Location2.class);
        when(mockAppContext.newAction(Uri.parse("action://DelWayPoilnt"))).thenReturn(mockAction);
        Exception ex = null;
        //WHEN display new location action
        try {
            testObject.testAction(mockLocation2);
        } catch (Exception e) {
            ex = e;
        }
        //THEN check if the action proper action wasn't run and exception was thrown
        assertThat(ex, is(notNullValue()));
    }

    @Test
    public void testIfActionHasProperLocationParamSet() {
        //GIVEN test object, Action mock
        AppContext mockAppContext = mockAll(true);
        Action mockAction = mock(Action.class);
        RouteDisplayerImplActEx testObject = new RouteDisplayerImplActEx(mockAppContext);
        Location2 mockLocation2 = mock(Location2.class);
        when(mockAppContext.newAction(Uri.parse("action://AddWayPoint"))).thenReturn(mockAction);
        Exception ex = null;
        //WHEN run test action
        try {
            testObject.testAction(mockLocation2);
        } catch (Exception e) {
            ex = e;
        }
        // THEN if the exception was thrown
        assertThat(ex, is(nullValue()));
    }

    @Test
    public void testIfActionHasNotLocationParamSet() {
        //GIVEN test object, Action mock
        AppContext mockAppContext = mockAll(true);
        Action mockAction = mock(Action.class);
        RouteDisplayerImplActEx testObject = new RouteDisplayerImplActEx(mockAppContext);
        when(mockAppContext.newAction(Uri.parse("action://AddWayPoint"))).thenReturn(mockAction);
        Exception ex = null;
        //WHEN run test action
        try {
            testObject.testAction(null);
        } catch (Exception e) {
            ex = e;
        }
        // THEN if the exception was thrown
        assertThat(ex, is(nullValue()));
    }

    @Test
    public void testIfActionHasProperParamSetup() {
        //GIVEN test object, Action mock
        AppContext mockAppContext = mockAll(true);
        Action mockAction = new MockAction();
        RouteDisplayerImplActEx testObject = new RouteDisplayerImplActEx(mockAppContext);
        Location2 mockLocation2 = mock(Location2.class);
        when(mockAppContext.newAction(Uri.parse("action://AddWayPoint"))).thenReturn(mockAction);
        //WHEN run test action
        testObject.testAction(mockLocation2);
        //THEN check if the param was added properly
        MockAction getAction = (MockAction) testObject.getAction();
        assertEquals(mockLocation2, getAction.getParam());
    }

    /**
     * Mock all object that are needed for RouteGuidanceTask
     * 
     * @param routeExist boolean if true RoutePlanning will return mock Route for getActiveRoute()
     *            else null
     * @return Mocked AppContext
     */
    private AppContext mockAll(boolean routeExist) {
        AppContext mockAppContext = mock(AppContext.class);
        TaskContext mockTaskKit = mock(TaskContext.class);
        SystemContext mockSysPort = mock(SystemContext.class);
        RouteGuidanceTask mockRoutGuidnace = mock(RouteGuidanceTask.class);
        RoutePlanningTask mockPlanning = mock(RoutePlanningTask.class);
        when(mockAppContext.getTaskKit()).thenReturn(mockTaskKit);
        when(mockAppContext.getSystemPort()).thenReturn(mockSysPort);
        when(mockAppContext.getSystemPort().getSettings(SystemContext.NAVUI_SETTINGS)).thenReturn(mock(SystemSettings.class));
        when(mockSysPort.getSystemObservable(SystemGpsObservable.class))
            .thenReturn(new MockSystemGpsObservable(LocationServiceState.STATE_GPS_ON));
        when(mockTaskKit.newTask(RouteGuidanceTask.class)).thenReturn(mockRoutGuidnace);
        when(mockTaskKit.newTask(RoutePlanningTask.class)).thenReturn(mockPlanning);

        if (routeExist) {
            Route mockRoute = mock(Route.class);
            when(mockRoutGuidnace.getActiveRoute()).thenReturn(mockRoute);
        }
        return mockAppContext;
    }

    private static class MockAction implements Action {

        private Object mParam;

        @Override
        public void addParameter(Object param) {
            mParam = param;
        }

        public Object getParam() {
            return mParam;
        }

        @Override
        public boolean dispatchAction() {
            return false;
        }
    }

    private static class RouteDisplayerImplActEx extends RouteDisplayerImpl {

        private boolean mPlanRoute = false;
        private boolean mShowAddWayPoint = false;
        private Action mAction = null;

        public RouteDisplayerImplActEx(AppContext appContext) {
            super(appContext);
        }

        public boolean isPlanRoute() {
            return mPlanRoute;
        }

        public boolean isShowAddWayPoint() {
            return mShowAddWayPoint;
        }

        public Action getAction() {
            return mAction;
        }

        public void testAction(Location2 location2) {
            addLocationToRoute(location2);
        }

        @Override
        protected void planRoute(Location2 location2) {
            mPlanRoute = true;
        }

        @Override
        protected void showAddWayPointDialog(Location2 location2) {
            mShowAddWayPoint = true;
        }

        @Override
        protected Action addLocationToRoute(Location2 location2) {
            mAction = super.addLocationToRoute(location2);
            return mAction;

        }
    }
}
