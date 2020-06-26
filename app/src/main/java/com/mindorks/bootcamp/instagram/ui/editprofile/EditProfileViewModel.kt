package com.mindorks.bootcamp.instagram.ui.editprofile

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.data.model.Image
import com.mindorks.bootcamp.instagram.data.model.User
import com.mindorks.bootcamp.instagram.data.remote.Networking
import com.mindorks.bootcamp.instagram.data.repository.EditProfileRepository
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.ui.base.BaseViewModel
import com.mindorks.bootcamp.instagram.utils.common.*
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import id.zelory.compressor.Compressor
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.URI


class EditProfileViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val userRepository: UserRepository,
    private val editProfileRepository: EditProfileRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {


    private val validationsList: MutableLiveData<List<Validation>> = MutableLiveData()

    val loadingSaveProfile = MutableLiveData<Boolean>()

    val popStack: MutableLiveData<Event<Map<String, String>>> = MutableLiveData()
    val user = userRepository.getCurrentUser()!!

    val nameField: MutableLiveData<String> = MutableLiveData()
    val bioField: MutableLiveData<String> = MutableLiveData()
    val emailField: MutableLiveData<String> = MutableLiveData()
    val profileImage: MutableLiveData<Image> = MutableLiveData()

    private val headers = mapOf(
        Pair(Networking.HEADER_API_KEY, Networking.API_KEY),
        Pair(Networking.HEADER_USER_ID, user.id),
        Pair(Networking.HEADER_ACCESS_TOKEN, user.accessToken)
    )

    fun onNameChange(name: String) = nameField.postValue(name)
    fun onBioChanged(bio: String) = bioField.postValue(bio)
    val nameValidation: LiveData<Resource<Int>> = filterValidation(Validation.Field.NAME)

    private fun filterValidation(name: Validation.Field) =
        Transformations.map(validationsList) {
            it.find { validation -> validation.field == name }
                ?.run { return@run this.resource }
                ?: Resource.unknown()
        }


    private val imageFile: MutableLiveData<File> = MutableLiveData()

    val openPickerDialog = MutableLiveData<Event<Boolean>>()
    val loading: MutableLiveData<Boolean> = MutableLiveData()
    private var mContext: Context? = null


    init {
        user.run {
            profileImage.postValue(Image(this.profilePicUrl ?: "" ,headers))
            nameField.postValue(this.name ?: "")
            emailField.postValue(this.email ?: "")
        }
    }

    override fun onCreate() {

    }

    fun openPickerDialog() {
        openPickerDialog.postValue(Event(true))
    }

    fun uploadUserProfile() {

        val validations = Validator.validateProfileFields(nameField.value)
        validationsList.postValue(validations)

        if (validations.isNotEmpty() && nameField.value != null) {
            val successValidation = validations.filter { it.resource.status == Status.SUCCESS }
            if (successValidation.size == validations.size && checkInternetConnectionWithMessage()) {

                loadingSaveProfile.postValue(true)
                imageFile.value?.let {
                    compositeDisposable.add(editProfileRepository.uploadPhoto(it, user)
                        .doOnError {
                            editProfileRepository.updateUserDetails(
                                user,
                                nameField.value,
                                bioField.value,
                                null
                            )
                        }
                        .flatMapCompletable {
                            editProfileRepository.updateUserDetails(
                                user,
                                nameField.value,
                                bioField.value,
                                it
                            )
                        }
                        .subscribeOn(schedulerProvider.io())
                        .subscribe({
                            loadingSaveProfile.postValue(false)
                            popStack.postValue(Event(emptyMap()))


                        }, {
                            handleNetworkError(it)
                            loadingSaveProfile.postValue(false)
                        })

                    )
                } ?: kotlin.run {
                    compositeDisposable.addAll(
                        editProfileRepository.updateUserDetails(
                            user,
                            nameField.value,
                            bioField.value,
                            null
                        ).subscribeOn(schedulerProvider.io())
                            .subscribe({
                                loadingSaveProfile.postValue(false)
                                popStack.postValue(Event(emptyMap()))

                            }, {
                                loadingSaveProfile.postValue(false)
                                handleNetworkError(it)

                            })
                    )
                }

            }
        }
    }

    fun holdProfileImageFromBitmap(bitmap: Bitmap, context: Context) {
        mContext = context
        loading.postValue(true)
        compositeDisposable.add(
            Single.fromCallable {
                Compressor.getDefault(mContext)
                    .compressToFile(File(persistImage(context, bitmap, "temp")));
            }.subscribeOn(schedulerProvider.io())
                .subscribe({
                    imageFile.postValue(it)
                    profileImage.postValue(Image(Uri.fromFile((it)).toString(),headers,loadFromNetwork = false))

                    mContext = null
                    loading.postValue(false)

                }, {
                    loading.postValue(false)
                })
        )

    }

    fun holdProfileImageFromInputStreams(inputStream: InputStream, context: Context) {
        mContext = context
        loading.postValue(true)
        compositeDisposable.add(
            Single.fromCallable {
                Compressor.getDefault(mContext)
                    .compressToFile(File(convertInputToFile(inputStream, mContext)));
            }

                .subscribeOn(schedulerProvider.io())
                .subscribe({
                    imageFile.postValue(it)
                    mContext = null
                    profileImage.postValue(Image(Uri.fromFile((it)).toString(),headers,loadFromNetwork = false))

                    loading.postValue(false)


                }, {
                    loading.postValue(false)
                })
        )

    }

    private fun persistImage(context: Context, bitmap: Bitmap, name: String): String? {
        val filesDir: File = context.getFilesDir()
        val imageFile = File(filesDir, "$name.jpg")
        val os: OutputStream
        try {
            os = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
            os.flush()
            os.close()
        } catch (e: Exception) {
            Log.e(javaClass.simpleName, "Error writing bitmap", e)
        }
        return imageFile.path
    }

    fun convertInputToFile(inputStream: InputStream, context: Context?): String? {
        val filesDir: File = context?.getFilesDir()!!
        val imageFile = File(filesDir, "Temp.jpg")
        try {

            FileOutputStream(imageFile).use { output ->
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int = 0
                while (inputStream.read(buffer).also({ read = it }) != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
        } finally {
            inputStream.close()
        }
        return imageFile.path
    }

}
