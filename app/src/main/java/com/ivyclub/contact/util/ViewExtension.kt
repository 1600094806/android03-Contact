package com.ivyclub.contact.util

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.core.view.isNotEmpty
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.transition.*
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.ivyclub.contact.R
import kotlin.math.roundToInt

fun <T : ViewDataBinding> ViewGroup.binding(
    @LayoutRes layoutRes: Int,
    attachToParent: Boolean = false
): T {
    return DataBindingUtil.inflate(LayoutInflater.from(context), layoutRes, this, attachToParent)
}

fun View.changeVisibilityWithDirection(direction: Int, visibility: Int, animationTime: Long) {
    val transition: Transition = TransitionSet().apply {
        addTransition(Fade())
        addTransition(Slide(direction))
        duration = animationTime
        addTarget(this@changeVisibilityWithDirection)
        addListener(object : Transition.TransitionListener {
            override fun onTransitionStart(transition: Transition) {}
            override fun onTransitionEnd(transition: Transition) {
                (this@changeVisibilityWithDirection).visibility = visibility
            }

            override fun onTransitionCancel(transition: Transition) {}
            override fun onTransitionPause(transition: Transition) {}
            override fun onTransitionResume(transition: Transition) {}
        })
    }
    TransitionManager.beginDelayedTransition(
        this.parent as ViewGroup,
        transition
    )
}

fun ViewDataBinding.hideKeyboard() {
    ViewCompat.getWindowInsetsController(this.root)?.hide(WindowInsetsCompat.Type.ime())
}

fun ChipGroup.setFriendChips(friendList: List<String>, chipCount: Int = friendList.size) {
    if (isNotEmpty()) removeAllViews()

    friendList.subList(0, chipCount.coerceAtMost(friendList.size)).forEachIndexed { index, name ->
        Chip(context).apply {
            text =
                if (friendList.size > chipCount && index == chipCount - 1) {
                    String.format(
                        context.getString(R.string.format_friend_count_etc),
                        name,
                        friendList.size - chipCount
                    )
                } else {
                    name
                }
            isEnabled = false
            setChipBackgroundColorResource(R.color.blue_100)
            setEnsureMinTouchTargetSize(false)
            chipMinHeight = 8f
        }.also {
            addView(it)
        }
    }
}

fun ViewGroup.addChips(names: List<String>, onCloseIconClick: (Int) -> (Unit)) {
    if (childCount > 1) {
        children.toList().subList(0, childCount - 1).forEach {
            removeView(it)
        }
    }

    val layoutParams = ViewGroup.MarginLayoutParams(
        ViewGroup.MarginLayoutParams.WRAP_CONTENT,
        ViewGroup.MarginLayoutParams.WRAP_CONTENT
    ).apply {
        rightMargin = context.dpToPx(4)
        topMargin = context.dpToPx(4)
    }

    names.forEachIndexed { index, name ->
        addView(
            Chip(context).apply {
                text = name
                setChipBackgroundColorResource(R.color.blue_100)
                setEnsureMinTouchTargetSize(false)
                chipMinHeight = 8f

                isCloseIconVisible = true
                setOnCloseIconClickListener {
                    onCloseIconClick(index)
                }
            }, childCount - 1, layoutParams
        )
    }
}

fun Context.dpToPx(dp: Int) =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics)
        .roundToInt()