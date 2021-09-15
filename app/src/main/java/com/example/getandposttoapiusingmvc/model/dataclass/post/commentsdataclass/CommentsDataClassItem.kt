package com.example.getandposttoapiusingmvc.model.dataclass.post.commentsdataclass

data class CommentsDataClassItem(
    val body: String,
    val email: String,
    val id: Int,
    val name: String,
    val postId: Int
)