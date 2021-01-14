package com.codestown.examinoo.activity;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

import com.codestown.examinoo.helper.AudienceProgress;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.codestown.examinoo.Constant;
import com.codestown.examinoo.R;
import com.codestown.examinoo.helper.AndroidMultiPartEntity;
import com.codestown.examinoo.helper.AppController;
import com.codestown.examinoo.helper.CircleImageView;
import com.codestown.examinoo.helper.Session;

import com.facebook.login.LoginManager;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.codestown.examinoo.helper.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileActivity extends AppCompatActivity {

    public CircleImageView imgProfile;
    public ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    public int reqReadPermission = 1;
    public int reqWritePermission = 2;
    public static int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static int SELECT_FILE = 110;
    Uri fileUri;
    public ProgressBar progressBar;
    private String filePath = null;
    File sourceFile;
    long totalSize = 0;
    public FloatingActionButton fabProfile;
    public TextView tvEmailId, tvUpdate, tvLogout;
    public AudienceProgress progress;
    public LinearLayout edtNameLayout, edtMobileLayout;
    public EditText edtName, edtMobile;
    public RelativeLayout mainLayout;
    Toolbar toolbar;
    private File output = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mainLayout = findViewById(R.id.mainLayout);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.update_profile));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtNameLayout = findViewById(R.id.edtNameLayout);
        edtMobileLayout = findViewById(R.id.edtMobileLayout);

        edtName = findViewById(R.id.edtUserName);
        edtMobile = findViewById(R.id.edtMobile);
        tvEmailId = findViewById(R.id.tvEmailId);
        tvUpdate = findViewById(R.id.tvUpdate);
        tvLogout = findViewById(R.id.tvLogout);

        fabProfile = findViewById(R.id.fabProfile);
        progressBar = findViewById(R.id.progressBar);
        progress = findViewById(R.id.progress);
        imgProfile = findViewById(R.id.imgProfile);
        imgProfile.setDefaultImageResId(R.drawable.ic_account);
        imgProfile.setImageUrl(Session.getUserData(Session.PROFILE, ProfileActivity.this), imageLoader);
        fabProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectProfileImage();
            }
        });

        tvLogout.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_logout, 0, 0, 0);
        edtName.setText(Session.getUserData(Session.NAME, ProfileActivity.this));
        edtMobile.setText(Session.getUserData(Session.MOBILE, ProfileActivity.this));
        tvEmailId.setText(Session.getUserData(Session.EMAIL, ProfileActivity.this));
        edtNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvUpdate.setVisibility(View.VISIBLE);
                edtName.setEnabled(true);
            }
        });
        edtMobileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvUpdate.setVisibility(View.VISIBLE);
                edtMobile.setEnabled(true);
            }
        });
        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtName.getText().toString();
                String mobile = edtMobile.getText().toString();
                if (edtName.getText().toString().isEmpty())
                    edtName.setError(getString(R.string.empty_alert_msg));
                else
                    UpdateProfile(name, mobile);
            }
        });
        Utils.LoadNativeAd(ProfileActivity.this);
    }

    public void UpdateProfile(final String name, final String mobile) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);

                            boolean error = obj.getBoolean("error");
                            String message = obj.getString("message");
                            if (!error) {
                                Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                                Session.setUserData(Session.MOBILE, mobile, ProfileActivity.this);
                                Session.setUserData(Session.NAME, name, ProfileActivity.this);
                                edtName.setText(name);
                                edtMobile.setText(mobile);
                                DrawerActivity.tvName.setText(name);
                                tvUpdate.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.accessKey, Constant.accessKeyValue);
                params.put(Constant.updateProfile, "1");
                params.put(Constant.email, Session.getUserData(Session.EMAIL, ProfileActivity.this));
                params.put(Constant.name, name);
                params.put(Constant.mobile, mobile);
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    public void SelectProfileImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, reqReadPermission);
            } else if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, reqWritePermission);
            } else {
                selectDialog();
            }
        } else {
            selectDialog();
        }
    }

    public void selectDialog() {
        final CharSequence[] items = {getString(R.string.take_photo), getString(R.string.from_library), getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.take_photo))) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                    output = new File(dir, "CameraContentDemo.jpeg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", output));
                    startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                } else if (items[item].equals(getString(R.string.from_library))) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, SELECT_FILE);
                } else if (items[item].equals(getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_FILE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setOutputCompressQuality(80)
                    .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                    .setAspectRatio(1, 1)
                    .start(ProfileActivity.this);

        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.activity(FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", output)).start(ProfileActivity.this);

        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                fileUri = result.getUri();
                new UploadFileToServer().execute();
            }
        }
    }


    public void Logout(View view) {
        SignOutWarningDialog();
    }

    public void UserStatistics(View view) {
        Intent intent = new Intent(getApplicationContext(), UserStatistics.class);
        startActivity(intent);
    }

    public void LeaderBoard(View view) {
        Intent intent = new Intent(getApplicationContext(), LeaderBoardActivity.class);
        startActivity(intent);
    }

    public void Bookmarks(View view) {
        Intent intent = new Intent(getApplicationContext(), BookmarkList.class);
        startActivity(intent);
    }

    public void InviteFriend(View view) {
        Intent intent = new Intent(getApplicationContext(), InviteFriendActivity.class);
        startActivity(intent);
    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constant.QUIZ_URL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                //publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                filePath = fileUri.getPath();
                sourceFile = new File(filePath);

                // Adding file data to http body
                entity.addPart(Constant.image, new FileBody(sourceFile));
                entity.addPart(Constant.accessKey, new StringBody(Constant.accessKeyValue));
                entity.addPart(Constant.userId, new StringBody(Session.getUserData(Session.USER_ID, ProfileActivity.this)));
                entity.addPart(Constant.upload_profile_image, new StringBody("1"));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: " + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;

            try {
                JSONObject jsonObject = new JSONObject(result);
                boolean error = jsonObject.getBoolean("error");
                if (!error) {

                    Session.setUserData(Session.PROFILE, jsonObject.getString("file_path"), ProfileActivity.this);
                    imgProfile.setImageUrl(jsonObject.getString("file_path"), imageLoader);
                    DrawerActivity.imgProfile.setImageUrl(jsonObject.getString("file_path"), imageLoader);


                    Toast.makeText(ProfileActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //showAlert(result);
            progressBar.setVisibility(View.GONE);
            super.onPostExecute(result);
        }

    }

    public void SignOutWarningDialog() {
        final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(ProfileActivity.this);
        // Setting Dialog Message
        alertDialog.setMessage(getString(R.string.logout_warning));
        alertDialog.setCancelable(false);
        final android.app.AlertDialog alertDialog1 = alertDialog.create();

        // Setting OK Button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                Session.clearUserSession(ProfileActivity.this);
                LoginManager.getInstance().logOut();
                LoginActivity.mAuth.signOut();
                Intent intentLogin = new Intent(ProfileActivity.this, LoginActivity.class);
                intentLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intentLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentLogin);
                finish();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog1.dismiss();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
