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
    public static Fragment currentfragment;
    ServerService serverService;
    ArrayList<StyleItem> like_arraylist = new ArrayList<>();
    ArrayList<LikeItem> like_likelistForStylelist = new ArrayList<>();
    ArrayList<CommentItem> like_commentlist = new ArrayList<>();
    ArrayList<ProfileItem> like_profilelist = new ArrayList<>();

    SwipeRefreshLayout like_swiperefreshlayout;
    RecyclerView like_recyclerview;

    StyleListAdapter like_styleListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_likelist, container, false);

        currentfragment = this;

        like_swiperefreshlayout = (SwipeRefreshLayout)rootView.findViewById(R.id.Sb_Like_Swiperefreshlayout);

        like_recyclerview = (RecyclerView) rootView.findViewById(R.id.Sb_Like_Recyclerview);
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
                    like_likelistForStylelist = SetArrayListUtils.setLikelistArray(likedataforstylelist);
                    like_commentlist = SetArrayListUtils.setCommentlistArray(commentdata);
                    like_arraylist = SetArrayListUtils.setStylelistArray(stylelistdata, likedata);
                    like_profilelist = SetArrayListUtils.setProfilelistArray(profiledata);
                    like_styleListAdapter = new StyleListAdapter(getContext(), like_arraylist, like_likelistForStylelist, like_commentlist, like_profilelist, currentfragment);
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
                            like_likelistForStylelist = SetArrayListUtils.setLikelistArray(likedataforstylelist);
                            like_commentlist = SetArrayListUtils.setCommentlistArray(commentdata);
                            like_arraylist = SetArrayListUtils.setStylelistArray(stylelistdata, likedata);
                            like_profilelist = SetArrayListUtils.setProfilelistArray(profiledata);
                            like_styleListAdapter = new StyleListAdapter(getContext(), like_arraylist, like_likelistForStylelist, like_commentlist, like_profilelist, currentfragment);
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

    @Override
    public void onResume() {
        super.onResume();

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
                    like_likelistForStylelist = SetArrayListUtils.setLikelistArray(likedataforstylelist);
                    like_commentlist = SetArrayListUtils.setCommentlistArray(commentdata);
                    like_arraylist = SetArrayListUtils.setStylelistArray(stylelistdata, likedata);
                    like_profilelist = SetArrayListUtils.setProfilelistArray(profiledata);
                    like_styleListAdapter = new StyleListAdapter(getContext(), like_arraylist, like_likelistForStylelist, like_commentlist, like_profilelist, currentfragment);
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
                            like_likelistForStylelist = SetArrayListUtils.setLikelistArray(likedataforstylelist);
                            like_commentlist = SetArrayListUtils.setCommentlistArray(commentdata);
                            like_arraylist = SetArrayListUtils.setStylelistArray(stylelistdata, likedata);
                            like_profilelist = SetArrayListUtils.setProfilelistArray(profiledata);
                            like_styleListAdapter = new StyleListAdapter(getContext(), like_arraylist, like_likelistForStylelist, like_commentlist, like_profilelist, currentfragment);
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

    }
}
