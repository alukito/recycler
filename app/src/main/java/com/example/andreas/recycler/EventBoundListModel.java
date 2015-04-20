package com.example.andreas.recycler;


import java.util.ArrayList;
import java.util.List;

import quile.util.ArraysUtil;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;

@SuppressWarnings("deprecation")
public class EventBoundListModel implements BoundListModel {
	
	private EventList eventList;
	private List listenerList = new ArrayList();
	private ListEventHandler eventHandler;

	public EventBoundListModel() {
		this(new BasicEventList());
	}
	
	public EventBoundListModel(EventList eventList) {
		this.eventList = eventList;
		eventHandler = new ListEventHandler();
		eventList.addListEventListener(eventHandler);
	}
	
	public void dispose() {
		eventList.dispose();
	}
	
	public EventList getEventList() {
		return eventList;
	}
	
	public boolean addItem(Object item) {
		writeLock();
		try {
		    boolean status = eventList.add(item);
		    return status;
		} finally {
		    writeUnlock();
		}
	}

	public Object removeItem(int index) {
		writeLock();
		try {
			Object removedItem = eventList.remove(index);
			return removedItem;        
		} finally {
			writeUnlock();
		}
	}

	public boolean removeItem(Object removedItem) {		
		writeLock();
        try {
        	boolean status = findAndRemoveObject(removedItem);
        	return status;
        } finally {
            writeUnlock();
        }
	}
	
	private boolean findAndRemoveObject(Object item) {
	    int idx = eventList.indexOf(item);
        if (idx < 0) {
            return false;
        } else {
            removeItem(idx);
            return true;
        }
	}
	
	public boolean updateItem(Object oldItem, Object newItem) {
		writeLock();
		try {
			int idx = getIndex(oldItem);
			if (idx < 0) {
				return false;
			}
			updateItem(idx, newItem);
			return true;
		} finally {
			writeUnlock();
		}
	}

	public boolean updateItem(int index, Object newItem) {
		writeLock();
		try {
			try {
				eventList.set(index, newItem);
				return true;
			} catch(IndexOutOfBoundsException iobe) {
				return false;
			}						
		} finally {
			writeUnlock();
		}
	}
	
	public void resetAll(Object[] items) {		
		resetAll(ArraysUtil.asList(items));		
	}

    public void resetAll(List items) {
        writeLock();
        eventHandler.switchOff();
        try {
            eventList.clear();
            eventList.addAll(items);
            /*
            tidak bisa pake ini, buggy saat list pake sortedList. Tapi kalau hanya basicEventList, bisa.
            GlazedLists.replaceAll(
                    eventList, items, false);
             */
            fireChanged();
        } finally {
            eventHandler.switchOn();
            writeUnlock();
        }
    }

    public void clearAll() {
		writeLock();
		eventHandler.switchOff();
		try {			
			eventList.clear();
			fireCleared();
		} finally {
			eventHandler.switchOn();
			writeUnlock();			
		}
	}
	
	public int getSize() {
		readLock();
        try {
            return eventList.size();
        } finally {
          readUnlock();
        }
	}

	public Object getItem(int index) {
		readLock();
		try {
		    return eventList.get(index);
		} catch (IndexOutOfBoundsException iex) {
		    return null;
		} finally {
		    readUnlock();
		}
	}
	
	public int getIndex(Object item) {
		readLock();
        try {
            int len = eventList.size();
            for (int i = 0 ; i < len ; i++) {
                if (eventList.get(i) == item) {
                    return i;
                }
            }
            return -1;
        } finally {
            readUnlock();
        }
	}

	public Object getLock() {
		return this;
	}

	public void addBoundListModelListener(BoundListModelListener listener) {
		listenerList.add(listener);
	}

	public void removeBoundListModelListener(BoundListModelListener listener) {
		listenerList.remove(listener);
	}	

    public void writeLock() {
        eventList.getReadWriteLock().writeLock().lock();
    }

    public void writeUnlock() {
        eventList.getReadWriteLock().writeLock().unlock();
    }

    public void readLock() {
        eventList.getReadWriteLock().readLock().lock();
    }

    public void readUnlock() {
        eventList.getReadWriteLock().readLock().unlock();
    }
    
    protected void fireAdded(Object added, int lastIdx) {
		for (int i = 0; i < listenerList.size(); i++) {
			BoundListModelListener listener = (BoundListModelListener) listenerList
					.get(i);
			listener.dataAdded(added, lastIdx);
		}
	}

	protected void fireRemoved(Object removed, int lastIdx) {
		for (int i = 0; i < listenerList.size(); i++) {
			BoundListModelListener listener = (BoundListModelListener) listenerList
					.get(i);
			listener.dataRemoved(removed, lastIdx);
		}
	}

	protected void fireUpdated(Object oldItem, Object newItem, int lastIdx) {
		for (int i = 0; i < listenerList.size(); i++) {
			BoundListModelListener listener = (BoundListModelListener) listenerList
					.get(i);
			listener.dataUpdated(oldItem, newItem, lastIdx);
		}
	}

	protected void fireCleared() {
		for (int i = 0; i < listenerList.size(); i++) {
			BoundListModelListener listener = (BoundListModelListener) listenerList
					.get(i);
			listener.dataCleared();
		}
	}

	protected void fireChanged() {
		for (int i = 0; i < listenerList.size(); i++) {
			BoundListModelListener listener = (BoundListModelListener) listenerList
					.get(i);
			listener.dataChanged();
		}
	}

    private class ListEventHandler implements ListEventListener {
    	private boolean on = true;
    	
    	// ListChanged dari GlazedList semuanya berupa fine grained
    	// artinya proses bersifat detail per item.
    	 
    	// Untuk kasus tertentu seperti clear dan reset, diperlukan
    	// event yang menyatakan kalau semua sudah berubah.
    	
    	// Oleh karena itu diperlukan semacam switch untuk mematikan 
    	// sementara proses fire event agar bisa di fire secara manual 
    	public void switchOn() {
    		this.on = true;
    	}
    	
    	public void switchOff() {
    		this.on = false;
    	}
    	
		public void listChanged(ListEvent listChanges) {
			if (!on) {
				return;
			}
			
			if (!listChanges.isReordering()) {
                while (listChanges.next()) {
                    int index = listChanges.getIndex();
                    switch (listChanges.getType()) {
                        case ListEvent.INSERT:
                            fireAdded(eventList.get(index), index);
                            break;
                        case ListEvent.UPDATE:
                        	fireUpdated(listChanges.getOldValue(), 
                        			eventList.get(index), index);
                        	break;
                        case ListEvent.DELETE:
                            fireRemoved(listChanges.getOldValue(), index);
                            break;
                        default:
                            break;
                    }
                }
            }			
		}
    	
    }
}
