package com.codestown.examinoo.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.codestown.examinoo.R;
import com.codestown.examinoo.helper.AppController;
import com.codestown.examinoo.helper.CircleTimer;
import com.codestown.examinoo.helper.AudienceProgress;

import com.codestown.examinoo.Constant;
import com.codestown.examinoo.helper.Session;
import com.codestown.examinoo.helper.Utils;
import com.codestown.examinoo.helper.TouchImageView;
import com.codestown.examinoo.model.Question;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.codestown.examinoo.activity.MainActivity.bookmarkDBHelper;
import static com.codestown.examinoo.activity.MainActivity.rewardedVideoAd;

public class PlayActivity extends AppCompatActivity implements OnClickListener {

    public Toolbar toolbar;
    public static AdRequest adRequest;

    public Question question;
    public int questionIndex = 0,
            btnPosition = 0,

    count_question_completed = 0,
            score = 0,
            level_coin = 6,
            correctQuestion = 0,
            inCorrectQuestion = 0,
            rightAns;
    public TextView option_a, option_b, option_c, option_d, option_e,
            tvScore, tvRight, tvWrong, coin_count;

    public static TextView btnOpt1, btnOpt2, btnOpt3, btnOpt4, btnOpt5, txtQuestion, txtQuestion1, tvAlert, tvIndex;
    public CompleteActivity completeActivity;
    public static Context context;
    public ImageView fifty_fifty, skip_question, resetTimer, audience_poll;
    public RelativeLayout playLayout, checkLayout, bottomLyt;
    public Button btnTry;
    public RelativeLayout layout_A, layout_B, layout_C, layout_D, layout_E;
    private Animation animation;
    private final Handler mHandler = new Handler();

    public Animation RightSwipe_A, RightSwipe_B, RightSwipe_C, RightSwipe_D, RightSwipe_E, Fade_in, fifty_fifty_anim;
    private AudienceProgress progressBarTwo_A, progressBarTwo_B, progressBarTwo_C, progressBarTwo_D, progressBarTwo_E;
    public static CircleTimer progressTimer;
    public static Timer timer;

    public static InterstitialAd interstitial;
    public static ArrayList<String> options;

    public static long leftTime = 0;
    public boolean isDialogOpen = false;
    public static ArrayList<Question> questionList;

