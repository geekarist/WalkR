package me.cpele.baladr

class LiveEvent<T>(private val value: T) {

    private var isConsumed: Boolean = false

    val consumed: T
        get() {
            synchronized(this) {
                isConsumed = true
                return value
            }
        }

}