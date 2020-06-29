package com.mindorks.bootcamp.instagram.data.repository

import com.mindorks.bootcamp.instagram.data.model.Post
import com.mindorks.bootcamp.instagram.data.model.User
import com.mindorks.bootcamp.instagram.data.remote.NetworkService
import com.mindorks.bootcamp.instagram.data.remote.request.PostCreationRequest
import com.mindorks.bootcamp.instagram.data.remote.request.PostLikeModifyRequest
import io.reactivex.Single
import javax.inject.Inject

class PostRepository @Inject constructor(private val networkService: NetworkService) {

    fun fetchHomePostList(
        firstPostId: String?,
        lastPostId: String?,
        user: User
    ): Single<List<Post>> {
        return networkService.getPosts(
            firstPostId = firstPostId,
            lastPostId = lastPostId,
            userId = user.id,
            accessToken = user.accessToken
        )
            .map { it.data }
    }


    fun likeHomePost(post: Post, user: User): Single<Post> {
        return networkService.likePost(
            PostLikeModifyRequest(postId = post.id),
            userId = user.id,
            accessToken = user.accessToken
        ).map {
            post.likedBy?.apply {
                this.find { postUser -> postUser.id == user.id } ?: this.add(
                    Post.User(
                        user.id,
                        user.name,
                        user.profilePicUrl
                    )
                )
            }
            return@map post
        }

    }

    fun unLikeHomePost(post: Post, user: User): Single<Post> {
        return networkService.unlikePost(
            PostLikeModifyRequest(postId = post.id),
            userId = user.id,
            accessToken = user.accessToken
        ).map {
            post.likedBy?.apply {
                this.find { postUser -> postUser.id == user.id }?.let {
                    this.remove(
                        Post.User(
                            user.id,
                            user.name,
                            user.profilePicUrl
                        ))
                }
            }
            return@map post
        }

    }
    fun createPost(imgUrl: String, imgWidth: Int, imgHeight: Int, user: User): Single<Post> =
        networkService.doPostCreationCall(
            PostCreationRequest(imgUrl, imgWidth, imgHeight), user.id, user.accessToken
        ).map {
            Post(
                it.data.id,
                it.data.imageUrl,
                it.data.imageWidth,
                it.data.imageHeight,
                Post.User(
                    user.id,
                    user.name,
                    user.profilePicUrl
                ),
                mutableListOf(),
                it.data.createdAt
            )
        }
}