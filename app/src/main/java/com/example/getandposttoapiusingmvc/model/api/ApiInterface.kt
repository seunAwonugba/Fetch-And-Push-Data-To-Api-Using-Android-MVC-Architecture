package com.example.getandposttoapiusingmvc.model.api

import com.example.getandposttoapiusingmvc.model.dataclass.post.PostDataClassItem
import com.example.getandposttoapiusingmvc.model.dataclass.post.commentsdataclass.CommentsDataClassItem
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Api Interface used to declare a get request
 */
interface ApiInterface {

    /**
     * Using get request annotation to get data from the end point
     */
    @GET("posts")
    fun getPostFromApiInInterface(): Call<List<PostDataClassItem>>

    @GET("posts/{postId}/comments")
    fun getCommentsInInterfacePerPost(@Path("postId") postId:String):Call<List<CommentsDataClassItem>>

    /**
     * Post comments endpoint
     */
    @POST("comments")
    fun pushCommentsToApiInRepository(@Body newComment: CommentsDataClassItem):Call<CommentsDataClassItem>

    /**
     * Post to Posts endpoint
     */
    @POST("posts")
    fun pushPostsToApiInRepository(@Body newpost: PostDataClassItem):Call<PostDataClassItem>

}