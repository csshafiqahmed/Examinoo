package com.codestown.examinoo.helper;

import android.app.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.codestown.examinoo.Constant;
import com.codestown.examinoo.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.codestown.examinoo.activity.LoginActivity;
import com.codestown.examinoo.model.Language;
import com.codestown.examinoo.model.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.codestown.examinoo.helper.AppController.StopSound;

public class Utils {
    public static TextToSpeech textToSpeech;
    public static AdRequest adRequest;
    public static InterstitialAd interstitial;
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    public static AlertDialog alertDialog;
    private static Vibrator sVibrator;
    public static int TotalQuestion = 1;
    public static int CoreectQuetion = 1;
    public static int WrongQuation = 1;
    public static int level_coin = 1;
    public static int level_score = 0;
    public static final long VIBRATION_DURATION = 100;
    public final static double DEG2RAD = (Math.PI / 180.0);
    public final static float FDEG2RAD = ((float) Math.PI / 180.f);

    @SuppressWarnings("unused")
    public final static double DOUBLE_EPSILON = Double.longBitsToDouble(1);

    @SuppressWarnings("unused")
    public final static float FLOAT_EPSILON = Float.intBitsToFloat(1);
    public static int RequestlevelNo = 1;
    public static final boolean DEFAULT_SOUND_SETTING = true;
    public static final boolean DEFAULT_VIBRATION_SETTING = true;
    public static final boolean DEFAULT_MUSIC_SETTING = false;
    public static final boolean DEFAULT_LAN_SETTING = true;
    public static final String FONTS = "fonts/";

