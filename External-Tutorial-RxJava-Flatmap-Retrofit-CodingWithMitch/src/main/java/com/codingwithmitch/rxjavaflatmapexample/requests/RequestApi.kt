package com.codingwithmitch.rxjavaflatmapexample.requests

import com.codingwithmitch.rxjavaflatmapexample.models.Comment
import com.codingwithmitch.rxjavaflatmapexample.models.Post

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

interface RequestApi {

    @get:GET("posts")
    val posts: Observable<List<Post>>

    @GET("posts/{id}/comments")
    fun getComments(
        @Path("id") id: Int
    ): Observable<List<Comment>>
}
