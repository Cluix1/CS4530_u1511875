package com.example.marblegame

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MarblePhysicsTest {
    @Test
    fun tiltMovesMarbleInScreenDirections() {
        val physics = MarblePhysics()
        physics.setBounds(400f, 600f)
        repeat(30) { physics.update(0.02f, -3f, 3f) }
        assertTrue(physics.position.x > 200f)
        assertTrue(physics.position.y > 300f)

        physics.reset()
        repeat(30) { physics.update(0.02f, 3f, -3f) }
        assertTrue(physics.position.x < 200f)
        assertTrue(physics.position.y < 300f)
    }

    @Test
    fun marbleStaysInsideEveryEdgeAndCanRollAway() {
        val physics = MarblePhysics()
        physics.setBounds(100f, 200f)
        repeat(300) { physics.update(0.02f, -9.8f, 9.8f) }
        assertEquals(100f, physics.position.x, 0f)
        assertEquals(200f, physics.position.y, 0f)
        physics.update(0.02f, 9.8f, -9.8f)
        assertTrue(physics.position.x < 100f)
        assertTrue(physics.position.y < 200f)
        repeat(300) { physics.update(0.02f, 9.8f, -9.8f) }
        assertEquals(0f, physics.position.x, 0f)
        assertEquals(0f, physics.position.y, 0f)
    }

    @Test
    fun frictionSlowsMarbleWhenDeviceIsFlat() {
        val physics = MarblePhysics()
        physics.setBounds(10000f, 10000f)
        repeat(30) { physics.update(0.02f, -3f, 0f) }
        val startX = physics.position.x
        physics.update(0.02f, 0f, 0f)
        val firstDistance = physics.position.x - startX
        repeat(100) { physics.update(0.02f, 0f, 0f) }
        val laterX = physics.position.x
        physics.update(0.02f, 0f, 0f)
        val laterDistance = physics.position.x - laterX
        assertTrue(laterDistance >= 0f)
        assertTrue(laterDistance < firstDistance / 10f)
    }

    @Test
    fun resizingClampsPositionAndResetCentersIt() {
        val physics = MarblePhysics()
        physics.setBounds(400f, 600f)
        physics.setBounds(100f, 80f)
        assertEquals(MarblePosition(100f, 80f), physics.position)
        physics.reset()
        assertEquals(MarblePosition(50f, 40f), physics.position)
        physics.setBounds(-10f, -10f)
        assertEquals(MarblePosition(), physics.position)
    }

    @Test
    fun pauseStopsOldVelocityAndLongFramesAreLimited() {
        val physics = MarblePhysics()
        physics.setBounds(400f, 600f)
        repeat(20) { physics.update(0.02f, -3f, 3f) }
        physics.stop()
        val pausedPosition = physics.position
        physics.update(3f, 0f, 0f)
        assertEquals(pausedPosition, physics.position)

        physics.reset()
        physics.update(3f, -3f, 3f)
        val delayedPosition = physics.position
        physics.reset()
        physics.update(0.05f, -3f, 3f)
        assertEquals(delayedPosition, physics.position)
    }
}
