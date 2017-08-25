package com.example.seo.stylebook;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CommentDataAdapter extends RecyclerView.Adapter<CommentListViewHolder> {
    Context context;
    ArrayList<CommentItem> items;
    ArrayList<ProfileItem> profileItems;

    public CommentDataAdapter(Context context, ArrayList<CommentItem> items, ArrayList<ProfileItem> profileItems) {
        this.context = context;
        this.items = items;
        this.profileItems = profileItems;
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    @Override
    public CommentListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_comment, null);
        return new CommentListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CommentListViewHolder holder, int position) {
        CommentItem item = items.get(position);

        Glide.with(context).load(item.getFacebookid() + ".jpg").into(holder.comment_image);
        holder.comment_name.setText(""+getName(item.getFacebookid(), profileItems));
        holder.comment_text.setText(item.getText());
        holder.comment_time.setText(""+TimeUtils.formatTimeString(Long.valueOf(item.getTime())));
    }

    public String getName(String facebookid, ArrayList<ProfileItem> profileItems) {
        for(int i = 0; i < profileItems.size(); i++) {
            if(profileItems.get(i).getFacebookid().equals(facebookid)){
                return profileItems.get(i).getName();
            }
        }

        return null;
    }
};