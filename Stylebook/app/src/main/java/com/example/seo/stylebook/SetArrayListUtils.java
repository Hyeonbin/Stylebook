package com.example.seo.stylebook;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by binny on 2017. 8. 31..
 */

public class SetArrayListUtils {

    public static ArrayList<StyleItem> setStylelistArray(JSONArray jsonArray) throws JSONException {
        ArrayList<StyleItem> arrayList = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            StyleItem styleItem = new StyleItem();
            styleItem.setId(jsonObject.getInt("id"));
            styleItem.setFacebookid(jsonObject.getString("facebookid"));
            styleItem.setImagename(jsonObject.getString("imagename"));
            styleItem.setText(jsonObject.getString("text"));
            styleItem.setTime(jsonObject.getString("time"));
            arrayList.add(styleItem);
        }
        return arrayList;
    }

    public static ArrayList<LikeItem> setLikelistArray(JSONArray jsonArray) throws JSONException {
        ArrayList<LikeItem> arrayList = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            LikeItem likeitem = new LikeItem();
            likeitem.setId(jsonObject.getInt("id"));
            likeitem.setListid(jsonObject.getInt("listid"));
            likeitem.setFacebookid(jsonObject.getString("facebookid"));
            likeitem.setTime(jsonObject.getString("time"));
            arrayList.add(likeitem);
        }
        return arrayList;
    }

    public static ArrayList<StyleItem> setStylelistArray(JSONArray stylelistdata, JSONArray likedata) throws JSONException{
        ArrayList<StyleItem> arrayList = new ArrayList<>();
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
                    arrayList.add(styleItem);
                }
            }
        }
        return arrayList;
    }

    public static ArrayList<CommentItem> setCommentlistArray(JSONArray jsonArray) throws JSONException {
        ArrayList<CommentItem> arrayList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            CommentItem commentItem = new CommentItem();
            commentItem.setId(jsonObject.getInt("id"));
            commentItem.setListid(jsonObject.getInt("listid"));
            commentItem.setFacebookid(jsonObject.getString("facebookid"));
            commentItem.setText(jsonObject.getString("text"));
            commentItem.setTime(jsonObject.getString("time"));
            arrayList.add(commentItem);
        }
        return arrayList;
    }

    public static ArrayList<ProfileItem> setProfilelistArrayForProfileActivity(JSONArray jsonArray) throws JSONException {
        ArrayList<ProfileItem> arrayList = new ArrayList<>();
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        ProfileItem profileItem = new ProfileItem();
        profileItem.setFacebookid(jsonObject.getString("facebookid"));
        profileItem.setName(jsonObject.getString("name"));
        profileItem.setProfileimage(jsonObject.getString("profileimage"));
        profileItem.setLocation(jsonObject.getString("location"));
        profileItem.setStyle(jsonObject.getString("style"));
        profileItem.setText(jsonObject.getString("text"));
        arrayList.add(profileItem);
        return arrayList;
    }

    public static ArrayList<ProfileItem> setProfilelistArray(JSONArray jsonArray) throws JSONException {
        ArrayList<ProfileItem> arrayList = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            ProfileItem profileItem = new ProfileItem();
            profileItem.setFacebookid(jsonObject.getString("facebookid"));
            profileItem.setName(jsonObject.getString("name"));
            profileItem.setLocation(jsonObject.getString("location"));
            profileItem.setStyle(jsonObject.getString("style"));
            profileItem.setText(jsonObject.getString("text"));
            arrayList.add(profileItem);
        }
        return arrayList;
    }
}
