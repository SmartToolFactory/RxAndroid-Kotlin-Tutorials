package com.codingwithmitch.rxjavaflatmapexample.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Post(
    @field:SerializedName("userId")
    @field:Expose
    var userId: Int, @field:SerializedName("id")
    @field:Expose
    var id: Int, @field:SerializedName("title")
    @field:Expose
    var title: String?, @field:SerializedName("body")
    @field:Expose
    var body: String?, var comments: List<Comment>?
) {

    override fun toString(): String {
        return "Post{" +
                "userId=" + userId +
                ", id=" + id +
                ", title='" + title + '\''.toString() +
                ", body='" + body + '\''.toString() +
                '}'.toString()
    }
}
