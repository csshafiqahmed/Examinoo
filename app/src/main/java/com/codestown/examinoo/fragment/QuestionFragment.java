package com.codestown.examinoo.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.toolbox.ImageLoader;
import com.codestown.examinoo.Constant;
import com.codestown.examinoo.R;
import com.codestown.examinoo.helper.AppController;
import com.codestown.examinoo.helper.Session;
import com.codestown.examinoo.helper.TouchImageView;
import com.codestown.examinoo.model.Question;

import java.util.ArrayList;
import java.util.Collections;

public class QuestionFragment extends Fragment implements View.OnClickListener {
    private ArrayList<Question> questionList;
    ArrayList<String> options;
    private static final String QUESTION_INDEX = "index";
    public ScrollView mainScroll, queScroll;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    TouchImageView imgQuestion;
    public RelativeLayout layout_A, layout_B, layout_C, layout_D, layout_E;

    public TextView tvIndex, tvQStatus, option_a, option_b, option_c, option_d, option_e;
    public TextView txtQuestion, txtQuestion1, btnOpt1, btnOpt2, btnOpt3, btnOpt4, btnOpt5;
    ImageView imgZoom, imgMic;
    int click = 0;
    public TextToSpeech textToSpeech;
    View view;

    public QuestionFragment() {
        // Required empty public constructor
    }

    public QuestionFragment(ArrayList<Question> questionList) {
        this.questionList = questionList;
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_questions, container, false);
        btnOpt1 = view.findViewById(R.id.btnOpt1);
        btnOpt2 = view.findViewById(R.id.btnOpt2);
        btnOpt3 = view.findViewById(R.id.btnOpt3);
        btnOpt4 = view.findViewById(R.id.btnOpt4);
        btnOpt5 = view.findViewById(R.id.btnOpt5);
        txtQuestion = view.findViewById(R.id.txtQuestion);
        txtQuestion1 = view.findViewById(R.id.txtQuestion1);

        tvIndex = view.findViewById(R.id.tvIndex);
        tvQStatus = view.findViewById(R.id.tvQStatus);
        imgQuestion = view.findViewById(R.id.imgQuestion);
        imgMic = view.findViewById(R.id.imgMic);
        imgZoom = view.findViewById(R.id.imgZoom);
        mainScroll = view.findViewById(R.id.mainScroll);
        queScroll = view.findViewById(R.id.queScroll);
        layout_A = view.findViewById(R.id.a_layout);
        layout_B = view.findViewById(R.id.b_layout);
        layout_C = view.findViewById(R.id.c_layout);
        layout_D = view.findViewById(R.id.d_layout);
        layout_E = view.findViewById(R.id.e_layout);
        option_a = view.findViewById(R.id.option_a);
        option_b = view.findViewById(R.id.option_b);
        option_c = view.findViewById(R.id.option_c);
        option_d = view.findViewById(R.id.option_d);
        option_e = view.findViewById(R.id.option_e);


        layout_A.setOnClickListener(this);
        layout_B.setOnClickListener(this);
        layout_C.setOnClickListener(this);
        layout_D.setOnClickListener(this);
        layout_E.setOnClickListener(this);


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

