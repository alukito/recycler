package com.example.andreas.recycler;

import android.view.View;

public interface ColumnDefinition<V extends View, T> {

	int getResourceId();

	void setViewValue(V view, T item);
}
