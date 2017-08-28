package com.example.seo.stylebook;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by Seo on 2017-07-21.
 */
public interface ServerService {
    public static final String API_URL = "http://10.0.2.2:3000/"; // Todo : 서버 아이피 주소 셋팅

    @GET("stylelist")
    Call<ResponseBody>getList();

    @FormUrlEncoded
    @POST("stylelist/addstyle")
    Call<ResponseBody>addStyle(
            @Field("facebookid") String facebookid,
            @Field("imagename") String imagename, // image 이름만 넣는다
            @Field("text") String text,
            @Field("time") String time
    );

    @Multipart
    @POST("stylelist/addimage")
    Call<ResponseBody> upImage(
            @Part MultipartBody.Part file,
            @Part("stylelistimage") RequestBody name
    );

    @FormUrlEncoded
    @POST("stylelist/likebtnclicked")
    Call<ResponseBody>likebtnClicked(
            @Field("listid") int listid,
            @Field("facebookid") String facebookid,
            @Field("time") String time
    );

    @FormUrlEncoded
    @POST("stylelist/likebtnunclicked")
    Call<ResponseBody>likebtnUnclicked(
            @Field("listid") int listid,
            @Field("facebookid") String facebookid
    );

    @GET("stylelist/likenum")
    Call<ResponseBody>getLikenum(
            @Query("listid") int listid
    );

    @FormUrlEncoded
    @POST("stylelist/comment")
    Call<ResponseBody>getComment(
            @Field("listid") int listid
    );

    @FormUrlEncoded
    @POST("stylelist/addcomment")
    Call<ResponseBody>addComment(
            @Field("listid") int listid,
            @Field("facebookid") String facebookid,
            @Field("text") String text,
            @Field("time") String time
    );

    // Todo : delete comment, modify comment도 구현

    @GET("stylelist/modifystyle")
    Call<ResponseBody>getStyle(
            @Query("listid") int listid
    );

    @FormUrlEncoded
    @POST("stylelist/modifystyle")
    Call<ResponseBody>modifyStyle(
            @Field("listid") int listid,
            @Field("imagename") String imagename, // image 이름만 넣는다
            @Field("text") String text,
            @Field("delimgsig") int delimgsig
    );

    @FormUrlEncoded
    @POST("stylelist/deletestyle")
    Call<ResponseBody>deleteStyle(
            @Field("listid") int listid
    );

    @FormUrlEncoded
    @POST("profile")
    Call<ResponseBody>getProfile(
            @Field("facebookid") String facebookid
    );

    @FormUrlEncoded
    @POST("profile/addprofile")
    Call<ResponseBody>addProfile(
            @Field("facebookid") String facebookid,
            @Field("name") String name,
            @Field("profileimage") String profileimage,
            @Field("location") String location,
            @Field("style") String style,
            @Field("text") String text
    );

    @FormUrlEncoded
    @POST("profile/modifyprofile")
    Call<ResponseBody>modifyMyprofile(
            @Field("facebookid") String facebookid,
            @Field("name") String name,
            @Field("profileimage") String profileimage,
            @Field("location") String location,
            @Field("style") String style,
            @Field("text") String text,
            @Field("delimgsig") int delimgsig
    );

    @Multipart
    @POST("profile/addimage")
    Call<ResponseBody> upprofileImage(
            @Part MultipartBody.Part file,
            @Part("profileimage") RequestBody name
    );

    @FormUrlEncoded
    @POST("search")
    Call<ResponseBody>getSearchlist(
            @Field("keyword") String keyword
    );

    @FormUrlEncoded
    @POST("like")
    Call<ResponseBody>getLikelist(
            @Field("facebookid") String facebookid
    );

    @FormUrlEncoded
    @POST("like/stylelist")
    Call<ResponseBody>getLikeStylelist(
            @Field("listid") int listid
    );
}
