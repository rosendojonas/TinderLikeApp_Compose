package com.jonasrosendo.tinder.shared_logic.either

sealed class Either<out L, out R> {
    data class Left<out L>(val valueLeft: L) : Either<L, Nothing>()

    data class Right<out R>(val valueRight: R) : Either<Nothing, R>()

    val isRight get() = this is Right<R>

    val isLeft get() = this is Left<L>

    fun <L> left(valueLeft: L) = Left(valueLeft)

    fun <R> right(valueRight: R) = Right(valueRight)

    fun fold(fnLeft: (L) -> Unit, fnRight: (R) -> Unit) {
        when (this) {
            is Left -> fnLeft(valueLeft)
            is Right -> fnRight(valueRight)
        }
    }
}