/*
 * Copyright (c) 1992-2018 TomTom N.V. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * licensee agreement between you and TomTom. If you are the licensee, you are only permitted
 * to use this Software in accordance with the terms of your license agreement. If you are
 * not the licensee then you are not authorised to use this software in any manner and should
 * immediately return it to TomTom N.V.
 */

package com.tomtom.r1navapp.test;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import com.harman.fcaclock.IFcaClockService;
import com.tomtom.navui.appkit.AppContext;
import com.tomtom.navui.systemport.SystemContext;
import com.tomtom.navui.taskkit.TaskContext;
import com.tomtom.navui.taskkit.currentposition.CurrentPositionTask;
import com.tomtom.navui.taskkit.route.Position;
import com.tomtom.navui.taskkit.route.RouteGuidanceTask.PositionStatusChangedListener.PositionStatus;
import com.tomtom.r1navapp.R1TimeZoneHandler;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.RoboSettings;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collection;
import java.util.TimeZone;

import static com.harman.fcaclock.Constants.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(ParameterizedRobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class R1TimeZoneHandlerTest {

    @ParameterizedRobolectricTestRunner.Parameters(name = "PositionStatus = {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {PositionStatus.GPS},
                {PositionStatus.SENSOR},
                {PositionStatus.SIMULATED},
                {PositionStatus.NO_POSITION}
        });
    }

    private final ComponentName mClockServiceComponentName =
            new ComponentName(FCA_CLOCK_PACKAGE, "FcaClockService");

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private AppContext mAppContext;

    @Mock
    private TaskContext mTaskContext;

    @Mock
    private SystemContext mSystemContext;

    @Mock
    private Context mContext;

    @Mock
    private IBinder mBinder;

    @Mock
    private IFcaClockService mFcaClockService;

    @Captor
    private ArgumentCaptor<ServiceConnection> mServiceConnectionCaptor;

    @Mock
    private CurrentPositionTask mCurrentPositionTask;

    @Mock
    private Position mPosition;

    private PositionStatus mPositionStatus;

    @BeforeClass
    public static void configure() {
        // Configure robolectric to tie all loopers to the same main scheduler
        // It is needed to test calls that happen in separate threads
        RoboSettings.setUseGlobalScheduler(true);
    }

    public R1TimeZoneHandlerTest(PositionStatus positionStatus) {
        mPositionStatus = positionStatus;
    }

    @Before
    public void setUp() {
        when(mContext.bindService(any(), any(), anyInt())).thenReturn(true);
        when(mAppContext.getSystemPort()).thenReturn(mSystemContext);
        when(mAppContext.getTaskKit()).thenReturn(mTaskContext);
        when(mSystemContext.getApplicationContext()).thenReturn(mContext);
        when(IFcaClockService.Stub.asInterface(mBinder)).thenReturn(mFcaClockService);
        when(mTaskContext.newTask(CurrentPositionTask.class)).thenReturn(mCurrentPositionTask);
        when(mCurrentPositionTask.getCurrentPositionStatus()).thenReturn(mPositionStatus);
    }

    @Test
    public void testConstructorWhenServiceExist() {
        // WHEN creating a R1TimeZoneHandler
        new R1TimeZoneHandler(mAppContext);

        // THEN an intent to bind with external service should be sent
        ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(mContext).bindService(intentCaptor.capture(), any(ServiceConnection.class),
                eq(Context.BIND_AUTO_CREATE));

        Intent intent = intentCaptor.getValue();

        // AND the destination for the intent should be com.harman.fcaclock package
        assertEquals(FCA_CLOCK_PACKAGE, intent.getPackage());

        // AND no actions follow until service is not connected
        verify(mTaskContext, never()).addContextStateListener(any());
        verify(mTaskContext, never()).newTask(any());
        verify(mCurrentPositionTask, never()).addCurrentPositionListener(any());
    }

    @Test
    public void testConstructorWhenServiceNotExist() {
        // GIVEN bindService always returns false
        when(mContext.bindService(any(), any(), anyInt())).thenReturn(false);

        // WHEN creating a R1TimeZoneHandler
        new R1TimeZoneHandler(mAppContext);

        // THEN an intent to bind with external service should be sent
        ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(mContext).bindService(intentCaptor.capture(), any(ServiceConnection.class),
                eq(Context.BIND_AUTO_CREATE));

        Intent intent = intentCaptor.getValue();

        // AND the destination for the intent should be com.harman.fcaclock package
        assertEquals(FCA_CLOCK_PACKAGE, intent.getPackage());

        // AND no actions follow
        verify(mTaskContext, never()).addContextStateListener(any());
        verify(mTaskContext, never()).newTask(any());
        verify(mCurrentPositionTask, never()).addCurrentPositionListener(any());
    }

    @Test
    public void testOnServiceConnectedWhenTaskContextIsNotReady() {
        // GIVEN an initialized R1TimeZoneHandler with not ready Task Context
        R1TimeZoneHandler timeZoneHandler = prepareTimeZoneHandler(false);

        // THEN TaskContext should add the R1TimeZoneHandler as a listener
        // and wait until it is ready before creating new CurrentPositionTask
        verify(mTaskContext).addContextStateListener(timeZoneHandler);
        verify(mTaskContext, never()).newTask(CurrentPositionTask.class);
        verify(mCurrentPositionTask, never()).addCurrentPositionListener(timeZoneHandler);
    }

    @Test
    public void testOnServiceConnectedWhenTaskContextIsReady() {
        // GIVEN an initialized R1TimeZoneHandler with ready Task Context
        R1TimeZoneHandler timeZoneHandler = prepareTimeZoneHandler(true);

        // THEN new task should be created and R1TimeZoneHandler should be added as it's listener
        // AND no action should be done against TaskContext
        verify(mTaskContext).newTask(CurrentPositionTask.class);
        verify(mCurrentPositionTask).addCurrentPositionListener(timeZoneHandler);
        verify(mTaskContext, never()).addContextStateListener(timeZoneHandler);
    }

    @Test
    public void testOnTaskContextReady() {
        // GIVEN an initialized R1TimeZoneHandler with not ready Task Context
        R1TimeZoneHandler timeZoneHandler = prepareTimeZoneHandler(false);

        // WHEN onTaskContextReady is called
        timeZoneHandler.onTaskContextReady();

        // THEN new task should be created and R1TimeZoneHandler should be added as it's listener
        verify(mTaskContext).newTask(CurrentPositionTask.class);
        verify(mCurrentPositionTask).addCurrentPositionListener(timeZoneHandler);
    }

    @Test
    public void testOnTaskContextLost() {
        // GIVEN an initialized R1TimeZoneHandler with ready Task Context
        R1TimeZoneHandler timeZoneHandler = prepareTimeZoneHandler(true);

        // AND then onTaskContextLost is called regardless of it's arguments
        timeZoneHandler.onTaskContextLost(null, null);

        // THEN release should be called for existing task
        verify(mCurrentPositionTask).release();
    }

    @Test
    public void testOnTaskContextLostAndReadyAgain() {
        // GIVEN an initialized R1TimeZoneHandler with ready Task Context
        R1TimeZoneHandler timeZoneHandler = prepareTimeZoneHandler(true);

        // AND then onTaskContextLost is called regardless of it's arguments
        timeZoneHandler.onTaskContextLost(null, null);

        // AND then onTaskContextReady is called again
        timeZoneHandler.onTaskContextReady();

        // THEN new task should be created and R1TimeZoneHandler should be added as it's listener
        verify(mTaskContext, times(2)).newTask(CurrentPositionTask.class);
        verify(mCurrentPositionTask, times(2)).addCurrentPositionListener(timeZoneHandler);
        verify(mCurrentPositionTask).release();
    }

    @Test
    public void testOnCurrentPositionResult() throws RemoteException {
        // TEST is valid only if "Use Global Scheduler" option is set
        // then all Threads's Runnables will be executed consequently in one main Thread
        assertEquals(true, RoboSettings.isUseGlobalScheduler());

        // GIVEN an initialized R1TimeZoneHandler with ready Task Context
        R1TimeZoneHandler timeZoneHandler = prepareTimeZoneHandler(true);

        // VERIFY setTimeZone is called when specific position received
        verifySetTimeZoneWasCalledAfterOnCurrentPositionResult(timeZoneHandler, true,
                "America/Los_Angeles");

        reset(mFcaClockService);

        // VERIFY setTimeZone is called when TimeZone changed
        verifySetTimeZoneWasCalledAfterOnCurrentPositionResult(timeZoneHandler, true,
                "America/Louisville");

        reset(mFcaClockService);

        // VERIFY setTimeZone isn't called after TimeZone remained the same
        verifySetTimeZoneWasCalledAfterOnCurrentPositionResult(timeZoneHandler, false,
                "America/Louisville");
    }

    @Test
    public void testOnServiceReconnected() throws RemoteException {
        // TEST is valid only if "Use Global Scheduler" option is set
        // then all Threads's Runnables will be executed consequently in one main Thread
        assertEquals(true, RoboSettings.isUseGlobalScheduler());

        // GIVEN an initialized R1TimeZoneHandler with ready Task Context
        R1TimeZoneHandler timeZoneHandler = prepareTimeZoneHandler(true);

        // AND reconnection happened
        mServiceConnectionCaptor.getValue().onServiceDisconnected(mClockServiceComponentName);
        mServiceConnectionCaptor.getValue().onServiceConnected(mClockServiceComponentName, mBinder);

        // THEN setTimeZone shouldn't be called as there is no TimeZone received
        verify(mFcaClockService, never()).setTimeZone(anyString(), anyInt());

        // GIVEN a TimeZone
        TimeZone timeZone = prepareTimeZone("America/Los_Angeles");

        // GIVEN onCurrentPositionResult is called
        callOnCurrentPositionResult(timeZoneHandler);

        reset(mFcaClockService);

        // AND reconnection happened again
        mServiceConnectionCaptor.getValue().onServiceDisconnected(mClockServiceComponentName);
        mServiceConnectionCaptor.getValue().onServiceConnected(mClockServiceComponentName, mBinder);

        // VERIFY setTimeZone is called after reconnection
        verifySetTimeZoneWasCalledConsideringPositionStatus(timeZone);
    }

    @NonNull
    private R1TimeZoneHandler prepareTimeZoneHandler(boolean isTaskContextReady) {
        R1TimeZoneHandler timeZoneHandler = new R1TimeZoneHandler(mAppContext);

        // Capture ServiceConnection is sent to bindService
        verify(mContext).bindService(any(Intent.class), mServiceConnectionCaptor.capture(),
                eq(Context.BIND_AUTO_CREATE));

        when(mTaskContext.isReady()).thenReturn(isTaskContextReady);
        mServiceConnectionCaptor.getValue().onServiceConnected(mClockServiceComponentName, mBinder);

        return timeZoneHandler;
    }

    @NonNull
    private TimeZone prepareTimeZone(String timeZoneID) {
        TimeZone timeZone = TimeZone.getTimeZone(timeZoneID);
        when(mCurrentPositionTask.getTimeZone()).thenReturn(timeZone);

        return timeZone;
    }

    private void callOnCurrentPositionResult(R1TimeZoneHandler timeZoneHandler) {
        timeZoneHandler.onCurrentPositionResult(mPosition);
        RuntimeEnvironment.getMasterScheduler().advanceToLastPostedRunnable();
    }

    private void verifySetTimeZoneWasCalledConsideringPositionStatus(TimeZone timeZone)
            throws RemoteException {
        if (mPositionStatus == PositionStatus.GPS) {
            // GIVEN the position is received from GPS
            // THEN setTimeZone should be called with proper arguments
            verify(mFcaClockService).setTimeZone(timeZone.getID(), timeZone.getDSTSavings());
        } else {
            // GIVEN there is no GPS position
            // THEN setTimeZone shouldn't be called at all
            verify(mFcaClockService, never()).setTimeZone(anyString(), anyInt());
        }
    }

    private void verifySetTimeZoneWasCalledAfterOnCurrentPositionResult(
            R1TimeZoneHandler timeZoneHandler,
            boolean wasCalled,
            String timeZoneID) throws RemoteException {

        TimeZone timeZone = prepareTimeZone(timeZoneID);

        callOnCurrentPositionResult(timeZoneHandler);

        if (wasCalled) {
            verifySetTimeZoneWasCalledConsideringPositionStatus(timeZone);
        } else {
            verify(mFcaClockService, never()).setTimeZone(anyString(), anyInt());
        }
    }

}
