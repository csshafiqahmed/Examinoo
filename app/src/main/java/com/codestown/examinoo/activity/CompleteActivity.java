package com.codestown.examinoo.activity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.codestown.examinoo.R;
import com.codestown.examinoo.helper.AppController;
import com.codestown.examinoo.helper.AudienceProgress;
import com.codestown.examinoo.Constant;
import com.codestown.examinoo.helper.Session;
import com.codestown.examinoo.helper.Utils;
import com.google.android.gms.ads.AdListener;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class CompleteActivity extends AppCompatActivity {

    public Toolbar toolbar;


    public TextView txt_result_title, tvTotalScore, tvLevelScore, tvCorrect, tvInCorrect, tvLevelCoins, tvTotalCoins, tvPlayNext;
    public ScrollView scrollView;
    public AudienceProgress result_prog;

    boolean isLevelCompleted;
    public RelativeLayout mainLayout;
    public String fromQue;
    public Context context;
    public int levelNo;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);
        mainLayout = findViewById(R.id.mainLayout);

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fromQue = getIntent().getStringExtra("fromQue");
        levelNo = getIntent().getIntExtra("levelNo", 1);
        context = CompleteActivity.this;

        Utils.loadAd(CompleteActivity.this);

        result_prog = findViewById(R.id.result_progress);
        result_prog.SetAttributes1();
        scrollView = findViewById(R.id.scrollView);
        txt_result_title = findViewById(R.id.txt_result_title);
        tvCorrect = findViewById(R.id.right);
        tvInCorrect = findViewById(R.id.wrong);
        tvTotalScore = findViewById(R.id.tvTtlScore);
        tvLevelScore = findViewById(R.id.tvScore);

        tvLevelScore.setText("" + Utils.level_score);
        tvLevelCoins = findViewById(R.id.tvCoin);
        tvTotalCoins = findViewById(R.id.tvTtlCoin);
        tvLevelCoins.setText("" + Utils.level_coin);


        tvCorrect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.correct, 0, 0, 0);
        tvInCorrect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.incorrect, 0, 0, 0);
        tvLevelScore.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rank, 0, 0, 0);
        tvLevelCoins.setCompoundDrawablesWithIntrinsicBounds(R.drawable.coins, 0, 0, 0);
        tvPlayNext = findViewById(R.id.tvPlayNext);
        getLevel();
        isLevelCompleted = Session.isLevelCompleted(CompleteActivity.this);

        if (isLevelCompleted) {
            scrollView.setBackgroundResource(R.drawable.complete_bg);
            if (Constant.TotalLevel == Utils.RequestlevelNo) {
                Utils.RequestlevelNo = 1;
                tvPlayNext.setText(getResources().getString(R.string.play_again));
            } else {
                Utils.RequestlevelNo = Utils.RequestlevelNo + 1;
                txt_result_title.setText(getString(R.string.completed));
                tvPlayNext.setText(getResources().getString(R.string.next_play));
                getSupportActionBar().setTitle(getResources().getString(R.string.next_play));
            }
        } else {
            scrollView.setBackgroundResource(R.drawable.complete_not_bg);
            txt_result_title.setText(getString(R.string.not_completed));
            tvPlayNext.setText(getResources().getString(R.string.play_next));
            getSupportActionBar().setTitle(getResources().getString(R.string.play_next));
        }
        result_prog.setCurrentProgress((double) getPercentageCorrect(Utils.TotalQuestion, Utils.CoreectQuetion));
        tvCorrect.setText("" + Utils.CoreectQuetion);
        tvInCorrect.setText("" + Utils.WrongQuation);

        Utils.interstitial.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Utils.loadAd(CompleteActivity.this);
            }
        });
        if (Session.isLogin(CompleteActivity.this)) {
            GetUserData();
        }
        Utils.LoadNativeAd(CompleteActivity.this);
    }

    public static float getPercentageCorrect(int questions, int correct) {
        float proportionCorrect = ((float) correct) / ((float) questions);
        return proportionCorrect * 100;
    }

    public void PlayAgain(View view) {

        Intent intent = new Intent(CompleteActivity.this, PlayActivity.class);
        intent.putExtra("fromQue", fromQue);
        intent.putExtra("levelNo", levelNo);
        startActivity(intent);
        ((CompleteActivity) context).finish();
    }

    public void ReviewAnswers(View view) {
        Intent intentReview = new Intent(CompleteActivity.this, ReviewActivity.class);
        intentReview.putExtra("from", "regular");
        startActivity(intentReview);
    }

    public void ShareScore(View view) {
        String shareMsg = "I have finished Level No : " + Utils.RequestlevelNo + " with " + Utils.level_score + " Score in " + getString(R.string.app_name);
        Utils.ShareInfo(scrollView, CompleteActivity.this, shareMsg);
    }

    public void RateApp(View view) {
        Utils.displayInterstitial();
        rateClicked();
    }

    public void Home(View view) {
        Intent intent1 = new Intent(CompleteActivity.this, MainActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent1.putExtra("type", "default");
        startActivity(intent1);
    }

    private void rateClicked() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.APP_LINK)));
        }
    }

    public void GetUserData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            if (!error) {
                                JSONObject jsonobj = obj.getJSONObject("data");
                                Constant.TOTAL_COINS = Integer.parseInt(jsonobj.getString(Constant.COINS));
                                tvTotalCoins.setText("" + Constant.TOTAL_COINS);
                                tvTotalScore.setText(jsonobj.getString(Constant.GLOBAL_SCORE));
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

    @Override
    public void onResume() {
        super.onResume();
        Utils.loadAd(CompleteActivity.this);
        Utils.CheckBgMusic(CompleteActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppController.StopSound();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void getLevel() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println("====response " + response);
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            if (!error) {
                                JSONObject object = obj.getJSONObject(Constant.DATA);
                                levelNo = (Integer.parseInt(object.getString(Constant.LEVEL)) );
                                LevelActivity.levelNo = (levelNo);
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
                params.put(Constant.GET_LEVEL_DATA, "1");
                params.put(Constant.category, String.valueOf(Constant.CATE_ID));
                params.put(Constant.subCategoryId, String.valueOf(Constant.SUB_CAT_ID));
                params.put(Constant.userId, Session.getUserData(Session.USER_ID, getApplicationContext()));

                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);

    }
}