package com.security.passwordmanager.presentation.model.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.security.passwordmanager.data.UID

class ComposableChildren <T : ComposableChild> (
    override val children: SnapshotStateList<T> = mutableStateListOf()
) : ComposableList<T> {

    val info: MutableMap<UID, ChildStatus> = mutableMapOf()

    constructor(children: Collection<T>) :
            this(mutableStateListOf<T>().apply { addAll(children) })


    override fun add(element: T): Boolean {
        info[element.uid] = ChildStatus.Created
        return super.add(element)
    }


    fun update(id: UID, action: T.() -> Unit = {}) {
        info.putIfAbsent(id, ChildStatus.Updated)
        children
            .find { it.uid == id }
            ?.let(action)
    }


    override fun remove(element: T): Boolean {
        info[element.uid] = ChildStatus.Deleted
        return super.remove(element)
    }
}


enum class ChildStatus {
    Created,
    Updated,
    Deleted
}



private interface ComposableList<T> : MutableList<T> {
    val children: SnapshotStateList<T>

    override val size get() = children.size

    override fun contains(element: T) = children.contains(element)

    override fun containsAll(elements: Collection<T>) = children.containsAll(elements)

    override fun add(element: T) = children.add(element)

    override fun add(index: Int, element: T) = children.add(index, element)

    override fun addAll(index: Int, elements: Collection<T>) =
        children.addAll(index, elements)

    override fun addAll(elements: Collection<T>) =
        children.addAll(elements)

    override fun clear() = children.clear()

    override fun get(index: Int): T = children[index]

    override fun isEmpty() = children.isEmpty()

    override fun iterator() = children.iterator()

    override fun listIterator() = children.listIterator()

    override fun listIterator(index: Int) = children.listIterator(index)

    override fun removeAt(index: Int) = children.removeAt(index)

    override fun subList(fromIndex: Int, toIndex: Int) =
        children.subList(fromIndex, toIndex)

    override fun set(index: Int, element: T) =
        children.set(index, element)

    override fun retainAll(elements: Collection<T>) =
        children.retainAll(elements)

    override fun removeAll(elements: Collection<T>) =
        children.removeAll(elements)

    override fun remove(element: T) = children.remove(element)

    override fun lastIndexOf(element: T) = children.lastIndexOf(element)

    override fun indexOf(element: T) = children.indexOf(element)
}