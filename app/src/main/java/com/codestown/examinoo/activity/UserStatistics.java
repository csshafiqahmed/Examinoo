package com.codestown.examinoo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.codestown.examinoo.Constant;
import com.codestown.examinoo.R;
import com.codestown.examinoo.helper.AppController;
import com.codestown.examinoo.helper.AudienceProgress;
import com.codestown.examinoo.helper.CircleImageView;
import com.codestown.examinoo.helper.Session;
import com.codestown.examinoo.helper.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserStatistics extends AppCompatActivity {


    Toolbar toolbar;
    TextView tvName, tvRank, tvScore, tvCoin, tvTotalQue, tvCorrect, tvInCorrect, tvCorrectP, tvInCorrectP;
    CircleImageView imgProfile;
    public AudienceProgress progress;
    String totalQues, correctQues, inCorrectQues, strongCate, weakCate, strongRatio, weakRatio;
    public ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    public RecyclerView recyclerView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_statistics);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.user_statistics));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        AdView mAdView = findViewById(R.id.banner_AdView);
        mAdView.loadAd(new AdRequest.Builder().build());
        imgProfile = findViewById(R.id.imgProfile);
        tvName = findViewById(R.id.tvName);
        tvRank = findViewById(R.id.tvRank);
        tvScore = findViewById(R.id.tvScore);
        tvCoin = findViewById(R.id.tvCoin);

        tvTotalQue = findViewById(R.id.tvAttended);
        tvCorrect = findViewById(R.id.tvCorrect);
        tvInCorrect = findViewById(R.id.tvInCorrect);
        tvCorrectP = findViewById(R.id.tvCorrectP);
        tvInCorrectP = findViewById(R.id.tvInCorrectP);
        progress = findViewById(R.id.progress);


        imgProfile.setDefaultImageResId(R.drawable.ic_account);
        imgProfile.setImageUrl(Session.getUserData(Session.PROFILE, UserStatistics.this), imageLoader);
        tvName.setText(getString(R.string.hello) + Session.getUserData(Session.NAME, UserStatistics.this));
        if (Utils.isNetworkAvailable(UserStatistics.this)) {
            GetUserData();
            GetUserStatistics();
        }
        Utils.LoadNativeAd(UserStatistics.this);
    }


    public void GetUserStatistics() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);

                            boolean error = obj.getBoolean("error");
                            if (!error) {
                                JSONObject object = obj.getJSONObject(Constant.DATA);
                                totalQues = object.getString(Constant.QUESTION_ANSWERED);
                                correctQues = object.getString(Constant.CORRECT_ANSWERS);
                                inCorrectQues = String.valueOf(Integer.parseInt(totalQues) - Integer.parseInt(correctQues));
                                strongCate = object.getString(Constant.STRONG_CATE);
                                weakCate = object.getString(Constant.WEAK_CATE);
                                strongRatio = object.getString(Constant.RATIO_1);
                                weakRatio = object.getString(Constant.RATIO_2);

                                tvTotalQue.setText(totalQues);
                                tvCorrect.setText(correctQues);
                                tvInCorrect.setText(inCorrectQues);
                                float percentCorrect = (Float.parseFloat(correctQues) * 100) / Float.parseFloat(totalQues);
                                float percentInCorrect = (Float.parseFloat(inCorrectQues) * 100) / Float.parseFloat(totalQues);
                                int perctn=Math.round(percentCorrect);
                                int perctnincorrect=Math.round(percentInCorrect);
                                tvCorrectP.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_green, 0, 0, 0);
                                tvInCorrectP.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_red, 0, 0, 0);
                                tvCorrectP.setText(perctn + getString(R.string.modulo_sign));
                                tvInCorrectP.setText(perctnincorrect+ getString(R.string.modulo_sign));
                                progress.SetAttributesForStatistics(getApplicationContext());
                                progress.setMaxProgress(Integer.parseInt(totalQues));
                                progress.setCurrentProgress(Integer.parseInt(correctQues));

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
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.accessKey, Constant.accessKeyValue);
                params.put(Constant.GET_USER_STATISTICS, "1");
                params.put(Constant.userId, Session.getUserData(Session.USER_ID, UserStatistics.this));
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    public void GetUserData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            if (!error) {
                                JSONObject jsonobj = obj.getJSONObject("data");
                                Constant.TOTAL_COINS = Integer.parseInt(jsonobj.getString(Constant.COINS));
                                tvCoin.setText("" + Constant.TOTAL_COINS);
                                tvRank.setText("" + jsonobj.getString(Constant.GLOBAL_RANK));
                                tvScore.setText(jsonobj.getString(Constant.GLOBAL_SCORE));
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
                params.put(Constant.GET_USER_BY_ID, "1");
                params.put(Constant.ID, Session.getUserData(Session.USER_ID, getApplicationContext()));

                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
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
