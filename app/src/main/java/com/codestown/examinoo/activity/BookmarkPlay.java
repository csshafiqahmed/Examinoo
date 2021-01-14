package com.codestown.examinoo.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.codestown.examinoo.Constant;
import com.codestown.examinoo.R;
import com.codestown.examinoo.helper.AppController;
import com.codestown.examinoo.helper.CircleTimer;
import com.codestown.examinoo.helper.Session;
import com.codestown.examinoo.helper.TouchImageView;
import com.codestown.examinoo.helper.Utils;
import com.codestown.examinoo.model.Question;

import java.util.ArrayList;
import java.util.Collections;

public class BookmarkPlay extends AppCompatActivity implements View.OnClickListener {
    public Animation RightSwipe_A, RightSwipe_B, RightSwipe_C, RightSwipe_D, RightSwipe_E, Fade_in;
    public int questionIndex = 0,
            correctQuestion = 0,
            inCorrectQuestion = 0;

    public TextView txtQuestion, txtQuestion1,
            option_a, option_b, option_c, option_d, option_e,
            btnOpt1, btnOpt2, btnOpt3, btnOpt4, btnOpt5, txtTrueQuestion, txtFalseQuestion;
    public RelativeLayout playLayout, checkLayout;
    public Button btnTry;
    public RelativeLayout layout_A, layout_B, layout_C, layout_D, layout_E;
    private final Handler mHandler = new Handler();
    public static CircleTimer progressTimer;
    public static Timer timer;
    public static ArrayList<String> options;

