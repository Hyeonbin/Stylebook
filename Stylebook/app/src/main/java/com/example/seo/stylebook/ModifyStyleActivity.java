package com.example.seo.stylebook;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CookieManager;
import java.util.ArrayList;
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
 * Created by binny on 2017. 8. 7..
 */

public class ModifyStyleActivity extends Activity {
    Retrofit retrofit;
    ServerService serverService;

    private static final int REQUEST_TAKE_PHOTO = 0; // 사진촬영 시그널 변수
    private static final int REQUEST_PHOTO_ALBUM = 1; // 사진앨범 시그널 변수
    private static final int REQUEST_CROP = 2; // 사진을 크롭하기 위한 시그널 변수

    String STYLE_ADDRESS = ServerService.API_URL + "stylelist/";

    private Uri ImageCaptureUri;
    private String absolutePath = null;
    private String strPhotoName;

    File file;
    MultipartBody.Part fileToUpload;
    RequestBody filename;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifystyle);

        ImageView modifystyle_backbtn = (ImageView)findViewById(R.id.Sb_Modifystyle_Backbtn);
        ImageView modifystyle_sendbtn = (ImageView)findViewById(R.id.Sb_Modifystyle_Sendbtn);
        ImageView modifystyle_image = (ImageView)findViewById(R.id.Sb_Modifystyle_Image);
        final EditText modifystyle_text = (EditText)findViewById(R.id.Sb_Modifystyle_Text);

        progressDialog = new ProgressDialog(ModifyStyleActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("데이터 수정 중");

        Glide.with(getApplicationContext()).load(STYLE_ADDRESS + getIntent().getStringExtra("image")).into(modifystyle_image);
        modifystyle_text.setText(getIntent().getStringExtra("text"));

        modifystyle_backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        modifystyle_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Todo : 사진 동작 처리
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

                new AlertDialog.Builder(ModifyStyleActivity.this, R.style.MyAlertDialogTheme)
                        .setTitle("업로드할 이미지 선택                ")
                        .setPositiveButton("사진촬영", CameraListener)
                        .setNeutralButton("취소", CancelListener)
                        .setNegativeButton("앨범선택", AlbumListener)
                        .show();
            }
        });

        modifystyle_sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttpClient modifystyle_client = new OkHttpClient.Builder().connectTimeout(10000, TimeUnit.MILLISECONDS).cookieJar(new JavaNetCookieJar(new CookieManager())).build();
                retrofit = new Retrofit.Builder().client(modifystyle_client).baseUrl(serverService.API_URL).build();
                serverService = retrofit.create(ServerService.class);

                int delimgsig = 0;
                String imagename;

                if(absolutePath != null) {
                    File file = new File(absolutePath);
                    RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
                    fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
                    filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
                    delimgsig = 1;
                    imagename = file.getName();
                } else {
                    delimgsig = 0;
                    imagename = getIntent().getStringExtra("image");
                }

                Call<ResponseBody> call = serverService.modifyStyle(
                    getIntent().getIntExtra("listid", -1),
                    imagename,
                    modifystyle_text.getText().toString(),
                    delimgsig
                );

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.v("ModifyStyleActivity", "call is completed!");
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        //Log.v("ModifyStyleActivity", t.getLocalizedMessage());
                    }
                });

                if(delimgsig == 1){
                    Call<ResponseBody> call1 = serverService.upImage(fileToUpload, filename);
                    call1.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.v("ModifyStyleActivity", "call1 is completed!");
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            //Log.v("ModifyStyleActivity", t.getLocalizedMessage());
                        }
                    });
                }
                progressDialog.show();
                resumeForFragment(getIntent().getIntExtra("fragment", -1));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 5000);
            }
        });

    }

    public void takeCamera() { // 카메라 촬영에 대한 메소드
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // 카메라 촬영 화면으로 이동

        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg"; // 현재 시간을 이름으로 하여 url 지정
        ImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Buddy", url)); // uri 지정

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
                intent.putExtra( "output", ImageCaptureUri);
                startActivityForResult(intent, REQUEST_CROP);

                break;

            case REQUEST_CROP :
                String full_path = ImageCaptureUri.getPath(); // 받은 uri path 저장

                // bitmap으로 변환 후 해당 imageview에 출력
                Bitmap bmp = BitmapFactory.decodeFile(full_path);
                ImageView modifystyle_image = (ImageView)findViewById(R.id.Sb_Modifystyle_Image);
                modifystyle_image.setImageBitmap(bmp);

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

    public void resumeForFragment(int n){
        if(n == 0)
            ((StyleListActivity)StyleListActivity.currentfragment).onResume();
        else if(n == 1)
            ((LikeActivity)LikeActivity.currentfragment).onResume();
        else if(n == 2)
            ((SearchActivity)SearchActivity.currentfragment).onResume();
        else
            ((ProfileActivity)ProfileActivity.currentfragment).onResume();
    }
}
