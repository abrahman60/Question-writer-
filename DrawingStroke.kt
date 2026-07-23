package com.example.data.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

data class PathPoint(val x: Float, val y: Float)

data class DrawingStroke(
    val points: List<PathPoint> = emptyList(),
    val colorArgb: Int = Color.Black.toArgb(),
    val strokeWidth: Float = 5f,
    val isEraser: Boolean = false
)
