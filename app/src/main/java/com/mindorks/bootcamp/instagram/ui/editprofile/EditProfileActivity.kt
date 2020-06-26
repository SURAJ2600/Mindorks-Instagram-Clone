package com.mindorks.bootcamp.instagram.ui.editprofile

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.di.component.ActivityComponent
import com.mindorks.bootcamp.instagram.ui.base.BaseActivity
import com.mindorks.bootcamp.instagram.utils.common.GlideHelper
import com.mindorks.bootcamp.instagram.utils.common.Status
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_edit_profile.pb_loading
import kotlinx.android.synthetic.main.layout_toolbar.*
import java.io.FileNotFoundException


class EditProfileActivity : BaseActivity<EditProfileViewModel>() {


    companion object {
        const val RESULT_GALLERY_IMG = 1001
        val REQUEST_IMAGE_CAPTURE = 1000
        val PARAM_TAGLINE = "TAG_LINE"

        const val TAG = "EditProfileActivity"
        fun startActivity(context: Context, paramTagline: String?) {
            context.startActivity(
                Intent(context, EditProfileActivity::class.java)
                    .putExtra(PARAM_TAGLINE, paramTagline)
            )
        }
    }


    override fun provideLayoutId() = R.layout.activity_edit_profile

    override fun injectDependencies(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }


    override fun setupObservers() {
        super.setupObservers()
        viewModel.openPickerDialog.observe(this
            , Observer {
                selectImage(this)
            })
        viewModel.loadingSaveProfile.observe(this, Observer {
            if (it) layout_progress_bar.visibility = View.VISIBLE
            else layout_progress_bar.visibility = View.GONE

        })

        viewModel.loading.observe(this, Observer {
            if (it) pb_loading.visibility = View.VISIBLE
            else pb_loading.visibility = View.GONE

        })

        viewModel.nameField.observe(this, Observer {
            if (et_name.text.toString() != it) et_name.setText(it)
        })

        viewModel.bioField.observe(this, Observer {
            if (et_bio.text.toString() != it) et_bio.setText(it)
        })

        viewModel.emailField.observe(this, Observer {
            if (et_email.text.toString() != it) et_email.setText(it)
        })

        viewModel.nameValidation.observe(this, Observer {
            when (it.status) {
                Status.ERROR -> layout_name.error = it.data?.run { getString(this) }
                else -> layout_name.isErrorEnabled = false
            }
        })

        viewModel.popStack.observe(this, Observer {
            it.getIfNotHandled()?.run {
                finish()
            }
        })

        viewModel.profileImage.observe(this, Observer {
            it?.apply {
                val glideRequest = Glide
                    .with(this@EditProfileActivity)
                    .load(if(loadFromNetwork) GlideHelper.getProtectedUrl(url, headers) else url)
                    .apply(RequestOptions.circleCropTransform())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_profile_add_pic))

                if (placeholderWidth > 0 && placeholderHeight > 0) {
                    val params = iv_profile_pic.layoutParams as ViewGroup.LayoutParams
                    params.width = placeholderWidth
                    params.height = placeholderHeight
                    iv_profile_pic.layoutParams = params
                    glideRequest
                        .apply(RequestOptions.overrideOf(placeholderWidth, placeholderHeight))
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_profile_signup))
                }
                glideRequest.into(iv_profile_pic)
            }

        })


    }

    override fun setupView(savedInstanceState: Bundle?) {

        intent?.extras?.let {
            viewModel.onBioChanged(it.getString(PARAM_TAGLINE, ""))
        }

        et_name.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onNameChange(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })
        et_bio.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onBioChanged(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })






        iv_profile_pic.setOnClickListener {
            viewModel.openPickerDialog()
        }

        tv_change_photo.setOnClickListener {
            viewModel.openPickerDialog()

        }
        iv_tool_bar_tick_icon.setOnClickListener {
            viewModel.uploadUserProfile()
        }

        iv_tool_bar_close_icon.setOnClickListener {
            finish()
        }

    }


    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun selectImage(context: Context) {
        val options =
            arrayOf<CharSequence>(
                getString(R.string.take_photo),
                getString(R.string.choose_from_gallery),
                getString(R.string.cancel)
            )
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(getString(R.string.choose_profile_picture))
        builder.setItems(options, DialogInterface.OnClickListener { dialog, item ->
            if (options[item] == getString(R.string.take_photo)) {
                dispatchTakePictureIntent()
            } else if (options[item] == getString(R.string.choose_from_gallery)) {
                Intent(Intent.ACTION_GET_CONTENT)
                    .apply {
                        type = "image/*"
                    }.run {
                        startActivityForResult(this, RESULT_GALLERY_IMG)
                    }

            } else if (
                options[item] == getString(R.string.cancel)) {
                dialog.dismiss()
            }
        })
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RESULT_GALLERY_IMG -> {
                    try {
                        data?.data?.let {
                            contentResolver?.openInputStream(it)?.run {
                                viewModel.holdProfileImageFromInputStreams(
                                    this,
                                    this@EditProfileActivity
                                )
                            }
                        } ?: showMessage(R.string.please_try_latter)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                        showMessage(R.string.please_try_latter)
                    }
                }
                REQUEST_IMAGE_CAPTURE -> {
                    viewModel.holdProfileImageFromBitmap(data?.extras?.get("data") as Bitmap, this)


                }
            }
        }
    }
}