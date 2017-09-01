package com.example.seo.stylebook;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by binny on 2017. 8. 18..
 */

public class CommentListViewHolder extends RecyclerView.ViewHolder {
    public ImageView comment_image;
    public TextView comment_name;
    public TextView comment_text;
    public TextView comment_time;

    public CommentListViewHolder(View view){
        super(view);
        comment_image = (ImageView)view.findViewById(R.id.Sb_Listview_Commentimg);
        comment_name = (TextView)view.findViewById(R.id.Sb_Listview_Commentperson);
        comment_text = (TextView)view.findViewById(R.id.Sb_Listview_Commenttext);
        comment_time = (TextView)view.findViewById(R.id.Sb_Listview_Commenttime);
    }
}
