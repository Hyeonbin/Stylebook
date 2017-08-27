package com.example.seo.stylebook;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.Profile;

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
public class StyleListActivity extends Fragment {
    Retrofit retrofit;
    public static Fragment currentfragment;
    ServerService serverService;
    ArrayList<StyleItem> stylelist_arraylist;
    ArrayList<LikeItem> stylelist_likelist;
    ArrayList<CommentItem> stylelist_commentlist;
    ArrayList<ProfileItem> stylelist_profilelist;

    FloatingActionButton stylelist_fab;
    SwipeRefreshLayout stylelist_swiperefreshlayout;
    RecyclerView stylelist_recyclerview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.floating_stylelist, container, false);

        currentfragment = this;

        stylelist_fab = (FloatingActionButton)rootView.findViewById(R.id.Sb_Floating_Stylelistbtn);

        stylelist_swiperefreshlayout = (SwipeRefreshLayout)rootView.findViewById(R.id.Sb_Stylelist_Swiperefreshlayout);

        stylelist_swiperefreshlayout.setColorSchemeColors(
            Color.BLACK, Color.WHITE
        );

        stylelist_recyclerview = (RecyclerView)rootView.findViewById(R.id.Sb_Stylelist_Recyclerview);
        LinearLayoutManager stylelist_layoutmanager = new LinearLayoutManager(getContext());
        stylelist_recyclerview.setHasFixedSize(true);
        stylelist_recyclerview.setLayoutManager(stylelist_layoutmanager);

        OkHttpClient stylelist_client = new OkHttpClient.Builder().connectTimeout(10000, TimeUnit.MILLISECONDS).cookieJar(new JavaNetCookieJar(new CookieManager())).build();
        retrofit = new Retrofit.Builder().client(stylelist_client).baseUrl(serverService.API_URL).build();
        serverService = retrofit.create(ServerService.class);
        final Call<ResponseBody> call = serverService.getList();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String responseResult = response.body().string();
                    JSONArray jsonArray = new JSONArray(responseResult);
                    Log.v("StyleListActivity", jsonArray.toString());
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    JSONArray stylelistdata = new JSONArray(jsonObject.getString("stylelistdata"));
                    JSONArray likedata = new JSONArray(jsonObject.getString("likedata"));
                    JSONArray commentdata = new JSONArray(jsonObject.getString("commentdata"));
                    JSONArray profiledata = new JSONArray(jsonObject.getString("profiledata"));
                    setStylelistArray(stylelistdata);
                    setLikelistArray(likedata);
                    setCommentlistArray(commentdata);
                    setProfilelistArray(profiledata);
                    StyleListAdapter stylelist_styleListAdapter = new StyleListAdapter(getContext(), stylelist_arraylist, stylelist_likelist, stylelist_commentlist, stylelist_profilelist);
                    stylelist_recyclerview.setAdapter(stylelist_styleListAdapter);
                    stylelist_styleListAdapter.notifyDataSetChanged();
                    Log.v("StyleListActivity", "Connect Completed!");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.v("StyleListActivity", t.getLocalizedMessage());
            }
        });

        stylelist_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddStyleActivity.class);
                startActivity(intent);
            }
        });

        stylelist_swiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                call.clone().enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String responseResult = response.body().string();
                            JSONArray jsonArray = new JSONArray(responseResult);
                            Log.v("StyleListActivity", jsonArray.toString());
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            JSONArray stylelistdata = new JSONArray(jsonObject.getString("stylelistdata"));
                            JSONArray likedata = new JSONArray(jsonObject.getString("likedata"));
                            JSONArray commentdata = new JSONArray(jsonObject.getString("commentdata"));
                            JSONArray profiledata = new JSONArray(jsonObject.getString("profiledata"));
                            setStylelistArray(stylelistdata);
                            setLikelistArray(likedata);
                            setCommentlistArray(commentdata);
                            setProfilelistArray(profiledata);
                            StyleListAdapter stylelist_styleListAdapter = new StyleListAdapter(getContext(), stylelist_arraylist, stylelist_likelist, stylelist_commentlist, stylelist_profilelist);
                            stylelist_recyclerview.setAdapter(stylelist_styleListAdapter);
                            stylelist_styleListAdapter.notifyDataSetChanged();
                            Log.v("StyleListActivity", "Connect Completed!");
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.v("StyleListActivity", t.getLocalizedMessage());
                    }
                });
                stylelist_swiperefreshlayout.setRefreshing(false);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        stylelist_swiperefreshlayout.setColorSchemeColors(
                Color.BLACK, Color.WHITE
        );

        LinearLayoutManager stylelist_layoutmanager = new LinearLayoutManager(getContext());
        stylelist_recyclerview.setHasFixedSize(true);
        stylelist_recyclerview.setLayoutManager(stylelist_layoutmanager);

        OkHttpClient stylelist_client = new OkHttpClient.Builder().connectTimeout(10000, TimeUnit.MILLISECONDS).cookieJar(new JavaNetCookieJar(new CookieManager())).build();
        retrofit = new Retrofit.Builder().client(stylelist_client).baseUrl(serverService.API_URL).build();
        serverService = retrofit.create(ServerService.class);
        final Call<ResponseBody> call = serverService.getList();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String responseResult = response.body().string();
                    JSONArray jsonArray = new JSONArray(responseResult);
                    Log.v("StyleListActivity", jsonArray.toString());
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    JSONArray stylelistdata = new JSONArray(jsonObject.getString("stylelistdata"));
                    JSONArray likedata = new JSONArray(jsonObject.getString("likedata"));
                    JSONArray commentdata = new JSONArray(jsonObject.getString("commentdata"));
                    JSONArray profiledata = new JSONArray(jsonObject.getString("profiledata"));
                    setStylelistArray(stylelistdata);
                    setLikelistArray(likedata);
                    setCommentlistArray(commentdata);
                    setProfilelistArray(profiledata);
                    StyleListAdapter stylelist_styleListAdapter = new StyleListAdapter(getContext(), stylelist_arraylist, stylelist_likelist, stylelist_commentlist, stylelist_profilelist);
                    stylelist_recyclerview.setAdapter(stylelist_styleListAdapter);
                    stylelist_styleListAdapter.notifyDataSetChanged();
                    Log.v("StyleListActivity", "Connect Completed!");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.v("StyleListActivity", t.getLocalizedMessage());
            }
        });

        stylelist_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddStyleActivity.class);
                startActivity(intent);
            }
        });

        stylelist_swiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                call.clone().enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String responseResult = response.body().string();
                            JSONArray jsonArray = new JSONArray(responseResult);
                            Log.v("StyleListActivity", jsonArray.toString());
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            JSONArray stylelistdata = new JSONArray(jsonObject.getString("stylelistdata"));
                            JSONArray likedata = new JSONArray(jsonObject.getString("likedata"));
                            JSONArray commentdata = new JSONArray(jsonObject.getString("commentdata"));
                            JSONArray profiledata = new JSONArray(jsonObject.getString("profiledata"));
                            setStylelistArray(stylelistdata);
                            setLikelistArray(likedata);
                            setCommentlistArray(commentdata);
                            setProfilelistArray(profiledata);
                            StyleListAdapter stylelist_styleListAdapter = new StyleListAdapter(getContext(), stylelist_arraylist, stylelist_likelist, stylelist_commentlist, stylelist_profilelist);
                            stylelist_recyclerview.setAdapter(stylelist_styleListAdapter);
                            stylelist_styleListAdapter.notifyDataSetChanged();
                            Log.v("StyleListActivity", "Connect Completed!");
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.v("StyleListActivity", t.getLocalizedMessage());
                    }
                });
                stylelist_swiperefreshlayout.setRefreshing(false);
            }
        });
    }

    private void setStylelistArray(JSONArray jsonArray) throws JSONException {
        stylelist_arraylist = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            StyleItem styleItem = new StyleItem();
            styleItem.setId(jsonObject.getInt("id"));
            styleItem.setFacebookid(jsonObject.getString("facebookid"));
            styleItem.setImagename(jsonObject.getString("imagename"));
            styleItem.setText(jsonObject.getString("text"));
            styleItem.setTime(jsonObject.getString("time"));
            stylelist_arraylist.add(styleItem);
        }
    }

    private void setLikelistArray(JSONArray jsonArray) throws JSONException {
        stylelist_likelist = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            LikeItem likeitem = new LikeItem();
            likeitem.setId(jsonObject.getInt("id"));
            likeitem.setListid(jsonObject.getInt("listid"));
            likeitem.setFacebookid(jsonObject.getString("facebookid"));
            likeitem.setTime(jsonObject.getString("time"));
            stylelist_likelist.add(likeitem);
        }
    }

    private void setCommentlistArray(JSONArray jsonArray) throws JSONException {
        stylelist_commentlist = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            CommentItem commentItem = new CommentItem();
            commentItem.setId(jsonObject.getInt("id"));
            commentItem.setListid(jsonObject.getInt("listid"));
            commentItem.setFacebookid(jsonObject.getString("facebookid"));
            commentItem.setText(jsonObject.getString("text"));
            commentItem.setTime(jsonObject.getString("time"));
            stylelist_commentlist.add(commentItem);
        }
    }

    private void setProfilelistArray(JSONArray jsonArray) throws JSONException {
        stylelist_profilelist = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            ProfileItem profileItem = new ProfileItem();
            profileItem.setFacebookid(jsonObject.getString("facebookid"));
            profileItem.setName(jsonObject.getString("name"));
            profileItem.setLocation(jsonObject.getString("location"));
            profileItem.setStyle(jsonObject.getString("style"));
            profileItem.setText(jsonObject.getString("text"));
            stylelist_profilelist.add(profileItem);
        }
    }
}
