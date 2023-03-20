package com.ahr.storyapp.ui.detail

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
class DetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepositoryImpl: StoryRepositoryImpl
    private lateinit var detailViewModel: DetailViewModel

    private val dummyToken = "90w4595wu5w3u459!"
    private val dummyId = "04q243-q023448q39-8rq23"
    private val dummyStory = Dummy.generateDummyStory()
    private val dummyErrorMessage = "Gagal mendapatkan detail!"

    @Before
    fun setUp() {
        detailViewModel = DetailViewModel(storyRepositoryImpl)
    }

    @Test
    fun `when Get Story Should Not Null and Return Success`() {
        val expectedStory = MutableLiveData<Response<Story>>()
        expectedStory.value = Response.Success(dummyStory)

        Mockito.`when`(storyRepositoryImpl.getDetailStory(dummyToken, dummyId)).thenReturn(expectedStory)
        val actualStory = detailViewModel.getDetailStory(dummyToken, dummyId).getOrAwaitValue()
        Mockito.verify(storyRepositoryImpl).getDetailStory(dummyToken, dummyId)

        assertNotNull(actualStory)
        assertTrue(actualStory is Response.Success)
        assertEquals(dummyStory, (actualStory as Response.Success).data)
    }

    @Test
    fun `when Get Story Should Empty and Return Empty`() {
        val expectedStory = MutableLiveData<Response<Story>>()
        expectedStory.value = Response.Empty

        Mockito.`when`(storyRepositoryImpl.getDetailStory(dummyToken, dummyId)).thenReturn(expectedStory)
        val actualStory = detailViewModel.getDetailStory(dummyToken, dummyId).getOrAwaitValue()
        Mockito.verify(storyRepositoryImpl).getDetailStory(dummyToken, dummyId)

        assertNotNull(actualStory)
        assertTrue(actualStory is Response.Empty)
    }

    @Test
    fun `when Get Story Should Null and Return Error`() {
        val expectedStory = MutableLiveData<Response<Story>>()
        expectedStory.value = Response.Error(dummyErrorMessage)

        Mockito.`when`(storyRepositoryImpl.getDetailStory(dummyToken, dummyId)).thenReturn(expectedStory)
        val actualStory = detailViewModel.getDetailStory(dummyToken, dummyId).getOrAwaitValue()
        Mockito.verify(storyRepositoryImpl).getDetailStory(dummyToken, dummyId)

        assertNotNull(actualStory)
        assertTrue(actualStory is Response.Error)
        assertEquals(dummyErrorMessage, (actualStory as Response.Error).message)
    }

}