    public static long leftTime = 0;
    public ArrayList<Question> questionList;
    public Question question;
    public TouchImageView imgQuestion;
    public ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    public ProgressBar imgProgress, rightProgress, wrongProgress;
    public ImageView imgZoom;
    int click = 0;
    private Animation animation;
    public TextView tvNoConnection, tvIndex;
    public TextView btnAnswer;
    public String trueOption;
    public Toolbar toolbar;
    public ScrollView mainScroll, queScroll;
    public TextToSpeech textToSpeech;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark_play);

        final int[] CLICKABLE = new int[]{R.id.a_layout, R.id.b_layout, R.id.c_layout, R.id.d_layout, R.id.e_layout};
        for (int i : CLICKABLE) {
            findViewById(i).setOnClickListener(this);
        }
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.bookmark_play1));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        InitializeTTF();
        questionList = BookmarkList.bookmarks;
        RightSwipe_A = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_right_a);
        RightSwipe_B = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_right_b);
        RightSwipe_C = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_right_c);
        RightSwipe_D = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_right_d);
        RightSwipe_E = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_right_e);
        Fade_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        playLayout = findViewById(R.id.innerLayout);
        playLayout.setVisibility(View.GONE);
        progressTimer = findViewById(R.id.progressBarTwo);
        progressTimer.setMaxProgress(Constant.CIRCULAR_MAX_PROGRESS);
        progressTimer.setCurrentProgress(Constant.CIRCULAR_MAX_PROGRESS);
        tvIndex = findViewById(R.id.tvIndex);
        mainScroll = findViewById(R.id.mainScroll);
        queScroll = findViewById(R.id.queScroll);
        imgProgress = findViewById(R.id.imgProgress);
        rightProgress = findViewById(R.id.rightProgress);
        wrongProgress = findViewById(R.id.wrongProgress);
        imgQuestion = findViewById(R.id.imgQuestion);

        checkLayout = findViewById(R.id.checkLayout);
        btnTry = findViewById(R.id.btnTry);
        btnAnswer = findViewById(R.id.btnAnswer);
        tvNoConnection = findViewById(R.id.tvNoConnection);
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

        imgZoom = findViewById(R.id.imgZoom);

        txtTrueQuestion = findViewById(R.id.txtTrueQuestion);
        txtTrueQuestion.setText("0");
        txtFalseQuestion = findViewById(R.id.txtFalseQuestion);
        txtFalseQuestion.setText("0");
        txtQuestion = findViewById(R.id.txtQuestion);
        txtQuestion1 = findViewById(R.id.txtQuestion1);
        layout_A = findViewById(R.id.a_layout);
        layout_B = findViewById(R.id.b_layout);
        layout_C = findViewById(R.id.c_layout);
        layout_D = findViewById(R.id.d_layout);
        layout_E = findViewById(R.id.e_layout);
        if (Session.getBoolean(Session.E_MODE, getApplicationContext()))
            layout_E.setVisibility(View.VISIBLE);


        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_ans_anim); // Change alpha from fully visible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the

        rightProgress.setMax(questionList.size());
        wrongProgress.setMax(questionList.size());

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
        if (Utils.isNetworkAvailable(BookmarkPlay.this)) {

            playLayout.setVisibility(View.VISIBLE);
            nextQuizQuestion();
            checkLayout.setVisibility(View.GONE);
        } else {
            playLayout.setVisibility(View.GONE);
            checkLayout.setVisibility(View.VISIBLE);
        }
        btnTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnAnswer.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if (question.getTrueAns().equals(options.get(0).trim())) {
                    trueOption = "A";
                } else if (question.getTrueAns().equals(options.get(1).trim())) {
                    trueOption = "B";
                } else if (question.getTrueAns().equals(options.get(2).trim())) {
                    trueOption = "C";
                } else if (question.getTrueAns().equals(options.get(3).trim())) {
                    trueOption = "D";
                } else if (Session.getBoolean(Session.E_MODE, getApplicationContext())) {
                    if (question.getTrueAns().equals(options.get(4).trim())) {
                        trueOption = "E";
                    }
                }
                btnAnswer.setText(getString(R.string.true_ans) + trueOption);
            }
        });
    }

    public void InitializeTTF() {
        textToSpeech = new TextToSpeech(BookmarkPlay.this, new TextToSpeech.OnInitListener() {

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

    @SuppressLint("SetTextI18n")
    private void nextQuizQuestion() {
        if (textToSpeech != null)
            textToSpeech.stop();
        stopTimer();
        starTimer();

        Constant.LeftTime = 0;
        leftTime = 0;
        if (questionIndex >= questionList.size()) {
            CompleteQuestions();
        }
        btnAnswer.setText("Show Answer");
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
                imgZoom.setOnClickListener(new View.OnClickListener() {
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
            options = new ArrayList<>();
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
            } else {
                Collections.shuffle(options);
                option_a.setVisibility(View.VISIBLE);
                option_b.setVisibility(View.VISIBLE);
                layout_C.setVisibility(View.VISIBLE);
                layout_D.setVisibility(View.VISIBLE);
                btnOpt1.setGravity(Gravity.NO_GRAVITY);
                btnOpt2.setGravity(Gravity.NO_GRAVITY);
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
            if (Session.getBoolean(Session.E_MODE, getApplicationContext()))
                if (options.size() == 5)
                    btnOpt4.setText(Html.fromHtml(options.get(4).trim()));

        }
    }


    private final Runnable mUpdateUITimerTask = new Runnable() {
        public void run() {
            if (getApplicationContext() != null) {
                nextQuizQuestion();
            }
        }
    };


    public void CheckSound() {
        if (Session.getSoundEnableDisable(getApplicationContext())) {
            Utils.backSoundonclick(getApplicationContext());
        }
        if (Session.getVibration(getApplicationContext())) {
            Utils.vibrate(getApplicationContext(), Utils.VIBRATION_DURATION);
        }
    }

    public void SettingButtonMethod() {
        CheckSound();
        stopTimer();
        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.open_next, R.anim.close_next);
    }


    public class Timer extends CountDownTimer {

        private Timer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            leftTime = millisUntilFinished;

            int progress = (int) (millisUntilFinished / 1000);
            if (progressTimer == null) {
                progressTimer = findViewById(R.id.progressBarTwo);
            } else {
                progressTimer.setCurrentProgress(progress);
            }
            //when left last 5 second we show progress color red
            if (millisUntilFinished <= 6000) {
                progressTimer.SetTimerAttributes(Color.RED, Color.RED);
            } else {
                progressTimer.SetTimerAttributes(Color.parseColor(Constant.PROGRESS_COLOR), Color.parseColor(Constant.PROGRESS_COLOR));
            }
        }

        @Override
        public void onFinish() {
            if (questionIndex >= questionList.size()) {
                CompleteQuestions();
            } else {


                playWrongSound();
                inCorrectQuestion = inCorrectQuestion + 1;
                txtFalseQuestion.setText(String.valueOf(inCorrectQuestion));
                wrongProgress.setProgress(inCorrectQuestion);
                mHandler.postDelayed(mUpdateUITimerTask, 100);
                questionIndex++;
            }

        }
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
            correctQuestion = correctQuestion + 1;
            rightProgress.setProgress(correctQuestion);
            txtTrueQuestion.setText(String.valueOf(correctQuestion));
        } else {
            playWrongSound();
            layout.setBackgroundResource(R.drawable.wrong_gradient);
            inCorrectQuestion = inCorrectQuestion + 1;
            wrongProgress.setProgress(inCorrectQuestion);
            txtFalseQuestion.setText(String.valueOf(inCorrectQuestion));
        }

        question.setSelectedAns(tvBtnOpt.getText().toString());
        if (Constant.QUICK_ANSWER_ENABLE.equals("1"))
            RightAnswerBackgroundSet();
        question.setAttended(true);
        stopTimer();
        questionIndex++;
        mHandler.postDelayed(mUpdateUITimerTask, 1000);

    }

    public void rightSound() {
        if (Session.getSoundEnableDisable(BookmarkPlay.this))
            Utils.setrightAnssound(BookmarkPlay.this);

        if (Session.getVibration(BookmarkPlay.this))
            Utils.vibrate(BookmarkPlay.this, Utils.VIBRATION_DURATION);

    }

    //play sound when answer is incorrect
    private void playWrongSound() {
        if (Session.getSoundEnableDisable(BookmarkPlay.this))
            Utils.setwronAnssound(BookmarkPlay.this);

        if (Session.getVibration(BookmarkPlay.this))
            Utils.vibrate(BookmarkPlay.this, Utils.VIBRATION_DURATION);

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
            Constant.LeftTime = 0;
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

    public void starTimer() {
        timer = new Timer(Constant.TIME_PER_QUESTION, Constant.COUNT_DOWN_TIMER);
        timer.start();
    }

    public void stopTimer() {
        if (timer != null)
            timer.cancel();
    }

    @Override
    public void onResume() {
        if (Constant.LeftTime != 0) {
            timer = new Timer(leftTime, 1000);
            timer.start();
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        Constant.LeftTime = 0;
        leftTime = 0;
        if (textToSpeech != null)
            textToSpeech.shutdown();
        stopTimer();
        super.onDestroy();
    }

    public void CompleteQuestions() {

        playLayout.setVisibility(View.GONE);
        checkLayout.setVisibility(View.VISIBLE);
        tvNoConnection.setText(getString(R.string.all_complete_msg));

    }


    @Override
    public void onPause() {
        Constant.LeftTime = leftTime;
        stopTimer();
        if (textToSpeech != null)
            textToSpeech.shutdown();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.bookmark).setVisible(false);
        menu.findItem(R.id.report).setVisible(false);
        //  menu.findItem(R.id.setting).setVisible(true);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.setting:
                SettingButtonMethod();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        stopTimer();
        Constant.LeftTime = 0;
        leftTime = 0;
        finish();
        super.onBackPressed();
    }
}
