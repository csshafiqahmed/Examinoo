package com.codestown.examinoo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.android.gms.ads.AdListener;
import com.codestown.examinoo.Constant;
import com.codestown.examinoo.R;
import com.codestown.examinoo.helper.AppController;
import com.codestown.examinoo.helper.CircleTimer;
import com.codestown.examinoo.helper.Session;
import com.codestown.examinoo.helper.Utils;
import com.codestown.examinoo.model.Question;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {
    public Toolbar toolbar;
    public TextView tvResultMsg, tvCorrect, tvInCorrect, tvTime, tvChallengeTime;
    public ScrollView scrollView;
    public CircleTimer progressBar;
    boolean isLevelCompleted;
    public RelativeLayout mainLayout;
    public String fromQue;
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        mainLayout = findViewById(R.id.mainLayout);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.result));
        fromQue = getIntent().getStringExtra("fromQue");
        context = ResultActivity.this;
        Utils.loadAd(ResultActivity.this);
        progressBar = findViewById(R.id.progressBar);
        tvTime = findViewById(R.id.tvTime);
        tvChallengeTime = findViewById(R.id.tvChallengeTime);
        scrollView = findViewById(R.id.scrollView);
        tvResultMsg = findViewById(R.id.tvResultMsg);
        tvCorrect = findViewById(R.id.right);
        tvInCorrect = findViewById(R.id.wrong);
        tvCorrect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.correct, 0, 0, 0);
        tvInCorrect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.incorrect, 0, 0, 0);
        isLevelCompleted = Session.isLevelCompleted(ResultActivity.this);

        ArrayList<String> correctList = new ArrayList<>();
        ArrayList<String> inCorrectList = new ArrayList<>();

        for (Question q : SelfChallengeQuestion.questionList) {
            if (q.isCorrect())
                correctList.add(getString(R.string.correct));
            else {
                if (q.isAttended())
                    inCorrectList.add(getString(R.string.incorrect));
            }
        }

        progressBar.SetTimerAttributes(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark), Color.WHITE);
        progressBar.setMaxProgress((int) Constant.CHALLENGE_TIME);
        progressBar.setCurrentProgress((int) (Constant.TAKE_TIME));
        tvTime.setText("" + getMinuteSeconds(Constant.TAKE_TIME));
        tvResultMsg.setText(getString(R.string.time_challenge_msg) + getMinuteSeconds(Constant.TAKE_TIME) + getString(R.string.sec));
        tvChallengeTime.setText(getString(R.string.challenge_time) + getMinuteSeconds(Constant.CHALLENGE_TIME));
        tvCorrect.setText("" + correctList.size());
        tvInCorrect.setText("" + inCorrectList.size());

        Utils.interstitial.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Utils.loadAd(ResultActivity.this);
            }
        });
        if (Session.isLogin(ResultActivity.this)) {
            GetUserData();
        }
        Utils.LoadNativeAd(ResultActivity.this);
    }

    public String getMinuteSeconds(long milliSeconds) {
        long totalSecs = (long) (milliSeconds / 1000.0);
        long minutes = (totalSecs / 60);
        long seconds = totalSecs % 60;
        String str = String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
        return str;
    }


    public void ReviewAnswers(View view) {
        Intent intentReview = new Intent(ResultActivity.this, ReviewActivity.class);
        intentReview.putExtra("from", "challenge");
        startActivity(intentReview);
    }


    public void ShareScore(View view) {
        String shareMsg = "I have finished   " + getMinuteSeconds(Constant.CHALLENGE_TIME)+ " minute self challenge in " + getMinuteSeconds(Constant.TAKE_TIME) + " minute in " + getString(R.string.app_name);
        Utils.ShareInfo(scrollView, ResultActivity.this, shareMsg);
    }

    public void RateApp(View view) {
        Utils.displayInterstitial();
        rateClicked();
    }

    public void Home(View view) {
        Intent intent1 = new Intent(ResultActivity.this, MainActivity.class);
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
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void PlayQuiz(View view) {
        Intent intent = new Intent(ResultActivity.this, CategoryActivity.class);
        startActivity(intent);
    }

    public void BattleQuiz(View view) {
        Intent intent = new Intent(ResultActivity.this, SearchPlayerActivity.class);
        startActivity(intent);
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
        Utils.loadAd(ResultActivity.this);
        Utils.CheckBgMusic(ResultActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppController.StopSound();
    }
}