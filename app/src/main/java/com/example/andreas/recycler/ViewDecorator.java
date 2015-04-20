package com.example.andreas.recycler;

import android.view.View;

public interface ViewDecorator {
	void decorateView(int position, Object item, View view);
}
