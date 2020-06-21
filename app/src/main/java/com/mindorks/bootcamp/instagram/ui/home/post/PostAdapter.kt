package com.mindorks.bootcamp.instagram.ui.home.post

import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import com.mindorks.bootcamp.instagram.data.model.Post
import com.mindorks.bootcamp.instagram.ui.base.BaseAdapter
import com.mindorks.bootcamp.instagram.ui.base.BaseItemViewHolder

class PostAdapter(lifecycle: Lifecycle,postList:ArrayList<Post>) : BaseAdapter<Post, PostItemViewHolder>(lifecycle,postList){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=PostItemViewHolder(parent)
}