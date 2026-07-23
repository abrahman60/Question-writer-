package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Note
import com.example.ui.components.DrawingCanvasDialog
import com.example.ui.components.MathVerticalDialog
import com.example.ui.components.NoteEditorCanvas
import com.example.ui.components.RichTextEditorToolbar
import com.example.ui.components.SymbolPickerDialog
import com.example.util.DocumentExportUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    note: Note,
    onNoteChange: (Note) -> Unit,
    onSaveNote: () -> Unit,
    onDeleteNote: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var showColorMenu by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }

    var showMathDialog by remember { mutableStateOf(false) }
    var showSymbolDialog by remember { mutableStateOf(false) }
    var showDrawingDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val noteColors = listOf(
        "#FFFFFF" to "সাদা",
        "#FCE8E6" to "হালকা লাল",
        "#FEEFC3" to "হালকা কমলা",
        "#FFF4CE" to "হলুদ",
        "#E6F4EA" to "হালকা সবুজ",
        "#E0F2FE" to "আকাশী",
        "#D1E9FC" to "নীল",
        "#F3E8FF" to "বেগুনি",
        "#FCE7F3" to "গোলাপী"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (note.id == 0L) "নতুন নোট তৈরি" else "নোট এডিটর",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onSaveNote()
                        onBackClick()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Color Picker Button
                    Box {
                        IconButton(onClick = { showColorMenu = true }) {
                            Icon(Icons.Default.ColorLens, contentDescription = "Change Color")
                        }

                        DropdownMenu(
                            expanded = showColorMenu,
                            onDismissRequest = { showColorMenu = false }
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .width(220.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                noteColors.take(5).forEach { (hex, name) ->
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color(android.graphics.Color.parseColor(hex)))
                                            .border(1.dp, Color.Gray, CircleShape)
                                            .clickable {
                                                onNoteChange(note.copy(colorHex = hex))
                                                showColorMenu = false
                                            }
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .width(220.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                noteColors.drop(5).forEach { (hex, name) ->
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color(android.graphics.Color.parseColor(hex)))
                                            .border(1.dp, Color.Gray, CircleShape)
                                            .clickable {
                                                onNoteChange(note.copy(colorHex = hex))
                                                showColorMenu = false
                                            }
                                    )
                                }
                            }
                        }
                    }

                    // Save Button
                    IconButton(onClick = {
                        onSaveNote()
                        Toast.makeText(context, "নোট সংরক্ষণ করা হয়েছে", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Save Note", tint = MaterialTheme.colorScheme.primary)
                    }

                    // Export / Share Options Menu
                    Box {
                        IconButton(onClick = { showMoreMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Export Menu")
                        }

                        DropdownMenu(
                            expanded = showMoreMenu,
                            onDismissRequest = { showMoreMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("কপি টু ক্লিপবোর্ড (Copy)") },
                                leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null) },
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Note Content", "${note.title}\n\n${note.content}")
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "টেক্সট কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                                    showMoreMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("গুগল ডকুমেন্টে পাঠান (Google Docs)") },
                                leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
                                onClick = {
                                    DocumentExportUtil.shareToGoogleDocs(context, note)
                                    showMoreMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Word (.docx) হিসেবে সেভ") },
                                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                                onClick = {
                                    val docxFile = DocumentExportUtil.exportToDocx(context, note)
                                    if (docxFile != null) {
                                        DocumentExportUtil.shareFile(context, docxFile, "application/vnd.openxmlformats-officedocument.wordprocessingml.document", note.title)
                                    }
                                    showMoreMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("PDF হিসেবে সেভ") },
                                leadingIcon = { Icon(Icons.Default.PictureAsPdf, contentDescription = null) },
                                onClick = {
                                    val pdfFile = DocumentExportUtil.exportToPdf(context, note)
                                    if (pdfFile != null) {
                                        DocumentExportUtil.shareFile(context, pdfFile, "application/pdf", note.title)
                                    }
                                    showMoreMenu = false
                                }
                            )
                            if (note.id != 0L) {
                                DropdownMenuItem(
                                    text = { Text("নোট মুছুন", color = Color.Red) },
                                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) },
                                    onClick = {
                                        showDeleteConfirmation = true
                                        showMoreMenu = false
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Rich Text Toolbar
            RichTextEditorToolbar(
                note = note,
                onNoteChange = onNoteChange,
                onOpenMathDialog = { showMathDialog = true },
                onOpenSymbolDialog = { showSymbolDialog = true },
                onOpenDrawingDialog = { showDrawingDialog = true }
            )

            // Document Canvas
            NoteEditorCanvas(
                note = note,
                onNoteChange = onNoteChange,
                modifier = Modifier.weight(1f)
            )
        }
    }

    // Math Vertical Dialog
    if (showMathDialog) {
        MathVerticalDialog(
            onDismiss = { showMathDialog = false },
            onInsertMathBlock = { formattedMath ->
                val newContent = if (note.content.isNotEmpty()) "${note.content}\n\n$formattedMath" else formattedMath
                onNoteChange(note.copy(content = newContent))
            }
        )
    }

    // Symbol Picker Dialog
    if (showSymbolDialog) {
        SymbolPickerDialog(
            onDismiss = { showSymbolDialog = false },
            onSymbolSelected = { selectedSymbol ->
                onNoteChange(note.copy(content = note.content + selectedSymbol))
            }
        )
    }

    // Drawing Canvas Dialog
    if (showDrawingDialog) {
        DrawingCanvasDialog(
            initialJsonData = note.drawingDataJson,
            onDismiss = { showDrawingDialog = false },
            onSaveDrawingJson = { drawingJson ->
                onNoteChange(note.copy(drawingDataJson = drawingJson))
                Toast.makeText(context, "ড্রইং যুক্ত করা হয়েছে", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Delete Confirmation
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("নোট মুছে ফেলা") },
            text = { Text("আপনি কি নিশ্চিতভাবে এই নোটটি মুছে ফেলতে চান?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteNote()
                        showDeleteConfirmation = false
                        onBackClick()
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("মুছুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}
