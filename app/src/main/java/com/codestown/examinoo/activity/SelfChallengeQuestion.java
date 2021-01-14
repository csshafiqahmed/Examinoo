package com.codestown.examinoo.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.codestown.examinoo.Constant;
import com.codestown.examinoo.R;
import com.codestown.examinoo.fragment.QuestionFragment;
import com.codestown.examinoo.helper.AppController;
import com.codestown.examinoo.helper.Session;
import com.codestown.examinoo.helper.Utils;
import com.codestown.examinoo.model.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.codestown.examinoo.activity.MainActivity.bookmarkDBHelper;

public class SelfChallengeQuestion extends AppCompatActivity {
    Toolbar toolbar;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    TextView tvTime, tvSubmit;
    String type, id, fromQue, limit;
    public static ImageView imgMark;
    public static ArrayList<Question> questionList;
    public static long leftTime, resumeTime;
    public static Timer timer;
    long challengeTime;
    ImageView imgNext;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_challenge_question);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.self_challenge));
        viewPager = findViewById(R.id.viewPager);
        tvTime = findViewById(R.id.tvTime);
        tvSubmit = findViewById(R.id.tvSubmit);

        imgNext = findViewById(R.id.imgNext);
        imgMark = findViewById(R.id.imgBookmark);
        progressBar = findViewById(R.id.progressBar);
        id = getIntent().getStringExtra("id");
        fromQue = getIntent().getStringExtra("type");
        limit = getIntent().getStringExtra("limit");
        imgMark.setTag("unmark");
        challengeTime = (getIntent().getIntExtra("time", 1) * 60 * 1000);
        getQuestionsFromJson();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (questionList != null) {
                    setBookmark();
                }
            }

            @Override
            public void onPageSelected(int position) {

                if (position == (questionList.size() - 1)) {
                    imgNext.setVisibility(View.GONE);
                } else {
                    imgNext.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        Utils.loadAd(getApplicationContext());
    }

    public void SubmitTest(View view) {
        resumeTime = leftTime;
        SubmitDialog();
    }

    public void setBookmark() {
        try {
            int isfav = bookmarkDBHelper.getBookmarks(questionList.get(viewPager.getCurrentItem()).getId());
            if (isfav == questionList.get(viewPager.getCurrentItem()).getId()) {

                imgMark.setImageResource(R.drawable.ic_mark);
                imgMark.setTag("mark");
            } else {
                imgMark.setImageResource(R.drawable.ic_unmark);
                imgMark.setTag("unmark");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void PreviousQuestion(View view) {
        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
    }

    public void NextQuestion(View view) {
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        if (viewPager.getCurrentItem() == (questionList.size() - 1)) {
            imgNext.setVisibility(View.GONE);
        } else {
            imgNext.setVisibility(View.VISIBLE);
        }
    }

    public void SaveAsBookmark(View view) {
        Question review = questionList.get(viewPager.getCurrentItem());
        ArrayList<String> options = questionList.get(viewPager.getCurrentItem()).getOptions();
        if (imgMark.getTag().equals("unmark")) {
            String solution = questionList.get(viewPager.getCurrentItem()).getNote();
            if (Session.getBoolean(Session.E_MODE, getApplicationContext())) {
                String optionE;
                if (options.size() == 5)
                    optionE = options.get(4).trim();
                else
                    optionE = "";
                MainActivity.bookmarkDBHelper.insertIntoDB(review.getId(),
                        review.getQuestion(),
                        review.getTrueAns(),
                        solution,
                        review.getImage(),
                        options.get(0).trim(),
                        options.get(1).trim(),
                        options.get(2).trim(),
                        options.get(3).trim(),
                        optionE);
            } else {
                MainActivity.bookmarkDBHelper.insertIntoDB(review.getId(),
                        review.getQuestion(),
                        review.getTrueAns(),
                        solution,
                        review.getImage(),
                        options.get(0).trim(),
                        options.get(1).trim(),
                        options.get(2).trim(),
                        options.get(3).trim(),
                        "");
            }
            imgMark.setImageResource(R.drawable.ic_mark);
            imgMark.setTag("mark");
        } else {
            MainActivity.bookmarkDBHelper.delete_id(questionList.get(viewPager.getCurrentItem()).getId());
            imgMark.setImageResource(R.drawable.ic_unmark);
            imgMark.setTag("unmark");
        }
    }


    public void getQuestionsFromJson() {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println("==== self " + response);
                            progressBar.setVisibility(View.VISIBLE);
                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean(Constant.ERROR);
                            if (!error) {
                                JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                                questionList = new ArrayList<>();
                                questionList.addAll(Utils.getQuestions(jsonArray, SelfChallengeQuestion.this)) ;
                                viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), questionList);
                                viewPager.setAdapter(viewPagerAdapter);
                                starTimer();
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
                params.put(Constant.getSelfChallengeQuestions, "1");
                params.put(Constant.LIMIT, limit);
                if (fromQue.equals("cate"))
                    params.put(Constant.category, "" + id);
                else if (fromQue.equals("subCate"))
                    params.put(Constant.subCategoryId, "" + id);
                if (Session.getBoolean(Session.LANG_MODE, getApplicationContext()))
                    params.put(Constant.LANGUAGE_ID, Session.getCurrentLanguage(getApplicationContext()));
                System.out.println("======params " + params.toString());
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    public void starTimer() {
        timer = new Timer(challengeTime, 1000);
        timer.start();
    }

    public void stopTimer() {
        if (timer != null)
            timer.cancel();
    }

    public void BottomResult(View view) {
        BottomSheetDialog();
    }

    public void BottomSheetDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(SelfChallengeQuestion.this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog, null);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(SelfChallengeQuestion.this, 10));

        ImageView imgClose = view.findViewById(R.id.imgClose);
        TextView tvAnswered = view.findViewById(R.id.tvAnswered);
        TextView tvNotAnswered = view.findViewById(R.id.tvNotAnswered);

        tvAnswered.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_answerd, 0, 0, 0);
        tvNotAnswered.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_not_answerd, 0, 0, 0);

        bottomSheetDialog.setContentView(view);

        BottomAdapter adapter = new BottomAdapter(questionList, SelfChallengeQuestion.this, bottomSheetDialog, viewPager);
        recyclerView.setAdapter(adapter);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.show();
        FrameLayout bottomSheet = bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        assert bottomSheet != null;
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);


    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        public ArrayList<Question> questionList;

        public ViewPagerAdapter(FragmentManager fm, ArrayList<Question> questionList) {
            super(fm);
            this.questionList = questionList;
        }

        @Override
        public Fragment getItem(int position) {
            return QuestionFragment.newInstance(position, questionList);
        }

        @Override
        public int getCount() {
            return questionList.size();
        }

    }

    public void SubmitDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(SelfChallengeQuestion.this);

        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.submit_dialog, null);
        dialog.setView(dialogView);
        TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView btnYes = dialogView.findViewById(R.id.btnYes);
        TextView btnNo = dialogView.findViewById(R.id.btnNo);
        tvMessage.setText(getString(R.string.self_challenge_sub_msg));
        final AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
             /*   if (resumeTime != 0) {
                    timer = new Timer(resumeTime, 1000);
                    timer.start();
                }*/
            }
        });
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTimer();
                alertDialog.dismiss();
                Constant.CHALLENGE_TIME = challengeTime;
                Constant.TAKE_TIME = (challengeTime - resumeTime);
                resumeTime = 0;
                leftTime = 0;
                challengeTime = 0;
                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void PlayAreaLeaveDialog() {
        resumeTime = leftTime;
        stopTimer();
        final AlertDialog.Builder dialog = new AlertDialog.Builder(SelfChallengeQuestion.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_leave_test, null);
        dialog.setView(dialogView);
        Button btnLeave = dialogView.findViewById(R.id.btnLeave);
        Button btnResume = dialogView.findViewById(R.id.btnResume);

        final AlertDialog alertDialog = dialog.create();
        Utils.setDialogBg(alertDialog);
        alertDialog.show();

        alertDialog.setCancelable(false);
        btnLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resumeTime = 0;
                leftTime = 0;
                challengeTime = 0;
                stopTimer();
                alertDialog.dismiss();
                finish();
            }
        });


        btnResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                if (resumeTime != 0) {
                    timer = new Timer(resumeTime, Constant.COUNT_DOWN_TIMER);
                    timer.start();
                }
            }
        });
        alertDialog.show();
    }

    public class Timer extends CountDownTimer {

        private Timer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @SuppressLint({"SetTextI18n", "DefaultLocale"})
        @Override
        public void onTick(long millisUntilFinished) {
            leftTime = millisUntilFinished;

            long totalSecs = (long) (millisUntilFinished / 1000.0);
            long minutes = (totalSecs / 60);
            long seconds = totalSecs % 60;
            tvTime.setText("" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
        }

        @Override
        public void onFinish() {
            stopTimer();
            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onPause() {
        resumeTime = leftTime;
        stopTimer();
        AppController.StopSound();
        super.onPause();
    }

    @Override
    protected void onResume() {
        AppController.playSound();
        if (resumeTime != 0) {
            timer = new Timer(resumeTime, 1000);
            timer.start();
        }
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        PlayAreaLeaveDialog();
    }

}
