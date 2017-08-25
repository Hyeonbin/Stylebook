package com.example.seo.stylebook;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Seo on 2017-07-21.
 */
public class StyleListViewHolder extends RecyclerView.ViewHolder {
    public ImageView stylelist_publisherimage;
    public TextView stylelist_publishername;
    public ImageView stylelist_image;
    public TextView stylelist_text;
    public ImageView stylelist_like;
    public TextView stylelist_likenum;
    public TextView stylelist_commentnum;
    public TextView stylelist_time;

    public StyleListViewHolder(View view) {
        super(view);
        stylelist_publisherimage = (ImageView)view.findViewById(R.id.Sb_Cardview_Publisherimage);
        stylelist_publishername = (TextView)view.findViewById(R.id.Sb_Cardview_Publishername);
        stylelist_image = (ImageView)view.findViewById(R.id.Sb_Cardview_Image);
        stylelist_text = (TextView)view.findViewById(R.id.Sb_Cardview_Text);
        stylelist_like  = (ImageView)view.findViewById(R.id.Sb_Cardview_Like);
        stylelist_likenum = (TextView)view.findViewById(R.id.Sb_Cardview_Likenum);
        stylelist_commentnum = (TextView)view.findViewById(R.id.Sb_Cardview_Commentnum);
        stylelist_time = (TextView)view.findViewById(R.id.Sb_Cardview_Time);
    }
}
