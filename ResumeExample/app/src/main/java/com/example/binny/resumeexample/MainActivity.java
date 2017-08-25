package com.example.binny.resumeexample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.RunnableFuture;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class MainActivity extends Activity {

    ArrayList<String> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);

        swipeRefreshLayout.setColorSchemeColors(
                Color.BLACK,
                Color.WHITE
        );

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerlistview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayList.add("Seo");
        arrayList.add("Kim");

        final RecyclerAdapter recyclerAdapter = new RecyclerAdapter(getApplicationContext(), arrayList);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.notifyDataSetChanged();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);

                android.os.Handler handler = new android.os.Handler();
                Runnable runnable;

                runnable = new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.this.finish();
                    }
                };

                handler.postDelayed(runnable, 1000);

                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

}
