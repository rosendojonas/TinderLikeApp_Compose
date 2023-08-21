package com.jonasrosendo.tinder.core

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class StateViewModel<T>(initialValue: T) : ViewModel() {
    protected val state = MutableStateFlow(initialValue)

    fun bindState() = state.asStateFlow()
}