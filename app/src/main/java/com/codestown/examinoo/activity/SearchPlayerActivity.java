package com.codestown.examinoo.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
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
import com.codestown.examinoo.helper.Session;
import com.codestown.examinoo.helper.Utils;
import com.codestown.examinoo.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SearchPlayerActivity extends AppCompatActivity {
    private static final String FORMAT = "%02d";
    private static CountDownTimer countDownTimer;
    public boolean exist = false;
    private Context mContext;

    public TextView tvPlayer1, tvPlayer2, tvTimeLeft, tvSecond, tvSearch, tvSearchPlayer, tvStateTitle;
    public NetworkImageView imgPlayer1, imgPlayer2;
    public DatabaseReference database, myRef;
    public ValueEventListener valueEventListener, valueEventListener1;
    boolean isRunning = false;
    public ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    public LinearLayout alertLayout, contentLayout;
    public RelativeLayout timerLayout;
    public ProgressBar progressBar;
    public AlertDialog quiteDialog;
    public RecyclerView recyclerView;
    public String pauseCheck = "regular", profilePlayer2, player1Name, player2Name, userProfile,
            userId1, userId2, player = "", opponentId = "", matchingId = "", email, languageId;

    public Toolbar toolbar;
    AlertDialog leaveDialog, timeAlertDialog, battleDialog;
    boolean isAvailable;
    public ArrayList<User> battleList;
    public ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_opponent);
        mContext = SearchPlayerActivity.this;
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.matching_opponent));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        contentLayout = findViewById(R.id.contentLayout);
        timerLayout = findViewById(R.id.timerLayout);
        alertLayout = findViewById(R.id.alertLayout);
        tvPlayer1 = findViewById(R.id.tv_player1_name);
        imgPlayer1 = findViewById(R.id.imgPlayer1);
        tvPlayer2 = findViewById(R.id.tv_player2_name);
        imgPlayer2 = findViewById(R.id.imgPlayer2);
        tvTimeLeft = findViewById(R.id.tv_time_left);
        tvStateTitle = findViewById(R.id.tvStateTitle);
        tvSearchPlayer = findViewById(R.id.tvSearchPlayer);
        imgPlayer1.setDefaultImageResId(R.drawable.ic_profile);
        imgPlayer2.setDefaultImageResId(R.drawable.ic_profile);
        imgPlayer2.setDefaultImageResId(R.drawable.ic_profile);
        progressBar = findViewById(R.id.progressBar);
        tvPlayer1.setText(getString(R.string.player_1));
        tvPlayer2.setText(getString(R.string.player_2));
        tvSecond = findViewById(R.id.tvSec);
        tvSearch = findViewById(R.id.tvSearch);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setNestedScrollingEnabled(false);
        GetBattleStatestics();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // this interface can be used to receive events about data changes at a location
        // This method will be called with a snapshot of the data at this location.

        getData();
    }


    public void getData() {

        if (Utils.isNetworkAvailable(SearchPlayerActivity.this)) {
            progressBar.setVisibility(View.VISIBLE);
            player = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            database = FirebaseDatabase.getInstance().getReference();
            //Utils.RemoveGameRoomId(player);
            //call listener
            player1Name = Session.getUserData(Session.NAME, getApplicationContext());
            userId1 = Session.getUserData(Session.USER_ID, getApplicationContext());
            userProfile = Session.getUserData(Session.PROFILE, getApplicationContext());
            email = Session.getUserData(Session.EMAIL, getApplicationContext());
            languageId = Session.getCurrentLanguage(getApplicationContext());

            tvPlayer1.setText(player1Name);
            imgPlayer1.setImageUrl(userProfile, imageLoader);

            progressBar.setVisibility(View.GONE);
            tvTimeLeft.setVisibility(View.VISIBLE);
            tvSecond.setVisibility(View.VISIBLE);
            alertLayout.setVisibility(View.GONE);
            contentLayout.setVisibility(View.VISIBLE);
            timerLayout.setVisibility(View.GONE);
            tvSearchPlayer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SearchPlayerClickMethod();
                }
            });
        } else {
            alertLayout.setVisibility(View.VISIBLE);
            contentLayout.setVisibility(View.GONE);
            setSnackBar();

        }

    }

    public void SearchPlayerClickMethod() {
        exist = true;
        timerLayout.setVisibility(View.VISIBLE);
        tvSearchPlayer.setVisibility(View.GONE);
        myRef = FirebaseDatabase.getInstance().getReference(Constant.DB_GAME_ROOM_NEW);
        final User user = new User(userId1, player1Name, userProfile, "1",Session.getCurrentLanguage(getApplicationContext()));
        myRef.child(player).setValue(user);
        startTimer();
        valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.exists()) {
                            if (ds.child(Constant.IS_AVAIL).getValue() != null) {
                                if (ds.child(Constant.IS_AVAIL).getValue().toString().equals("1") && ds.child(Constant.LANG_ID).getValue().toString().equals(Session.getCurrentLanguage(getApplicationContext()))&& !ds.getKey().equals(player)) {
                                    opponentId = ds.getKey();
                                    player2Name = ds.child(Constant.NAME).getValue().toString();
                                    profilePlayer2 = ds.child(Constant.IMAGE).getValue().toString();
                                    userId2 = ds.child(Constant.USER_ID).getValue().toString();
                                    matchingId = player;
                                    isAvailable = true;
                                    tvPlayer2.setText(player2Name);
                                    imgPlayer2.setImageUrl(profilePlayer2, imageLoader);
                                    myRef.child(opponentId).child(Constant.MATCHING_ID).setValue(player);
                                    myRef.child(opponentId).child(Constant.IS_AVAIL).setValue("0");
                                    myRef.child(opponentId).child(Constant.OPPONENT_ID).setValue(player);

                                    myRef.child(player).child(Constant.MATCHING_ID).setValue(player);
                                    myRef.child(player).child(Constant.IS_AVAIL).setValue("0");
                                    myRef.child(player).child(Constant.OPPONENT_ID).setValue(opponentId);

                                 /*   Map<String, Object> taskMap = new HashMap<String, Object>();
                                    taskMap.put(Constant.IS_AVAIL, "0");
                                    taskMap.put(Constant.OPPONENT_ID, player);
                                    taskMap.put(Constant.MATCHING_ID, player);
                                    myRef.child(opponentId).updateChildren(taskMap);

                                    Map<String, Object> taskMap1 = new HashMap<String, Object>();
                                    taskMap.put(Constant.IS_AVAIL, "0");
                                    taskMap.put(Constant.OPPONENT_ID, opponentId);
                                    taskMap.put(Constant.MATCHING_ID, player);
                                    myRef.child(player).updateChildren(taskMap1);*/
                                   //callBattlePlayActivity(opponentId,matchingId,userId2,player2Name,profilePlayer2);
                                    break;
                                }

                            }
                        }
                    }

                }
            }

            // This method will be triggered in the event that this listener either failed at the server
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        myRef.addListenerForSingleValueEvent(valueEventListener1);
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Constant.IS_AVAIL).exists()) {
                    if (dataSnapshot.child(Constant.IS_AVAIL).getValue().toString().equals("0")) {
                        if (dataSnapshot.child(Constant.OPPONENT_ID).exists()&& dataSnapshot.child(Constant.MATCHING_ID).exists()) {
                            opponentId = dataSnapshot.child(Constant.OPPONENT_ID).getValue().toString();
                            if (!opponentId.isEmpty()) {
                                myRef.child(opponentId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.getKey() != null) {
                                            opponentId = snapshot.getKey();
                                            player2Name = snapshot.child(Constant.NAME).getValue(String.class);
                                            profilePlayer2 = snapshot.child(Constant.IMAGE).getValue(String.class);
                                            userId2 = snapshot.child(Constant.USER_ID).getValue(String.class);
                                            matchingId = snapshot.child(Constant.MATCHING_ID).getValue(String.class);
                                            isAvailable = true;
                                            tvPlayer2.setText(player2Name);
                                            imgPlayer2.setImageUrl(profilePlayer2, imageLoader);
                                            callBattlePlayActivity(opponentId,matchingId,userId2,player2Name,profilePlayer2);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }

                    } else {
                        isAvailable = false;
                    }


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        myRef.child(player).addValueEventListener(valueEventListener);
    }

    public void setSnackBar() {
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getData();
                    }
                });

        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }


    // use timer toh get opposite player in specific time
    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(Constant.OPPONENT_SEARCH_TIME, Constant.COUNT_DOWN_TIMER) {
            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            public void onTick(long millisUntilFinished) {
                isRunning = true;
                int progress = (int) (millisUntilFinished / 1000);
                tvTimeLeft.setText("" + String.format(FORMAT, progress));
            }

            public void onFinish() {
                isRunning = false;
                tvTimeLeft.setText("00");
                showTimeUpAlert(getString(R.string.robot));
            }
        }.start();
    }

    @SuppressLint("SetTextI18n")
    private void showTimeUpAlert(final String playWith) {
        try {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            myRef.child(player).child(Constant.IS_AVAIL).setValue("0");
            final AlertDialog.Builder dialog = new AlertDialog.Builder(SearchPlayerActivity.this);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View dialogView = inflater.inflate(R.layout.dialog_time_up, null);
            dialog.setView(dialogView);
            TextView tvExit = dialogView.findViewById(R.id.tvExit);

            CircleImageView playerImg = dialogView.findViewById(R.id.imgPlayer);
            LinearLayout tryLayout = dialogView.findViewById(R.id.tryLayout);
            TextView btnRobot = dialogView.findViewById(R.id.btnRobot);
            TextView btnTryAgain = dialogView.findViewById(R.id.btnTryAgain);

            tryLayout.setVisibility(View.VISIBLE);
            playerImg.setErrorImageResId(R.drawable.ic_android);
            playerImg.setDefaultImageResId(R.drawable.ic_android);

            timeAlertDialog = dialog.create();
            tvExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteGameRoom();
                    finish();
                    timeAlertDialog.dismiss();
                }
            });

            btnRobot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (playWith.equals(getString(R.string.player_2)) || playWith.equals(getString(R.string.robot))) {
                        callRobotPlayActivity();

                    }
                    timeAlertDialog.dismiss();
                }
            });
            btnTryAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    timeAlertDialog.dismiss();
                    deleteGameRoom();
                    ReloadUserForBattle();
                }
            });
            timeAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            timeAlertDialog.setCancelable(false);
            timeAlertDialog.show();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ReloadUserForBattle() {
        tvPlayer1.setText(getString(R.string.player_1));
        tvPlayer2.setText(getString(R.string.player_2));
        player = "";
        opponentId = "";
        imgPlayer1.setImageUrl("removed", imageLoader);
        imgPlayer2.setImageUrl("removed", imageLoader);
        imgPlayer1.setDefaultImageResId(R.drawable.ic_profile);
        imgPlayer2.setDefaultImageResId(R.drawable.ic_profile);
        imgPlayer1.setErrorImageResId(R.drawable.ic_profile);
        imgPlayer2.setErrorImageResId(R.drawable.ic_profile);
        matchingId = "";
        getData();
        SearchPlayerClickMethod();

    }

    private void callBattlePlayActivity(String opponentId, String matchingId, String userId2, String player2Name, String player2Profile) {


            exist = false;
            startActivity(new Intent(mContext, BattlePlayActivity.class)
                    .putExtra("gameid", matchingId)
                    .putExtra("opponentId", opponentId)
                    .putExtra("user_id1", userId1)
                    .putExtra("user_id2", userId2)
                    .putExtra("player2Name",player2Name)
                    .putExtra("player2Profile",player2Profile)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            if (valueEventListener != null)
                myRef.child(player).removeEventListener(valueEventListener);

            if (countDownTimer != null) {
                countDownTimer.cancel();
            }

            finish();


    }

    private void callRobotPlayActivity() {
        exist = false;
        startActivity(new Intent(mContext, RobotPlayActivity.class)
                .putExtra("battlePlayer", tvPlayer2.getText().toString())
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        deleteGameRoom();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        finish();


    }


    public void GetBattleStatestics() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //  System.out.println("======battle state res " + response);
                            JSONObject jsonObject = new JSONObject(response);
                            String error = jsonObject.getString(Constant.ERROR);
                            if (error.equalsIgnoreCase("false")) {
                                JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                                battleList = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    User battle = new User();
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    battle.setOpponentName(object.getString(Constant.OPPONENT_NAME));
                                    battle.setOpponentProfile(object.getString(Constant.OPPONENT_PROFILE));
                                    battle.setResut(object.getString(Constant.MY_STATUS));
                                    battleList.add(battle);
                                }

                                BattleAdapter adapter = new BattleAdapter(getApplicationContext(), battleList);
                                recyclerView.setAdapter(adapter);
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
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.accessKey, Constant.accessKeyValue);
                params.put(Constant.GET_BATTLE_STATISTICS, "1");
                params.put(Constant.userId, Session.getUserData(Session.USER_ID, getApplicationContext()));
                params.put(Constant.OFFSET, "0");
                params.put(Constant.LIMIT, Constant.BATTLE_PAGE_LIMIT);
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public class BattleAdapter extends RecyclerView.Adapter<BattleAdapter.ItemRowHolder> {
        private ArrayList<User> battleList;
        private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        public Context context;

        public BattleAdapter(Context context, ArrayList<User> battleList) {
            this.battleList = battleList;
            this.context = context;
        }

        @Override
        public BattleAdapter.ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.battle_layout, parent, false);
            return new ItemRowHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemRowHolder holder, final int position) {
            User battle = battleList.get(position);
            holder.imgProfile.setDefaultImageResId(R.drawable.ic_profile1);
            holder.imgOpponent.setDefaultImageResId(R.drawable.ic_profile1);
            holder.imgProfile.setImageUrl(Session.getUserData(Session.PROFILE, context), imageLoader);
            holder.imgOpponent.setImageUrl(battle.getOpponentProfile(), imageLoader);
            holder.tvUserName.setText(Session.getUserData(Session.NAME, context));
            holder.tvOpponent.setText(battle.getOpponentName());
            holder.tvResult.setText(battle.getResut());
        }

        @Override
        public int getItemCount() {
            return battleList.size();
        }

        public class ItemRowHolder extends RecyclerView.ViewHolder {
            TextView tvUserName, tvOpponent, tvResult;
            CircleImageView imgProfile, imgOpponent;

            public ItemRowHolder(View itemView) {
                super(itemView);
                tvUserName = itemView.findViewById(R.id.tvUserName);
                tvOpponent = itemView.findViewById(R.id.tvOpponent);
                tvResult = itemView.findViewById(R.id.tvResult);
                imgProfile = itemView.findViewById(R.id.imgProfile);
                imgOpponent = itemView.findViewById(R.id.imgOpponent);

            }
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

    }


    public void BackDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(SearchPlayerActivity.this);
        // Setting Dialog Message
        alertDialog.setMessage(getResources().getString(R.string.back_message));
        alertDialog.setCancelable(false);
        leaveDialog = alertDialog.create();
        // Setting OK Button
        alertDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (Utils.isNetworkAvailable(SearchPlayerActivity.this)) {
                    if (countDownTimer != null)
                        countDownTimer.cancel();
                    if (myRef != null) {
                        deleteGameRoom();
                    }
                }
                leaveDialog.dismiss();
                finish();
                // onBackPressed();
            }
        });
        alertDialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                leaveDialog.dismiss();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (countDownTimer != null)
            BackDialog();
        else
            super.onBackPressed();

    }

    @Override
    protected void onPause() {
        super.onPause();
        //AppController.StopSound();
        if (Utils.isNetworkAvailable(SearchPlayerActivity.this))
            if (pauseCheck.equals("regular")) {
                if (exist) {
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    myRef.child(player).removeEventListener(valueEventListener);
                    finish();
                }
            }

    }

    public void deleteGameRoom() {
        if (myRef != null && valueEventListener != null) {
            myRef.child(player).removeValue();
            myRef.child(player).removeEventListener(valueEventListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Utils.RemoveGameRoomId(player);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //    deleteGameRoom();
        if (myRef != null && valueEventListener != null)
            myRef.child(player).removeEventListener(valueEventListener);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }


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
                pauseCheck = "setting";
                Intent playQuiz = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(playQuiz);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
