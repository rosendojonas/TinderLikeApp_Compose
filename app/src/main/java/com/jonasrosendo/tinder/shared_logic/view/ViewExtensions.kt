package com.jonasrosendo.tinder.shared_logic.view

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

fun <T> Fragment.observeState(stateFlow: StateFlow<T>, onChanged: (t: T) -> Unit) {
    lifecycleScope.launch {
        stateFlow.flowWithLifecycle(lifecycle).collect {
            onChanged(it)
        }
    }
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}