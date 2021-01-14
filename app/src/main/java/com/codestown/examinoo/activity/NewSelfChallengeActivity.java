package com.codestown.examinoo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.codestown.examinoo.Constant;
import com.codestown.examinoo.R;
import com.codestown.examinoo.helper.AppController;
import com.codestown.examinoo.helper.Session;
import com.codestown.examinoo.helper.Utils;
import com.codestown.examinoo.model.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewSelfChallengeActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView cateRecyclerView, subRecyclerView, questionView, timeView;
    ProgressBar progressBar1, progressBar2;
    public ArrayList<Category> categoryList, subCateList;

    Snackbar snackbar;
    RelativeLayout subCateLyt;

    String cateId = "", subCateId = "", selectedQues = "", selectedMin = "";
    boolean isSubCateAvailable;
    String challengeType = "", ID = "";
    TextView tvCateName, tvSubCateName, tvAlert, tvSelectQues;
    Spinner cateSpinner, subCateSpinner;
    ArrayList<Integer> queNoList, minuteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_self_challenge);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.self_challenge));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        subCateLyt = findViewById(R.id.subCateLyt);
        tvCateName = findViewById(R.id.tvCateName);
        tvAlert = findViewById(R.id.tvAlert);
        tvSubCateName = findViewById(R.id.tvSubCateName);
        tvSelectQues = findViewById(R.id.tvSelectQues);
        cateSpinner = findViewById(R.id.cateSpinner);
        subCateSpinner = findViewById(R.id.subCateSpinner);
        progressBar1 = findViewById(R.id.progressBar1);
        progressBar2 = findViewById(R.id.progressBar2);
        cateRecyclerView = findViewById(R.id.cateRecyclerview);
        subRecyclerView = findViewById(R.id.subRecyclerview);
        questionView = findViewById(R.id.questionView);
        timeView = findViewById(R.id.timeView);
        questionView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        timeView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        tvCateName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand, 0);
        tvSubCateName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand, 0);
        tvCateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cateSpinner.performClick();
            }
        });
        tvSubCateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subCateSpinner.performClick();
            }
        });
        Utils.LoadNativeAd(NewSelfChallengeActivity.this);
        getData();
        setTime();
    }

    public void setTime() {
        minuteList = new ArrayList<>();
        for (int i = 0; i <= Constant.MAX_MINUTES; i++) {
            if (i % 3 == 0) {
                if (i != 0)
                    minuteList.add(i);
            }
        }
        SelectAdapter adapter = new SelectAdapter(getApplicationContext(), minuteList, "time");
        timeView.setAdapter(adapter);
    }

    public void setQuestionCount(int queLength) {
        System.out.println("===que size " + queLength);
        queNoList = new ArrayList<>();
        for (int i = 0; i <= queLength; i++) {
            if (i % 5 == 0) {
                if (i != 0)
                    queNoList.add(i);
            }
        }
        if (queNoList.size() == 0)
            tvAlert.setVisibility(View.VISIBLE);
        else
            tvAlert.setVisibility(View.GONE);

        SelectAdapter adapter = new SelectAdapter(getApplicationContext(), queNoList, "que");
        questionView.setAdapter(adapter);
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

    private void getData() {
        if (Utils.isNetworkAvailable(NewSelfChallengeActivity.this)) {
            GetCategories();
            invalidateOptionsMenu();
        } else {
            setSnackBar();
        }

    }

    private void setTextViewDrawableColor(TextView textView, int color) {
        textView.setBackgroundResource(R.drawable.button_gradient);
        textView.setTextColor(Color.WHITE);
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getApplicationContext(), color), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    public void GetCategories() {
        progressBar1.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            categoryList = new ArrayList<>();
                            JSONObject jsonObject = new JSONObject(response);
                            String error = jsonObject.getString(Constant.ERROR);
                            System.out.println("====cate res " + response);
                            if (error.equalsIgnoreCase("false")) {

                                JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Category category = new Category();
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    category.setId(object.getString(Constant.ID));
                                    category.setName(object.getString(Constant.CATEGORY_NAME));
                                    category.setImage(object.getString(Constant.IMAGE));
                                    category.setTtlQues(object.getString(Constant.NO_OF_QUES));
                                    category.setNoOfCate(object.getString(Constant.NO_OF_CATE));
                                    categoryList.add(category);
                                }

                                CustomAdapter customAdapter = new CustomAdapter(categoryList, "cate");
                                cateSpinner.setAdapter(customAdapter);
                                cateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        Category cate = categoryList.get(i);
                                        cateId = cate.getId();
                                        tvCateName.setText(cate.getName());
                                        setTextViewDrawableColor(tvCateName, R.color.white);
                                        if (cate.getNoOfCate().equalsIgnoreCase("0")) {
                                            System.out.println("cate ttl que "+cate.getTtlQues());
                                            if(Integer.parseInt(cate.getTtlQues())>=5) {
                                                questionView.setVisibility(View.VISIBLE);
                                                setQuestionCount(Integer.parseInt(cate.getTtlQues()));
                                                isSubCateAvailable = false;
                                                subCateLyt.setVisibility(View.GONE);
                                            }else {
                                                isSubCateAvailable = false;
                                                subCateLyt.setVisibility(View.GONE);
                                                questionView.setVisibility(View.GONE);
                                                tvAlert.setVisibility(View.VISIBLE);
                                                selectedQues="";
                                            }
                                        } else
                                            GetSubCategories(cateId);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                });


                            }
                            progressBar1.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar1.setVisibility(View.GONE);
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.accessKey, Constant.accessKeyValue);
                if (Session.getBoolean(Session.LANG_MODE, getApplicationContext())) {
                    params.put(Constant.GET_CATE_BY_LANG, "1");
                    params.put(Constant.LANGUAGE_ID, Session.getCurrentLanguage(getApplicationContext()));
                } else
                    params.put(Constant.getCategories, "1");
                return params;
            }
        };

        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void GetSubCategories(final String cateId) {
        progressBar2.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            subCateList = new ArrayList<>();
                            JSONObject jsonObject = new JSONObject(response);
                            String error = jsonObject.getString(Constant.ERROR);
                            System.out.println("====sub cate res " + response);
                            if (error.equalsIgnoreCase("false")) {
                                JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Category category = new Category();
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    category.setId(object.getString(Constant.ID));
                                    category.setName(object.getString(Constant.SUB_CATE_NAME));
                                    category.setImage(object.getString(Constant.IMAGE));
                                    category.setTtlQues(object.getString(Constant.NO_OF_CATE));
                                    subCateList.add(category);
                                }
                                isSubCateAvailable = true;
                                subCateLyt.setVisibility(View.VISIBLE);
                                CustomAdapter customAdapter = new CustomAdapter(subCateList, "subCate");
                                subCateSpinner.setAdapter(customAdapter);
                                subCateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                      Category subCate = subCateList.get(i);
                                       subCateId = subCate.getId();
                                        tvSubCateName.setText(subCate.getName());
                                        setTextViewDrawableColor(tvSubCateName, R.color.white);
                                        if(Integer.parseInt(subCate.getTtlQues())>=5) {
                                            setQuestionCount(Integer.parseInt(subCate.getTtlQues()));
                                            questionView.setVisibility(View.VISIBLE);
                                        }else {
                                            questionView.setVisibility(View.GONE);
                                            tvAlert.setVisibility(View.VISIBLE);
                                            selectedQues="";
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                });
                            } else {
                                isSubCateAvailable = false;
                                subCateLyt.setVisibility(View.GONE);

                            }
                            progressBar2.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar2.setVisibility(View.GONE);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.accessKey, Constant.accessKeyValue);
                params.put(Constant.getSubCategory, "1");
                params.put(Constant.categoryId, cateId);
                System.out.println("====params " + params.toString());
                return params;
            }
        };

        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void StartChallenge(View view) {
        if (cateId.isEmpty()) {
            Toast.makeText(getApplicationContext(), "please select category", Toast.LENGTH_SHORT).show();
        } else if (isSubCateAvailable && subCateId.isEmpty()) {
            Toast.makeText(getApplicationContext(), "please select subCategory", Toast.LENGTH_SHORT).show();
        } else if (selectedQues.isEmpty()) {
            Toast.makeText(getApplicationContext(), "please select questions", Toast.LENGTH_SHORT).show();
        } else if (selectedMin.isEmpty()) {
            Toast.makeText(getApplicationContext(), "please select minutes", Toast.LENGTH_SHORT).show();
        } else {

            if (!isSubCateAvailable) {
                challengeType = "cate";
                ID = cateId;
            } else {
                challengeType = "subCate";
                ID = subCateId;
            }
            Intent intent = new Intent(getApplicationContext(), SelfChallengeQuestion.class);
            intent.putExtra("type", challengeType);
            intent.putExtra("id", ID);
            intent.putExtra("limit", "" + selectedQues);
            intent.putExtra("time", Integer.parseInt(selectedMin));
            startActivity(intent);

        }

    }

    public class CustomAdapter extends BaseAdapter {
        ArrayList<Category> cateList;
        String type;

        public CustomAdapter(ArrayList<Category> cateList, String type) {
            this.cateList = cateList;
            this.type = type;
        }

        @Override
        public int getCount() {
            return cateList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.lyt_spinner, null);
            Category category = cateList.get(i);

            TextView tvName = view.findViewById(R.id.tvName);
            TextView tvTotal = view.findViewById(R.id.tvTotal);
            tvName.setText(category.getName());
            tvTotal.setText(getString(R.string.ttl_ques) + category.getTtlQues());
            if (type.equals("cate")) {
                tvTotal.setVisibility(View.GONE);
            }
            return view;
        }
    }

    public class CateAdapter extends RecyclerView.Adapter<CateAdapter.ItemRowHolder> {
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        private ArrayList<Category> cateList;
        private Context mContext;
        private int res;
        String type;

        public CateAdapter(Context context, ArrayList<Category> cateList, int res, String type) {
            this.cateList = cateList;
            this.mContext = context;
            this.res = res;
            this.type = type;
        }

        @Override
        public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(res, parent, false);
            return new ItemRowHolder(v);
        }


        @Override
        public void onBindViewHolder(@NonNull final ItemRowHolder holder, final int position) {
            final Category category = cateList.get(position);
            if (type.equals("cate")) {
                if (cateId.equals(category.getId())) {
                    holder.cardView.setForeground(mContext.getResources().getDrawable(R.drawable.forground_bg));
                    holder.setIsRecyclable(false);
                }
            } else {
                holder.tvTtlQue.setVisibility(View.VISIBLE);
                holder.tvCate.setMinLines(1);
                holder.tvTtlQue.setText(mContext.getString(R.string.ttl_ques) + category.getTtlQues());
                if (subCateId.equals(category.getId())) {
                    holder.cardView.setForeground(mContext.getResources().getDrawable(R.drawable.forground_bg));
                    holder.setIsRecyclable(false);
                }
            }
            holder.tvCate.setText(category.getName());
            holder.imgCate.setDefaultImageResId(R.drawable.ic_launcher);
            holder.imgCate.setImageUrl(category.getImage(), imageLoader);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (type.equals("cate")) {
                        cateId = category.getId();
                        GetSubCategories(category.getId());
                    } else {
                        subCateId = category.getId();

                    }
                    notifyDataSetChanged();

                }
            });
        }

        @Override
        public int getItemCount() {
            return (null != cateList ? cateList.size() : 0);
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class ItemRowHolder extends RecyclerView.ViewHolder {

            public TextView tvCate, tvTtlQue;
            public NetworkImageView imgCate;
            CardView cardView;

            public ItemRowHolder(View itemView) {
                super(itemView);
                tvCate = itemView.findViewById(R.id.tvCate);
                tvTtlQue = itemView.findViewById(R.id.tvTtlQue);
                imgCate = itemView.findViewById(R.id.imgCate);
                cardView = itemView.findViewById(R.id.cardView);
            }
        }
    }

    public class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.ItemRowHolder> {
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        private ArrayList<Integer> cateList;
        private Context mContext;

        String type;

        public SelectAdapter(Context context, ArrayList<Integer> cateList, String type) {
            this.cateList = cateList;
            this.mContext = context;

            this.type = type;
        }

        @Override
        public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_slection, parent, false);
            return new ItemRowHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final ItemRowHolder holder, final int position) {
            if (type.equals("time")) {
                if (selectedMin.equals(String.valueOf(cateList.get(position)))) {
                    holder.tvSelect.setBackgroundResource(R.drawable.button_gradient);
                    holder.tvSelect.setTextColor(Color.WHITE);
                    holder.setIsRecyclable(false);
                } else
                    holder.tvSelect.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));


            } else {

                if (selectedQues.equals(String.valueOf(cateList.get(position)))) {
                    holder.tvSelect.setBackgroundResource(R.drawable.button_gradient);
                    holder.tvSelect.setTextColor(Color.WHITE);
                    holder.setIsRecyclable(false);
                } else
                    holder.tvSelect.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
            }
            holder.tvSelect.setText(String.valueOf(cateList.get(position)));


            holder.tvSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (type.equals("time")) {
                        selectedMin = String.valueOf(cateList.get(position));
                    } else {
                        selectedQues = String.valueOf(cateList.get(position));

                    }
                    notifyDataSetChanged();

                }
            });
        }

        @Override
        public int getItemCount() {
            return (null != cateList ? cateList.size() : 0);
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class ItemRowHolder extends RecyclerView.ViewHolder {

            public TextView tvSelect;


            public ItemRowHolder(View itemView) {
                super(itemView);
                tvSelect = itemView.findViewById(R.id.tvSelect);

            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppController.StopSound();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppController.playSound();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
