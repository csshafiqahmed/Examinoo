package com.codestown.examinoo.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.codestown.examinoo.Constant;
import com.codestown.examinoo.R;
import com.codestown.examinoo.helper.AppController;
import com.codestown.examinoo.helper.Session;
import com.codestown.examinoo.helper.TouchImageView;
import com.codestown.examinoo.model.Question;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewFragment extends Fragment {

    private ArrayList<Question> questionList;
    ArrayList<String> options;
    private static final String QUESTION_INDEX = "question_index";
    public ScrollView mainScroll, queScroll;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    TouchImageView imgQuestion;
    public RelativeLayout layout_A, layout_B, layout_C, layout_D, layout_E, noteLyt;

    public TextView tvSolution, tvIndex, tvQStatus;
    public TextView txtQuestion, txtQuestion1, option_a, option_b, option_c, option_d, option_e, btnOpt1, btnOpt2, btnOpt3, btnOpt4, btnOpt5, tvExtraNote;
    ImageView imgZoom, imgMic;
    int click = 0;
    public TextToSpeech textToSpeech;

    public ReviewFragment() {
        // Required empty public constructor
    }


    public ReviewFragment(ArrayList<Question> questionList) {
        this.questionList = questionList;

    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review, container, false);
        btnOpt1 = view.findViewById(R.id.btnOpt1);
        btnOpt2 = view.findViewById(R.id.btnOpt2);
        btnOpt3 = view.findViewById(R.id.btnOpt3);
        btnOpt4 = view.findViewById(R.id.btnOpt4);
        btnOpt5 = view.findViewById(R.id.btnOpt5);
        option_a = view.findViewById(R.id.option_a);
        option_b = view.findViewById(R.id.option_b);
        option_c = view.findViewById(R.id.option_c);
        option_d = view.findViewById(R.id.option_d);
        option_e = view.findViewById(R.id.option_e);
        txtQuestion = view.findViewById(R.id.txtQuestion);
        txtQuestion1 = view.findViewById(R.id.txtQuestion1);
        tvExtraNote = view.findViewById(R.id.tvExtraNote);
        tvSolution = view.findViewById(R.id.tvSolution);
        tvIndex = view.findViewById(R.id.tvIndex);
        tvQStatus = view.findViewById(R.id.tvQStatus);


        imgQuestion = view.findViewById(R.id.imgQuestion);

        imgMic = view.findViewById(R.id.imgMic);
        imgZoom = view.findViewById(R.id.imgZoom);
        mainScroll = view.findViewById(R.id.mainScroll);
        queScroll = view.findViewById(R.id.queScroll);
        noteLyt = view.findViewById(R.id.noteLyt);
        layout_A = view.findViewById(R.id.a_layout);
        layout_B = view.findViewById(R.id.b_layout);
        layout_C = view.findViewById(R.id.c_layout);
        layout_D = view.findViewById(R.id.d_layout);
        layout_E = view.findViewById(R.id.e_layout);
        InitializeTTF();
        assert getArguments() != null;
        final Question question = questionList.get(getArguments().getInt(QUESTION_INDEX));

        mainScroll.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                v.findViewById(R.id.queScroll).getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });
        queScroll.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                //Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        tvIndex.setText("" + (getArguments().getInt(QUESTION_INDEX) + 1));
        imgQuestion.resetZoom();
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
        if (Session.getBoolean(Session.E_MODE, getActivity())) {
            if (options.size() == 4)
                layout_E.setVisibility(View.GONE);
            else
                layout_E.setVisibility(View.VISIBLE);

        }
        btnOpt1.setText(Html.fromHtml(options.get(0).trim()));
        btnOpt2.setText(Html.fromHtml(options.get(1).trim()));
        btnOpt3.setText(Html.fromHtml(options.get(2).trim()));
        btnOpt4.setText(Html.fromHtml(options.get(3).trim()));
        if (Session.getBoolean(Session.E_MODE, getActivity())) {
            if (options.size() == 5)
                btnOpt5.setText(Html.fromHtml(options.get(4).trim()));

        }
        layout_A.setBackgroundResource(R.drawable.answer_bg);
        layout_B.setBackgroundResource(R.drawable.answer_bg);
        layout_C.setBackgroundResource(R.drawable.answer_bg);
        layout_D.setBackgroundResource(R.drawable.answer_bg);
        layout_E.setBackgroundResource(R.drawable.answer_bg);

        if (question.getNote().isEmpty()) {
            noteLyt.setVisibility(View.GONE);
        } else {
            noteLyt.setVisibility(View.VISIBLE);
        }

        tvSolution.setVisibility(View.GONE);
        tvExtraNote.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down, 0);
        tvExtraNote.setTag("up");
        tvExtraNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String solution = question.getNote();
                if (tvExtraNote.getTag().equals("up")) {
                    tvExtraNote.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_up, 0);
                    tvSolution.setVisibility(View.VISIBLE);
                    tvSolution.setText(Html.fromHtml(solution));
                    tvExtraNote.setTag("down");
                } else {
                    tvExtraNote.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down, 0);
                    tvSolution.setVisibility(View.GONE);
                    tvExtraNote.setTag("up");
                }

            }
        });


        if (!question.getImage().isEmpty()) {
            imgZoom.setVisibility(View.VISIBLE);
            txtQuestion1.setVisibility(View.VISIBLE);
            txtQuestion.setVisibility(View.GONE);
            imgQuestion.setImageUrl(question.getImage(), imageLoader);
            imgQuestion.setVisibility(View.VISIBLE);

            imgQuestion.setImageUrl(question.getImage(), imageLoader);
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

        String wrongAns;
        if (question.getSelectedAns() != null) {
            wrongAns = question.getSelectedAns().trim();
            tvQStatus.setVisibility(View.GONE);
        } else {
            wrongAns = "";
            tvQStatus.setVisibility(View.VISIBLE);
        }

        if (btnOpt1.getText().toString().equalsIgnoreCase(wrongAns)) {
            layout_A.setBackgroundResource(R.drawable.wrong_gradient);
        } else if (btnOpt2.getText().toString().equalsIgnoreCase(wrongAns)) {
            layout_B.setBackgroundResource(R.drawable.wrong_gradient);
        } else if (btnOpt3.getText().toString().equalsIgnoreCase(wrongAns)) {
            layout_C.setBackgroundResource(R.drawable.wrong_gradient);
        } else if (btnOpt4.getText().toString().equalsIgnoreCase(wrongAns)) {
            layout_D.setBackgroundResource(R.drawable.wrong_gradient);
        } else if (btnOpt5.getText().toString().equalsIgnoreCase(wrongAns)) {
            layout_E.setBackgroundResource(R.drawable.wrong_gradient);
        }
        RightAnswerBackgroundSet(question);

        imgMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech.speak(question.getQuestion(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        return view;
    }


    public void InitializeTTF() {
        textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {

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


    public void RightAnswerBackgroundSet(Question question) {
        if (btnOpt1.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            layout_A.setBackgroundResource(R.drawable.right_gradient);


        } else if (btnOpt2.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            layout_B.setBackgroundResource(R.drawable.right_gradient);


        } else if (btnOpt3.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            layout_C.setBackgroundResource(R.drawable.right_gradient);


        } else if (btnOpt4.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            layout_D.setBackgroundResource(R.drawable.right_gradient);

        } else if (btnOpt5.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            layout_E.setBackgroundResource(R.drawable.right_gradient);

        }
    }


    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    public static ReviewFragment newInstance(int sectionNumber, ArrayList<Question> questionList) {
        ReviewFragment fragment = new ReviewFragment(questionList);
        Bundle args = new Bundle();
        args.putInt(QUESTION_INDEX, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
}
