package com.mindorks.bootcamp.instagram.data.repository

import com.mindorks.bootcamp.instagram.data.model.Post
import com.mindorks.bootcamp.instagram.data.model.ProfileUser
import com.mindorks.bootcamp.instagram.data.model.User
import com.mindorks.bootcamp.instagram.data.remote.NetworkService
import io.reactivex.Single
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val networkService: NetworkService) {

    fun fetchUserProfile(user: User): Single<ProfileUser> {
        return networkService.getUserProfileInformation(
            userId = user.id,
            accessToken = user.accessToken
        )
            .map {
                it.data
            }
    }


    fun fetchMyPosts(user: User): Single<List<Post>> {
        return networkService.getMyPosts(
            userId = user.id,
            accessToken = user.accessToken
        )
            .map {
                it.myPosts

            }
    }

}