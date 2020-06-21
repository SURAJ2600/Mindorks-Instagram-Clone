package com.mindorks.bootcamp.instagram.ui.home

import androidx.lifecycle.MutableLiveData
import com.mindorks.bootcamp.instagram.data.model.Post
import com.mindorks.bootcamp.instagram.data.repository.PostRepository
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.ui.base.BaseViewModel
import com.mindorks.bootcamp.instagram.utils.common.Resource
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import org.jetbrains.annotations.Async

class HomeViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val list: ArrayList<Post>,
    private val userRepository: UserRepository,
    private val paginator: PublishProcessor<Pair<String?, String?>>,
    private val postRepository: PostRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {


    val loading: MutableLiveData<Boolean> = MutableLiveData()
    val mPosts: MutableLiveData<Resource<List<Post>>> = MutableLiveData()
    private val user = userRepository.getCurrentUser()!!


    init {
        compositeDisposable.add(
            paginator
                .onBackpressureDrop()
                .doOnNext {
                    loading.postValue(true)
                }
                .concatMapSingle { pageIds ->
                    return@concatMapSingle postRepository
                        .fetchHomePostList(pageIds.first, pageIds.second, user)
                        .subscribeOn(schedulerProvider.io())
                        .doOnError {
                            loading.postValue(false)
                            handleNetworkError(it)
                        }
                }
                .subscribe(
                    {
                        list.addAll(it)
                        loading.postValue(false)
                        mPosts.postValue(Resource.success(it))
                    },
                    {
                        loading.postValue(false)
                        handleNetworkError(it)
                    }
                )
        )
    }


   private fun loadMore(){
        val firstPostId = if(list.isNotEmpty()) list[0].id else null
        val lastPostId  = if(list.size > 1 ) list[list.size - 1].id else null
        if(checkInternetConnection()) paginator.onNext(Pair(firstPostId,lastPostId))
    }
    fun onLoadMore(){
        if( loading.value !== null && loading.value == false) loadMore()
    }




    override fun onCreate() {
        loadMore()
    }
}