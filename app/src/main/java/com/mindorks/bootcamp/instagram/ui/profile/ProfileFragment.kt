package com.mindorks.bootcamp.instagram.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.di.component.FragmentComponent
import com.mindorks.bootcamp.instagram.ui.base.BaseFragment
import com.mindorks.bootcamp.instagram.ui.login.LoginActivity
import com.mindorks.bootcamp.instagram.ui.splash.SplashActivity
import com.mindorks.bootcamp.instagram.utils.common.GlideHelper
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.item_view_post.view.*

class ProfileFragment : BaseFragment<ProfileViewModel>() {

    companion object {

        const val TAG = "ProfileFragment"

        fun newInstance(): ProfileFragment {
            val args = Bundle()
            val fragment = ProfileFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_profile

    override fun injectDependencies(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }

    override fun setupObservers() {
        super.setupObservers()

      viewModel.apply {



          loading.observe(this@ProfileFragment, Observer {
             if(it) pb_loading.visibility=View.VISIBLE
              else pb_loading.visibility=View.GONE
          })

          launchSplash.observe(this@ProfileFragment, Observer {
              it.getIfNotHandled()?.run { startActivity(Intent(activity!!, SplashActivity::class.java))
              }
          })

          name.observe(this@ProfileFragment, Observer {
              tvName.text =it

          })
          profileImage.observe(this@ProfileFragment, Observer {
              it?.apply {
                  val glideRequest = Glide
                      .with(this@ProfileFragment)
                      .load(GlideHelper.getProtectedUrl(url, headers))

                  if (placeholderWidth > 0 && placeholderHeight > 0) {
                      val params = ivProfile.layoutParams as ViewGroup.LayoutParams
                      params.width = placeholderWidth
                      params.height = placeholderHeight
                      ivProfile.layoutParams = params
                      glideRequest
                          .apply(RequestOptions.overrideOf(placeholderWidth, placeholderHeight))
                          .apply(RequestOptions.placeholderOf(R.drawable.ic_profile_signup))
                  }
                  glideRequest.into(ivProfile.ivPost)
              }

          })
          tagLine.observe(this@ProfileFragment, Observer {
              tvProfileDescription.text =it

          })
          postCount.observe(this@ProfileFragment, Observer {
              tvPostcount.text =it.toString()

          })

      }


    }

    override fun setupView(view: View) {
        tvLogout.setOnClickListener {
            viewModel.logoutUser()
        }
    }

}