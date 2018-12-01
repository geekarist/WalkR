package me.cpele.baladr

class LiveEvent<T>(private val value: T) {

    private var isConsumed: Boolean = false

    val consumed: T?
        get() {
            return if (isConsumed) {
                null
            } else {
                synchronized(this) {
                    isConsumed = true
                    value
                }
            }
        }

}