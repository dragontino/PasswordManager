package com.security.passwordmanager.presentation.model.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.security.passwordmanager.data.UID

class ComposableChildren<T : ComposableChild>(
    private val children: SnapshotStateList<T> = mutableStateListOf()
) : MutableList<T> by children {

    val info: MutableMap<UID, ChildStatus> = mutableMapOf()

    constructor(children: Collection<T>) :
            this(mutableStateListOf<T>().apply { addAll(children) })


    override fun add(element: T): Boolean {
        info[element.uid] = ChildStatus.Created
        return children.add(element)
    }


    fun update(id: UID, action: T.() -> Unit = {}) {
        info.putIfAbsent(id, ChildStatus.Updated)
        children
            .find { it.uid == id }
            ?.let(action)
    }


    override fun remove(element: T): Boolean {
        info[element.uid] = ChildStatus.Deleted
        return children.remove(element)
    }
}


enum class ChildStatus {
    Created,
    Updated,
    Deleted
}