package com.mindorks.bootcamp.instagram.data.remote


import com.mindorks.bootcamp.instagram.data.model.Post
import com.mindorks.bootcamp.instagram.data.remote.request.*
import com.mindorks.bootcamp.instagram.data.remote.response.*
import io.reactivex.Single
import okhttp3.MultipartBody
import java.util.*

class FakeNetworkService : NetworkService {
    override fun doDummyCall(request: DummyRequest, apiKey: String): Single<DummyResponse> {
        TODO("Not yet implemented")
    }

    override fun doLoginCall(request: LoginRequest, apiKey: String): Single<LoginResponse> {
        return Single.just(
            LoginResponse(
                "statusCode",
                200,
                "Login SuccessFull",
                "ACSSS",
                "userId",
                "userName",
                "userEmail",
                "profilePic"
            )
        )
    }

    override fun doSignUp(request: SignUpRequest, apiKey: String): Single<SignUpResponse> {
        TODO("Not yet implemented")
    }

    override fun getPosts(
        firstPostId: String?,
        lastPostId: String?,
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<PostListResponse> {
        val creator = Post.User("userId1", "name1", "profilePic")
        val creator2 = Post.User("userId1", "name1", "profilePic")
        val likedBy = mutableListOf(
            Post.User("userId3", "name3", "profilePic"),
            Post.User("userId4", "name4", "profilePic")
        )
        val post1 = Post("postId1", "imageUrl", 400, 400, creator, likedBy, Date())
        val post2 = Post("postId2", "imageUrl", 400, 400, creator2, likedBy, Date())
        val postListResponse = PostListResponse("statusCode", "success", listOf(post1, post2))
        return Single.just(postListResponse)

    }

    override fun likePost(
        postLikeModifyRequest: PostLikeModifyRequest,
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<GeneralResponse> {
        TODO("Not yet implemented")
    }

    override fun unlikePost(
        postLikeModifyRequest: PostLikeModifyRequest,
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<GeneralResponse> {
        TODO("Not yet implemented")
    }

    override fun getUserProfileInformation(
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<UserProfileResponse> {
        TODO("Not yet implemented")
    }

    override fun getMyPosts(
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<MyPostListResponse> {
        TODO("Not yet implemented")
    }

    override fun logoutUser(
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<GeneralResponse> {
        TODO("Not yet implemented")
    }

    override fun doProfileImageUpload(
        image: MultipartBody.Part,
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<ImageResponse> {
        TODO("Not yet implemented")
    }

    override fun updateUserProfile(
        body: UpdateUserProfile,
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<GeneralResponse> {
        TODO("Not yet implemented")
    }

    override fun doPostCreationCall(
        request: PostCreationRequest,
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<PostCreationResponse> {
        TODO("Not yet implemented")
    }
}