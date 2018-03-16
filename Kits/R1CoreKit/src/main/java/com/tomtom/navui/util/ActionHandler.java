package com.tomtom.navui.util;

/**
 * Action handler to handle events and actions from other applications.
 * 
 * @param <T> T - can be URL or Intent or any type which will be supported by application
 */
public interface ActionHandler<T> {

    /**
     * Triggers action handling, of the type given by {@link T}
     * @param input - an action to be handled by a particular handler
     */
    void handle(T input);
}
