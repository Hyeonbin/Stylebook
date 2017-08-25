package com.example.seo.stylebook;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.io.IOException;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Seo on 2017-07-21.
 */
public class StyleListAdapter extends RecyclerView.Adapter<StyleListViewHolder> {
    Context context;
    ArrayList<StyleItem> items;
    ArrayList<LikeItem> likeitems;
    ArrayList<CommentItem> commentItems;
    ArrayList<ProfileItem> profileItems;
    Retrofit retrofit;
    ServerService serverService;
    OkHttpClient like_client;

    String PROFILE_ADDRESS = "http://10.0.2.2:3000/profile/";
    String STYLE_ADDRESS = "http://10.0.2.2:3000/stylelist/";

    public StyleListAdapter(Context context, ArrayList<StyleItem> items, ArrayList<LikeItem> likeitems, ArrayList<CommentItem> commentItems, ArrayList<ProfileItem> profileItems) {
        this.context = context;
        this.items = items;
        this.likeitems = likeitems;
        this.commentItems = commentItems;
        this.profileItems = profileItems;
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    @Override
    public StyleListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_stylelist, null);
        return new StyleListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final StyleListViewHolder holder, int position) {
        // Todo: likenum, commentnum -> 데이터베이스 각각 불러서 직접 숫자를 셀 것 인가?
        like_client = new OkHttpClient.Builder().connectTimeout(10000, TimeUnit.MILLISECONDS).cookieJar(new JavaNetCookieJar(new CookieManager())).build();
        retrofit = new Retrofit.Builder().client(like_client).baseUrl(serverService.API_URL).build();
        serverService = retrofit.create(ServerService.class);

        final StyleItem item = items.get(position);
        Glide.with(context).load(PROFILE_ADDRESS + item.getFacebookid() + ".jpg").into(holder.stylelist_publisherimage);
        holder.stylelist_publishername.setText(""+getName(item.getFacebookid(), profileItems));
        Glide.with(context).load(STYLE_ADDRESS + item.getImagename()).into(holder.stylelist_image);
        holder.stylelist_text.setText(item.getText());
        holder.stylelist_likenum.setText(""+getLikenum(item.getId(), likeitems));
        holder.stylelist_commentnum.setText(""+getCommentnum(item.getId(), commentItems));
        holder.stylelist_time.setText(""+TimeUtils.formatTimeString(Long.valueOf(item.getTime())));
        if(identifyLike(item.getId(), AccessToken.getCurrentAccessToken().getUserId(), likeitems) == 1)
            holder.stylelist_like.setImageResource(R.drawable.like_red);
        else
            holder.stylelist_like.setImageResource(R.drawable.like);

        holder.stylelist_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Todo : 좋아요 버튼 처리 요망, 좋아요 버튼 색 처리
                if(identifyLike(item.getId(), item.getFacebookid(), likeitems) == 0) {
                    holder.stylelist_like.setImageResource(R.drawable.like_red);
                    LikeItem likeItem = new LikeItem();
                    likeItem.setFacebookid(AccessToken.getCurrentAccessToken().getUserId());
                    likeItem.setListid(item.getId());
                    likeItem.setTime(String.valueOf(System.currentTimeMillis()));
                    likeitems.add(likeItem);
                    holder.stylelist_likenum.setText(""+getLikenum(item.getId(), likeitems));
                    Call<ResponseBody> call2 = serverService.likebtnClicked(
                            item.getId(),
                            AccessToken.getCurrentAccessToken().getUserId(),
                            "1"
                    );
                    call2.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.v("Likebtn", "Likebtn is clicked");
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                          //  Log.v("Likebtn", t.getLocalizedMessage());
                        }
                    });
                } else {
                    holder.stylelist_like.setImageResource(R.drawable.like);
                    deleteLikelist(item.getId(), AccessToken.getCurrentAccessToken().getUserId(), likeitems);
                    holder.stylelist_likenum.setText(""+getLikenum(item.getId(), likeitems));
                    Call<ResponseBody> call2 = serverService.likebtnUnclicked(
                            item.getId(),
                            AccessToken.getCurrentAccessToken().getUserId()
                    );
                    call2.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.v("Likebtn", "Likebtn is clicked");
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                          //  Log.v("Likebtn", t.getLocalizedMessage());
                        }
                    });
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Todo : 버튼 처리 요망 -> id값을 CommentActivity로 전달
                Intent intent = new Intent(view.getContext(), CommentActivity.class);
                intent.putExtra("listid", item.getId());
                view.getContext().startActivity(intent);
            }
        });
    }

    public int getLikenum(int listid, ArrayList<LikeItem> likeitems) {
        int cnt = 0;
        for(int i = 0 ; i < likeitems.size(); i++){
            if(likeitems.get(i).getListid() == listid)
                cnt++;
        }

        return cnt;
    }

    public int getCommentnum(int listid, ArrayList<CommentItem> commentItems) {
        int cnt = 0;
        for(int i = 0; i < commentItems.size(); i++){
            if(commentItems.get(i).getListid() == listid)
                cnt++;
        }

        return cnt;
    }

    public String getName(String facebookid, ArrayList<ProfileItem> profileItems) {
        for(int i = 0; i < profileItems.size(); i++) {
            if(profileItems.get(i).getFacebookid().equals(facebookid)){
                return profileItems.get(i).getName();
            }
        }

        return null;
    }

    public int identifyLike(int listid, String facebookid, ArrayList<LikeItem> likeitems) {
        for(int i = 0; i < likeitems.size(); i++){
            if(likeitems.get(i).getListid() == listid && likeitems.get(i).getFacebookid().equals(facebookid))
                return 1;
        }

        return 0;
    }

    private void deleteLikelist(int listid, String facebookid, ArrayList<LikeItem> likeItems) {
        for(int i = 0; i < likeItems.size(); i++){
            if(likeitems.get(i).getListid() == listid && likeitems.get(i).getFacebookid().equals(facebookid))
                likeItems.remove(i);
        }
    }
}