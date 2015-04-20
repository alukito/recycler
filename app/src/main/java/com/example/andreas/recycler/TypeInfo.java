package com.example.andreas.recycler;

/**
 * Created by johnyj on 26/08/14.
 */
public interface TypeInfo {

    public int getLayoutResourceId();

    public ColumnDefinition[] getColumnDefinition();

    public ViewInitializer getViewInitializer();

    public boolean isTypeCorrect(Object object);

    public ViewDecorator getViewDecorator();
}