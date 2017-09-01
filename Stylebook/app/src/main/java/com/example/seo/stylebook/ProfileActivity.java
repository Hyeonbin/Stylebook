package com.example.seo.stylebook;


import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
public class ProfileActivity extends Fragment{
    Retrofit retrofit;
    public static Fragment currentfragment;
    ServerService serverService;
    ArrayList<ProfileItem> profile_profilelist;
    ArrayList<StyleItem> profile_arraylist;
    ArrayList<LikeItem> profile_likelist;
    ArrayList<CommentItem> profile_commentlist;

    FloatingActionButton profile_fab;
    ImageView profile_image;
    TextView profile_name;
    TextView profile_location;
    TextView profile_style;
    TextView profile_text;
    RecyclerView profile_recyclerview;

    String PROFILE_ADDRESS = ServerService.API_URL + "profile/";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.floating_profile, container, false);

        currentfragment = this;

        profile_fab = (FloatingActionButton)rootView.findViewById(R.id.Sb_Floating_Profilebtn);

        profile_image = (ImageView)rootView.findViewById(R.id.Sb_Profile_Image);
        profile_name = (TextView)rootView.findViewById(R.id.Sb_Profile_Name);
        profile_location = (TextView)rootView.findViewById(R.id.Sb_Profile_Location);
        profile_style = (TextView)rootView.findViewById(R.id.Sb_Profile_Style);
        profile_text = (TextView)rootView.findViewById(R.id.Sb_Profile_Text);

        profile_recyclerview = (RecyclerView) rootView.findViewById(R.id.Sb_Profile_Recyclerview);
        LinearLayoutManager profile_layoutmanager = new LinearLayoutManager(getContext());
        profile_recyclerview.setHasFixedSize(true);
        profile_recyclerview.setLayoutManager(profile_layoutmanager);

        profile_recyclerview.setFocusable(false);

        OkHttpClient like_client = new OkHttpClient.Builder().connectTimeout(10000, TimeUnit.MILLISECONDS).cookieJar(new JavaNetCookieJar(new CookieManager())).build();
        retrofit = new Retrofit.Builder().client(like_client).baseUrl(serverService.API_URL).build();
        serverService = retrofit.create(ServerService.class);
        Call<ResponseBody> call = serverService.getProfile(AccessToken.getCurrentAccessToken().getUserId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String responseResult = response.body().string();
                    JSONArray jsonArray = new JSONArray(responseResult);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    Log.v("ProfileActivity", jsonArray.toString());
                    JSONArray profiledata = new JSONArray(jsonObject.getString("profiledata"));
                    JSONArray stylelistdata = new JSONArray(jsonObject.getString("stylelistdata"));
                    JSONArray likedata = new JSONArray(jsonObject.getString("likedata"));
                    JSONArray commentdata = new JSONArray(jsonObject.getString("commentdata"));
                    JSONObject profileObject = profiledata.getJSONObject(0);
                    if(!profileObject.getString("profileimage").equals("no"))
                        Glide.with(getContext()).load(PROFILE_ADDRESS + AccessToken.getCurrentAccessToken().getUserId()).into(profile_image); //Todo : 프로필 사진 처리
                    profile_name.setText(profileObject.getString("name"));
                    profile_location.setText(profileObject.getString("location"));
                    profile_style.setText(profileObject.getString("style"));
                    profile_text.setText(profileObject.getString("text"));
                    profile_arraylist = SetArrayListUtils.setStylelistArray(stylelistdata);
                    profile_likelist = SetArrayListUtils.setLikelistArray(likedata);
                    profile_commentlist = SetArrayListUtils.setCommentlistArray(commentdata);
                    profile_profilelist = SetArrayListUtils.setProfilelistArray(profiledata);
                    profile_recyclerview.setAdapter(new StyleListAdapter(getContext(), profile_arraylist, profile_likelist, profile_commentlist, profile_profilelist, currentfragment));
                    Log.v("ProfileActivity", "Connect Completed!");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //Log.v("ProfileActivity", t.getLocalizedMessage());
            }
        });

        profile_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ModifyProfileActivity.class);
                intent.putExtra("name", profile_profilelist.get(0).getName());
                intent.putExtra("profileimage", profile_profilelist.get(0).getProfileimage());
                intent.putExtra("location", profile_profilelist.get(0).getLocation());
                intent.putExtra("style", profile_profilelist.get(0).getStyle());
                intent.putExtra("text", profile_profilelist.get(0).getText());
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        LinearLayoutManager profile_layoutmanager = new LinearLayoutManager(getContext());
        profile_recyclerview.setHasFixedSize(true);
        profile_recyclerview.setLayoutManager(profile_layoutmanager);

        profile_recyclerview.setFocusable(false);

        OkHttpClient like_client = new OkHttpClient.Builder().connectTimeout(10000, TimeUnit.MILLISECONDS).cookieJar(new JavaNetCookieJar(new CookieManager())).build();
        retrofit = new Retrofit.Builder().client(like_client).baseUrl(serverService.API_URL).build();
        serverService = retrofit.create(ServerService.class);
        Call<ResponseBody> call = serverService.getProfile(AccessToken.getCurrentAccessToken().getUserId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String responseResult = response.body().string();
                    JSONArray jsonArray = new JSONArray(responseResult);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    Log.v("ProfileActivity", jsonArray.toString());
                    JSONArray profiledata = new JSONArray(jsonObject.getString("profiledata"));
                    JSONArray stylelistdata = new JSONArray(jsonObject.getString("stylelistdata"));
                    JSONArray likedata = new JSONArray(jsonObject.getString("likedata"));
                    JSONArray commentdata = new JSONArray(jsonObject.getString("commentdata"));
                    JSONObject profileObject = profiledata.getJSONObject(0);
                    if(!profileObject.getString("profileimage").equals("no"))
                        Glide.with(getContext()).load(PROFILE_ADDRESS + AccessToken.getCurrentAccessToken().getUserId()).into(profile_image); //Todo : 프로필 사진 처리
                    profile_name.setText(profileObject.getString("name"));
                    profile_location.setText(profileObject.getString("location"));
                    profile_style.setText(profileObject.getString("style"));
                    profile_text.setText(profileObject.getString("text"));
                    profile_arraylist = SetArrayListUtils.setStylelistArray(stylelistdata);
                    profile_likelist = SetArrayListUtils.setLikelistArray(likedata);
                    profile_commentlist = SetArrayListUtils.setCommentlistArray(commentdata);
                    profile_profilelist = SetArrayListUtils.setProfilelistArray(profiledata);
                    profile_recyclerview.setAdapter(new StyleListAdapter(getContext(), profile_arraylist, profile_likelist, profile_commentlist, profile_profilelist, currentfragment));
                    Log.v("ProfileActivity", "Connect Completed!");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //Log.v("ProfileActivity", t.getLocalizedMessage());
            }
        });

        profile_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ModifyProfileActivity.class);
                intent.putExtra("name", profile_profilelist.get(0).getName());
                intent.putExtra("profileimage", profile_profilelist.get(0).getProfileimage());
                intent.putExtra("location", profile_profilelist.get(0).getLocation());
                intent.putExtra("style", profile_profilelist.get(0).getStyle());
                intent.putExtra("text", profile_profilelist.get(0).getText());
                startActivity(intent);
            }
        });
    }

}