            layout_C.setVisibility(View.GONE);
            layout_D.setVisibility(View.GONE);
           // option_a.setText(getString(R.string.bullet));
           // option_b.setText(getString(R.string.bullet));

        } else {
            Collections.shuffle(options);

            layout_C.setVisibility(View.VISIBLE);
            layout_D.setVisibility(View.VISIBLE);

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
        System.out.println("====  get selected " + question.getSelectedAns());
        if (question.getSelectedAns() != null)
            if (question.getSelectedAns().equals(btnOpt1.getText().toString())) {
                option_a.setTextColor(Color.WHITE);
                option_a.setBackgroundResource(R.drawable.option_bg);
            } else if (question.getSelectedAns().equals(btnOpt2.getText().toString())) {
                option_b.setTextColor(Color.WHITE);
                option_b.setBackgroundResource(R.drawable.option_bg);
            } else if (question.getSelectedAns().equals(btnOpt3.getText().toString())) {
                option_c.setTextColor(Color.WHITE);
                option_c.setBackgroundResource(R.drawable.option_bg);
            } else if (question.getSelectedAns().equals(btnOpt4.getText().toString())) {
                option_d.setTextColor(Color.WHITE);
                option_d.setBackgroundResource(R.drawable.option_bg);
            } else if (question.getSelectedAns().equals(btnOpt5.getText().toString())) {
                option_e.setTextColor(Color.WHITE);
                option_e.setBackgroundResource(R.drawable.option_bg);
            }

        //RightAnswerBackgroundSet(question);

        imgMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech.speak(question.getQuestion(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {

        assert getArguments() != null;
        switch (v.getId()) {
            case R.id.a_layout:
                AddReview(questionList.get(getArguments().getInt(QUESTION_INDEX, 0)), btnOpt1, option_a);
                //OptionBgColorChange("a", view);
                break;

            case R.id.b_layout:
                AddReview(questionList.get(getArguments().getInt(QUESTION_INDEX, 0)), btnOpt2, option_b);
                //OptionBgColorChange("b", view);
                break;

            case R.id.c_layout:
                AddReview(questionList.get(getArguments().getInt(QUESTION_INDEX, 0)), btnOpt3, option_c);
                //OptionBgColorChange("c", view);
                break;

            case R.id.d_layout:
                AddReview(questionList.get(getArguments().getInt(QUESTION_INDEX, 0)), btnOpt4, option_d);
                //OptionBgColorChange("d", view);
                break;

            case R.id.e_layout:
                AddReview(questionList.get(getArguments().getInt(QUESTION_INDEX, 0)), btnOpt5, option_e);
                //OptionBgColorChange("e", view);
                break;
        }

    }

    public void AddReview(Question question, TextView tvBtnOpt, TextView tvOpt) {

        if (!question.getSelectedOpt().equalsIgnoreCase(tvOpt.getText().toString())) {

            if (tvBtnOpt.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
                question.setCorrect(true);
            } else {
                question.setCorrect(false);
            }

            question.setSelectedOpt(tvOpt.getText().toString());
            question.setAttended(true);
            tvOpt.setBackgroundResource(R.drawable.option_bg);
            tvOpt.setTextColor(Color.WHITE);
            question.setSelectedAns(tvBtnOpt.getText().toString());
            OptionBgColorChange(tvOpt.getText().toString(), view);

        } else {
            question.setAttended(false);
            question.setCorrect(false);
            tvOpt.setBackgroundResource(R.drawable.option_bg_border);
            tvOpt.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
            question.setSelectedOpt("none");

        }
        // OptionBgChange(question);
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


    public void OptionBgChange(Question question) {

        if (question.getSelectedAns().equals(btnOpt1.getText().toString())) {
            option_a.setTextColor(Color.WHITE);
            option_a.setBackgroundResource(R.drawable.option_bg);
        } else if (question.getSelectedAns().equals(btnOpt2.getText().toString())) {
            option_b.setTextColor(Color.WHITE);
            option_b.setBackgroundResource(R.drawable.option_bg);
        } else if (question.getSelectedAns().equals(btnOpt3.getText().toString())) {
            option_c.setTextColor(Color.WHITE);
            option_c.setBackgroundResource(R.drawable.option_bg);
        } else if (question.getSelectedAns().equals(btnOpt4.getText().toString())) {
            option_d.setTextColor(Color.WHITE);
            option_d.setBackgroundResource(R.drawable.option_bg);
        } else if (question.getSelectedAns().equals(btnOpt5.getText().toString())) {
            option_e.setTextColor(Color.WHITE);
            option_e.setBackgroundResource(R.drawable.option_bg);
        }
    }

    public void OptionBgColorChange(String option, View view) {
        String[] opt = new String[]{"a", "b", "c", "d", "e"};
        for (String s : opt) {
            int txt = getResources().getIdentifier("option_" + s, "id", getActivity().getPackageName());
            TextView textView = view.findViewById(txt);

            if (option.equalsIgnoreCase(s)) {
                System.out.println("==== option  " + option + "  =====  " + s);
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundResource(R.drawable.option_bg);
            } else {
                textView.setTextColor(Color.BLACK);
                textView.setBackgroundResource(R.drawable.option_bg_border);
            }
        }
    }

    @Override
    public void onPause() {
        if (textToSpeech != null) {
            textToSpeech.shutdown();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    public static QuestionFragment newInstance(int sectionNumber, ArrayList<Question> questionList) {
        QuestionFragment fragment = new QuestionFragment(questionList);
        Bundle args = new Bundle();
        args.putInt(QUESTION_INDEX, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
}
