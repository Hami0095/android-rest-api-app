package com.hami.api_testing;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiInterface {
    @GET("/posts")
    Call<List<Post>> getAllPosts();

    @GET("/posts/{id}/comments")
    Call<List<Comment>> getComments(@Path("id") int postId);

    @PUT("/posts/{id}")
    @FormUrlEncoded
    Call<Post> updatePost(@Path("id") int postId, @Field("title") String title, @Field("body") String body);

    @DELETE("/posts/{id}")
    Call<Void> deletePost(@Path("id") int postId);
}
