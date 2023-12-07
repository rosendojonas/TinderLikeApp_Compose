package com.jonasrosendo.tinder.core

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<ViewState, ViewEvent, ViewAction> : ViewModel() {
    protected abstract val initialViewState: ViewState
    protected abstract fun processAction(action: ViewAction)

    protected open var lastViewState: ViewState = initialViewState

    private val _viewState = MutableStateFlow(value = initialViewState)
    val viewState = _viewState.asStateFlow()

    private val _viewEvents = Channel<ViewEvent?>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    private val _viewActions = Channel<ViewAction>(Channel.BUFFERED)

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    init {
        coroutineScope.launch {
            _viewActions.consumeEach { action ->
                processAction(action)
            }
        }
    }
    protected open fun updateViewState(state: ViewState) {
        _viewState.value = state
        lastViewState = state
    }

    protected fun sendEvent(event: ViewEvent) = coroutineScope.launch {
        _viewEvents.send(event)
    }

    fun applyAction(action: ViewAction) = coroutineScope.launch {
        _viewActions.send(action)
    }
}