package com.mindorks.bootcamp.instagram.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.mindorks.bootcamp.instagram.data.model.Image
import com.mindorks.bootcamp.instagram.data.model.Post
import com.mindorks.bootcamp.instagram.data.model.ProfileUser
import com.mindorks.bootcamp.instagram.data.model.User
import com.mindorks.bootcamp.instagram.data.remote.Networking
import com.mindorks.bootcamp.instagram.data.repository.PostRepository
import com.mindorks.bootcamp.instagram.data.repository.ProfileRepository
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.ui.base.BaseViewModel
import com.mindorks.bootcamp.instagram.utils.common.Event
import com.mindorks.bootcamp.instagram.utils.common.Resource
import com.mindorks.bootcamp.instagram.utils.common.TimeUtils
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class ProfileViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val profileRepository: ProfileRepository,
    private val userRepository: UserRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    private val user = userRepository.getCurrentUser()!!

    private val userInformation = MutableLiveData<ProfileUser>()
    val loading: MutableLiveData<Boolean> = MutableLiveData()
    val mPosts: MutableLiveData<List<Post>> = MutableLiveData()

    val launchSplash: MutableLiveData<Event<Map<String, String>>> = MutableLiveData()


    val name: LiveData<String> = Transformations.map(userInformation) { it.name }
    val tagLine: LiveData<String> = Transformations.map(userInformation) { it.tagline }
    val postCount: LiveData<Int> = Transformations.map(mPosts) { it?.size ?: 0 }
    val profileImage: LiveData<Image> = Transformations.map(userInformation) {
        it.profilePicUrl?.run { Image(this, headers) }
    }
    private val headers = mapOf(
        Pair(Networking.HEADER_API_KEY, Networking.API_KEY),
        Pair(Networking.HEADER_USER_ID, user.id),
        Pair(Networking.HEADER_ACCESS_TOKEN, user.accessToken)
    )

    override fun onCreate() {
        getProfileDetails()
    }


    fun logoutUser() {
        loading.postValue(true)
        compositeDisposable.add(
            userRepository.doUserLogout(user)
                .subscribeOn(Schedulers.io())

                .subscribe({
                    userRepository.removeCurrentUser()
                    loading.postValue(false)
                    launchSplash.postValue(Event(emptyMap()))
                },
                    {
                        handleNetworkError(it)
                    })
        )


    }

    fun getProfileDetails() {
        loading.postValue(true)
        compositeDisposable.add(profileRepository.fetchUserProfile(user).flatMap {
            userInformation.postValue(it)
            return@flatMap io.reactivex.Single.just(it)
        }
            .doOnError { handleNetworkError(it) }
            .flatMap {
                return@flatMap profileRepository.fetchMyPosts(user)
            }
            .subscribeOn(Schedulers.io())
            .subscribe({
                loading.postValue(false)
                mPosts.postValue(it)
            },
                {

                    loading.postValue(false)
                    handleNetworkError(it)
                })
        )

    }
}