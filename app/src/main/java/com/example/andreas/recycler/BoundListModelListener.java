package com.example.andreas.recycler;

public interface BoundListModelListener {	
    void dataAdded(Object addedValue, int lastIdx);
    void dataRemoved(Object removedValue, int lastIdx);
    void dataUpdated(Object oldValue, Object newValue, int lastIdx);
    void dataCleared();
    void dataChanged();
}
