package com.ahr.storyapp.ui.addstory

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.ahr.storyapp.data.StoryRepositoryImpl
import com.ahr.storyapp.domain.Response
import com.ahr.storyapp.helper.getOrAwaitValue
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@RunWith(MockitoJUnitRunner::class)
class AddStoryViewModelTest {

    @Mock
    private lateinit var storyRepositoryImpl: StoryRepositoryImpl
    private lateinit var addStoryViewModel: AddStoryViewModel

    private val dummyToken = "fnakewf4082934jfbejqf408q02349qvabejr"
    private val dummyDescription = "In publishing and graphic design, Lorem ipsum is a placeholder text commonly used to demonstrate the visual form of a document or a typeface without relying on meaningful content. Lorem ipsum may be used as a placeholder before final copy is available."

    private val dummySuccessAddNewStoryMessage = "Berhasil menambah story"
    private val dummyFailedAddNewStoryMessage = "Gagal menambah story"

    @Mock
    private lateinit var dummyImage: File

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        addStoryViewModel = AddStoryViewModel(storyRepositoryImpl)
    }

    @Test
    fun `when Add New Story Should Success`() {
        val expectedOutput = MutableLiveData<Response<String>>()
        expectedOutput.value = Response.Success(dummySuccessAddNewStoryMessage)

        Mockito.`when`(storyRepositoryImpl.addNewStory(dummyToken, dummyImage, dummyDescription)).thenReturn(expectedOutput)
        val actualOutput = addStoryViewModel.uploadNewStory(dummyToken, dummyImage, dummyDescription).getOrAwaitValue()
        Mockito.verify(storyRepositoryImpl).addNewStory(dummyToken, dummyImage, dummyDescription)

        assertNotNull(actualOutput)
        assertTrue(actualOutput is Response.Success)
        assertEquals(dummySuccessAddNewStoryMessage, (actualOutput as Response.Success).data)
    }

    @Test
    fun `when Add New Story Should Error`() {
        val expectedResponse = MutableLiveData<Response<String>>()
        expectedResponse.value = Response.Error(dummyFailedAddNewStoryMessage)

        Mockito.`when`(storyRepositoryImpl.addNewStory(dummyToken, dummyImage, dummyDescription)).thenReturn(expectedResponse)
        val actualResponse = addStoryViewModel.uploadNewStory(dummyToken, dummyImage, dummyDescription).getOrAwaitValue()
        Mockito.verify(storyRepositoryImpl).addNewStory(dummyToken, dummyImage, dummyDescription)

        assertNotNull(actualResponse)
        assertTrue(actualResponse is Response.Error)
        assertEquals(dummyFailedAddNewStoryMessage, (actualResponse as Response.Error).message)
    }

}