package com.practice.venues;

/**
 * Created by nandit on 17/11/18.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.security.AccessControlContext;
import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
//    private ItemData[] itemsData;
    private ArrayList<ItemData> itemsData = new ArrayList<ItemData>();
    private final MainActivity.RecyclerViewItemClickListener mListener;
    public MyAdapter(MainActivity.RecyclerViewItemClickListener mListener, ArrayList<ItemData> itemsData) {
        this.itemsData = itemsData;
        this.mListener = mListener;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, null);

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // - get data from your itemsData at this position
        // - replace the contents of the view with that itemsData
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickListenerForItem(position, itemsData);
            }
        });
        viewHolder.txtViewTitle.setText(itemsData.get(position).getTitle());
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtViewTitle;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.item_title);
        }
    }


    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        try {
            return itemsData.size();
        }
        catch (NullPointerException e) {
            Log.e("Exception", "itemsData is null");
            return 0;
        }
    }
}