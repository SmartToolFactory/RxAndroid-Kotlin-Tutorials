package com.codingwithmitch.rxjavaflatmapexample.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Comment(
    @field:Expose
    @field:SerializedName("postId")
    var postId: Int, @field:Expose
    @field:SerializedName("id")
    var id: Int, @field:Expose
    @field:SerializedName("name")
    var name: String?, @field:Expose
    @field:SerializedName("email")
    var email: String?, @field:Expose
    @field:SerializedName("body")
    var body: String?
)