    public static void backSoundonclick(Context mContext) {
        try {
            int resourceId = R.raw.click2;
            MediaPlayer mediaplayer = MediaPlayer.create(mContext, resourceId);
            mediaplayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setrightAnssound(Context mContext) {
        try {
            int resourceId = R.raw.right;
            MediaPlayer mediaplayer = MediaPlayer.create(mContext, resourceId);
            mediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    mp.reset();
                    mp.release();
                }
            });
            mediaplayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void CheckBgMusic(Activity activity) {
        if (Session.getMusicEnableDisable(activity))
            AppController.playSound();
        else
            StopSound();
    }

    public static void setwronAnssound(Context mContext) {
        try {
            int resourceId = R.raw.wrong;
            MediaPlayer mediaplayer = MediaPlayer.create(mContext, resourceId);
            mediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    mp.reset();
                    mp.release();
                }
            });
            mediaplayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void vibrate(Context context, long duration) {
        if (sVibrator == null) {
            sVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
        if (sVibrator != null) {
            if (duration == 0) {
                duration = 50;
            }
            sVibrator.vibrate(duration);
        }
    }


    public static boolean isNetworkAvailable(Activity activity) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity == null) {
                return false;
            } else {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                for (NetworkInfo networkInfo : info) {
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }

    public static void loadAd(Context context) {

        interstitial = new InterstitialAd(context);
        interstitial.setAdUnitId(context.getString(R.string.admob_interstitial_id));
        adRequest = new AdRequest.Builder().build();
        interstitial.loadAd(adRequest);
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

    public static void CheckVibrateOrSound(Context context) {

        if (Session.getSoundEnableDisable(context)) {
            backSoundonclick(context);
        }
        if (Session.getVibration(context)) {
            vibrate(context, Utils.VIBRATION_DURATION);
        }
    }

    public static void InitializeTTF(Context context) {
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setSpeechRate(1f);
                    textToSpeech.setPitch(1.1f);

                }
            }
        });
    }

    public static void btnClick(View view, Activity activity) {
        Animation myAnim = AnimationUtils.loadAnimation(activity, R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        view.startAnimation(myAnim);
        CheckVibrateOrSound(activity);
    }

    public static void setDialogBg(AlertDialog alertDialog) {
        if (alertDialog != null)
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    static class MyBounceInterpolator implements android.view.animation.Interpolator {
        private double mAmplitude = 1;
        private double mFrequency = 10;

        MyBounceInterpolator(double amplitude, double frequency) {
            mAmplitude = amplitude;
            mFrequency = frequency;
        }

        public float getInterpolation(float time) {
            return (float) (-1 * Math.pow(Math.E, -time / mAmplitude) *
                    Math.cos(mFrequency * time) + 1);
        }
    }

    public static Bitmap getBitmapFromView(View view, int height, int width) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return bitmap;
    }

    public static void saveImage(ScrollView scrollView, Activity activity) {
        try {

            Bitmap bitmap = getBitmapFromView(scrollView, scrollView.getChildAt(0).getHeight(), scrollView.getChildAt(0).getWidth());
            File cachePath = new File(activity.getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 75, stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void ShareImage(Activity activity, String shareMsg) {
        File imagePath = new File(activity.getCacheDir(), "images");

        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", newFile);

        if (contentUri != null) {
            String shareBody = shareMsg;
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, activity.getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            activity.startActivity(Intent.createChooser(shareIntent, "Share via"));
        }
    }

    public static void ShareInfo(ScrollView scrollView, Activity activity, String shareMsg) {
        ProgressDialog pDialog = new ProgressDialog(activity);
        pDialog.setMessage("Please wait...");
        pDialog.setIndeterminate(true);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(true);
        new DownloadFiles(scrollView, pDialog, activity, shareMsg).execute();
    }

    public static class DownloadFiles extends AsyncTask<String, Integer, String> {
        ScrollView scrollView;
        ProgressDialog pDialog;
        Activity activity;
        String shareMsg;

        public DownloadFiles(ScrollView linearLayout, ProgressDialog pDialog, Activity activity, String shareMsg) {
            this.scrollView = linearLayout;
            this.pDialog = pDialog;
            this.activity = activity;
            this.shareMsg = shareMsg;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            saveImage(scrollView, activity);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pDialog != null)
                pDialog.show();
        }


        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pDialog != null)
                pDialog.dismiss();
            ShareImage(activity, shareMsg);
        }
    }

    public static void GetSystemConfig(final Context context) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            if (!error) {
                                System.out.println("====setting res " + response);
                                JSONObject jsonobj = obj.getJSONObject("data");
                                Constant.APP_LINK = jsonobj.getString(Constant.KEY_APP_LINK);
                                Constant.MORE_APP_URL = jsonobj.getString(Constant.KEY_MORE_APP);
                                Constant.LANGUAGE_MODE = jsonobj.getString(Constant.KEY_LANGUAGE_MODE);
                                Constant.OPTION_E_MODE = jsonobj.getString(Constant.KEY_OPTION_E_MODE);
                                Constant.SHARE_APP_TEXT = jsonobj.getString(Constant.KEY_SHARE_TEXT);
                                Constant.REFER_COIN_VALUE = jsonobj.getString(Constant.REFER_COIN);
                                Constant.EARN_COIN_VALUE = jsonobj.getString(Constant.EARN_COIN);
                                Constant.REWARD_COIN_VALUE = Integer.parseInt(jsonobj.getString(Constant.REWARD_COIN));
                                Constant.QUICK_ANSWER_ENABLE = jsonobj.getString(Constant.KEY_ANSWER_MODE);

                                if (Constant.LANGUAGE_MODE.equals("1"))
                                    Session.setBoolean(Session.LANG_MODE, true, context);
                                else {
                                    Session.setBoolean(Session.LANG_MODE, false, context);
                                    //Session.setBoolean(Session.IS_FIRST_TIME, false, context);
                                    Session.setCurrentLanguage(Constant.D_LANG_ID, context);
                                }

                                if (Constant.OPTION_E_MODE.equals("1"))
                                    Session.setBoolean(Session.E_MODE, true, context);
                                else
                                    Session.setBoolean(Session.E_MODE, false, context);


                            }
                        } catch (Exception e) {
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
                params.put(Constant.GET_SYSTEM_CONFIG, "1");
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public static void postTokenToServer(final Context context, final String token) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            if (!error) {
                                Session.setUserData(Session.FCM, token, context);
                                FirebaseDatabase.getInstance().getReference("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("fcm_id").setValue(token);
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
                params.put(Constant.updateFcmId, "1");
                params.put(Constant.userId, Session.getUserData(Session.USER_ID, context));
                params.put(Constant.fcmId, token);
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    public static void UpdateCoin(final Context context, final String coins) {
        final String token = Session.getDeviceToken(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            if (!error) {

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
                params.put(Constant.updateFcmId, "1");
                params.put(Constant.userId, Session.getUserData(Session.USER_ID, context));
                params.put(Constant.fcmId, token);
                params.put(Constant.COINS, coins);
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    public static ArrayList<Question> getQuestions(JSONArray jsonArray, Activity activity) {
        ArrayList<Question> questionList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                Question question = new Question();
                JSONObject object = jsonArray.getJSONObject(i);
                question.setId(Integer.parseInt(object.getString(Constant.ID)));
                question.setQuestion(object.getString(Constant.QUESTION));
                question.setImage(object.getString(Constant.IMAGE));
                question.setQueType(object.getString(Constant.QUE_TYPE));
                question.addOption(object.getString(Constant.OPTION_A).trim());
                question.addOption(object.getString(Constant.OPTION_B).trim());
                question.addOption(object.getString(Constant.OPTION_C).trim());
                question.addOption(object.getString(Constant.OPTION_D).trim());
                if (Session.getBoolean(Session.E_MODE, activity)) {
                    if (!object.getString(Constant.OPTION_E).isEmpty() || !object.getString(Constant.OPTION_E).equals(""))
                        question.addOption(object.getString(Constant.OPTION_E).trim());
                }
                question.setSelectedOpt("none");
                String rightAns = object.getString("answer");
                question.setAnsOption(rightAns);
                if (rightAns.equalsIgnoreCase("A")) {
                    question.setTrueAns(object.getString(Constant.OPTION_A).trim());
                } else if (rightAns.equalsIgnoreCase("B")) {
                    question.setTrueAns(object.getString(Constant.OPTION_B).trim());
                } else if (rightAns.equalsIgnoreCase("C")) {
                    question.setTrueAns(object.getString(Constant.OPTION_C).trim());
                } else if (rightAns.equalsIgnoreCase("D")) {
                    question.setTrueAns(object.getString(Constant.OPTION_D).trim());
                } else if (rightAns.equalsIgnoreCase("E")) {
                    question.setTrueAns(object.getString(Constant.OPTION_E).trim());
                }

                question.setLevel(object.getString(Constant.LEVEL));
                question.setNote(object.getString(Constant.NOTE));

                questionList.add(question);


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return questionList;
    }

    public static void SignOutWarningDialog(final Activity activity) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        // Setting Dialog Message
        alertDialog.setMessage(activity.getResources().getString(R.string.logout_warning));
        alertDialog.setCancelable(false);
        final AlertDialog alertDialog1 = alertDialog.create();

        // Setting OK Button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                Session.clearUserSession(activity);
                LoginManager.getInstance().logOut();
                LoginActivity.mAuth.signOut();
                FirebaseAuth.getInstance().signOut();
                Intent intentLogin = new Intent(activity, LoginActivity.class);
                activity.startActivity(intentLogin);
                activity.finish();
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

    public static void transparentStatusAndNavigation(Activity context) {

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true, context);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false, context);
            context.getWindow().setStatusBarColor(Color.TRANSPARENT);
            //context.getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    public static void GetLanguage(final RecyclerView languageView, final Context context, final AlertDialog alertDialog) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {


                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean(Constant.ERROR);
                            if (!error) {
                                JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                                ArrayList<Language> languageList = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Language language = new Language();
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    language.setId(object.getString(Constant.ID));
                                    language.setLanguage(object.getString(Constant.LANGUAGE));
                                    languageList.add(language);
                                }
                                if (languageList.size() == 1) {
                                    Session.setCurrentLanguage(languageList.get(0).getId(), context);
                                    Session.setBoolean(Session.IS_FIRST_TIME, true, context);
                                }
                                LanguageAdapter languageAdapter = new LanguageAdapter(context, languageList, alertDialog);
                                languageView.setAdapter(languageAdapter);

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
                params.put(Constant.GET_LANGUAGES, "1");
                return params;
            }
        };

        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public static class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ItemRowHolder> {
        private ArrayList<Language> dataList;
        private Context mContext;
        AlertDialog alertDialog;

        public LanguageAdapter(Context context, ArrayList<Language> dataList, AlertDialog alertDialog) {
            this.dataList = dataList;
            this.mContext = context;
            this.alertDialog = alertDialog;
        }

        @Override
        public LanguageAdapter.ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.language_layout, parent, false);
            return new LanguageAdapter.ItemRowHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull LanguageAdapter.ItemRowHolder holder, final int position) {

            final Language language = dataList.get(position);
            final ItemRowHolder itemRowHolder = holder;
            itemRowHolder.tvLanguage.setText(language.getLanguage());
            if (Session.getCurrentLanguage(mContext).equals(language.getId())) {
                itemRowHolder.radio.setImageResource(R.drawable.ic_radio_check);
            } else {
                itemRowHolder.radio.setImageResource(R.drawable.ic_radio_unchecked);
            }
            itemRowHolder.radio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemRowHolder.radio.setImageResource(R.drawable.ic_radio_check);
                    Session.setCurrentLanguage(language.getId(), mContext);
                    Session.setBoolean(Session.IS_FIRST_TIME, true, mContext);
                    notifyDataSetChanged();
                    alertDialog.dismiss();

                }
            });

        }

        @Override
        public int getItemCount() {
            return (dataList.size());
        }

        public class ItemRowHolder extends RecyclerView.ViewHolder {
            public ImageView radio;
            public TextView tvLanguage;


            public ItemRowHolder(View itemView) {
                super(itemView);
                radio = itemView.findViewById(R.id.radio);
                tvLanguage = itemView.findViewById(R.id.tvLanguage);
            }

        }

    }

    public static void ForgotPasswordPopUp(final Activity activity, final FirebaseAuth firebaseAuth) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.lyt_forgot_password, null);
        dialog.setView(dialogView);
        TextView tvSubmit = dialogView.findViewById(R.id.tvSubmit);

        final EditText edtEmail = dialogView.findViewById(R.id.edtEmail);

        final AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setCancelable(true);
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString().trim();
                if (email.isEmpty())
                    edtEmail.setError(activity.getResources().getString(R.string.email_alert_1));
                else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
                    edtEmail.setError(activity.getResources().getString(R.string.email_alert_2));
                else {
                    firebaseAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(activity, "Email sent", Toast.LENGTH_SHORT).show();
                                        alertDialog.dismiss();
                                    }
                                }
                            });
                }
            }
        });

        alertDialog.show();
    }

    public static void LoadNativeAd(final AppCompatActivity activity) {
        AdLoader.Builder builder2 = new AdLoader.Builder(activity, activity.getString(R.string.native_unit_id));
        final AdLoader adLoader2 = builder2.forUnifiedNativeAd(
                new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {

                        FrameLayout frameLayout = activity.findViewById(R.id.adFrameLyt);
                        UnifiedNativeAdView adView = (UnifiedNativeAdView) activity.getLayoutInflater().inflate(R.layout.lyt_native_ads, null);
                        // populateNativeAdView(unifiedNativeAd,adView);
                        populateNativeAdView(unifiedNativeAd, adView);
                        frameLayout.removeAllViews();
                        frameLayout.addView(adView);
                    }
                }).build();
        adLoader2.loadAd(new AdRequest.Builder().build());
    }

    public static void populateNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        // Some assets are guaranteed to be in every UnifiedNativeAd


        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));


        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.GONE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.GONE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);
    }

    public static void setWindowFlag(final int bits, boolean on, Activity context) {
        Window win = context.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}