package com.codestown.examinoo.activity;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.codestown.examinoo.R;
import com.codestown.examinoo.helper.AppController;
import com.codestown.examinoo.helper.Utils;
import com.codestown.examinoo.model.Question;

import java.util.ArrayList;

public class BookmarkList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    TextView tvNoBookmarked;
    public static ArrayList<Question> bookmarks;
    TextView btnPlay;
    public RelativeLayout mainLayout;
    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark_list);
        mainLayout = findViewById(R.id.mainLayout);

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.bookmark_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnPlay = findViewById(R.id.btnPlay);
        tvNoBookmarked = findViewById(R.id.emptyMsg);
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        bookmarks = MainActivity.bookmarkDBHelper.getAllBookmarkedList();

        //when bookmark note available show message
        if (bookmarks.size() == 0) {
            tvNoBookmarked.setVisibility(View.VISIBLE);
            btnPlay.setVisibility(View.GONE);
        }
        BookMarkAdapter adapter = new BookMarkAdapter(getApplicationContext(), bookmarks);
        recyclerView.setAdapter(adapter);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent playIntent = new Intent(BookmarkList.this, BookmarkPlay.class);
                startActivity(playIntent);
            }
        });
    }

    public class BookMarkAdapter extends RecyclerView.Adapter<BookMarkAdapter.ItemRowHolder> {
        private ArrayList<Question> bookmarks;
        private ImageLoader imageLoader = AppController.getInstance().getImageLoader();

        public BookMarkAdapter(Context context, ArrayList<Question> bookmarks) {
            this.bookmarks = bookmarks;

        }

        @Override
        public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_layout, parent, false);
            return new ItemRowHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemRowHolder holder, final int position) {


            final Question bookmark = bookmarks.get(position);
            holder.tvNo.setText((position + 1) + ".");
            holder.tvQue.setText(Html.fromHtml(bookmark.getQuestion()));
            holder.tvAns.setText(Html.fromHtml(bookmark.getTrueAns()));
            holder.tvNote.setText(Html.fromHtml(bookmark.getNote()));
            holder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.bookmarkDBHelper.delete_id(bookmark.getId());
                    bookmarks.remove(position);
                    notifyDataSetChanged();
                    if (bookmarks.size() == 0) {
                        tvNoBookmarked.setVisibility(View.VISIBLE);
                        btnPlay.setVisibility(View.GONE);
                    }
                }
            });

            if (!bookmark.getImage().isEmpty()) {
                holder.imgQuestion.setVisibility(View.VISIBLE);
                holder.imgQuestion.setImageUrl(bookmark.getImage(), imageLoader);
            }
            if (bookmark.getNote().isEmpty())
                holder.noteLyt.setVisibility(View.GONE);
            else
                holder.noteLyt.setVisibility(View.VISIBLE);


        }

        @Override
        public int getItemCount() {
            return bookmarks.size();
        }

        public class ItemRowHolder extends RecyclerView.ViewHolder {
            TextView tvNo, tvQue, tvAns, tvNote;
            ImageView remove;
            LinearLayout noteLyt;
            NetworkImageView imgQuestion;

            public ItemRowHolder(View itemView) {
                super(itemView);
                tvNo = itemView.findViewById(R.id.tvIndex);
                tvQue = itemView.findViewById(R.id.tvQuestion);
                tvAns = itemView.findViewById(R.id.tvAnswer);
                tvNote = itemView.findViewById(R.id.tvNote);
                remove = itemView.findViewById(R.id.imgDelete);
                imgQuestion = itemView.findViewById(R.id.queImg);
                noteLyt = itemView.findViewById(R.id.noteLyt);


            }
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
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.setting:
                Utils.CheckVibrateOrSound(BookmarkList.this);
                Intent playQuiz = new Intent(BookmarkList.this, SettingActivity.class);
                startActivity(playQuiz);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
