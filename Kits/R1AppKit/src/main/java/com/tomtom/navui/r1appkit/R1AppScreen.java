package com.tomtom.navui.r1appkit;

import com.tomtom.navui.sigappkit.SigAppScreen;
import com.tomtom.navui.taskkit.TaskContext;
import com.tomtom.navui.appkit.AppContext;

public class R1AppScreen extends SigAppScreen{
    @Override
    public void onCreateTasks(TaskContext taskKit) {
        //not needed by app screen
    }

    @Override
    public void onReleaseTasks() {
        //not needed by app screen
    }

    protected R1AppScreen(final AppContext context) {
        super(context);
    }
}
