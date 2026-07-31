package com.example.exercisetracker.exercise.classifier

import com.example.exercisetracker.exercise.ExerciseType
import com.example.exercisetracker.pose.Joint
import com.example.exercisetracker.pose.testPose
import org.junit.Assert.assertEquals
import org.junit.Test

class DefaultExerciseClassifierTest {
    private val classifier = DefaultExerciseClassifier()

    @Test
    fun classify_horizontalTorso_returnsPushUp() {
        val pose = testPose().copy(
            leftShoulder = Joint(0f, -0.1f, 1f),
            rightShoulder = Joint(0f, 0.1f, 1f),
            leftHip = Joint(1f, -0.1f, 1f),
            rightHip = Joint(1f, 0.1f, 1f)
        )

        assertEquals(ExerciseType.PUSH_UP, classifier.classify(pose))
    }

    @Test
    fun classify_verticalTorso_returnsSquat() {
        val pose = testPose().copy(
            leftShoulder = Joint(-0.1f, 0f, 1f),
            rightShoulder = Joint(0.1f, 0f, 1f),
            leftHip = Joint(-0.1f, 1f, 1f),
            rightHip = Joint(0.1f, 1f, 1f)
        )

        assertEquals(ExerciseType.SQUAT, classifier.classify(pose))
    }
}
