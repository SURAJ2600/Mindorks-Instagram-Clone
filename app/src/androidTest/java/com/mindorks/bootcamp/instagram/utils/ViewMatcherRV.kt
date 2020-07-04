package com.mindorks.bootcamp.instagram.utils

import android.view.View
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView

import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher


object ViewMatcherRV {

    fun atPosition(position: Int,  itemMatcher: Matcher<View>,targetViewId:Int): BoundedMatcher<View?, RecyclerView> {
        checkNotNull(itemMatcher)
        return object : BoundedMatcher<View?, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has item at position $position: ")
                itemMatcher.describeTo(description)
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                val viewHolder =
                    view.findViewHolderForAdapterPosition(position)
                        ?: // has no item on such position
                        return false
                val targetView = viewHolder!!.itemView.findViewById<View>(targetViewId)
                return itemMatcher.matches(targetView)
            }
        }
    }
}