package com.hami.api_testing;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> postList;
    private Context context;

    public PostAdapter(List<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        holder.titleTextView.setText(post.getTitle());
        holder.bodyTextView.setText(post.getBody());

        // Handle Comments Button
        holder.commentsButton.setOnClickListener(v -> fetchComments(post.getId()));

        // Handle Delete Button
        holder.deleteButton.setOnClickListener(v -> deletePost(post.getId(), position));
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, bodyTextView;
        Button commentsButton, deleteButton;

        public PostViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            bodyTextView = itemView.findViewById(R.id.bodyTextView);
            commentsButton = itemView.findViewById(R.id.commentsButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    private void fetchComments(int postId) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://jsonplaceholder.typicode.com/posts/" + postId + "/comments";

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                ((MainActivity) context).runOnUiThread(() ->
                        Toast.makeText(context, "Failed to fetch comments", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    ((MainActivity) context).runOnUiThread(() -> showCommentsDialog(responseBody));
                }
            }
        });
    }

    private void showCommentsDialog(String comments) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Comments");
        builder.setMessage(comments);
        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void deletePost(int postId, int position) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://jsonplaceholder.typicode.com/posts/" + postId;

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                ((MainActivity) context).runOnUiThread(() ->
                        Toast.makeText(context, "Failed to delete post", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    ((MainActivity) context).runOnUiThread(() -> {
                        postList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}
