package com.tomtom.navui.sigappkit.controller;

import com.tomtom.navui.core.BaseModel;
import com.tomtom.navui.core.Model;
import com.tomtom.navui.systemport.SystemGpsObservable;

/**
 * Mock for SystemGpsObservable. You have to set GPS in the constructor.
 */
public class MockSystemGpsObservable implements SystemGpsObservable {
    
    private final Model<Attributes> mModel;
    
    public MockSystemGpsObservable(LocationServiceState state) {
        mModel = new BaseModel<Attributes>(Attributes.class);
        mModel.putEnum(Attributes.GPS_STATUS, state);
    }

    @Override
    public Model<Attributes> getModel() {
        return mModel;
    }

    @Override
    public void release() {
        
    }
}
