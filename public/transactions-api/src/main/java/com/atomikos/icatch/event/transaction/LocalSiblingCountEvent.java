package com.atomikos.icatch.event.transaction;

import com.atomikos.icatch.event.Event;

/**
 * Event for notifying event listeners when local siblings are added or terminated
 *
 * @author Chris Matthews (github: chrisbmatthews)
 */
public class LocalSiblingCountEvent extends Event {
    private int currentLocalSiblingsAdded = 0;
    private int currentLocalSiblingsTerminated = 0;

    public LocalSiblingCountEvent(int currentLocalSiblingsAdded, int currentLocalSiblingsTerminated) {
        this.currentLocalSiblingsAdded = currentLocalSiblingsAdded;
        this.currentLocalSiblingsTerminated = currentLocalSiblingsTerminated;
    }

    public LocalSiblingCountEvent() {
    }

    public int getCurrentLocalSiblingsAdded() {
        return currentLocalSiblingsAdded;
    }

    public void setCurrentLocalSiblingsAdded(int currentLocalSiblingsAdded) {
        this.currentLocalSiblingsAdded = currentLocalSiblingsAdded;
    }

    public int getCurrentLocalSiblingsTerminated() {
        return currentLocalSiblingsTerminated;
    }

    public void setCurrentLocalSiblingsTerminated(int currentLocalSiblingsTerminated) {
        this.currentLocalSiblingsTerminated = currentLocalSiblingsTerminated;
    }
}
