package com.example.andreas.recycler;

/**
 * Created by andreas on 4/20/15.
 */
public class AndroidBoundListModel extends AndroidEventListModel {

    public AndroidBoundListModel(EventBoundListModel model) {
        super(model.getEventList());
    }
}
