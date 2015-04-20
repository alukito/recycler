package com.example.andreas.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import ca.odell.glazedlists.listfield.ListDataEvent;
import ca.odell.glazedlists.listfield.ListDataListener;

/**
 * Created by andreas on 4/16/15.
 */
public class GlazedRecyclerAdapter<T> extends RecyclerView.Adapter<GlazedRecyclerAdapter.ViewHolder> {

    private AndroidEventListModel listModel;
    private final TypeInfo[] typeInfo;
    private final UpdateHandler updateHandler;


    public GlazedRecyclerAdapter(
            AndroidBoundListModel listModel,
            TypeInfo[] typeInfo) {
        this.updateHandler = new UpdateHandler();
        this.listModel = initListModel(listModel, updateHandler);
        this.typeInfo = typeInfo;
    }

    private AndroidEventListModel initListModel(
            AndroidEventListModel listModel, ListDataListener updateHandler) {
        listModel.addListDataListener(updateHandler);
        return listModel;
    }

    public void dispose() {
        listModel.removeListDataListener(updateHandler);
        listModel = null;
    }

    private class UpdateHandler implements ListDataListener {

        @Override
        public void contentsChanged(ListDataEvent e) {
            if(isStructureChanged(e)) {
                notifyDataSetChanged();
            } else {
                notifyItemRangeChanged(e.getIndex0(), getRangeCount(e));
            }
        }

        @Override
        public void intervalAdded(ListDataEvent e) {
            if(isStructureChanged(e)) {
                notifyDataSetChanged();
            } else {
                notifyItemRangeInserted(e.getIndex0(), getRangeCount(e));
            }
        }

        @Override
        public void intervalRemoved(ListDataEvent e) {
            if(isStructureChanged(e)) {
                notifyDataSetChanged();
            } else {
                notifyItemRangeRemoved(e.getIndex0(), getRangeCount(e));
            }
        }

        private boolean isStructureChanged(ListDataEvent e) {
            return e.getIndex1() == Integer.MAX_VALUE;
        }

        private int getRangeCount(ListDataEvent e) {
            return e.getIndex1() - e.getIndex0() + 1;
        }
    }

    @Override
    public GlazedRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(
                typeInfo[type].getLayoutResourceId(), parent, false);

        ViewInitializer viewInitializer = typeInfo[type].getViewInitializer();
        if (viewInitializer != null) {
            viewInitializer.init(convertView);
        }

        ViewHolder viewHolder = createHolder(convertView, typeInfo[type].getColumnDefinition());

        return viewHolder;
    }

    protected ViewHolder createHolder(View convertView, ColumnDefinition[] columnDefinitions) {
        final int count = columnDefinitions.length;
        final View[] views = new View[count];

        for (int i = 0; i < count; i++) {
            views[i] = convertView.findViewById(columnDefinitions[i].getResourceId());
        }

        ViewHolder viewHolder = new ViewHolder(convertView);
        viewHolder.views = views;

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(GlazedRecyclerAdapter.ViewHolder holder, int position) {
        final T item = getItemSafeCast(position);

        int type = holder.getItemViewType();

        updateEachColumn(item, holder.views, typeInfo[type].getColumnDefinition());
        decorateRow(position, item, holder.itemView, typeInfo[type].getViewDecorator());
    }

    @SuppressWarnings("unchecked")
    private T getItemSafeCast(final int position) {
        return (T) listModel.getElementAt(position);
    }

    @Override
    public int getItemCount() {
        return listModel.getSize();
    }

    private void updateEachColumn(T item, View[] holders, ColumnDefinition[] columnDefinitions) {
        final int count = columnDefinitions.length;

        for(int i=0 ; i < count; i++) {
            final View holderView = holders[i];
            if (holderView != null) {
                ColumnDefinition<View,T> colDef = columnDefinitions[i];
                colDef.setViewValue(holderView, item);
            }
        }
    }

    private void decorateRow(
            int position, Object item, View view, ViewDecorator viewDecorator) {
        if (viewDecorator != null) {
            viewDecorator.decorateView(position, item, view);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        View[] views;

        public ViewHolder(View rootView) {
            super(rootView);
        }
    }
}
