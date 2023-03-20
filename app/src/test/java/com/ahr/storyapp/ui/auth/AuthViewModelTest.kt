@file:OptIn(ExperimentalCoroutinesApi::class)

package com.ahr.storyapp.ui.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.ahr.storyapp.data.StoryRepositoryImpl
import com.ahr.storyapp.data.local.datastore.AuthPreferences
import com.ahr.storyapp.domain.Login
import com.ahr.storyapp.domain.Register
import com.ahr.storyapp.domain.Response
import com.ahr.storyapp.helper.Dummy
import com.ahr.storyapp.helper.MainDispatcherRule
import com.ahr.storyapp.helper.getOrAwaitValue
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AuthViewModelTest {

    @Mock
    private lateinit var storyRepositoryImpl: StoryRepositoryImpl
    @Mock
    private lateinit var authPreferences: AuthPreferences

    private lateinit var authViewModel: AuthViewModel

    private val dummyName = "Amita Putry Prasasti"
    private val dummyEmail = "amitaputri55@gmail.com"
    private val dummyPassword = "123456"

    private val dummyLogin = Dummy.generateDummyLogin()
    private val dummyLoginFailedMessage = "Login failed"

    private val dummyRegister = Dummy.generateDummyRegister()
    private val dummyRegisterFailedMessage = "Register failed"

    private val dummyAuthToken = "naier094094q3029ng"

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        authViewModel = AuthViewModel(storyRepositoryImpl, authPreferences)
    }

    @Test
    fun `when Login Success then Return Success`() = runTest {
        val expectedResponse = MutableLiveData<Response<Login>>()
        expectedResponse.value = Response.Success(dummyLogin)

        Mockito.`when`(storyRepositoryImpl.login(dummyEmail, dummyPassword)).thenReturn(expectedResponse)
        val actualResponse = authViewModel.login(dummyEmail, dummyPassword).getOrAwaitValue()
        Mockito.verify(storyRepositoryImpl).login(dummyEmail, dummyPassword)

        assertNotNull(actualResponse)
        assertTrue(actualResponse is Response.Success)
        assertEquals(dummyLogin, (actualResponse as Response.Success).data)
    }

    @Test
    fun `when Login Failed then Return Error`() = runTest {
        val expectedResponse = MutableLiveData<Response<Login>>()
        expectedResponse.value = Response.Error(dummyLoginFailedMessage)

        Mockito.`when`(storyRepositoryImpl.login(dummyEmail, dummyPassword)).thenReturn(expectedResponse)
        val actualResponse = authViewModel.login(dummyEmail, dummyPassword).getOrAwaitValue()
        Mockito.verify(storyRepositoryImpl).login(dummyEmail, dummyPassword)

        assertNotNull(actualResponse)
        assertTrue(actualResponse is Response.Error)
        assertEquals(dummyLoginFailedMessage, (actualResponse as Response.Error).message)
    }

    @Test
    fun `when Register Success then Return Success`() = runTest {
        val expectedResponse = MutableLiveData<Response<Register>>()
        expectedResponse.value = Response.Success(dummyRegister)

        Mockito.`when`(storyRepositoryImpl.register(dummyName, dummyEmail, dummyPassword)).thenReturn(expectedResponse)
        val actualResponse = authViewModel.register(dummyName, dummyEmail, dummyPassword).getOrAwaitValue()
        Mockito.verify(storyRepositoryImpl).register(dummyName, dummyEmail, dummyPassword)

        assertNotNull(actualResponse)
        assertTrue(actualResponse is Response.Success)
        assertEquals(dummyRegister, (actualResponse as Response.Success).data)
    }

    @Test
    fun `when Register Failed then Return Error`() = runTest {
        val expectedResponse = MutableLiveData<Response<Register>>()
        expectedResponse.value = Response.Error(dummyRegisterFailedMessage)

        Mockito.`when`(storyRepositoryImpl.register(dummyName, dummyEmail, dummyPassword)).thenReturn(expectedResponse)
        val actualResponse = authViewModel.register(dummyName, dummyEmail, dummyPassword).getOrAwaitValue()
        Mockito.verify(storyRepositoryImpl).register(dummyName, dummyEmail, dummyPassword)

        assertNotNull(actualResponse)
        assertTrue(actualResponse is Response.Error)
        assertEquals(dummyRegisterFailedMessage, (actualResponse as Response.Error).message)
    }

    @Test
    fun `success get auth token`() = runTest {
        val expectedResponse = flow {
            emit(dummyAuthToken)
        }

        Mockito.`when`(authPreferences.authToken).thenReturn(expectedResponse)
        authViewModel.getAuthToken()
        val actualResponse = authViewModel.authToken.getOrAwaitValue()
        Mockito.verify(authPreferences).authToken

        assertNotNull(actualResponse)
        assertEquals(dummyAuthToken, actualResponse)
    }

    @Test
    fun `update auth token`() = runTest {
        authViewModel.updateAuthToken(dummyAuthToken)
        Mockito.verify(authPreferences).updateAuthToken(dummyAuthToken)
    }

    @Test
    fun `remove auth token`() = runTest {
        authViewModel.removeAuthToken()
        Mockito.verify(authPreferences).removeAuthToken()
    }
}