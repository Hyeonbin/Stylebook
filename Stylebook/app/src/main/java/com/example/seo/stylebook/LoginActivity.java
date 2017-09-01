package com.example.seo.stylebook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.CookieManager;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Seo on 2017-07-11.
 */
public class LoginActivity extends Activity{
    LoginButton login_btn;
    CallbackManager callbackManager;

    Retrofit retrofit;
    ServerService serverService;

    ProgressDialog progressDialog;
    boolean loginsig;
    JSONArray profiledata = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("프로필 데이터 생성 중");

        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.seo.stylebook", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        OkHttpClient stylelist_client = new OkHttpClient.Builder().connectTimeout(10000, TimeUnit.MILLISECONDS).cookieJar(new JavaNetCookieJar(new CookieManager())).build();
        retrofit = new Retrofit.Builder().client(stylelist_client).baseUrl(serverService.API_URL).build();
        serverService = retrofit.create(ServerService.class);

        callbackManager = CallbackManager.Factory.create();
        login_btn = (LoginButton)findViewById(R.id.Sb_Login_Btn);
        login_btn.setReadPermissions(Arrays.asList("public_profile", "email"));
        login_btn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                /*Call<ResponseBody> call1 = serverService.getProfile(AccessToken.getCurrentAccessToken().getUserId());
                call1.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String responseResult = response.body().string();
                            JSONArray jsonArray = new JSONArray(responseResult);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            profiledata = new JSONArray(jsonObject.getString("profiledata"));
                            loginsig = checkProfile();
                            Log.v("LoginActivity", profiledata.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
                if(!loginsig) {*/
                GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Call<ResponseBody> call = null;
                        try {
                            call = serverService.addProfile(
                                    AccessToken.getCurrentAccessToken().getUserId(),
                                    object.getString("name"),
                                    "no",
                                    null,
                                    null,
                                    null
                            );
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday,first_name,last_name");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();

                progressDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 5000);
            }


            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }


        });
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private boolean checkProfile() throws JSONException
    {
        for(int i = 0; i < profiledata.length(); i++){
            if(profiledata.getJSONObject(i).getString("facebookid").equals(AccessToken.getCurrentAccessToken().getUserId()))
                return true;
        }

        return false;
    }

}
