package com.example.seo.stylebook;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.LoginFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
public class SearchActivity extends Fragment {
    Retrofit retrofit;
    public static Fragment currentfragment;
    ServerService serverService;
    ArrayList<StyleItem> search_arraylist;
    ArrayList<LikeItem> search_likelist;
    ArrayList<CommentItem> search_commentlist;
    ArrayList<ProfileItem> search_profilelist;

    RecyclerView search_recyclerview;
    EditText search_text;
    ImageView search_btn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.activity_search, container, false);

        currentfragment = this;

        search_recyclerview = (RecyclerView) rootView.findViewById(R.id.Sb_Search_Recyclerview);
        LinearLayoutManager search_layoutmanager = new LinearLayoutManager(getContext());
        search_recyclerview.setHasFixedSize(true);
        search_recyclerview.setLayoutManager(search_layoutmanager);

        search_text = (EditText)rootView.findViewById(R.id.Sb_Search_Edittext);
        search_btn = (ImageView)rootView.findViewById(R.id.Sb_Search_Enter);
        OkHttpClient search_client = new OkHttpClient.Builder().connectTimeout(10000, TimeUnit.MILLISECONDS).cookieJar(new JavaNetCookieJar(new CookieManager())).build();
        retrofit = new Retrofit.Builder().client(search_client).baseUrl(serverService.API_URL).build();
        serverService = retrofit.create(ServerService.class);

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Todo: 클릭 시 serverservice의 getSearchlist() 호출
                if(search_text.getText() != null) {
                    Call<ResponseBody> call = serverService.getSearchlist(search_text.getText().toString());
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                String responseResult = response.body().string();
                                JSONArray jsonArray = new JSONArray(responseResult);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                Log.v("SearchActivity", jsonArray.toString());
                                JSONArray stylelistdata = new JSONArray(jsonObject.getString("stylelistdata"));
                                JSONArray likedata = new JSONArray(jsonObject.getString("likedata"));
                                JSONArray commentdata = new JSONArray(jsonObject.getString("commentdata"));
                                JSONArray profiledata = new JSONArray(jsonObject.getString("profiledata"));
                                setSearchArray(stylelistdata);
                                setLikelistArray(likedata);
                                setCommentlistArray(commentdata);
                                setProfilelistArray(profiledata);
                                search_recyclerview.setAdapter(new StyleListAdapter(getContext(), search_arraylist, search_likelist, search_commentlist, search_profilelist));
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.v("SearchActivity", t.getLocalizedMessage());
                        }
                    });
                } else {
                    // Todo: 검색어가 없음을 토스트 메시지로 알린다
                    Toast.makeText(getContext(), "검색어를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        LinearLayoutManager search_layoutmanager = new LinearLayoutManager(getContext());
        search_recyclerview.setHasFixedSize(true);
        search_recyclerview.setLayoutManager(search_layoutmanager);

        OkHttpClient search_client = new OkHttpClient.Builder().connectTimeout(10000, TimeUnit.MILLISECONDS).cookieJar(new JavaNetCookieJar(new CookieManager())).build();
        retrofit = new Retrofit.Builder().client(search_client).baseUrl(serverService.API_URL).build();
        serverService = retrofit.create(ServerService.class);

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Todo: 클릭 시 serverservice의 getSearchlist() 호출
                if(search_text.getText() != null) {
                    Call<ResponseBody> call = serverService.getSearchlist(search_text.getText().toString());
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                String responseResult = response.body().string();
                                JSONArray jsonArray = new JSONArray(responseResult);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                Log.v("SearchActivity", jsonArray.toString());
                                JSONArray stylelistdata = new JSONArray(jsonObject.getString("stylelistdata"));
                                JSONArray likedata = new JSONArray(jsonObject.getString("likedata"));
                                JSONArray commentdata = new JSONArray(jsonObject.getString("commentdata"));
                                JSONArray profiledata = new JSONArray(jsonObject.getString("profiledata"));
                                setSearchArray(stylelistdata);
                                setLikelistArray(likedata);
                                setCommentlistArray(commentdata);
                                setProfilelistArray(profiledata);
                                search_recyclerview.setAdapter(new StyleListAdapter(getContext(), search_arraylist, search_likelist, search_commentlist, search_profilelist));
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.v("SearchActivity", t.getLocalizedMessage());
                        }
                    });
                } else {
                    // Todo: 검색어가 없음을 토스트 메시지로 알린다
                }
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }

    private void setSearchArray(JSONArray jsonArray) throws JSONException {
        search_arraylist = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject jsonObject = (JSONObject)jsonArray.get(i);
            StyleItem styleItem = new StyleItem();
            styleItem.setId(jsonObject.getInt("id"));
            styleItem.setFacebookid(jsonObject.getString("facebookid"));
            styleItem.setImagename(jsonObject.getString("imagename"));
            styleItem.setText(jsonObject.getString("text"));
            styleItem.setTime(jsonObject.getString("time"));
            search_arraylist.add(styleItem);
        }
    }

    private void setLikelistArray(JSONArray jsonArray) throws JSONException {
        search_likelist = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            LikeItem likeitem = new LikeItem();
            likeitem.setId(jsonObject.getInt("id"));
            likeitem.setListid(jsonObject.getInt("listid"));
            likeitem.setFacebookid(jsonObject.getString("facebookid"));
            likeitem.setTime(jsonObject.getString("time"));
            search_likelist.add(likeitem);
        }
    }

    private void setCommentlistArray(JSONArray jsonArray) throws JSONException {
        search_commentlist = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            CommentItem commentItem = new CommentItem();
            commentItem.setId(jsonObject.getInt("id"));
            commentItem.setListid(jsonObject.getInt("listid"));
            commentItem.setFacebookid(jsonObject.getString("facebookid"));
            commentItem.setText(jsonObject.getString("text"));
            commentItem.setTime(jsonObject.getString("time"));
            search_commentlist.add(commentItem);
        }
    }

    private void setProfilelistArray(JSONArray jsonArray) throws JSONException {
        search_profilelist = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            ProfileItem profileItem = new ProfileItem();
            profileItem.setFacebookid(jsonObject.getString("facebookid"));
            profileItem.setName(jsonObject.getString("name"));
            profileItem.setLocation(jsonObject.getString("location"));
            profileItem.setStyle(jsonObject.getString("style"));
            profileItem.setText(jsonObject.getString("text"));
            search_profilelist.add(profileItem);
        }
    }
}
