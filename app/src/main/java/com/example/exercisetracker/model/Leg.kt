package com.example.exercisetracker.model

import com.example.exercisetracker.pose.Joint

data class Leg(
    val hip: Joint,
    val knee: Joint,
    val ankle: Joint
)