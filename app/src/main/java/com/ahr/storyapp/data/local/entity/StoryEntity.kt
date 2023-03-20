package com.ahr.storyapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ahr.storyapp.domain.Story
import com.google.gson.annotations.SerializedName

@Entity(tableName = "story")
data class StoryEntity(
    @PrimaryKey
    @get:SerializedName("id")
    val id: String,
    @get:SerializedName("photo_url")
    val photoUrl: String,
    @get:SerializedName("name")
    val name: String,
    @get:SerializedName("description")
    val description: String,
    @get:SerializedName("created_at")
    val createdAt: String,
    @get:SerializedName("lon")
    val lon: Double = 0.0,
    @get:SerializedName("lat")
    val lat: Double = 0.0
)

fun List<StoryEntity>.asDomain(): List<Story> =
    map { storyEntity -> storyEntity.asDomain() }

fun StoryEntity.asDomain(): Story =
    Story(
        id = id,
        name = name,
        description = description,
        photoUrl = photoUrl,
        createdAt = createdAt,
        lat = lat,
        lon = lon,
    )
