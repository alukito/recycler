/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.andreas.recycler;

import java.util.List;

public interface BoundListModel {
	
    public int getSize() ;

    public Object getItem(int index);

    public boolean addItem(Object item);
    
	public void resetAll(Object[] items);		
	
	public void resetAll(List items);

    public Object removeItem(int index);

    public boolean removeItem(Object removedItem);

    public boolean updateItem(Object oldItem, Object newItem);
    
    public boolean updateItem(int index, Object newItem);
    
    public void clearAll();
    
    public int getIndex(Object item);
    
    public Object getLock();

    public void addBoundListModelListener(BoundListModelListener listener);
	
	public void removeBoundListModelListener(BoundListModelListener listener);
}
