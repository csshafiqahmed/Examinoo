package com.codestown.examinoo.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.codestown.examinoo.helper.BookmarkDBHelper;
import com.codestown.examinoo.R;
import com.codestown.examinoo.helper.AppController;
import com.codestown.examinoo.Constant;
import com.codestown.examinoo.helper.Session;
import com.codestown.examinoo.helper.Utils;
import com.facebook.login.LoginManager;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends DrawerActivity implements View.OnClickListener {

    SharedPreferences settings;


    public static RewardedVideoAd rewardedVideoAd;
    public static BookmarkDBHelper bookmarkDBHelper;
    public String type;
    public View divider;
    public LinearLayout lytBattle, lytPlay, lytSelfChallenge;
    public LinearLayout bottomLyt;
    public LinearLayout lytMidScreen;
    public String status = "0";
    public TextView tvAlert, tvBattle, tvPlay, tvSelfChallenge;
    public Toolbar toolbar;


    AlertDialog alertDialog;
    ImageView imgLogout;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.transparentStatusAndNavigation(MainActivity.this);

        getLayoutInflater().inflate(R.layout.activity_main, frameLayout);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        divider = findViewById(R.id.divider);
        try {

            bookmarkDBHelper = new BookmarkDBHelper(getApplicationContext());
            bookmarkDBHelper.createDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        drawerToggle = new ActionBarDrawerToggle
                (
                        this,
                        drawerLayout, toolbar,
                        R.string.drawer_open,
                        R.string.drawer_close
                ) {
        };

        lytSelfChallenge = findViewById(R.id.lytSelfChallenge);
        lytBattle = findViewById(R.id.lytBattle);
        lytPlay = findViewById(R.id.lytPlay);
        lytMidScreen = findViewById(R.id.midScreen);
        bottomLyt = findViewById(R.id.bottomLayout);
        tvAlert = findViewById(R.id.tvAlert);
        imgLogout = findViewById(R.id.imgLogout);
        tvBattle = findViewById(R.id.tvBattle);
        tvBattle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.battle, 0, 0, 0);
        tvPlay = findViewById(R.id.tvPlay);
        tvSelfChallenge = findViewById(R.id.tvSelfChallenge);
        tvPlay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.play, 0, 0, 0);
        tvSelfChallenge.setCompoundDrawablesWithIntrinsicBounds(R.drawable.challange, 0, 0, 0);
        //battle button only shown when user already login
        lytSelfChallenge.setOnClickListener(this);
        lytBattle.setOnClickListener(this);
        lytPlay.setOnClickListener(this);

        settings = getSharedPreferences(Session.SETTING_Quiz_PREF, 0);
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getApplicationContext());
        PlayActivity.loadRewardedVideoAd(MainActivity.this);
        type = getIntent().getStringExtra("type");

        assert type != null;
        if (!type.equals("null")) {
            if (type.equals("category")) {
                Constant.TotalLevel = Integer.parseInt(getIntent().getStringExtra("maxLevel"));
                Constant.CATE_ID = Integer.parseInt(getIntent().getStringExtra("cateId"));
                if (getIntent().getStringExtra("no_of").equals("0")) {
                    Intent intent = new Intent(MainActivity.this, LevelActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, SubcategoryActivity.class);
                    startActivity(intent);
                }
            }
        }

        navigationView.getMenu().getItem(3).setActionView(R.layout.cart_count_layout);
        NavigationCartCount();

        if (Utils.isNetworkAvailable(MainActivity.this)) {
            if (Session.getBoolean(Session.LANG_MODE, getApplicationContext()))
                LanguageDialog(MainActivity.this);
            if (Session.isLogin(getApplicationContext())) {
                imgLogout.setImageResource(R.drawable.logout);
                GetUserStatus();
            } else {
                imgLogout.setImageResource(R.drawable.login);
            }
        }

    }

    public void LanguageDialog(Activity activity) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater1 = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater1.inflate(R.layout.language_dialog, null);
        dialog.setView(dialogView);
        RecyclerView languageView = dialogView.findViewById(R.id.recyclerView);
        languageView.setLayoutManager(new LinearLayoutManager(activity));
        alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Utils.GetLanguage(languageView, activity, alertDialog);
    }

    public void GetUserStatus() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            if (!error) {
                                JSONObject jsonobj = obj.getJSONObject("data");
                                if (jsonobj.getString(Constant.status).equals(Constant.DE_ACTIVE)) {
                                    Session.clearUserSession(getApplicationContext());
                                    FirebaseAuth.getInstance().signOut();
                                    LoginManager.getInstance().logOut();
                                    Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
                                    startActivity(intentLogin);
                                    finish();
                                } else {
                                    Constant.TOTAL_COINS = Integer.parseInt(jsonobj.getString(Constant.COINS));
                                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                        @Override
                                        public void onSuccess(InstanceIdResult instanceIdResult) {
                                            String token = instanceIdResult.getToken();
                                            if (!token.equals(Session.getUserData(Session.FCM, getApplicationContext()))) {
                                                Utils.postTokenToServer(getApplicationContext(), token);
                                            }
                                        }
                                    });
                                    // Utils.RemoveGameRoomId(FirebaseAuth.getInstance().getUid());
                                }
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lytBattle:
                Utils.btnClick(view, MainActivity.this);
                if (!Session.isLogin(MainActivity.this)) {
                    LoginPopUp();
                } else {
                    if (Session.getBoolean(Session.LANG_MODE, getApplicationContext())) {
                        if (Session.getCurrentLanguage(getApplicationContext()).equals(Constant.D_LANG_ID)) {
                            if (alertDialog != null)
                                alertDialog.show();
                        } else {
                            startActivity(new Intent(MainActivity.this, SearchPlayerActivity.class));
                        }
                    } else {
                        startActivity(new Intent(MainActivity.this, SearchPlayerActivity.class));
                    }
                }
                break;
            case R.id.lytPlay:
                Utils.btnClick(view, MainActivity.this);
                if (!Session.isLogin(MainActivity.this)) {
                    LoginPopUp();
                } else {
                    startGame();
                }
                break;
            case R.id.lytSelfChallenge:
                Utils.btnClick(view, MainActivity.this);
                SelfChallenge();
                break;
        }
    }


    public void UpdateProfile() {
        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
    }


    //send registration token to server
    public void SelfChallenge() {
        if (Session.getBoolean(Session.LANG_MODE, getApplicationContext())) {
            if (Session.getCurrentLanguage(getApplicationContext()).equals(Constant.D_LANG_ID)) {
                if (alertDialog != null)
                    alertDialog.show();

            } else {
                startActivity(new Intent(MainActivity.this, NewSelfChallengeActivity.class));
            }
        } else {
            startActivity(new Intent(MainActivity.this, NewSelfChallengeActivity.class));
        }
    }

    public void LeaderBoard(View view) {
        Utils.btnClick(view, MainActivity.this);
        if (!Session.isLogin(MainActivity.this)) {
            LoginPopUp();
        } else {
            startActivity(new Intent(MainActivity.this, LeaderBoardActivity.class));
        }
    }

    public void Logout(View view) {
        Utils.btnClick(view, MainActivity.this);
        if (!Session.isLogin(MainActivity.this)) {
            LoginPopUp();
        } else {
            Utils.SignOutWarningDialog(MainActivity.this);
        }
    }

    public void StartQuiz(View view) {
        if (!Session.isLogin(MainActivity.this)) {
            LoginPopUp();
        } else {
            startGame();
        }
    }

    private void startGame() {

        if (Session.getBoolean(Session.LANG_MODE, getApplicationContext())) {
            if (Session.getCurrentLanguage(getApplicationContext()).equals(Constant.D_LANG_ID)) {
                if (alertDialog != null)
                    alertDialog.show();
            } else {
                startActivity(new Intent(MainActivity.this, CategoryActivity.class));
            }
        } else {
            startActivity(new Intent(MainActivity.this, CategoryActivity.class));
        }

    }

    public void UserProfile(View view) {
        Utils.btnClick(view, MainActivity.this);
        if (!Session.isLogin(MainActivity.this)) {
            LoginPopUp();
        } else {
            UpdateProfile();
        }
    }

    public void Settings(View view) {
        Utils.btnClick(view, MainActivity.this);
        Utils.CheckVibrateOrSound(MainActivity.this);
        Intent playQuiz = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(playQuiz);
        overridePendingTransition(R.anim.open_next, R.anim.close_next);
    }

    public void LoginPopUp() {
        Intent intent = new Intent(getApplicationContext(), LoginPopup.class);
        startActivity(intent);
    }

    public void NavigationCartCount() {

        View viewCount = navigationView.getMenu().getItem(3).setActionView(R.layout.cart_count_layout).getActionView();
        TextView tvCount = viewCount.findViewById(R.id.tvCount);
        if (Session.getNCount(getApplicationContext()) == 0) {
            tvCount.setVisibility(View.GONE);
        } else {
            tvCount.setVisibility(View.VISIBLE);
        }
        tvCount.setText(String.valueOf(Session.getNCount(getApplicationContext())));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (Session.getBoolean(Session.LANG_MODE, getApplicationContext()))
            menu.findItem(R.id.language).setVisible(true);
        else
            menu.findItem(R.id.language).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.language:
                if (alertDialog != null)
                    alertDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    @Override
    protected void onPause() {
        AppController.StopSound();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppController.playSound();
        if (Utils.isNetworkAvailable(MainActivity.this)) {
            Utils.GetSystemConfig(getApplicationContext());

            if (Session.getBoolean(Session.LANG_MODE, getApplicationContext())) {
                LanguageDialog(MainActivity.this);
            }
            invalidateOptionsMenu();
            if (Session.isLogin(MainActivity.this)) {
                NavigationCartCount();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