    TouchImageView imgQuestion;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    ProgressBar imgProgress, rightProgress, wrongProgress, progressBar;
    ImageView imgZoom, imgMic;
    int click = 0;
    int textSize;
    public TextToSpeech textToSpeech;
    RelativeLayout mainLayout;
    public String fromQue;
    public ScrollView mainScroll, queScroll;
    public int levelNo;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        mainLayout = findViewById(R.id.play_layout);

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.level_txt) + Utils.RequestlevelNo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = PlayActivity.this;
        fromQue = getIntent().getStringExtra("fromQue");
        levelNo = getIntent().getIntExtra("levelNo", 1);
        completeActivity = new CompleteActivity();
        final int[] CLICKABLE = new int[]{R.id.a_layout, R.id.b_layout, R.id.c_layout, R.id.d_layout, R.id.e_layout};

        for (int i : CLICKABLE) {
            findViewById(i).setOnClickListener(this);
        }

        InitializeTTF();
        textSize = Integer.parseInt(Session.getSavedTextSize(PlayActivity.this));
        Session.removeSharedPreferencesData(PlayActivity.this);
        RightSwipe_A = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.anim_right_a);
        RightSwipe_B = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.anim_right_b);
        RightSwipe_C = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.anim_right_c);
        RightSwipe_D = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.anim_right_d);
        RightSwipe_E = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.anim_right_e);
        Fade_in = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.fade_out);
        fifty_fifty_anim = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.fifty_fifty);

        resetAllValue();
        mainScroll.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                v.findViewById(R.id.queScroll).getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });
        queScroll.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        rewardedVideoAd.setRewardedVideoAdListener(rewardedVideoAdListener);
        progressTimer.setMaxProgress(Constant.CIRCULAR_MAX_PROGRESS);
        progressTimer.setCurrentProgress(Constant.CIRCULAR_MAX_PROGRESS);
        try {
            interstitial = new InterstitialAd(PlayActivity.this);
            interstitial.setAdUnitId(getString(R.string.admob_interstitial_id));
            adRequest = new AdRequest.Builder().build();
            interstitial.loadAd(adRequest);
            interstitial.setAdListener(new AdListener() {
                @Override
                public void onAdOpened() {
                    //when ads show , we have to stop timer
                    stopTimer();
                }

                @Override
                public void onAdClosed() {
                    //after ad close we restart timer
                    // timer1= new timer(Constant.TIME_PER_QUESTION, Constant.COUNT_DOWN_TIMER);
                    if (!tvAlert.getText().equals(getString(R.string.no_enough_question)))
                        starTimer();


                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void InitializeTTF() {
        textToSpeech = new TextToSpeech(PlayActivity.this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Constant.ttsLanguage);
                    textToSpeech.setSpeechRate(1f);
                    textToSpeech.setPitch(1.1f);

                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public static void ChangeTextSize(int size) {

        if (btnOpt1 != null)
            btnOpt1.setTextSize(size);
        if (btnOpt2 != null)
            btnOpt2.setTextSize(size);
        if (btnOpt3 != null)
            btnOpt3.setTextSize(size);
        if (btnOpt4 != null)
            btnOpt4.setTextSize(size);
        if (btnOpt5 != null)
            btnOpt5.setTextSize(size);
        if (txtQuestion != null)
            txtQuestion.setTextSize(size);
        if (txtQuestion1 != null)
            txtQuestion1.setTextSize(size);
    }

    @SuppressLint("SetTextI18n")
    public void resetAllValue() {

        playLayout = findViewById(R.id.innerLayout);
        tvIndex = findViewById(R.id.tvIndex);

        progressTimer = findViewById(R.id.circleTimer);
        mainScroll = findViewById(R.id.mainScroll);
        queScroll = findViewById(R.id.queScroll);
        progressBar = findViewById(R.id.progressBar);
        coin_count = findViewById(R.id.coin_count);
        imgProgress = findViewById(R.id.imgProgress);
        rightProgress = findViewById(R.id.rightProgress);
        wrongProgress = findViewById(R.id.wrongProgress);
        imgQuestion = findViewById(R.id.imgQuestion);

        tvAlert = findViewById(R.id.tvNoConnection);
        checkLayout = findViewById(R.id.checkLayout);
        bottomLyt = findViewById(R.id.bottom_panel);

        btnTry = findViewById(R.id.btnTry);

        btnOpt1 = findViewById(R.id.btnOpt1);
        btnOpt2 = findViewById(R.id.btnOpt2);
        btnOpt3 = findViewById(R.id.btnOpt3);
        btnOpt4 = findViewById(R.id.btnOpt4);
        btnOpt5 = findViewById(R.id.btnOpt5);

        option_a = findViewById(R.id.option_a);
        option_b = findViewById(R.id.option_b);
        option_c = findViewById(R.id.option_c);
        option_d = findViewById(R.id.option_d);
        option_e = findViewById(R.id.option_e);


        imgMic = findViewById(R.id.imgMic);
        imgZoom = findViewById(R.id.imgZoom);
        fifty_fifty = findViewById(R.id.fifty_fifty);
        skip_question = findViewById(R.id.skip_quation);
        resetTimer = findViewById(R.id.reset_timer);
        audience_poll = findViewById(R.id.audience_poll);


        tvAlert.setText(getString(R.string.msg_no_internet));
        tvRight = findViewById(R.id.txtTrueQuestion);
        tvRight.setText("0");
        tvWrong = findViewById(R.id.txtFalseQuestion);
        tvWrong.setText("0");
        txtQuestion = findViewById(R.id.txtQuestion);
        txtQuestion1 = findViewById(R.id.txtQuestion1);
        layout_A = findViewById(R.id.a_layout);
        layout_B = findViewById(R.id.b_layout);
        layout_C = findViewById(R.id.c_layout);
        layout_D = findViewById(R.id.d_layout);
        layout_E = findViewById(R.id.e_layout);
        ChangeTextSize(textSize);
        progressBarTwo_A = findViewById(R.id.progress_A);
        progressBarTwo_B = findViewById(R.id.progress_B);
        progressBarTwo_C = findViewById(R.id.progress_C);
        progressBarTwo_D = findViewById(R.id.progress_D);
        progressBarTwo_E = findViewById(R.id.progress_E);
        progressBarTwo_A.SetAttributes();
        progressBarTwo_B.SetAttributes();
        progressBarTwo_C.SetAttributes();
        progressBarTwo_D.SetAttributes();
        progressBarTwo_E.SetAttributes();

        animation = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.right_ans_anim); // Change alpha from fully visible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the
        count_question_completed = Session.getCountQuestionCompleted(getApplicationContext());
        tvScore = findViewById(R.id.txtScore);
        tvScore.setText(String.valueOf(score));
        coin_count.setText(String.valueOf(Constant.TOTAL_COINS));


        if (Utils.isNetworkAvailable(PlayActivity.this)) {
            getQuestionsFromJson();

        } else {
            tvAlert.setText(getString(R.string.msg_no_internet));
            playLayout.setVisibility(View.GONE);
            checkLayout.setVisibility(View.VISIBLE);

        }

        imgMic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech.speak(questionList.get(questionIndex).getQuestion(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });


        btnTry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvAlert.getText().equals(getString(R.string.no_enough_question)))
                    BackButtonMethod();
                else {
                    if (Utils.isNetworkAvailable(PlayActivity.this)) {
                        getQuestionsFromJson();

                    } else {
                        tvAlert.setText(getString(R.string.msg_no_internet));
                        playLayout.setVisibility(View.GONE);
                        checkLayout.setVisibility(View.VISIBLE);

                    }
                }
            }
        });

    }


    private void nextQuizQuestion() {
        if (textToSpeech != null)
            textToSpeech.stop();
        stopTimer();
        starTimer();

        Constant.LeftTime = 0;
        leftTime = 0;
        setAgain();
        if (questionIndex >= questionList.size()) {
            levelCompleted();
        }
        invalidateOptionsMenu();

        layout_A.setBackgroundResource(R.drawable.answer_bg);
        layout_B.setBackgroundResource(R.drawable.answer_bg);
        layout_C.setBackgroundResource(R.drawable.answer_bg);
        layout_D.setBackgroundResource(R.drawable.answer_bg);
        layout_E.setBackgroundResource(R.drawable.answer_bg);
        layout_A.clearAnimation();
        layout_B.clearAnimation();
        layout_C.clearAnimation();
        layout_D.clearAnimation();
        layout_E.clearAnimation();
        layout_A.setClickable(true);
        layout_B.setClickable(true);
        layout_C.setClickable(true);
        layout_D.setClickable(true);
        layout_E.setClickable(true);
        btnOpt1.startAnimation(RightSwipe_A);
        btnOpt2.startAnimation(RightSwipe_B);
        btnOpt3.startAnimation(RightSwipe_C);
        btnOpt4.startAnimation(RightSwipe_D);
        btnOpt5.startAnimation(RightSwipe_E);
        txtQuestion1.startAnimation(Fade_in);
        txtQuestion.startAnimation(Fade_in);

        if (questionIndex < questionList.size()) {
            question = questionList.get(questionIndex);
            int temp = questionIndex;
            imgQuestion.resetZoom();
            tvIndex.setText(++temp + "");

            if (!question.getImage().isEmpty()) {
                imgZoom.setVisibility(View.VISIBLE);
                txtQuestion1.setVisibility(View.VISIBLE);
                txtQuestion.setVisibility(View.GONE);
                imgQuestion.setImageUrl(question.getImage(), imageLoader);
                imgQuestion.setVisibility(View.VISIBLE);
                imgProgress.setVisibility(View.GONE);
                imgZoom.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        click++;
                        if (click == 1)
                            imgQuestion.setZoom(1.25f);
                        else if (click == 2)
                            imgQuestion.setZoom(1.50f);
                        else if (click == 3)
                            imgQuestion.setZoom(1.75f);
                        else if (click == 4) {
                            imgQuestion.setZoom(2.00f);
                            click = 0;
                        }
                    }
                });
            } else {
                imgZoom.setVisibility(View.GONE);
                imgQuestion.setVisibility(View.GONE);
                txtQuestion1.setVisibility(View.GONE);
                txtQuestion.setVisibility(View.VISIBLE);
            }

            txtQuestion.setText(question.getQuestion());
            txtQuestion1.setText(question.getQuestion());
            options = new ArrayList<String>();
            options.addAll(question.getOptions());

            if (question.getQueType().equals(Constant.TRUE_FALSE)) {
                option_a.setVisibility(View.GONE);
                option_b.setVisibility(View.GONE);
                layout_C.setVisibility(View.GONE);
                layout_D.setVisibility(View.GONE);
                btnOpt1.setPadding(0, 25, 0, 25);
                btnOpt2.setPadding(0, 25, 0, 25);
                btnOpt1.setGravity(Gravity.CENTER);
                btnOpt2.setGravity(Gravity.CENTER);
                bottomLyt.setVisibility(View.GONE);
            } else {
                Collections.shuffle(options);
                option_a.setVisibility(View.VISIBLE);
                option_b.setVisibility(View.VISIBLE);
                layout_C.setVisibility(View.VISIBLE);
                layout_D.setVisibility(View.VISIBLE);
                btnOpt1.setGravity(Gravity.NO_GRAVITY);
                btnOpt2.setGravity(Gravity.NO_GRAVITY);
                bottomLyt.setVisibility(View.VISIBLE);
            }

            if (Session.getBoolean(Session.E_MODE, getApplicationContext())) {
                if (options.size() == 4)
                    layout_E.setVisibility(View.GONE);
                else
                    layout_E.setVisibility(View.VISIBLE);

            }
            btnOpt1.setText(Html.fromHtml(options.get(0).trim()));
            btnOpt2.setText(Html.fromHtml(options.get(1).trim()));
            btnOpt3.setText(Html.fromHtml(options.get(2).trim()));
            btnOpt4.setText(Html.fromHtml(options.get(3).trim()));
            if (Session.getBoolean(Session.E_MODE, getApplicationContext())) {
                if (options.size() == 5)
                    btnOpt5.setText(Html.fromHtml(options.get(4).trim()));
            }

        }
    }

    public void levelCompleted() {
        Utils.TotalQuestion = questionList.size();
        Utils.CoreectQuetion = correctQuestion;
        Utils.WrongQuation = inCorrectQuestion;

        stopTimer();
        int total = questionList.size();
        int percent = (correctQuestion * 100) / total;

        if (percent >= 30 && percent <= 40) {
            Constant.TOTAL_COINS = Constant.TOTAL_COINS + Constant.giveOneCoin;
            level_coin = Constant.giveOneCoin;

        } else if (percent >= 40 && percent < 60) {
            Constant.TOTAL_COINS = Constant.TOTAL_COINS + Constant.giveTwoCoins;
            level_coin = Constant.giveTwoCoins;

        } else if (percent >= 60 && percent < 80) {
            Constant.TOTAL_COINS = Constant.TOTAL_COINS + Constant.giveThreeCoins;
            level_coin = Constant.giveThreeCoins;

        } else if (percent >= 80) {
            Constant.TOTAL_COINS = Constant.TOTAL_COINS + Constant.giveFourCoins;
            level_coin = Constant.giveFourCoins;

        } else
            level_coin = 0;


        Utils.level_coin = level_coin;
        Utils.level_score = score;
        coin_count.setText(String.valueOf(Constant.TOTAL_COINS));
        if (Session.isLogin(getApplicationContext())) {
            if (score >= 0)
                UpdateScore(String.valueOf(score));

            SetUserStatistics(String.valueOf(questionList.size()), String.valueOf(correctQuestion), String.valueOf(percent));
        }
        if (percent >= Constant.PASSING_PER && levelNo == Utils.RequestlevelNo) {
            // levelNo = levelNo + 1;
            SaveLevel();

        }

        if (percent >= Constant.PASSING_PER)
            Session.setLevelComplete(getApplicationContext(), true);
        else
            Session.setLevelComplete(getApplicationContext(), false);


        Intent intent = new Intent(getApplicationContext(), CompleteActivity.class);
        intent.putExtra("fromQue", fromQue);
        intent.putExtra("levelNo", levelNo);
        startActivity(intent);
        finish();
        //((PlayActivity) context).finish();
        blankAllValue();

    }

    public void AddReview(Question question, TextView tvBtnOpt, RelativeLayout layout) {
        layout_A.setClickable(false);
        layout_B.setClickable(false);
        layout_C.setClickable(false);
        layout_D.setClickable(false);
        layout_E.setClickable(false);

        if (tvBtnOpt.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            rightSound();

            layout.setBackgroundResource(R.drawable.right_gradient);
            layout.startAnimation(animation);
            score = score + Constant.FOR_CORRECT_ANS;
            correctQuestion = correctQuestion + 1;
            tvScore.setText(String.valueOf(score));
            tvRight.setText(String.valueOf(correctQuestion));
            rightProgress.setProgress(correctQuestion);
        } else {

            playWrongSound();
            layout.setBackgroundResource(R.drawable.wrong_gradient);
            score = score - Constant.PENALTY;
            inCorrectQuestion = inCorrectQuestion + 1;
            tvScore.setText(String.valueOf(score));
            tvWrong.setText(String.valueOf(inCorrectQuestion));
            wrongProgress.setProgress(inCorrectQuestion);
        }

        question.setSelectedAns(tvBtnOpt.getText().toString());
        if (Constant.QUICK_ANSWER_ENABLE.equals("1"))
            RightAnswerBackgroundSet();
        question.setAttended(true);
        stopTimer();

        questionIndex++;
        mHandler.postDelayed(mUpdateUITimerTask, 1000);

    }

    public void RightAnswerBackgroundSet() {
        if (btnOpt1.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            layout_A.setBackgroundResource(R.drawable.right_gradient);
            layout_A.startAnimation(animation);

        } else if (btnOpt2.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            layout_B.setBackgroundResource(R.drawable.right_gradient);
            layout_B.startAnimation(animation);

        } else if (btnOpt3.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            layout_C.setBackgroundResource(R.drawable.right_gradient);
            layout_C.startAnimation(animation);

        } else if (btnOpt4.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            layout_D.setBackgroundResource(R.drawable.right_gradient);
            layout_D.startAnimation(animation);

        } else if (btnOpt5.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            layout_E.setBackgroundResource(R.drawable.right_gradient);
            layout_E.startAnimation(animation);
        }
    }

    @Override
    public void onClick(View v) {
        if (questionIndex < questionList.size()) {
            question = questionList.get(questionIndex);
            layout_A.setClickable(false);
            layout_B.setClickable(false);
            layout_C.setClickable(false);
            layout_D.setClickable(false);
            layout_E.setClickable(false);
            if (progressBarTwo_A.getVisibility() == (View.VISIBLE)) {
                progressBarTwo_A.setVisibility(View.GONE);
                progressBarTwo_B.setVisibility(View.GONE);
                progressBarTwo_C.setVisibility(View.GONE);
                progressBarTwo_D.setVisibility(View.GONE);
                progressBarTwo_E.setVisibility(View.GONE);
                option_a.setVisibility(View.VISIBLE);
                option_b.setVisibility(View.VISIBLE);
                option_c.setVisibility(View.VISIBLE);
                option_d.setVisibility(View.VISIBLE);
                option_e.setVisibility(View.VISIBLE);
            }
            switch (v.getId()) {
                case R.id.a_layout:
                    AddReview(question, btnOpt1, layout_A);
                    break;

                case R.id.b_layout:
                    AddReview(question, btnOpt2, layout_B);

                    break;
                case R.id.c_layout:
                    AddReview(question, btnOpt3, layout_C);

                    break;
                case R.id.d_layout:
                    AddReview(question, btnOpt4, layout_D);

                    break;
                case R.id.e_layout:
                    AddReview(question, btnOpt5, layout_E);

                    break;
            }

        }
    }


    private final Runnable mUpdateUITimerTask = new Runnable() {
        public void run() {
            if (getApplicationContext() != null) {
                if (checkLayout.getVisibility() != View.VISIBLE)
                    nextQuizQuestion();

            }
        }
    };


    public void PlayAreaLeaveDialog() {


        if (!tvAlert.getText().equals(getResources().getString(R.string.no_enough_question))) {
            stopTimer();
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(PlayActivity.this);
            // Setting Dialog Message
            alertDialog.setMessage(getString(R.string.exit_msg));
            alertDialog.setCancelable(false);
            final AlertDialog alertDialog1 = alertDialog.create();
            // Setting OK Button
            alertDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to execute after dialog closed
                    stopTimer();
                    leftTime = 0;
                    Constant.LeftTime = 0;
                    if (textToSpeech != null) {
                        textToSpeech.shutdown();
                    }

                    ((PlayActivity) context).finish();

                }
            });

            alertDialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog1.dismiss();
                    Constant.LeftTime = leftTime;
                    if (Constant.LeftTime != 0) {

                        timer = new Timer(leftTime, 1000);
                        timer.start();


                    }
                }
            });
            // Showing Alert Message
            alertDialog.show();
        } else {
            ((PlayActivity) context).finish();
        }
    }


    //play sound when answer is correct
    public void rightSound() {
        if (Session.getSoundEnableDisable(PlayActivity.this))
            Utils.setrightAnssound(PlayActivity.this);

        if (Session.getVibration(PlayActivity.this))
            Utils.vibrate(PlayActivity.this, Utils.VIBRATION_DURATION);

    }

    //play sound when answer is incorrect
    private void playWrongSound() {
        if (Session.getSoundEnableDisable(PlayActivity.this))
            Utils.setwronAnssound(PlayActivity.this);

        if (Session.getVibration(PlayActivity.this))
            Utils.vibrate(PlayActivity.this, Utils.VIBRATION_DURATION);

    }

    //set progress again after next question
    private void setAgain() {
        if (progressBarTwo_A.getVisibility() == (View.VISIBLE)) {
            progressBarTwo_A.setVisibility(View.GONE);
            progressBarTwo_B.setVisibility(View.GONE);
            progressBarTwo_C.setVisibility(View.GONE);
            progressBarTwo_D.setVisibility(View.GONE);
            progressBarTwo_E.setVisibility(View.GONE);
        }
    }

    public void FiftyFifty(View view) {
        Utils.btnClick(view, PlayActivity.this);
        CheckSound();
        if (!Session.isFiftyFiftyUsed(PlayActivity.this)) {
            if (Constant.TOTAL_COINS >= 4) {
                btnPosition = 0;
                Constant.TOTAL_COINS = Constant.TOTAL_COINS - 4;
                if (Session.isLogin(getApplicationContext()))
                    Utils.UpdateCoin(getApplicationContext(), String.valueOf(Constant.TOTAL_COINS));
                coin_count.setText(String.valueOf(Constant.TOTAL_COINS));
                if (btnOpt1.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
                    btnPosition = 1;
                if (btnOpt2.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
                    btnPosition = 2;
                if (btnOpt3.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
                    btnPosition = 3;
                if (btnOpt4.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
                    btnPosition = 4;

                if (Session.getBoolean(Session.E_MODE, getApplicationContext()))
                    FiftyFiftyWith_E();
                else
                    FiftyFiftyWithout_E();

                Session.setFifty_Fifty(PlayActivity.this);
            } else
                ShowRewarded(PlayActivity.this);

        } else
            AlreadyUsed();
    }

    public void FiftyFiftyWithout_E() {

        if (btnPosition == 1) {
            layout_B.startAnimation(fifty_fifty_anim);
            layout_C.startAnimation(fifty_fifty_anim);
            layout_B.setClickable(false);
            layout_C.setClickable(false);

        } else if (btnPosition == 2) {
            layout_C.startAnimation(fifty_fifty_anim);
            layout_D.startAnimation(fifty_fifty_anim);
            layout_C.setClickable(false);
            layout_D.setClickable(false);

        } else if (btnPosition == 3) {
            layout_D.startAnimation(fifty_fifty_anim);
            layout_A.startAnimation(fifty_fifty_anim);
            layout_D.setClickable(false);
            layout_A.setClickable(false);

        } else if (btnPosition == 4) {
            layout_A.startAnimation(fifty_fifty_anim);
            layout_B.startAnimation(fifty_fifty_anim);
            layout_A.setClickable(false);
            layout_B.setClickable(false);
        }
    }

    public void FiftyFiftyWith_E() {
        if (btnOpt5.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim())) {
            btnPosition = 5;
        }

        if (btnPosition == 1) {
            layout_B.startAnimation(fifty_fifty_anim);
            layout_C.startAnimation(fifty_fifty_anim);
            layout_B.setClickable(false);
            layout_C.setClickable(false);

        } else if (btnPosition == 2) {
            layout_C.startAnimation(fifty_fifty_anim);
            layout_D.startAnimation(fifty_fifty_anim);
            layout_C.setClickable(false);
            layout_D.setClickable(false);

        } else if (btnPosition == 3) {
            layout_D.startAnimation(fifty_fifty_anim);
            layout_E.startAnimation(fifty_fifty_anim);
            layout_D.setClickable(false);
            layout_E.setClickable(false);

        } else if (btnPosition == 4) {

            layout_E.startAnimation(fifty_fifty_anim);
            layout_A.startAnimation(fifty_fifty_anim);
            layout_E.setClickable(false);
            layout_A.setClickable(false);

        } else if (btnPosition == 5) {
            layout_A.startAnimation(fifty_fifty_anim);
            layout_B.startAnimation(fifty_fifty_anim);
            layout_A.setClickable(false);
            layout_B.setClickable(false);
        }
    }

    public void SkipQuestion(View view) {
        Utils.btnClick(view, PlayActivity.this);
        CheckSound();

        if (!Session.isSkipUsed(PlayActivity.this)) {
            if (Constant.TOTAL_COINS >= 2) {
                stopTimer();
                leftTime = 0;
                Constant.LeftTime = 0;

                Constant.TOTAL_COINS = Constant.TOTAL_COINS - 2;
                if (Session.isLogin(getApplicationContext()))
                    Utils.UpdateCoin(getApplicationContext(), String.valueOf(Constant.TOTAL_COINS));
                coin_count.setText(String.valueOf(Constant.TOTAL_COINS));
                questionIndex++;
                layout_A.setBackgroundResource(R.drawable.answer_bg);
                layout_B.setBackgroundResource(R.drawable.answer_bg);
                layout_C.setBackgroundResource(R.drawable.answer_bg);
                layout_D.setBackgroundResource(R.drawable.answer_bg);
                layout_E.setBackgroundResource(R.drawable.answer_bg);

                nextQuizQuestion();
                Session.setSkip(PlayActivity.this);
            } else
                ShowRewarded(PlayActivity.this);
        } else
            AlreadyUsed();
    }

    public void AudiencePoll(View view) {
        Utils.btnClick(view, PlayActivity.this);
        CheckSound();
        if (!Session.isAudiencePollUsed(PlayActivity.this)) {
            if (Constant.TOTAL_COINS >= 4) {
                btnPosition = 0;
                Constant.TOTAL_COINS = Constant.TOTAL_COINS - 4;
                if (Session.isLogin(getApplicationContext()))
                    Utils.UpdateCoin(getApplicationContext(), String.valueOf(Constant.TOTAL_COINS));
                coin_count.setText(String.valueOf(Constant.TOTAL_COINS));
                if (Session.getBoolean(Session.E_MODE, getApplicationContext()))
                    AudienceWith_E();
                else
                    AudienceWithout_E();

                option_a.setVisibility(View.VISIBLE);
                option_b.setVisibility(View.VISIBLE);
                option_c.setVisibility(View.VISIBLE);
                option_d.setVisibility(View.VISIBLE);
                option_e.setVisibility(View.VISIBLE);
                Session.setAudiencePoll(PlayActivity.this);
            } else
                ShowRewarded(PlayActivity.this);
        } else
            AlreadyUsed();
    }

    public void AudienceWithout_E() {
        int min = 45;
        int max = 70;
        Random r = new Random();
        int A = r.nextInt(max - min + 1) + min;
        int remain1 = 100 - A;
        int B = r.nextInt(((remain1 - 10)) + 1);
        int remain2 = remain1 - B;
        int C = r.nextInt(((remain2 - 5)) + 1);
        int D = remain2 - C;
        progressBarTwo_A.setVisibility(View.VISIBLE);
        progressBarTwo_B.setVisibility(View.VISIBLE);
        progressBarTwo_C.setVisibility(View.VISIBLE);
        progressBarTwo_D.setVisibility(View.VISIBLE);

        if (btnOpt1.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
            btnPosition = 1;
        if (btnOpt2.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
            btnPosition = 2;
        if (btnOpt3.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
            btnPosition = 3;
        if (btnOpt4.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
            btnPosition = 4;

        if (btnPosition == 1) {
            progressBarTwo_A.setCurrentProgress(A);
            progressBarTwo_B.setCurrentProgress(B);
            progressBarTwo_C.setCurrentProgress(C);
            progressBarTwo_D.setCurrentProgress(D);

        } else if (btnPosition == 2) {
            progressBarTwo_B.setCurrentProgress(A);
            progressBarTwo_C.setCurrentProgress(C);
            progressBarTwo_D.setCurrentProgress(D);
            progressBarTwo_A.setCurrentProgress(B);

        } else if (btnPosition == 3) {
            progressBarTwo_C.setCurrentProgress(A);
            progressBarTwo_B.setCurrentProgress(C);
            progressBarTwo_D.setCurrentProgress(D);
            progressBarTwo_A.setCurrentProgress(B);

        } else if (btnPosition == 4) {
            progressBarTwo_D.setCurrentProgress(A);
            progressBarTwo_B.setCurrentProgress(C);
            progressBarTwo_C.setCurrentProgress(D);
            progressBarTwo_A.setCurrentProgress(B);

        }
    }

    public void AudienceWith_E() {
        int min = 45;
        int max = 70;
        Random r = new Random();
        int A = r.nextInt(max - min + 1) + min;
        int remain1 = 100 - A;
        int B = r.nextInt(((remain1 - 8)) + 1);
        int remain2 = remain1 - B;
        int C = r.nextInt(((remain2 - 4)) + 1);
        int remain3 = remain2 - C;
        int D = r.nextInt(((remain3 - 2)) + 1);
        int E = remain3 - D;
        progressBarTwo_A.setVisibility(View.VISIBLE);
        progressBarTwo_B.setVisibility(View.VISIBLE);
        progressBarTwo_C.setVisibility(View.VISIBLE);
        progressBarTwo_D.setVisibility(View.VISIBLE);
        progressBarTwo_E.setVisibility(View.VISIBLE);

        if (btnOpt1.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
            btnPosition = 1;
        if (btnOpt2.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
            btnPosition = 2;
        if (btnOpt3.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
            btnPosition = 3;
        if (btnOpt4.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
            btnPosition = 4;
        if (btnOpt5.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
            btnPosition = 5;

        if (btnPosition == 1) {
            progressBarTwo_A.setCurrentProgress(A);
            progressBarTwo_B.setCurrentProgress(B);
            progressBarTwo_C.setCurrentProgress(C);
            progressBarTwo_D.setCurrentProgress(D);
            progressBarTwo_E.setCurrentProgress(E);

        } else if (btnPosition == 2) {

            progressBarTwo_B.setCurrentProgress(A);
            progressBarTwo_C.setCurrentProgress(B);
            progressBarTwo_D.setCurrentProgress(C);
            progressBarTwo_E.setCurrentProgress(D);
            progressBarTwo_A.setCurrentProgress(E);

        } else if (btnPosition == 3) {

            progressBarTwo_C.setCurrentProgress(A);
            progressBarTwo_D.setCurrentProgress(B);
            progressBarTwo_E.setCurrentProgress(C);
            progressBarTwo_A.setCurrentProgress(D);
            progressBarTwo_B.setCurrentProgress(E);

        } else if (btnPosition == 4) {

            progressBarTwo_D.setCurrentProgress(A);
            progressBarTwo_E.setCurrentProgress(B);
            progressBarTwo_A.setCurrentProgress(C);
            progressBarTwo_B.setCurrentProgress(D);
            progressBarTwo_C.setCurrentProgress(E);

        } else if (btnPosition == 5) {
            progressBarTwo_E.setCurrentProgress(A);
            progressBarTwo_A.setCurrentProgress(B);
            progressBarTwo_B.setCurrentProgress(C);
            progressBarTwo_C.setCurrentProgress(D);
            progressBarTwo_D.setCurrentProgress(E);
        }
    }

    public void ResetTimer(View view) {
        Utils.btnClick(view, PlayActivity.this);
        CheckSound();
        if (!Session.isResetUsed(PlayActivity.this)) {
            if (Constant.TOTAL_COINS >= 2) {
                Constant.TOTAL_COINS = Constant.TOTAL_COINS - 2;
                if (Session.isLogin(getApplicationContext()))
                    Utils.UpdateCoin(getApplicationContext(), String.valueOf(Constant.TOTAL_COINS));
                coin_count.setText(String.valueOf(Constant.TOTAL_COINS));
                Constant.LeftTime = 0;
                leftTime = 0;
                stopTimer();
                starTimer();
                Session.setReset(PlayActivity.this);
            } else
                ShowRewarded(PlayActivity.this);
        } else
            AlreadyUsed();
    }

    //Show alert dialog when lifeline already used in current level
    public void AlreadyUsed() {

        stopTimer();

        final AlertDialog.Builder dialog = new AlertDialog.Builder(PlayActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.lifeline_dialog, null);
        dialog.setView(dialogView);

        TextView ok = (TextView) dialogView.findViewById(R.id.ok);
        final AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();

        alertDialog.setCancelable(false);
        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
                if (leftTime != 0) {

                    timer = new Timer(leftTime, 1000);
                    timer.start();

                }
            }
        });

    }

    public void BackButtonMethod() {

        CheckSound();
        PlayAreaLeaveDialog();

    }

    public void CheckSound() {

        if (Session.getSoundEnableDisable(PlayActivity.this))
            Utils.backSoundonclick(PlayActivity.this);

        if (Session.getVibration(PlayActivity.this))
            Utils.vibrate(PlayActivity.this, Utils.VIBRATION_DURATION);
    }

    public void SettingButtonMethod() {
        CheckSound();
        stopTimer();


        Intent intent = new Intent(PlayActivity.this, SettingActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.open_next, R.anim.close_next);
    }

    public void getQuestionsFromJson() {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println("==== question " + response);
                            progressBar.setVisibility(View.VISIBLE);
                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean(Constant.ERROR);
                            if (!error) {
                                JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                                questionList = new ArrayList<>();
                                questionList.addAll(Utils.getQuestions(jsonArray, PlayActivity.this));
                                rightProgress.setMax(questionList.size());
                                wrongProgress.setMax(questionList.size());
                                nextQuizQuestion();
                                playLayout.setVisibility(View.VISIBLE);
                                checkLayout.setVisibility(View.GONE);


                            } else {
                                NotEnoughQuestion();
                            }
                            progressBar.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.accessKey, Constant.accessKeyValue);
                params.put(Constant.getQuestionByLevel, "1");
                params.put(Constant.Level, String.valueOf(Utils.RequestlevelNo));
                if (fromQue.equals("cate"))
                    params.put(Constant.category, "" + Constant.CATE_ID);
                else if (fromQue.equals("subCate"))
                    params.put(Constant.subCategoryId, "" + Constant.SUB_CAT_ID);
                if (Session.getBoolean(Session.LANG_MODE, getApplicationContext()))
                    params.put(Constant.LANGUAGE_ID, Session.getCurrentLanguage(getApplicationContext()));

                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void NotEnoughQuestion() {
        invalidateOptionsMenu();
        checkLayout.setVisibility(View.VISIBLE);
        tvAlert.setText(getString(R.string.no_enough_question));
        btnTry.setText(getString(R.string.go_back));
        progressBar.setVisibility(View.GONE);
        playLayout.setVisibility(View.GONE);

    }

    //filter current level question
    public static ArrayList<Question> filter(ArrayList<Question> models, String query) {
        query = query.toLowerCase();

        final ArrayList<Question> filteredModelList = new ArrayList<>();
        for (Question model : models) {
            final String text = "" + model.getLevel();
            if (text.equals(query)) {
                filteredModelList.add(model);
            }
        }

        return filteredModelList;
    }

    public void blankAllValue() {
        questionIndex = 0;
        count_question_completed = 0;
        score = 0;
        correctQuestion = 0;
        inCorrectQuestion = 0;
    }

    public void UpdateScore(final String score) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //System.out.println("=== update " + response);
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            String message = obj.getString("message");

                            if (error) {
                                Toast.makeText(PlayActivity.this, message, Toast.LENGTH_SHORT).show();
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
                params.put(Constant.setMonthlyLeaderboard, "1");
                params.put(Constant.userId, Session.getUserData(Session.USER_ID, PlayActivity.this));
                params.put(Constant.SCORE, score);
                System.out.println("====params " + params.toString());
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void SetUserStatistics(final String ttlQue, final String correct, final String percent) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //  System.out.println("=== statistic res " + response);
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
                params.put(Constant.SET_USER_STATISTICS, "1");
                params.put(Constant.userId, Session.getUserData(Session.USER_ID, PlayActivity.this));
                params.put(Constant.QUESTION_ANSWERED, ttlQue);
                params.put(Constant.CORRECT_ANSWERS, correct);
                params.put(Constant.COINS, String.valueOf(Constant.TOTAL_COINS));
                params.put(Constant.RATIO, percent);
                params.put(Constant.cate_id, String.valueOf(Constant.CATE_ID));
                System.out.println("====s params " + params.toString());
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    //Show dialog for rewarded ad
    //if user has not enough coin to use lifeline
    public void ShowRewarded(final Context context) {
        stopTimer();
        if (!Utils.isNetworkAvailable(PlayActivity.this)) {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle("Internet Connection Error!");
            dialog.setMessage("Internet Connection Error! Please connect to working Internet connection");
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    if (leftTime != 0) {
                        timer = new Timer(leftTime, 1000);
                        timer.start();
                    }

                }
            });
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (leftTime != 0) {
                        timer = new Timer(leftTime, 1000);
                        timer.start();
                    }

                }
            });
            dialog.show();


        } else {

            final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View dialogView = inflater.inflate(R.layout.dialog_alert_coin, null);
            dialog.setView(dialogView);
            TextView skip = dialogView.findViewById(R.id.skip);
            TextView watchNow = dialogView.findViewById(R.id.watch_now);
            final AlertDialog alertDialog = dialog.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();

            alertDialog.setCancelable(false);
            skip.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    isDialogOpen = false;
                    if (leftTime != 0) {

                        timer = new Timer(leftTime, 1000);
                        timer.start();

                    }
                }
            });
            watchNow.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    showRewardedVideo();
                    alertDialog.dismiss();
                    isDialogOpen = false;
                }
            });
        }
    }

    public void showRewardedVideo() {

        if (rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.show();
        } else if (!rewardedVideoAd.isLoaded()) {
            loadRewardedVideoAd(PlayActivity.this);
            if (rewardedVideoAd.isLoaded()) {
                rewardedVideoAd.show();
            }
        }
    }

    public static void loadRewardedVideoAd(Context context) {

        if (!rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.loadAd(context.getResources().getString(R.string.admob_Rewarded_Video_Ads), new AdRequest.Builder().build());
        }
    }

    RewardedVideoAdListener rewardedVideoAdListener = new RewardedVideoAdListener() {
        @Override
        public void onRewardedVideoAdLoaded() {
        }

        @Override
        public void onRewardedVideoCompleted() {

        }

        @Override
        public void onRewardedVideoAdOpened() {
        }

        @Override
        public void onRewardedVideoStarted() {
        }

        @Override
        public void onRewardedVideoAdClosed() {
            loadRewardedVideoAd(PlayActivity.this);
        }

        @Override
        public void onRewarded(RewardItem reward) {
            // Reward the user.
            Constant.TOTAL_COINS = (Constant.TOTAL_COINS + Constant.REWARD_COIN_VALUE);
            if (Session.isLogin(getApplicationContext()))
                Utils.UpdateCoin(getApplicationContext(), String.valueOf(Constant.TOTAL_COINS));
        }

        @Override
        public void onRewardedVideoAdLeftApplication() {
        }

        @Override
        public void onRewardedVideoAdFailedToLoad(int i) {
            if (Utils.isNetworkAvailable(PlayActivity.this)) {
                if (interstitial.isLoaded()) {
                    interstitial.show();
                    Constant.TOTAL_COINS = (Constant.TOTAL_COINS + Constant.REWARD_COIN_VALUE);
                } else {
                    interstitial = new InterstitialAd(PlayActivity.this);
                    interstitial.setAdUnitId(getString(R.string.admob_interstitial_id));
                    AdRequest adRequest = new AdRequest.Builder().build();
                    interstitial.loadAd(adRequest);
                    if (interstitial.isLoaded()) {
                        interstitial.show();
                        Constant.TOTAL_COINS = (Constant.TOTAL_COINS + Constant.REWARD_COIN_VALUE);
                    }
                }
                if (Session.isLogin(getApplicationContext()))
                    Utils.UpdateCoin(getApplicationContext(), String.valueOf(Constant.TOTAL_COINS));
            }
        }


    };
    public void SaveLevel() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            System.out.println("====response set level " + response);
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            if (!error) {
LevelActivity.levelNo=(levelNo+1);
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
                params.put(Constant.SET_LEVEL_DATA, "1");
                params.put(Constant.LEVEL, String.valueOf(levelNo+1));
                params.put(Constant.category, String.valueOf(Constant.CATE_ID));
                params.put(Constant.subCategoryId, String.valueOf(Constant.SUB_CAT_ID));
                params.put(Constant.userId, Session.getUserData(Session.USER_ID, getApplicationContext()));

                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);

    }
    public class Timer extends CountDownTimer {
        private Timer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            leftTime = millisUntilFinished;
            //   System.out.println("===  timer  " + millisUntilFinished);
            int progress = (int) (millisUntilFinished / 1000);

            if (progressTimer == null)
                progressTimer = findViewById(R.id.progressBarTwo);
            else
                progressTimer.setCurrentProgress(progress);

            //when left last 5 second we show progress color red
            if (millisUntilFinished <= 6000)
                progressTimer.SetTimerAttributes(Color.RED, Color.RED);
            else
                progressTimer.SetTimerAttributes(Color.parseColor(Constant.PROGRESS_COLOR), Color.parseColor(Constant.PROGRESS_COLOR));

        }

        @Override
        public void onFinish() {
            if (questionIndex >= questionList.size()) {
                levelCompleted();
            } else {
                playWrongSound();
                score = score - Constant.PENALTY;
                inCorrectQuestion = inCorrectQuestion + 1;
                tvScore.setText(String.valueOf(score));
                tvWrong.setText(String.valueOf(inCorrectQuestion));
                wrongProgress.setProgress(inCorrectQuestion);
                mHandler.postDelayed(mUpdateUITimerTask, 100);
                questionIndex++;
            }

        }
    }

    public void starTimer() {
        timer = new Timer(Constant.TIME_PER_QUESTION, Constant.COUNT_DOWN_TIMER);
        timer.start();
    }

    public void stopTimer() {
        if (timer != null)
            timer.cancel();
    }

    public static void displayInterstitial() {
        if (interstitial.isLoaded()) {
            interstitial.show();
        } else {
            adRequest = new AdRequest.Builder().build();
            interstitial.loadAd(adRequest);
            interstitial.show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        Utils.CheckBgMusic(PlayActivity.this);
        if (Constant.LeftTime != 0) {
            timer = new Timer(leftTime, 1000);
            timer.start();
        }
        coin_count.setText(String.valueOf(Constant.TOTAL_COINS));
        super.onResume();
    }

    @Override
    public void onPause() {
        AppController.StopSound();
        Constant.LeftTime = leftTime;
        stopTimer();
        if (textToSpeech != null)
            textToSpeech.shutdown();
        super.onPause();
    }


    @Override
    public void onDestroy() {
        leftTime = 0;
        stopTimer();
        if (textToSpeech != null)
            textToSpeech.shutdown();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.report).setVisible(false);


        if (checkLayout.getVisibility() == View.VISIBLE)
            menu.findItem(R.id.bookmark).setVisible(false);
        else {
            menu.findItem(R.id.bookmark).setVisible(true);
            final MenuItem menuItem = menu.findItem(R.id.bookmark);
            menuItem.setTitle("unmark");
            if (question != null) {
                int isfav = bookmarkDBHelper.getBookmarks(questionList.get(questionIndex).getId());

                if (isfav == question.getId()) {
                    menuItem.setIcon(R.drawable.ic_mark);
                    menuItem.setTitle("mark");
                } else {
                    menuItem.setIcon(R.drawable.ic_unmark);
                    menuItem.setTitle("unmark");
                }
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case R.id.bookmark:

                if (menuItem.getTitle().equals("unmark")) {

                    if (Session.getBoolean(Session.E_MODE, getApplicationContext())) {
                        String optionE;
                        if (options.size() == 5)
                            optionE = options.get(4).trim();
                        else
                            optionE = "";
                        bookmarkDBHelper.insertIntoDB(questionList.get(questionIndex).getId(),
                                question.getQuestion(),
                                question.getTrueAns(),
                                question.getNote(),
                                question.getImage(),
                                options.get(0).trim(),
                                options.get(1).trim(),
                                options.get(2).trim(),
                                options.get(3).trim(),
                                optionE);
                    } else {
                        bookmarkDBHelper.insertIntoDB(questionList.get(questionIndex).getId(),
                                question.getQuestion(),
                                question.getTrueAns(),
                                question.getNote(),
                                question.getImage(),
                                options.get(0).trim(),
                                options.get(1).trim(),
                                options.get(2).trim(),
                                options.get(3).trim(),
                                "");
                    }
                    menuItem.setIcon(R.drawable.ic_mark);
                    menuItem.setTitle("mark");
                } else {
                    bookmarkDBHelper.delete_id(question.getId());
                    menuItem.setIcon(R.drawable.ic_unmark);
                    menuItem.setTitle("unmark");
                }
                return true;
            case R.id.setting:
                SettingButtonMethod();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onBackPressed() {

        PlayAreaLeaveDialog();

    }
}