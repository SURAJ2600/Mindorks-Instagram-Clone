package com.mindorks.bootcamp.instagram.data.repository

import com.mindorks.bootcamp.instagram.data.model.User
import com.mindorks.bootcamp.instagram.data.remote.NetworkService
import com.mindorks.bootcamp.instagram.data.remote.Networking
import com.mindorks.bootcamp.instagram.data.remote.response.PostListResponse
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import io.reactivex.Single
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class PostRepositoryTest {
    @Mock
    lateinit var networkService: NetworkService

    lateinit var postRepository: PostRepository


    @Before
    fun init() {
        Networking.API_KEY = "xyz"
        postRepository = PostRepository(networkService)
    }

    @Test
    fun fetchHomePostList_requestDoHomePOstListApiCall() {
        val email = "suraj260@gmail.com"
        val password = "123456"
        val user = User("id", "suraj", email, "acsksus")
        doReturn(Single.just(PostListResponse("200", "Success", listOf())))
            .`when`(networkService)
            .getPosts(
                "1", "2",
                user.id, user.accessToken, Networking.API_KEY
            )
        postRepository.fetchHomePostList("1", "2", user)
        verify(networkService).getPosts(
            "1", "2",
            user.id, user.accessToken, Networking.API_KEY
        )


    }


}