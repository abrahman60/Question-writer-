package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DrawingStroke
import com.example.data.model.PathPoint
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun DrawingCanvasDialog(
    initialJsonData: String? = null,
    onDismiss: () -> Unit,
    onSaveDrawingJson: (jsonData: String) -> Unit
) {
    val strokes = remember { mutableStateListOf<DrawingStroke>() }
    val undoneStrokes = remember { mutableStateListOf<DrawingStroke>() }

    // Initialize from JSON if present
    remember(initialJsonData) {
        if (!initialJsonData.isNullOrEmpty()) {
            val parseResult = deserializeDrawingStrokes(initialJsonData)
            strokes.clear()
            strokes.addAll(parseResult)
        }
    }

    var currentColor by remember { mutableStateOf(Color.Black) }
    var currentStrokeWidth by remember { mutableFloatStateOf(6f) }
    var isEraser by remember { mutableStateOf(false) }

    var currentPathPoints by remember { mutableStateOf<List<PathPoint>>(emptyList()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "হাতে আঁকা / ড্রইং (Drawing Board)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Row {
                    IconButton(
                        onClick = {
                            if (strokes.isNotEmpty()) {
                                undoneStrokes.add(strokes.removeAt(strokes.size - 1))
                            }
                        },
                        enabled = strokes.isNotEmpty()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo")
                    }
                    IconButton(
                        onClick = {
                            if (undoneStrokes.isNotEmpty()) {
                                strokes.add(undoneStrokes.removeAt(undoneStrokes.size - 1))
                            }
                        },
                        enabled = undoneStrokes.isNotEmpty()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = "Redo")
                    }
                    IconButton(
                        onClick = {
                            strokes.clear()
                            undoneStrokes.clear()
                        }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear", tint = Color.Red)
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Color Palette
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val colors = listOf(
                        Color.Black, Color.Red, Color(0xFF1D4ED8),
                        Color(0xFF047857), Color(0xFF7C3AED), Color(0xFFD97706),
                        Color.Gray
                    )

                    colors.forEach { col ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(col)
                                .border(
                                    width = if (currentColor == col && !isEraser) 3.dp else 1.dp,
                                    color = if (currentColor == col && !isEraser) MaterialTheme.colorScheme.primary else Color.LightGray,
                                    shape = CircleShape
                                )
                                .padding(4.dp)
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = Color.Transparent,
                                onClick = {
                                    currentColor = col
                                    isEraser = false
                                }
                            ) {}
                        }
                    }

                    // Eraser Toggle
                    IconButton(
                        onClick = { isEraser = !isEraser }
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Eraser",
                            tint = if (isEraser) Color.Red else Color.Gray
                        )
                    }
                }

                // Stroke Width Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEraser) "ইরেজার আকার:" else "ব্রাশ সাইজ:",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Slider(
                        value = currentStrokeWidth,
                        onValueChange = { currentStrokeWidth = it },
                        valueRange = 2f..30f,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Interactive Canvas Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    currentPathPoints = listOf(PathPoint(offset.x, offset.y))
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    currentPathPoints = currentPathPoints + PathPoint(change.position.x, change.position.y)
                                },
                                onDragEnd = {
                                    if (currentPathPoints.isNotEmpty()) {
                                        val newStroke = DrawingStroke(
                                            points = currentPathPoints,
                                            colorArgb = if (isEraser) Color.White.toArgb() else currentColor.toArgb(),
                                            strokeWidth = currentStrokeWidth,
                                            isEraser = isEraser
                                        )
                                        strokes.add(newStroke)
                                        undoneStrokes.clear()
                                        currentPathPoints = emptyList()
                                    }
                                }
                            )
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Render finished strokes
                        strokes.forEach { stroke ->
                            if (stroke.points.size > 1) {
                                val p = Path()
                                p.moveTo(stroke.points[0].x, stroke.points[0].y)
                                for (i in 1 until stroke.points.size) {
                                    p.lineTo(stroke.points[i].x, stroke.points[i].y)
                                }
                                drawPath(
                                    path = p,
                                    color = Color(stroke.colorArgb),
                                    style = Stroke(
                                        width = stroke.strokeWidth,
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )
                            }
                        }

                        // Render active stroke in real-time
                        if (currentPathPoints.size > 1) {
                            val activeP = Path()
                            activeP.moveTo(currentPathPoints[0].x, currentPathPoints[0].y)
                            for (i in 1 until currentPathPoints.size) {
                                activeP.lineTo(currentPathPoints[i].x, currentPathPoints[i].y)
                            }
                            drawPath(
                                path = activeP,
                                color = if (isEraser) Color.White else currentColor,
                                style = Stroke(
                                    width = currentStrokeWidth,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val serializedJson = serializeDrawingStrokes(strokes)
                    onSaveDrawingJson(serializedJson)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("সংরক্ষণ করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}

// Inline drawing display composable inside note list or note detail
@Composable
fun DrawingDisplayView(
    jsonData: String,
    modifier: Modifier = Modifier
) {
    val strokes = remember(jsonData) { deserializeDrawingStrokes(jsonData) }

    Canvas(modifier = modifier) {
        strokes.forEach { stroke ->
            if (stroke.points.size > 1) {
                val p = Path()
                p.moveTo(stroke.points[0].x, stroke.points[0].y)
                for (i in 1 until stroke.points.size) {
                    p.lineTo(stroke.points[i].x, stroke.points[i].y)
                }
                drawPath(
                    path = p,
                    color = Color(stroke.colorArgb),
                    style = Stroke(
                        width = stroke.strokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    }
}

private fun serializeDrawingStrokes(strokes: List<DrawingStroke>): String {
    return try {
        val rootArray = JSONArray()
        for (s in strokes) {
            val obj = JSONObject()
            obj.put("colorArgb", s.colorArgb)
            obj.put("strokeWidth", s.strokeWidth.toDouble())
            obj.put("isEraser", s.isEraser)

            val pointsArray = JSONArray()
            for (pt in s.points) {
                val ptObj = JSONObject()
                ptObj.put("x", pt.x.toDouble())
                ptObj.put("y", pt.y.toDouble())
                pointsArray.put(ptObj)
            }
            obj.put("points", pointsArray)
            rootArray.put(obj)
        }
        rootArray.toString()
    } catch (e: Exception) {
        ""
    }
}

private fun deserializeDrawingStrokes(json: String): List<DrawingStroke> {
    if (json.isEmpty()) return emptyList()
    return try {
        val result = mutableListOf<DrawingStroke>()
        val rootArray = JSONArray(json)
        for (i in 0 until rootArray.length()) {
            val obj = rootArray.getJSONObject(i)
            val colorArgb = obj.getInt("colorArgb")
            val strokeWidth = obj.getDouble("strokeWidth").toFloat()
            val isEraser = obj.optBoolean("isEraser", false)

            val pointsList = mutableListOf<PathPoint>()
            val pointsArray = obj.getJSONArray("points")
            for (j in 0 until pointsArray.length()) {
                val ptObj = pointsArray.getJSONObject(j)
                val x = ptObj.getDouble("x").toFloat()
                val y = ptObj.getDouble("y").toFloat()
                pointsList.add(PathPoint(x, y))
            }

            result.add(DrawingStroke(pointsList, colorArgb, strokeWidth, isEraser))
        }
        result
    } catch (e: Exception) {
        emptyList()
    }
}
