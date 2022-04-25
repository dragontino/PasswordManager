package com.security.passwordmanager

import androidx.recyclerview.widget.RecyclerView

interface RecyclerCallback {
    fun onScroll(recyclerDirection: RecyclerDirection, currentState: RecyclerState)
    fun onStateChanged(newState: RecyclerState)
}

enum class RecyclerDirection {
    DOWN,
    UP,
    STOPPED
}

enum class RecyclerState {
    STOPPED,
    SCROLLED_BY_USER,
    SCROLLED_AUTO;

    companion object {
        fun getState(state: Int) = when (state) {
            RecyclerView.SCROLL_STATE_IDLE -> STOPPED
            RecyclerView.SCROLL_STATE_DRAGGING -> SCROLLED_BY_USER
            else -> SCROLLED_AUTO
        }
    }
}