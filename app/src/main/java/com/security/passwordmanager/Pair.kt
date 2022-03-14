package com.security.passwordmanager

class Pair<X, Y>(var first: X, var second: Y)

operator fun <A>Pair<A, A>.get(index : Int): A = if (index == 0) first
else second

operator fun <A>Pair<A, A>.set(index: Int, value : A) = if (index == 0) first = value
else second = value

fun <A>Pair<A, A>.setAll(block: (element: A) -> Unit) {
    block(first)
    block(second)
}

fun <B>getPair(block: (position: Int) -> B) =
    Pair(block(0), block(1))