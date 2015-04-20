package com.example.andreas.recycler;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;


public class MainActivity extends ActionBarActivity {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private GlazedRecyclerAdapter<Hore> glazedRecyclerAdapter;
    private EventBoundListModel listModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        listModel = new EventBoundListModel();

        Hore hore = new Hore();
        hore.hore1 = "Horeee";
        hore.hore2 = "Yaak !";

        listModel.addItem(hore);

        hore = new Hore();
        hore.hore1 = "Horeee 2";
        hore.hore2 = "Yaak 2!";

        listModel.addItem(hore);

        glazedRecyclerAdapter = new GlazedRecyclerAdapter<Hore>(new AndroidBoundListModel(listModel),
                new TypeInfo[] {
                        new TypeInfo() {
                            @Override
                            public int getLayoutResourceId() {
                                return R.layout.my_text_view;
                            }

                            @Override
                            public ColumnDefinition[] getColumnDefinition() {
                                return new ColumnDefinition[] {
                                    new ColumnDefinition<TextView, Hore>() {
                                        @Override
                                        public void setViewValue(TextView view, Hore item) {
                                            view.setText(item.hore1);
                                        }

                                        @Override
                                        public int getResourceId() {
                                            return R.id.text1;
                                        }
                                    },
                                    new ColumnDefinition<TextView, Hore>() {
                                        @Override
                                        public void setViewValue(TextView view, Hore item) {
                                            view.setText(item.hore2);
                                        }

                                        @Override
                                        public int getResourceId() {
                                            return R.id.text2;
                                        }
                                    }
                                };
                            }

                            @Override
                            public ViewInitializer getViewInitializer() {
                                return null;
                            }

                            @Override
                            public boolean isTypeCorrect(Object object) {
                                return true;
                            }

                            @Override
                            public ViewDecorator getViewDecorator() {
                                return null;
                            }
                        }
                });

        mRecyclerView.setAdapter(glazedRecyclerAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    Hore hore = new Hore();
                    hore.hore1 = "Horeee ";
                    hore.hore2 = "Yaak !";

                    listModel.addItem(hore);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class Hore {
        String hore1;
        String hore2;
    }
}
