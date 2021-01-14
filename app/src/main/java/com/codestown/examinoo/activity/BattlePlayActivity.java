package com.codestown.examinoo.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.codestown.examinoo.Constant;
import com.codestown.examinoo.R;
import com.codestown.examinoo.helper.AppController;
import com.codestown.examinoo.helper.CircleImageView;
import com.codestown.examinoo.helper.CircleTimer;
import com.codestown.examinoo.helper.Session;
import com.codestown.examinoo.helper.TouchImageView;
import com.codestown.examinoo.helper.Utils;
import com.codestown.examinoo.model.Question;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class BattlePlayActivity extends AppCompatActivity implements View.OnClickListener {
    public static TextView btnOpt1, btnOpt2, btnOpt3, btnOpt4, btnOpt5, txtQuestion, txtQuestion1,
            p2ans_a, p2ans_b, p2ans_c, p2ans_d, p2ans_e, tvPlayer1Name, tvPlayer2Name, btnQuitGame,
            txtQuestionIndex, option_a, option_b, option_c, option_d, option_e, txtTrueQuestion, txtFalseQuestion;
    public static ArrayList<String> options;


    private final Handler mHandler = new Handler();


    public Question question;

    public Toolbar toolbar;
    public RelativeLayout playLayout;
    public Animation RightSwipe_A, RightSwipe_B, RightSwipe_C, RightSwipe_D, RightSwipe_E, Fade_in;

    CircleImageView imgPlayer1, imgPlayer2;
    DatabaseReference myGameRef;
    ImageView imgZoom, imgMic;
    RelativeLayout layout_A, layout_B, layout_C, layout_D, layout_E;

    TouchImageView imgQuestion;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    ProgressBar imgProgress, rightProgress, wrongProgress;

    private Context mContext;
    private Animation animation;
    public CircleTimer circleTimer;

    public long leftTime;
    public MyCountDownTimer timer;
    public AlertDialog quitAlertDialog;
    public TextSwitcher right_p1, right_p2, right_p01, right_p02;
    public Animation in, out;
    public TextToSpeech textToSpeech;
    public ProgressBar progressBar;
    public RelativeLayout mainLayout;
    public ScrollView mainScroll, queScroll;
    public int questionIndex = 0, correctQuestion = 0, inCorrectQuestion = 0, click = 0, textSize, preScore = 0;
    public String userId1, userId2, Player1Name, Player2Name, winner, winnerMessage,
            matchingId = "", player1Key = "", player2Key = "", optionClicked = "false",
            tts = "tts", profilePlayer1, profilePlayer2, winDialogTitle, pauseCheck = "regular";
    public ValueEventListener player1Listener, player2Listener;
    public boolean isPlayerLeft;
    public ArrayList<Question> battleQuestionList;

    public String index = "index00";
    int attendedQue = 0;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);
        mContext = BattlePlayActivity.this;
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
       setTelephoneListener();
        matchingId = getIntent().getStringExtra("gameid");

        player1Key = FirebaseAuth.getInstance().getCurrentUser().getUid();
        player2Key = getIntent().getStringExtra("opponentId");
        userId1 = getIntent().getStringExtra("user_id1");
        userId2 = getIntent().getStringExtra("user_id2");
        Player2Name = getIntent().getStringExtra("player2Name");
        profilePlayer2 = getIntent().getStringExtra("player2Profile");
        final String player1UId = player1Key, player2UId = player2Key;

        myGameRef = FirebaseDatabase.getInstance().getReference().child(Constant.DB_GAME_ROOM_NEW);

        int[] CLICKABLE = new int[]{R.id.a_layout, R.id.b_layout, R.id.c_layout, R.id.d_layout, R.id.e_layout};
        for (int i : CLICKABLE) {
            findViewById(i).setOnClickListener(this);
        }
        InitializeTTF();
        progressBar = findViewById(R.id.progressBar);
        txtQuestionIndex = findViewById(R.id.tvIndex);
        right_p1 = findViewById(R.id.right_p1);
        right_p2 = findViewById(R.id.right_p2);
        right_p01 = findViewById(R.id.right_p01);
        right_p02 = findViewById(R.id.right_p02);

        p2ans_a = findViewById(R.id.p2ans_a);
        p2ans_b = findViewById(R.id.p2ans_b);
        p2ans_c = findViewById(R.id.p2ans_c);
        p2ans_d = findViewById(R.id.p2ans_d);
        p2ans_e = findViewById(R.id.p2ans_e);

        mainScroll = findViewById(R.id.mainScroll);
        queScroll = findViewById(R.id.queScroll);
        imgProgress = findViewById(R.id.imgProgress);
        rightProgress = findViewById(R.id.rightProgress);
        wrongProgress = findViewById(R.id.wrongProgress);
        imgQuestion = findViewById(R.id.imgQuestion);

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
        imgMic = findViewById(R.id.imgMic);

        mainLayout = findViewById(R.id.main_layout);
        tvPlayer1Name = findViewById(R.id.tv_player1_name);
        tvPlayer2Name = findViewById(R.id.tv_player2_name);
        imgPlayer1 = findViewById(R.id.iv_player1_pic);
        imgPlayer2 = findViewById(R.id.iv_player2_pic);

        btnQuitGame = findViewById(R.id.btn_quit);
        imgPlayer1.setDefaultImageResId(R.drawable.ic_account);
        imgPlayer2.setDefaultImageResId(R.drawable.ic_android);
        textSize = Integer.parseInt(Session.getSavedTextSize(BattlePlayActivity.this));
        Session.removeSharedPreferencesData(BattlePlayActivity.this);

        RightSwipe_A = AnimationUtils.loadAnimation(mContext, R.anim.anim_right_a);
        RightSwipe_B = AnimationUtils.loadAnimation(mContext, R.anim.anim_right_b);
        RightSwipe_C = AnimationUtils.loadAnimation(mContext, R.anim.anim_right_c);
        RightSwipe_D = AnimationUtils.loadAnimation(mContext, R.anim.anim_right_d);
        RightSwipe_E = AnimationUtils.loadAnimation(mContext, R.anim.anim_right_e);
        Fade_in = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);


        playLayout = findViewById(R.id.innerLayout);
        playLayout.setVisibility(View.GONE);

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


        ChangeTextSize(textSize);
        circleTimer = findViewById(R.id.circleTimer);
        circleTimer.setMaxProgress(Constant.CIRCULAR_MAX_PROGRESS);
        circleTimer.setCurrentProgress(Constant.CIRCULAR_MAX_PROGRESS);
        animation = AnimationUtils.loadAnimation(BattlePlayActivity.this, R.anim.right_ans_anim); // Change alpha from fully visible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the


        if (Utils.isNetworkAvailable(BattlePlayActivity.this)) {
            init();
            getQuestionsFromJson();
        } else {
            View parentLayout = findViewById(android.R.id.content);
            Snackbar snackbar = Snackbar
                    .make(parentLayout, getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
            snackbar.show();

        }
        btnQuitGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        in = AnimationUtils.loadAnimation(this, R.anim.slide_up1);
        out = AnimationUtils.loadAnimation(this, R.anim.slide_up);


        right_p1.setFactory(mFactory);
        right_p01.setFactory(mFactory);
        right_p2.setFactory(mFactory);
        right_p02.setFactory(mFactory);

        right_p1.setCurrentText(String.valueOf(correctQuestion));
        right_p01.setCurrentText(String.valueOf(inCorrectQuestion));
        right_p2.setCurrentText(String.valueOf(correctQuestion));
        right_p02.setCurrentText(String.valueOf(inCorrectQuestion));

        right_p1.setInAnimation(in);
        right_p1.setOutAnimation(out);

        right_p01.setOutAnimation(out);
        right_p2.setInAnimation(in);
        right_p2.setOutAnimation(out);

        right_p02.setOutAnimation(out);
        imgMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech.speak(battleQuestionList.get(questionIndex).getQuestion(), TextToSpeech.QUEUE_FLUSH, null);
                tts = "ttsCall";
            }
        });
        mainScroll.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
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

    }

    public void InitializeTTF() {
        textToSpeech = new TextToSpeech(BattlePlayActivity.this, new TextToSpeech.OnInitListener() {

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

    private final Runnable mUpdateUITimerTask = new Runnable() {
        public void run() {
            progressBar.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);
            nextQuizQuestion();
        }
    };

    private void init() {


        Player1Name = Session.getUserData(Session.NAME, getApplicationContext());
        tvPlayer1Name.setText(Player1Name);
        profilePlayer1 = Session.getUserData(Session.PROFILE, getApplicationContext());
        imgPlayer1.setImageUrl(profilePlayer1, imageLoader);

        tvPlayer2Name.setText(Player2Name);
        imgPlayer2.setImageUrl(profilePlayer2, imageLoader);


        p2ans_a.setText(Player2Name);
        p2ans_b.setText(Player2Name);
        p2ans_c.setText(Player2Name);
        p2ans_d.setText(Player2Name);
        p2ans_e.setText(Player2Name);
        playLayout.setVisibility(View.VISIBLE);


        player1Listener = new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {

                        if (!isPlayerLeft) {
                            final int[] p1_que = new int[1];
                            final int[] r_1 = new int[1];
                            final int[] p2_que = new int[1];
                            final int[] r_2 = new int[1];
                            final String[] p2_sel = new String[1];
                            if (dataSnapshot.hasChild(Constant.QUESTIONS)) {
                                p1_que[0] = (int) dataSnapshot.child(Constant.QUESTIONS).getChildrenCount();

                            }
                            player2Key = getIntent().getStringExtra("opponentId");
                            player2Listener = myGameRef.child(player2Key).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.child(Constant.LEFT_BATTLE).exists()) {
                                        if ((boolean) snapshot.child(Constant.LEFT_BATTLE).getValue()) {
                                            if (quitAlertDialog == null) {
                                                showOtherUserQuitDialog();
                                            }
                                            isPlayerLeft = true;
                                        }
                                    } else {
                                        if (snapshot.child(Constant.QUESTIONS).exists()) {
                                            p2_que[0] = (int) snapshot.child(Constant.QUESTIONS).getChildrenCount();
                                            p2_sel[0] = snapshot.child(Constant.QUESTIONS).child(String.valueOf(p2_que[0] - 1)).child(Constant.SEL_ANS).getValue(String.class);
                                            if (snapshot.child(Constant.RIGHT_ANS).exists()) {
                                                r_2[0] = Integer.parseInt(snapshot.child(Constant.RIGHT_ANS).getValue().toString());
                                            }
                                        }

                                        if (p1_que[0] == p2_que[0]) {
                                            if (optionClicked.equalsIgnoreCase("true")) {
                                                if (btnOpt1.getText().toString().equalsIgnoreCase(p2_sel[0])) {
                                                    p2ans_a.setVisibility(View.VISIBLE);
                                                    assert p2_sel[0] != null;
                                                    if (p2_sel[0].equalsIgnoreCase(question.getTrueAns())) {
                                                        p2ans_a.setTextColor(getResources().getColor(R.color.wrong_dark));
                                                    } else {
                                                        p2ans_a.setTextColor(getResources().getColor(R.color.right_dark));
                                                    }

                                                } else if (btnOpt2.getText().toString().equalsIgnoreCase(p2_sel[0])) {
                                                    p2ans_b.setVisibility(View.VISIBLE);
                                                    assert p2_sel[0] != null;
                                                    if (p2_sel[0].equalsIgnoreCase(question.getTrueAns())) {
                                                        p2ans_b.setTextColor(getResources().getColor(R.color.wrong_dark));
                                                    } else {
                                                        p2ans_b.setTextColor(getResources().getColor(R.color.right_dark));
                                                    }

                                                } else if (btnOpt3.getText().toString().equalsIgnoreCase(p2_sel[0])) {
                                                    p2ans_c.setVisibility(View.VISIBLE);
                                                    assert p2_sel[0] != null;
                                                    if (p2_sel[0].equalsIgnoreCase(question.getTrueAns())) {
                                                        p2ans_c.setTextColor(getResources().getColor(R.color.wrong_dark));
                                                    } else {
                                                        p2ans_c.setTextColor(getResources().getColor(R.color.right_dark));
                                                    }

                                                } else if (btnOpt4.getText().toString().equalsIgnoreCase(p2_sel[0])) {
                                                    p2ans_d.setVisibility(View.VISIBLE);
                                                    assert p2_sel[0] != null;
                                                    if (p2_sel[0].equalsIgnoreCase(question.getTrueAns())) {
                                                        p2ans_d.setTextColor(getResources().getColor(R.color.wrong_dark));
                                                    } else {
                                                        p2ans_d.setTextColor(getResources().getColor(R.color.right_dark));
                                                    }

                                                } else if (btnOpt5.getText().toString().equalsIgnoreCase(p2_sel[0])) {
                                                    p2ans_e.setVisibility(View.VISIBLE);
                                                    assert p2_sel[0] != null;
                                                    if (p2_sel[0].equalsIgnoreCase(question.getTrueAns())) {
                                                        p2ans_e.setTextColor(getResources().getColor(R.color.wrong_dark));
                                                    } else {
                                                        p2ans_e.setTextColor(getResources().getColor(R.color.right_dark));
                                                    }

                                                }
                                            }
                                            if (dataSnapshot.child(Constant.RIGHT_ANS).exists()) {
                                                r_1[0] = Integer.parseInt(dataSnapshot.child(Constant.RIGHT_ANS).getValue().toString());
                                            }

                                            if (r_2[0] == battleQuestionList.size()) {
                                                right_p02.setText("");
                                            }

                                            if (preScore != r_2[0]) {
                                                right_p2.setText(String.valueOf(r_2[0]));
                                                preScore = r_2[0];
                                            }
                                            if (p1_que[0] == battleQuestionList.size()) {
                                                if (attendedQue == battleQuestionList.size()) {
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (r_1[0] > r_2[0]) {
                                                                winnerMessage = Player1Name + getString(R.string.msg_win_battle);
                                                                winner = "you";
                                                                winDialogTitle = getString(R.string.congrats);
                                                                showWinnerDialog();
                                                                SetBattleStatistics("0", userId1);

                                                            } else if (r_2[0] > r_1[0]) {
                                                                winnerMessage = Player2Name + getString(R.string.msg_opponent_win_battle);
                                                                winner = Player2Name;
                                                                winDialogTitle = getString(R.string.next_time);
                                                                showWinnerDialog();
                                                            } else {

                                                                showResetGameAlert();
                                                                SetBattleStatistics("1", "");
                                                            }

                                                            //btnQuitGame.setText("GO BACK");
                                                        }
                                                    }, 1500);
                                                }
                                            } else {
                                                if (!index.equalsIgnoreCase("index" + questionIndex))
                                                    mHandler.postDelayed(mUpdateUITimerTask, 1000);
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }


    public void getQuestionsFromJson() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {
                            battleQuestionList = new ArrayList<>();
                            battleQuestionList.clear();
                            System.out.println("====battle ques " + response);
                            JSONObject jsonObject = new JSONObject(response);
                            String error = jsonObject.getString(Constant.ERROR);
                            if (error.equalsIgnoreCase("false")) {
                                JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                                battleQuestionList.addAll(Utils.getQuestions(jsonArray, BattlePlayActivity.this));
                                Constant.MAX_QUESTION_PER_BATTLE = battleQuestionList.size();
                                myGameRef.child(player1Key).addValueEventListener(player1Listener);
                                rightProgress.setMax(battleQuestionList.size());
                                wrongProgress.setMax(battleQuestionList.size());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.accessKey, Constant.accessKeyValue);
                params.put(Constant.getRandomQuestion, "1");
                params.put(Constant.GAME_ROOM_KEY, matchingId);
                if (Session.getBoolean(Session.LANG_MODE, getApplicationContext()))
                    params.put(Constant.LANGUAGE_ID, Session.getCurrentLanguage(getApplicationContext()));
                System.out.println("====battle params " + params.toString());
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void SetBattleStatistics(final String isDraw, final String winnerId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.accessKey, Constant.accessKeyValue);
                params.put(Constant.SET_BATTLE_STATISTICS, "1");
                params.put(Constant.USER_ID1, userId1);
                params.put(Constant.USER_ID2, userId2);
                params.put(Constant.WINNER_ID, winnerId);
                params.put(Constant.IS_DRAWN, isDraw);
                System.out.println("===params " + params);
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void onClick(View v) {
        if (questionIndex < battleQuestionList.size()) {
            question = battleQuestionList.get(questionIndex);
            layout_A.setClickable(false);
            layout_B.setClickable(false);
            layout_C.setClickable(false);
            layout_D.setClickable(false);
            layout_E.setClickable(false);



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


            optionClicked = "true";

        }
    }

    public void AddReview(Question question, TextView tvBtnOpt, RelativeLayout layout) {
        layout_A.setClickable(false);
        layout_B.setClickable(false);
        layout_C.setClickable(false);
        layout_D.setClickable(false);
        layout_E.setClickable(false);

        if (tvBtnOpt.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            layout.setBackgroundResource(R.drawable.right_gradient);
            correctQuestion = correctQuestion + 1;
            rightProgress.setProgress(correctQuestion);
            txtTrueQuestion.setText(String.valueOf(correctQuestion));
            addScore(tvBtnOpt.getText().toString().trim());

        } else {
            layout.setBackgroundResource(R.drawable.wrong_gradient);
            inCorrectQuestion = inCorrectQuestion + 1;
            wrongProgress.setProgress(inCorrectQuestion);
            txtFalseQuestion.setText(String.valueOf(inCorrectQuestion));
            WrongQuestion(tvBtnOpt.getText().toString().trim());
        }

        question.setSelectedAns(tvBtnOpt.getText().toString());
        if (Constant.QUICK_ANSWER_ENABLE.equals("1"))
            RightAnswerBackgroundSet();
        question.setAttended(true);
        attendedQue = (attendedQue + 1);
        questionIndex++;

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

    private void showOtherUserQuitDialog() {
        stopTimer();
        try {
            pauseCheck = "win";
            final AlertDialog.Builder dialog = new AlertDialog.Builder(BattlePlayActivity.this);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View dialogView = inflater.inflate(R.layout.dialog_reset_game, null);
            dialog.setView(dialogView);
            dialog.setCancelable(false);
            quitAlertDialog = dialog.create();

            TextView tvMessage = dialogView.findViewById(R.id.tv_message);
            TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
            tvTitle.setText(Player1Name);
            TextView btnok = dialogView.findViewById(R.id.btn_ok);

            tvMessage.setText("You Win!! \n" + Player2Name + getString(R.string.leave_battle_txt));
            btnok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myGameRef.child(player1Key).removeValue();
                    myGameRef.child(player2Key).removeValue();
                    DestroyKey(matchingId);
                    quitAlertDialog.dismiss();
                    finish();
                }
            });


            Objects.requireNonNull(quitAlertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            quitAlertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showQuitGameAlertDialog() {
        try {
            pauseCheck = "win";
            stopTimer();
            final AlertDialog.Builder dialog1 = new AlertDialog.Builder(BattlePlayActivity.this);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View dialogView = inflater.inflate(R.layout.dialog_leave_battle, null);
            dialog1.setView(dialogView);
            dialog1.setCancelable(true);

            final AlertDialog alertDialog = dialog1.create();
            TextView tvMessage = dialogView.findViewById(R.id.tv_message);
            TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
            tvTitle.setText(Player1Name);
            TextView btnok = dialogView.findViewById(R.id.btn_ok);
            TextView btnNo = dialogView.findViewById(R.id.btnNo);
            tvMessage.setText(getString(R.string.msg_alert_leave));
            btnok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myGameRef.child(player1Key).child(Constant.LEFT_BATTLE).setValue(true);
                    alertDialog.dismiss();
                    finish();

                }
            });
            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    timer = new MyCountDownTimer(leftTime, 1000);
                    timer.start();
                    alertDialog.dismiss();
                }
            });
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.setCancelable(false);
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void DestroyKey(final String roomKey) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            String error = jsonObject.getString(Constant.ERROR);

                            if (error.equalsIgnoreCase("false")) {
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.accessKey, Constant.accessKeyValue);
                params.put(Constant.getRandomQuestion, "1");
                params.put(Constant.GAME_ROOM_KEY, roomKey);
                params.put(Constant.DESTROY_GAME_KEY, "1");
                System.out.println("===destroy params " + params.toString());
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void showWinnerDialog() {
        try {
            pauseCheck = "win";
            stopTimer();

            clearQuestionList();
            final AlertDialog.Builder dialog1 = new AlertDialog.Builder(BattlePlayActivity.this);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View dialogView = inflater.inflate(R.layout.winner_dialog, null);
            dialog1.setView(dialogView);
            dialog1.setCancelable(false);
            DestroyKey(matchingId);
            final AlertDialog alertDialog = dialog1.create();
            TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
            TextView tvMessage = dialogView.findViewById(R.id.tv_message);
            Button btnok = dialogView.findViewById(R.id.btn_ok);
            Button btnReBattle = dialogView.findViewById(R.id.btnReBattle);
            NetworkImageView winnerImg = dialogView.findViewById(R.id.winnerImg);
            winnerImg.setDefaultImageResId(R.drawable.ic_profile);
            if (winner.equals("you")) {
                tvTitle.setText(getString(R.string.congrats));
                tvMessage.setText(winnerMessage);
                winnerImg.setImageUrl(profilePlayer1, imageLoader);
            } else {
                tvTitle.setText(getString(R.string.next_time));
                tvMessage.setText(winnerMessage);
                winnerImg.setImageUrl(profilePlayer2, imageLoader);
            }

            btnok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myGameRef.child(player1Key).removeValue();
                    resetValues();
                    alertDialog.dismiss();
                    finish();
                }
            });

            btnReBattle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myGameRef.child(player1Key).removeValue();
                    Intent intentReBattle = new Intent(BattlePlayActivity.this, SearchPlayerActivity.class);
                    intentReBattle.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentReBattle);
                    alertDialog.dismiss();
                    finish();
                }
            });
            alertDialog.show();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showResetGameAlert() {
        pauseCheck = "win";
        clearQuestionList();
        try {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_reset_game);
            dialog.setCancelable(false);
            TextView tvMessage = dialog.findViewById(R.id.tv_message);
            TextView btnok = dialog.findViewById(R.id.btn_ok);
            tvMessage.setText(getString(R.string.msg_draw_game));
            btnok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myGameRef.child(player1Key).removeValue();
                    DestroyKey(matchingId);
                    finish();
                    dialog.dismiss();
                }
            });
            dialog.show();
            Window window = dialog.getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void clearQuestionList() {
        if (battleQuestionList != null)
            battleQuestionList.clear();


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

    private ViewSwitcher.ViewFactory mFactory = new ViewSwitcher.ViewFactory() {

        @Override
        public View makeView() {
            // Create a new TextView
            TextView t = new TextView(BattlePlayActivity.this);
            t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
            t.setTextAppearance(BattlePlayActivity.this, android.R.style.TextAppearance_Large);
            t.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
            return t;
        }
    };


    public class MyCountDownTimer extends CountDownTimer {

        private MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            leftTime = millisUntilFinished;
            // System.out.println("=======timer " + isPlayerLeft + " === " + millisUntilFinished);
            int progress = (int) (millisUntilFinished / 1000);

            if (circleTimer == null) {
                circleTimer = findViewById(R.id.circleTimer);
            } else {
                circleTimer.setCurrentProgress(progress);
            }
            //when left last 5 second we show progress color red
            if (millisUntilFinished <= 6000) {
                circleTimer.SetTimerAttributes(Color.RED, Color.RED);
            } else {
                circleTimer.SetTimerAttributes(Color.parseColor(Constant.PROGRESS_COLOR), Color.parseColor(Constant.PROGRESS_COLOR));
            }
        }

        @Override
        public void onFinish() {
            if (questionIndex <= battleQuestionList.size()) {
                if (!isPlayerLeft)
                    if (optionClicked.equals("false")) {
                        layout_A.setClickable(false);
                        layout_B.setClickable(false);
                        layout_C.setClickable(false);
                        layout_D.setClickable(false);
                        layout_E.setClickable(false);
                        attendedQue = (attendedQue + 1);
                        WrongQuestion("");
                        questionIndex++;
                    }
            }

        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        resetValues();
        UpdateOnlineStatus();
        System.out.println("===stop call");
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
        if (tts.equals("tts")) {
            if (pauseCheck.equals("regular")) {
                myGameRef.child(player1Key).child(Constant.LEFT_BATTLE).setValue(true);
                UpdateOnlineStatus();
                resetValues();
                finish();

            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UpdateOnlineStatus();
        resetValues();
        finish();
    }

    public void resetValues() {

        clearQuestionList();
        player1Key = "";
        player2Key = "";
        matchingId = "";
        leftTime = 0;
        stopTimer();
        if (textToSpeech != null) {
            textToSpeech.shutdown();
        }
        if (quitAlertDialog != null) {
            if (quitAlertDialog.isShowing()) {
                quitAlertDialog.dismiss();
            }
        }
    }

    public void UpdateOnlineStatus() {
        if (myGameRef != null && player1Listener != null) {
            myGameRef.child(player1Key).removeEventListener(player1Listener);

        }
        if (player2Listener != null)
            myGameRef.child(player2Key).removeEventListener(player2Listener);
    }

    private void addScore(final String sel_ans) {
        rightSound();
        if (correctQuestion == battleQuestionList.size()) {
            right_p01.setText("");
        }
        right_p1.setText(String.valueOf(correctQuestion));
        Map<String, Object> taskMap = new HashMap<String, Object>();
        taskMap.put(Constant.RIGHT_ANS, String.valueOf(correctQuestion));
        taskMap.put(Constant.QUESTIONS + "/" + questionIndex + "/" + Constant.SEL_ANS, sel_ans);
        myGameRef.child(player1Key).updateChildren(taskMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        System.out.println("success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Map<String, Object> taskMap = new HashMap<String, Object>();
                        taskMap.put(Constant.RIGHT_ANS, String.valueOf(correctQuestion));
                        taskMap.put(Constant.QUESTIONS + "/" + questionIndex + "/" + Constant.SEL_ANS, sel_ans);
                        myGameRef.child(player1Key).updateChildren(taskMap);
                    }
                });


    }

    private void WrongQuestion(final String sel_ans) {
        setAgain();
        playWrongSound();
        Map<String, Object> taskMap = new HashMap<String, Object>();
        // taskMap.put(Constant.RIGHT_ANS, correctQuestion);
        taskMap.put(Constant.QUESTIONS + "/" + questionIndex + "/" + Constant.SEL_ANS, sel_ans);
        myGameRef.child(player1Key).updateChildren(taskMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        System.out.println("success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("===== wrong fail");
                        Map<String, Object> taskMap = new HashMap<String, Object>();
                        taskMap.put(Constant.QUESTIONS + "/" + questionIndex + "/" + Constant.SEL_ANS, sel_ans);
                        myGameRef.child(player1Key).updateChildren(taskMap);
                    }
                });

    }

    /*
     * Save score in Preferences
     */
    //play sound when answer is correct
    public void rightSound() {
        if (Session.getSoundEnableDisable(mContext)) {
            Utils.setrightAnssound(mContext);
        }
        if (Session.getVibration(mContext)) {
            Utils.vibrate(mContext, Utils.VIBRATION_DURATION);
        }
    }

    //play sound when answer is incorrect
    private void playWrongSound() {
        if (Session.getSoundEnableDisable(mContext)) {
            Utils.setwronAnssound(mContext);
        }
        if (Session.getVibration(mContext)) {
            Utils.vibrate(mContext, Utils.VIBRATION_DURATION);
        }
    }

    //set progress again after next question
    private void setAgain() {

        p2ans_a.setVisibility(View.GONE);
        p2ans_b.setVisibility(View.GONE);
        p2ans_c.setVisibility(View.GONE);
        p2ans_d.setVisibility(View.GONE);
        p2ans_e.setVisibility(View.GONE);


    }

    private void nextQuizQuestion() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
        optionClicked = "false";
        tts = "tts";
        setAgain();
        stopTimer();
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

        if (questionIndex < battleQuestionList.size()) {
            btnOpt1.startAnimation(RightSwipe_A);
            btnOpt2.startAnimation(RightSwipe_B);
            btnOpt3.startAnimation(RightSwipe_C);
            btnOpt4.startAnimation(RightSwipe_D);
            btnOpt5.startAnimation(RightSwipe_E);
            txtQuestion1.startAnimation(Fade_in);
            index = "index" + questionIndex;
            timer = new MyCountDownTimer(Constant.TIME_PER_QUESTION, Constant.COUNT_DOWN_TIMER);
            timer.start();
            question = battleQuestionList.get(questionIndex);
            int temp = questionIndex;
            imgQuestion.resetZoom();
            txtQuestionIndex.setText(++temp + "");
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

            btnOpt1.setText(options.get(0).trim());
            btnOpt2.setText(options.get(1).trim());
            btnOpt3.setText(options.get(2).trim());
            btnOpt4.setText(options.get(3).trim());
            if (Session.getBoolean(Session.E_MODE, getApplicationContext()))
                if (options.size() == 5)
                    btnOpt5.setText(options.get(4).trim());

        }


    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public void setTelephoneListener() {
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    onPause();
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };

        TelephonyManager  telephony = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onBackPressed() {
        showQuitGameAlertDialog();

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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.setting:
                pauseCheck = "setting";
                Intent intent = new Intent(BattlePlayActivity.this, SettingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
