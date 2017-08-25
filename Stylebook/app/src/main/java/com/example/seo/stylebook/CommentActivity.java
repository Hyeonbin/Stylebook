package com.example.seo.stylebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.Profile;

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
 * Created by Seo on 2017-07-22.
 */
public class CommentActivity extends Activity {
    Retrofit retrofit;
    ServerService serverService;
    ArrayList<CommentItem> comment_arraylist;
    ArrayList<ProfileItem> comment_profilelist;

    int comment_listid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        comment_listid = getIntent().getIntExtra("listid", -1);
        final RecyclerView comment_listview = (RecyclerView) findViewById(R.id.Sb_Comment_Recyclerview);
        LinearLayoutManager stylelist_layoutmanager = new LinearLayoutManager(getApplicationContext());
        comment_listview.setHasFixedSize(true);
        comment_listview.setLayoutManager(stylelist_layoutmanager);

        final EditText comment_edittext = (EditText)findViewById(R.id.Sb_Comment_Edittext);
        ImageView comment_send = (ImageView)findViewById(R.id.Sb_Comment_Send);

        OkHttpClient comment_client = new OkHttpClient.Builder().connectTimeout(10000, TimeUnit.MILLISECONDS).cookieJar(new JavaNetCookieJar(new CookieManager())).build();
        retrofit = new Retrofit.Builder().client(comment_client).baseUrl(serverService.API_URL).build();
        serverService = retrofit.create(ServerService.class);
        final Call<ResponseBody> call = serverService.getComment(comment_listid); // Todo: listid값 넣는다
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String responseResult = response.body().string();
                    JSONArray jsonArray = new JSONArray(responseResult);
                    Log.v("CommentActivity", jsonArray.toString());
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    JSONArray commentdata = new JSONArray(jsonObject.getString("commentdata"));
                    JSONArray profiledata = new JSONArray(jsonObject.getString("profiledata"));
                    setCommentArray(commentdata);
                    setProfilelistArray(profiledata);
                    CommentDataAdapter commentDataAdapter = new CommentDataAdapter(getApplicationContext(), comment_arraylist, comment_profilelist);
                    comment_listview.setAdapter(commentDataAdapter);
                    commentDataAdapter.notifyDataSetChanged();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //Log.v("CommentActivity", t.getLocalizedMessage());
            }
        });

        comment_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date currentDate = new Date();
                Call<ResponseBody> call1 = serverService.addComment(
                        comment_listid,
                        AccessToken.getCurrentAccessToken().getUserId(),
                        comment_edittext.getText().toString(),
                        String.valueOf(System.currentTimeMillis())
                );
                call1.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.v("CommentActivity", "Comment send completed!");
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        //Log.v("CommentActivity", t.getLocalizedMessage());
                    }
                });
                // 버튼을 누른 후 키보드는 내려간다
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                call.clone().enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String responseResult = response.body().string();
                            JSONArray jsonArray = new JSONArray(responseResult);
                            Log.v("CommentActivity", jsonArray.toString());
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            JSONArray commentdata = new JSONArray(jsonObject.getString("commentdata"));
                            JSONArray profiledata = new JSONArray(jsonObject.getString("profiledata"));
                            setCommentArray(commentdata);
                            setProfilelistArray(profiledata);
                            CommentDataAdapter commentDataAdapter = new CommentDataAdapter(getApplicationContext(), comment_arraylist, comment_profilelist);
                            comment_listview.setAdapter(commentDataAdapter);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.v("CommentActivity", t.getLocalizedMessage());
                    }
                });
            }
        });
    }

    private void setCommentArray(JSONArray jsonArray) throws JSONException {
        comment_arraylist = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject jsonObject = (JSONObject)jsonArray.getJSONObject(i);
            CommentItem commentItem = new CommentItem();
            commentItem.setId(jsonObject.getInt("id"));
            commentItem.setListid(jsonObject.getInt("listid"));
            commentItem.setFacebookid(jsonObject.getString("facebookid"));
            commentItem.setText(jsonObject.getString("text"));
            commentItem.setTime(jsonObject.getString("time"));
            comment_arraylist.add(commentItem);
        }
    }

    private void setProfilelistArray(JSONArray jsonArray) throws JSONException {
        comment_profilelist = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            ProfileItem profileItem = new ProfileItem();
            profileItem.setFacebookid(jsonObject.getString("facebookid"));
            profileItem.setName(jsonObject.getString("name"));
            profileItem.setLocation(jsonObject.getString("location"));
            profileItem.setStyle(jsonObject.getString("style"));
            profileItem.setText(jsonObject.getString("text"));
            comment_profilelist.add(profileItem);
        }
    }
}