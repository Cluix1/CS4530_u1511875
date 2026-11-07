package com.example.marblegame

import kotlin.math.exp

data class MarblePosition(val x: Float = 0f, val y: Float = 0f)

class MarblePhysics {
    var position = MarblePosition()
        private set
    private var velocityX = 0f
    private var velocityY = 0f
    private var maxX = 0f
    private var maxY = 0f
    private var hasBounds = false

    fun setBounds(width: Float, height: Float) {
        maxX = width.coerceAtLeast(0f)
        maxY = height.coerceAtLeast(0f)
        if (!hasBounds) {
            hasBounds = true
            reset()
        } else {
            position = MarblePosition(position.x.coerceIn(0f, maxX), position.y.coerceIn(0f, maxY))
            stop()
        }
    }

    fun reset() {
        position = MarblePosition(maxX / 2f, maxY / 2f)
        stop()
    }

    fun stop() {
        velocityX = 0f
        velocityY = 0f
    }

    fun update(dt: Float, gravityX: Float, gravityY: Float) {
        if (!hasBounds || dt <= 0f) return
        // Limit the time step so a delayed frame does not cause a big jump.
        val seconds = dt.coerceAtMost(0.05f)
        val friction = exp(-2f * seconds)
        // Sensor x points right and y points up. Screen offsets point down.
        velocityX = (velocityX - gravityX * 70f * seconds) * friction
        velocityY = (velocityY + gravityY * 70f * seconds) * friction
        val nextX = position.x + velocityX * seconds
        val nextY = position.y + velocityY * seconds
        val x = nextX.coerceIn(0f, maxX)
        val y = nextY.coerceIn(0f, maxY)
        if (nextX <= 0f || nextX >= maxX) velocityX = 0f
        if (nextY <= 0f || nextY >= maxY) velocityY = 0f
        position = MarblePosition(x, y)
    }
}
