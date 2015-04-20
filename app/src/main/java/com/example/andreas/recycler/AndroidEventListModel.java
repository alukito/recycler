package com.example.andreas.recycler;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.TransformedList;
import ca.odell.glazedlists.listfield.EventListModel;

public class AndroidEventListModel extends EventListModel {

	public AndroidEventListModel(EventList source) {
		super(source);
	}
	
	@Override
	protected TransformedList proxyWrappedEventList(EventList source) {
		return new AndroidThreadProxyEventList(source);
	}

}
