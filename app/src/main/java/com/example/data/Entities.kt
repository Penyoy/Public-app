package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val email: String,
    val avatarUrl: String,
    val level: Int = 1,
    val rankName: String = "Bronze V",
    val characterClass: String = "Warrior",
    val points: Int = 0,
    val winRate: String = "50%",
    val guild: String = "Guildless",
    val bio: String = "Hello Aetheria! Let's explore the realm together.",
    val isCurrentUser: Boolean = false
)

@Entity(tableName = "forum_posts")
data class ForumPostEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val authorName: String,
    val authorAvatarUrl: String,
    val title: String,
    val content: String,
    val category: String,
    val timestamp: Long = System.currentTimeMillis(),
    val likes: Int = 0,
    val commentsCount: Int = 0,
    val likedByCurrentUser: Boolean = false
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val postId: Int,
    val authorName: String,
    val authorAvatarUrl: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
