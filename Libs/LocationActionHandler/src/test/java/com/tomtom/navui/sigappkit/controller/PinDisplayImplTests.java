package com.tomtom.navui.sigappkit.controller;

import android.content.Intent;

import com.tomtom.navui.appkit.AppContext;
import com.tomtom.navui.systemport.SystemContext;
import com.tomtom.navui.systemport.systemcomponents.ScreenSystemComponent;
import com.tomtom.navui.taskkit.Location2;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Config(manifest = "./src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class PinDisplayImplTests {

    @Test
    public void testShouldRunHomeScreen() {
        //GIVEN implementation of displayer
        AppContext mckAppContext = mock(AppContext.class);
        SystemContext mckSysContext = mock(SystemContext.class);
        when(mckSysContext.getComponent(ScreenSystemComponent.class)).thenReturn(mock(ScreenSystemComponent.class));
        when(mckAppContext.getSystemPort()).thenReturn(mckSysContext);
        PinDisplayerImpl tested = new PinDisplayerImpl(mckAppContext);
        //WHEN we try display location  
        tested.displayLocation(mock(Location2.class));
        //THEN start screen should be called
        verify(mckAppContext.getSystemPort().getComponent(ScreenSystemComponent.class)).startScreen(any(Intent.class));
    }
    
}
