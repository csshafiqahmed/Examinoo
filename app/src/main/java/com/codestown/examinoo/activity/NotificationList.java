package com.codestown.examinoo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.codestown.examinoo.Constant;
import com.codestown.examinoo.R;
import com.codestown.examinoo.helper.AppController;
import com.codestown.examinoo.helper.Session;
import com.codestown.examinoo.helper.Utils;
import com.codestown.examinoo.model.Category;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationList extends AppCompatActivity {
    public RecyclerView recyclerView;
    public ProgressBar progressBar;
    public AdView mAdView;
    public TextView empty_msg;
    public RelativeLayout layout;
    public static ArrayList<Category> notificationList;
    public SwipeRefreshLayout swipeRefreshLayout;
    public Snackbar snackbar;
    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.noti_list));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAdView = findViewById(R.id.banner_AdView);
        mAdView.loadAd(new AdRequest.Builder().build());

        empty_msg = findViewById(R.id.txtblanklist);
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeLayout);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        getData();
        Session.setNCount(0, getApplicationContext());
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void getData() {
        progressBar.setVisibility(View.VISIBLE);
        if (Utils.isNetworkAvailable(NotificationList.this)) {
            GetNotificationList();
        } else {
            setSnackBar();
            progressBar.setVisibility(View.GONE);
        }

    }

    public void setSnackBar() {
        snackbar = Snackbar
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

    /*
     * Get Quiz Category from Json
     */
    public void GetNotificationList() {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            notificationList = new ArrayList<>();
                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean(Constant.ERROR);

                            if (!error) {
                                JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Category category = new Category();
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    category.setName(object.getString("title"));
                                    category.setMessage(object.getString("message"));
                                    category.setImage(object.getString(Constant.IMAGE));
                                    notificationList.add(category);

                                }
                                NotificationAdapter adapter = new NotificationAdapter(NotificationList.this, notificationList);
                                recyclerView.setAdapter(adapter);

                            }else {
                                empty_msg.setVisibility(View.VISIBLE);
                                empty_msg.setText("No Notification!!");
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
                params.put(Constant.GET_NOTIFICATIONS, "1");
                return params;
            }
        };

        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (snackbar != null) {
            snackbar.dismiss();
        }
    }

    public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ItemRowHolder> {
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        private ArrayList<Category> dataList;
        private Context mContext;

        public NotificationAdapter(Context context, ArrayList<Category> dataList) {
            this.dataList = dataList;
            this.mContext = context;
        }

        @Override
        public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_lyt, parent, false);
            return new ItemRowHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemRowHolder holder, final int position) {
            empty_msg.setVisibility(View.GONE);
            final Category notification = dataList.get(position);
            if (notification.getName().length() > 0)
                holder.tvPre.setText(notification.getName().substring(0, 1));
            holder.tvTitle.setText(notification.getName());
            holder.tvDes.setText(Html.fromHtml(notification.getMessage()));
            if (!notification.getImage().isEmpty()) {
                holder.img.setImageUrl(notification.getImage(), imageLoader);
                holder.img.setVisibility(View.VISIBLE);

            }
        }

        @Override
        public int getItemCount() {
            return (null != dataList ? dataList.size() : 0);
        }

        public class ItemRowHolder extends RecyclerView.ViewHolder {

            public TextView tvTitle, tvPre, tvDes;
            public NetworkImageView img;

            public ItemRowHolder(View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvPre = itemView.findViewById(R.id.tvPre);
                tvDes = itemView.findViewById(R.id.tvDes);
                img = itemView.findViewById(R.id.img);
            }
        }
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
}
