package com.example.seo.stylebook;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.CookieManager;
import java.util.ArrayList;
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
public class LikeActivity extends Fragment {
    Retrofit retrofit;
    ServerService serverService;
    ArrayList<StyleItem> like_arraylist = new ArrayList<>();
    ArrayList<LikeItem> like_likelistForStylelist = new ArrayList<>();
    ArrayList<CommentItem> like_commentlist = new ArrayList<>();
    ArrayList<ProfileItem> like_profilelist = new ArrayList<>();

    StyleListAdapter like_styleListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_likelist, container, false);

        final SwipeRefreshLayout like_swiperefreshlayout = (SwipeRefreshLayout)rootView.findViewById(R.id.Sb_Like_Swiperefreshlayout);

        final RecyclerView like_recyclerview = (RecyclerView) rootView.findViewById(R.id.Sb_Like_Recyclerview);
        LinearLayoutManager like_layoutmanager = new LinearLayoutManager(getContext());
        like_recyclerview.setHasFixedSize(true);
        like_recyclerview.setLayoutManager(like_layoutmanager);

        OkHttpClient like_client = new OkHttpClient.Builder().connectTimeout(10000, TimeUnit.MILLISECONDS).cookieJar(new JavaNetCookieJar(new CookieManager())).build();
        retrofit = new Retrofit.Builder().client(like_client).baseUrl(serverService.API_URL).build();
        serverService = retrofit.create(ServerService.class);
        final Call<ResponseBody> call = serverService.getLikelist(AccessToken.getCurrentAccessToken().getUserId()); // Todo : facebook id 입력
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String responseResult = response.body().string();
                    JSONArray jsonArray = new JSONArray(responseResult);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    Log.v("LikeActivity", jsonArray.toString());
                    JSONArray likedata = new JSONArray(jsonObject.getString("likedata"));
                    JSONArray likedataforstylelist = new JSONArray(jsonObject.getString("likedataforstylelist"));
                    JSONArray commentdata = new JSONArray(jsonObject.getString("commentdata"));
                    JSONArray stylelistdata = new JSONArray(jsonObject.getString("stylelistdata"));
                    JSONArray profiledata = new JSONArray(jsonObject.getString("profiledata"));
                    setLikelistArray(likedataforstylelist);
                    setCommentlistArray(commentdata);
                    setStylelistArray(stylelistdata, likedata);
                    setProfilelistArray(profiledata);
                    like_styleListAdapter = new StyleListAdapter(getContext(), like_arraylist, like_likelistForStylelist, like_commentlist, like_profilelist);
                    like_recyclerview.setAdapter(like_styleListAdapter);
                    like_styleListAdapter.notifyDataSetChanged();
                    Log.v("LikeActivity", "Call is completed!");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
               // Log.v("LikeActivity", t.getLocalizedMessage());
            }
        });

        like_swiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                call.clone().enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String responseResult = response.body().string();
                            JSONArray jsonArray = new JSONArray(responseResult);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            Log.v("LikeActivity", jsonArray.toString());
                            JSONArray likedata = new JSONArray(jsonObject.getString("likedata"));
                            JSONArray likedataforstylelist = new JSONArray(jsonObject.getString("likedataforstylelist"));
                            JSONArray commentdata = new JSONArray(jsonObject.getString("commentdata"));
                            JSONArray stylelistdata = new JSONArray(jsonObject.getString("stylelistdata"));
                            JSONArray profiledata = new JSONArray(jsonObject.getString("profiledata"));
                            setLikelistArray(likedataforstylelist);
                            setCommentlistArray(commentdata);
                            setStylelistArray(stylelistdata, likedata);
                            setProfilelistArray(profiledata);
                            like_styleListAdapter = new StyleListAdapter(getContext(), like_arraylist, like_likelistForStylelist, like_commentlist, like_profilelist);
                            like_recyclerview.setAdapter(like_styleListAdapter);
                            like_styleListAdapter.notifyDataSetChanged();
                            Log.v("LikeActivity", "Call is completed!");
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                       // Log.v("LikeActivity", t.getLocalizedMessage());
                    }
                });

                like_swiperefreshlayout.setRefreshing(false);
            }
        });

        return rootView;
    }

    private void setLikelistArray(JSONArray jsonArray) throws JSONException {
        like_likelistForStylelist = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            LikeItem likeitem = new LikeItem();
            likeitem.setId(jsonObject.getInt("id"));
            likeitem.setListid(jsonObject.getInt("listid"));
            likeitem.setFacebookid(jsonObject.getString("facebookid"));
            likeitem.setTime(jsonObject.getString("time"));
            like_likelistForStylelist.add(likeitem);
        }
    }

    private void setCommentlistArray(JSONArray jsonArray) throws JSONException {
        like_commentlist = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            CommentItem commentItem = new CommentItem();
            commentItem.setId(jsonObject.getInt("id"));
            commentItem.setListid(jsonObject.getInt("listid"));
            commentItem.setFacebookid(jsonObject.getString("facebookid"));
            commentItem.setText(jsonObject.getString("text"));
            commentItem.setTime(jsonObject.getString("time"));
            like_commentlist.add(commentItem);
        }
    }

    private void setStylelistArray(JSONArray stylelistdata, JSONArray likedata) throws JSONException{
        like_arraylist = new ArrayList<>();
        for(int i = 0; i < stylelistdata.length(); i++){
            JSONObject jsonObject = stylelistdata.getJSONObject(i);
            for(int j = 0; j < likedata.length(); j++) {
                if(jsonObject.getInt("id") == likedata.getJSONObject(j).getInt("listid")) {
                    StyleItem styleItem = new StyleItem();
                    styleItem.setId(jsonObject.getInt("id"));
                    styleItem.setFacebookid(jsonObject.getString("facebookid"));
                    styleItem.setImagename(jsonObject.getString("imagename"));
                    styleItem.setText(jsonObject.getString("text"));
                    styleItem.setTime(jsonObject.getString("time"));
                    like_arraylist.add(styleItem);
                }
            }
        }
    }

    private void setProfilelistArray(JSONArray jsonArray) throws JSONException {
        like_profilelist = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            ProfileItem profileItem = new ProfileItem();
            profileItem.setFacebookid(jsonObject.getString("facebookid"));
            profileItem.setName(jsonObject.getString("name"));
            profileItem.setLocation(jsonObject.getString("location"));
            profileItem.setStyle(jsonObject.getString("style"));
            profileItem.setText(jsonObject.getString("text"));
            like_profilelist.add(profileItem);
        }
    }
}
