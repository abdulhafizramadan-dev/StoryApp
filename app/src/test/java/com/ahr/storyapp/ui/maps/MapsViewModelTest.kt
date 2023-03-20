package com.ahr.storyapp.ui.maps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.ahr.storyapp.data.StoryRepositoryImpl
import com.ahr.storyapp.domain.Response
import com.ahr.storyapp.domain.Story
import com.ahr.storyapp.helper.Dummy
import com.ahr.storyapp.helper.getOrAwaitValue
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepositoryImpl: StoryRepositoryImpl
    private lateinit var mapsViewModel: MapsViewModel

    private val dummyToken = "90w4595wu5w3u459!"
    private val dummyErrorMessage = "Gagal mendapatkan detail!"
    private val dummyStories = Dummy.generateDummyStories()

    @Before
    fun setUp() {
        mapsViewModel = MapsViewModel(storyRepositoryImpl)
    }

    @Test
    fun `when Get Stories Should Not Null and Return Success`() {
        val expectedStories = MutableLiveData<Response<List<Story>>>()
        expectedStories.value = Response.Success(dummyStories)

        Mockito.`when`(storyRepositoryImpl.getStoriesWithLatLng(dummyToken)).thenReturn(expectedStories)
        val actualStories = mapsViewModel.getStories(dummyToken).getOrAwaitValue()
        Mockito.verify(storyRepositoryImpl).getStoriesWithLatLng(dummyToken)

        assertNotNull(actualStories)
        assertTrue(actualStories is Response.Success)
        assertEquals(dummyStories.size, (actualStories as Response.Success).data.size)
    }

    @Test
    fun `when Get Stories Should Empty and Return Empty`() {
        val expectedStories = MutableLiveData<Response<List<Story>>>()
        expectedStories.value = Response.Empty

        Mockito.`when`(storyRepositoryImpl.getStoriesWithLatLng(dummyToken)).thenReturn(expectedStories)
        val actualStories = mapsViewModel.getStories(dummyToken).getOrAwaitValue()
        Mockito.verify(storyRepositoryImpl).getStoriesWithLatLng(dummyToken)

        assertNotNull(actualStories)
        assertTrue(actualStories is Response.Empty)
    }

    @Test
    fun `when Get Stories Should Null and Return Error`() {
        val expectedStories = MutableLiveData<Response<List<Story>>>()
        expectedStories.value = Response.Error(dummyErrorMessage)

        Mockito.`when`(storyRepositoryImpl.getStoriesWithLatLng(dummyToken)).thenReturn(expectedStories)
        val actualStories = mapsViewModel.getStories(dummyToken).getOrAwaitValue()
        Mockito.verify(storyRepositoryImpl).getStoriesWithLatLng(dummyToken)

        assertNotNull(actualStories)
        assertTrue(actualStories is Response.Error)
        assertEquals(dummyErrorMessage, (actualStories as Response.Error).message)
    }
}