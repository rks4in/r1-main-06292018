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

package com.tomtom.r1navapp;

import com.tomtom.navui.taskkit.currentposition.CurrentPositionTask;
import com.tomtom.navui.taskkit.route.Position;
import com.tomtom.navui.taskkit.route.RouteGuidanceTask;
import com.tomtom.navui.taskkit.TaskContext;
import com.tomtom.navui.util.Log;
import com.tomtom.navui.util.Releasable;

import java.util.TimeZone;

public class R1TimeZoneHandler implements Releasable,
                                          RouteGuidanceTask.CurrentPositionListener,
                                          TaskContext.ContextStateListener {
    private static final String TAG = "R1TimeZoneHandler";

    private TaskContext mTaskContext;
    private CurrentPositionTask mCurrentPositionTask;
    private TimeZone mPreviousTimeZone;

    public void init(TaskContext taskContext) {
        mTaskContext = taskContext;

        mTaskContext.addContextStateListener(this);
        if (mTaskContext.isReady()) {
            onTaskContextReady();
        }
    }

    @Override
    public void release() {
        releaseCurrentPositionTask();

        mTaskContext.removeContextStateListener(this);
        mTaskContext = null;
    }

    @Override
    public void onTaskContextReady() {
        if (mCurrentPositionTask == null) {
            mCurrentPositionTask = mTaskContext.newTask(CurrentPositionTask.class);
            mCurrentPositionTask.addCurrentPositionListener(this);
        }
    }

    @Override
    public void onTaskContextMapStateChange(TaskContext.MapState mapState) {
        // Not needed
    }

    @Override
    public void onTaskContextLost(Boolean recoverable, ErrorCode error) {
        releaseCurrentPositionTask();
    }

    @Override
    public void onCurrentPositionResult(Position position) {
        if (mCurrentPositionTask != null) {
            if (mCurrentPositionTask.getCurrentPositionStatus() ==
                    RouteGuidanceTask.PositionStatusChangedListener.PositionStatus.GPS) {
                TimeZone timeZone = mCurrentPositionTask.getTimeZone();
                if (!timeZone.equals(mPreviousTimeZone)) {
                    if (Log.D) {
                        Log.d(TAG, "onCurrentPositionResult: new TimeZone received - " +
                                timeZone.getDisplayName());
                    }

                    mPreviousTimeZone = timeZone;
                }
            }
        }
    }

    private void releaseCurrentPositionTask() {
        if (mCurrentPositionTask != null) {
            mCurrentPositionTask.release();
            mCurrentPositionTask = null;
        }
    }
}