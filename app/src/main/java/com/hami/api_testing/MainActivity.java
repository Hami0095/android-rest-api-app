package com.hami.api_testing;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList = new ArrayList<>();
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize OkHttpClient
        client = new OkHttpClient();

        // Fetch posts and set up adapter
        fetchPosts();
    }

    private void fetchPosts() {
        String url = "https://jsonplaceholder.typicode.com/posts";

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Failed to load posts", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    postList = Post.parseJsonToList(responseBody); // Parse JSON into Post list

                    runOnUiThread(() -> {
                        postAdapter = new PostAdapter(postList, MainActivity.this);
                        recyclerView.setAdapter(postAdapter);
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(MainActivity.this, "Error loading posts", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
}
