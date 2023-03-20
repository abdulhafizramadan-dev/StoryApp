package com.ahr.storyapp.helper

import com.ahr.storyapp.data.local.entity.StoryEntity
import com.ahr.storyapp.domain.Login
import com.ahr.storyapp.domain.Register
import com.ahr.storyapp.domain.Story

object Dummy {

    fun generateDummyStories(): List<Story> {
        return (1..10).map {
            Story(
                id = "oig3049809w3utwjg3qo475943875943qvnekgrb3iughi43yt98475943793802q",
                name = "Amita Putry",
                description = "Lorem ipsum, or lipsum as it is sometimes known, is dummy text used in laying out print, graphic or web designs.",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1670924758484_Zi7XXg6v.jpg",
                createdAt = "2022-12-13T09:45:58.485Z",
                lat = 0.0,
                lon = 0.0
            )
        }
    }

    fun generateDummyStory(): Story {
        return Story(
            id = "oig3049809w3utwjg3qo475943875943qvnekgrb3iughi43yt98475943793802q",
            name = "Amita Putry",
            description = "Lorem ipsum, or lipsum as it is sometimes known, is dummy text used in laying out print, graphic or web designs.",
            photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1670924758484_Zi7XXg6v.jpg",
            createdAt = "2022-12-13T09:45:58.485Z",
            lat = 0.0,
            lon = 0.0
        )
    }

    fun generateDummyStoryEntities(): List<StoryEntity> {
        return (1..2).map {
            StoryEntity(
                id = "oig3049809w3utwjg3qo475943875943qvnekgrb3iughi43yt98475943793802q",
                name = "Amita Putry",
                description = "Lorem ipsum, or lipsum as it is sometimes known, is dummy text used in laying out print, graphic or web designs.",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1670924758484_Zi7XXg6v.jpg",
                createdAt = "2022-12-13T09:45:58.485Z",
                lat = 0.0,
                lon = 0.0
            )
        }
    }

    fun generateDummyLogin(): Login =
        Login(
            "Amita Putry",
            "lfajeaoefa83039qksejfi",
            "r0q9r802nfqo34rq04330qjrbq-rq32ri09qvn"
        )

    fun generateDummyRegister(): Register =
        Register(
            true,
            "Register success"
        )
}