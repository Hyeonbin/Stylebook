package com.example.binny.resumeexample;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by binny on 2017. 8. 8..
 */

public class RecyclerHolder extends RecyclerView.ViewHolder{
    TextView name;

    public RecyclerHolder(View view) {
        super(view);

        name = (TextView)view.findViewById(R.id.name);
    }
}
