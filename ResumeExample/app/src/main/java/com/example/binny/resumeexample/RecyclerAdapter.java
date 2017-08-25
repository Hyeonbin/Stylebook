package com.example.binny.resumeexample;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by binny on 2017. 8. 8..
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerHolder> {
    Context context;
    ArrayList<String> items;

    public RecyclerAdapter(Context context, ArrayList<String> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_recyclerview, null);
        return new RecyclerHolder(v);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    @Override
    public void onBindViewHolder(RecyclerHolder holder, int position) {
        String name = items.get(position);
        holder.name.setText(name);
    }
}
