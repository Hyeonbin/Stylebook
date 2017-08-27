package com.example.seo.stylebook;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CookieManager;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.JavaNetCookieJar;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Seo on 2017-07-21.
 */
public class AddStyleActivity extends Activity {
    ImageView addstyle_cancelbtn;
    ImageView addstyle_sendbtn;
    ImageView addstyle_image;
    EditText addstyle_text;

    Retrofit retrofit;
    ServerService serverService;

    private static final int REQUEST_TAKE_PHOTO = 0; // 사진촬영 시그널 변수
    private static final int REQUEST_PHOTO_ALBUM = 1; // 사진앨범 시그널 변수
    private static final int REQUEST_CROP = 2; // 사진을 크롭하기 위한 시그널 변수

    private Uri ImageCaptureUri;
    private String absolutePath = null;
    private String strPhotoName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addstyle);

        addstyle_cancelbtn = (ImageView)findViewById(R.id.Sb_Addstyle_Cancelbtn);
        addstyle_sendbtn = (ImageView)findViewById(R.id.Sb_Addstyle_Sendbtn);
        addstyle_image = (ImageView)findViewById(R.id.Sb_Addstyle_Image);
        addstyle_text = (EditText)findViewById(R.id.Sb_Addstyle_Text);

        addstyle_cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        addstyle_sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Todo : 게시 버튼, 예외처리 필요
                if(absolutePath == null && addstyle_text.getText().toString().getBytes().length <= 0){
                    Toast toast = Toast.makeText(getApplicationContext(), "사진과 내용이 입력되지 않았습니다", Toast.LENGTH_LONG);
                    toast.show();
                } else if(absolutePath != null && addstyle_text.getText().toString().getBytes().length <= 0){
                    Toast toast = Toast.makeText(getApplicationContext(), "내용이 입력되지 않았습니다", Toast.LENGTH_LONG);
                    toast.show();
                } else if(absolutePath == null && addstyle_text.getText().toString().getBytes().length >= 0){
                    Toast toast = Toast.makeText(getApplicationContext(), "사진이 입력되지 않았습니다", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    OkHttpClient stylelist_client = new OkHttpClient.Builder().connectTimeout(10000, TimeUnit.MILLISECONDS).cookieJar(new JavaNetCookieJar(new CookieManager())).build();
                    retrofit = new Retrofit.Builder().client(stylelist_client).baseUrl(serverService.API_URL).build();
                    serverService = retrofit.create(ServerService.class);

                    File file = new File(absolutePath);
                    RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
                    final MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
                    final RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

                    Call<ResponseBody> call = serverService.addStyle(
                            AccessToken.getCurrentAccessToken().getUserId(),
                            file.getName(),
                            addstyle_text.getText().toString(),
                            String.valueOf(System.currentTimeMillis())
                    );
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.v("AddStyleActivity", "Send Data Completed!");
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.v("AddStyleActivity", t.getLocalizedMessage());
                        }
                    });
                    Call<ResponseBody> call1 = serverService.upImage(fileToUpload, filename);
                    call1.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.v("AddStyleActivity", "Send Image Completed!");
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.v("AddStyleActivity", t.getLocalizedMessage());
                        }
                    });
                    ((StyleListActivity)StyleListActivity.currentfragment).onResume();
                    ((LikeActivity)LikeActivity.currentfragment).onResume();
                    ((ProfileActivity)ProfileActivity.currentfragment).onResume();
                    ((SearchActivity)SearchActivity.currentfragment).onResume();
                    finish();
                }
           }
        });

        addstyle_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Todo : 사진 버튼
                checkPermission();
                DialogInterface.OnClickListener CameraListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        takeCamera();
                    }
                };
                DialogInterface.OnClickListener AlbumListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        takeAlbum();
                    }
                };
                DialogInterface.OnClickListener CancelListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };

                new AlertDialog.Builder(AddStyleActivity.this, R.style.MyAlertDialogTheme)
                        .setTitle("업로드할 이미지 선택           ")
                        .setPositiveButton("사진촬영", CameraListener)
                        .setNeutralButton("취소", CancelListener)
                        .setNegativeButton("앨범선택", AlbumListener)
                        .show();
            }
        });

    }

    public void takeCamera() { // 카메라 촬영에 대한 메소드
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // 카메라 촬영 화면으로 이동

        String url = AccessToken.getCurrentAccessToken().getUserId() + "_stylelistimg_" + String.valueOf(System.currentTimeMillis()) + ".jpg"; // 현재 시간을 이름으로 하여 url 지정
        strPhotoName = url;
        ImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Stylebook", url)); // uri 지정

        intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageCaptureUri); // 사진 찍은 후 결과 데이터 처리
        startActivityForResult(intent, REQUEST_TAKE_PHOTO); // 시그널과 함께 result 메소드로 이동
    }

    public void takeAlbum() { // 앨범 선택에 대한 메소드
        Intent intent = new Intent(Intent.ACTION_PICK); // 앨범 선택 화면으로 이동
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE); // 이미지 타입으로 지정
        startActivityForResult(intent, REQUEST_PHOTO_ALBUM); // 시그널과 함께 result 메소드로 이동
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) { // result를 받기 위한 메소드
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK) // 제대로 된 result를 받지 못한 경우, 메소드 종료
            return;

        switch (requestCode)
        {
            case REQUEST_PHOTO_ALBUM : // 앨범에 대한 시그널
                ImageCaptureUri = data.getData(); // uri 받음

                File original_file = getImageFile(ImageCaptureUri); // 원래 파일을 미리 저장

                ImageCaptureUri = createSaveCropFile(); // 크롭한 이미지 파일에 대한 uri를 새로 저장
                File cpoy_file = new File(ImageCaptureUri.getPath()); // 새로운 uri에 대해서 파일 할당

                copyFile(original_file, cpoy_file); // copyFile 메소드로 원래 이미지를 copy

                // 사진을 crop하기 위해 request_crop으로 이동
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(ImageCaptureUri, "image/*");
                intent.putExtra("output", ImageCaptureUri);
                startActivityForResult(intent, REQUEST_CROP);

                break;

            case REQUEST_CROP :
                String full_path = ImageCaptureUri.getPath(); // 받은 uri path 저장

                // bitmap으로 변환 후 해당 imageview에 출력
                Bitmap bmp = BitmapFactory.decodeFile(full_path);
                addstyle_image.setImageBitmap(bmp);

                absolutePath = full_path; // 데이터베이스에 저장하기 위해 uri path는 따로 한번 더 저장

                break;

            case REQUEST_TAKE_PHOTO :
                // 사진을 찍은 후 crop하기 위해 request_crop으로 이동
                Intent intent1 = new Intent("com.android.camera.action.CROP");
                intent1.setDataAndType(ImageCaptureUri, "image/*");
                intent1.putExtra( "output", ImageCaptureUri);
                startActivityForResult(intent1, REQUEST_CROP);
                break;
        }
    }

    private Uri createSaveCropFile(){ // crop한 파일의 uri를 만들기 위한 메소드
        Uri uri;
        String url = AccessToken.getCurrentAccessToken().getUserId() + "_stylelistimg_" +  String.valueOf(System.currentTimeMillis()) + ".jpg";
        strPhotoName = url;
        uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Stylebook", url));
        return uri;
    }

    public static boolean copyFile(File srcFile, File destFile) { // 사진을 앨범에서 선택한 경우 원본 이미지에서 카피하기 위한 메소드
        boolean result = false;
        try {
            InputStream in = new FileInputStream(srcFile);
            try {
                result = copyToFile(in, destFile);
            } finally  {
                in.close();
            }
            result = true;
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    private File getImageFile(Uri uri) { // uri를 통해 이미지 파일로 만들기 위한 메소드
        String[] projection = { MediaStore.Images.Media.DATA };
        if (uri == null) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        Cursor mCursor = getContentResolver().query(uri, projection, null, null,
                MediaStore.Images.Media.DATE_MODIFIED + " desc");
        if(mCursor == null || mCursor.getCount() < 1) { // 데이터가 없는 경우
            return null;
        }
        int column_index = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        mCursor.moveToFirst();

        String path = mCursor.getString(column_index);

        if (mCursor !=null ) {
            mCursor.close();
            mCursor = null;
        }

        return new File(path);
    }

    private static boolean copyToFile(InputStream inputStream, File destFile) { // 새로운 파일로 스트림에 있는 데이터를 저장하기 위한 메소드
        try {
            OutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[4096]; // 바이트 단위로 저장
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) { // 읽을 데이터가 있을 때까지 반복
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                out.close();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    private void checkPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionListener permissionListener = new PermissionListener() {
                @Override
                public void onPermissionGranted() {

                }

                @Override
                public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                    Toast.makeText(getApplicationContext(), "권한을 거부하셨습니다", Toast.LENGTH_SHORT).show();
                }
            };
            new TedPermission(getApplication())
                    .setPermissionListener(permissionListener)
                    .setRationaleMessage("카메라와 앨범에 대한 접근 권한이 필요합니다")
                    .setDeniedMessage("접근 권한이 없습니다")
                    .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_NETWORK_STATE)
                    .check();
        }
    }
}
