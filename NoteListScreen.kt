package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Note
import com.example.ui.components.DrawingDisplayView
import com.example.util.DocumentExportUtil
import com.example.util.FontProvider
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NoteListScreen(
    notes: List<Note>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onNoteClick: (Note) -> Unit,
    onCreateNoteClick: () -> Unit,
    onTogglePin: (Note) -> Unit,
    onDeleteNote: (Note) -> Unit,
    onDuplicateNote: (Note) -> Unit
) {
    val context = LocalContext.current
    var noteToDelete by remember { mutableStateOf<Note?>(null) }

    val categories = listOf("সব", "সাধারণ", "পরীক্ষার প্রশ্ন", "গণিত", "উর্দু", "বাংলা", "আরবি")

    val pinnedNotes = notes.filter { it.isPinned }
    val unpinnedNotes = notes.filter { !it.isPinned }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Description,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "স্মার্ট নোট ও এডিটর",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                actions = {
                    IconButton(onClick = onToggleDarkMode) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Theme Toggle",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateNoteClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "New Note")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("নতুন নোট", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("নোট বা প্রশ্নের শিরোনাম খুঁজুন...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear Search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp)
            )

            // Category filter pills
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { onCategoryChange(cat) },
                        label = { Text(cat, fontWeight = if (selectedCategory == cat) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (notes.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.NoteAdd,
                            contentDescription = null,
                            modifier = Modifier.size(72.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = "কোনো নোট খুঁজে পাওয়া যায়নি",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "নিচের '+' বাটনে ক্লিক করে প্রথম নোট তৈরি করুন",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                // Google Keep Style Staggered Grid Notes
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalItemSpacing = 10.dp,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Pinned Header
                    if (pinnedNotes.isNotEmpty()) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            Text(
                                text = "পিন করা নোটসমূহ (Pinned)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                            )
                        }

                        items(pinnedNotes, key = { "pinned_${it.id}" }) { note ->
                            NoteCardItem(
                                note = note,
                                onClick = { onNoteClick(note) },
                                onTogglePin = { onTogglePin(note) },
                                onDelete = { noteToDelete = note },
                                onDuplicate = { onDuplicateNote(note) }
                            )
                        }

                        item(span = StaggeredGridItemSpan.FullLine) {
                            Text(
                                text = "অন্যান্য নোটসমূহ",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp, start = 4.dp)
                            )
                        }
                    }

                    items(unpinnedNotes, key = { "unpinned_${it.id}" }) { note ->
                        NoteCardItem(
                            note = note,
                            onClick = { onNoteClick(note) },
                            onTogglePin = { onTogglePin(note) },
                            onDelete = { noteToDelete = note },
                            onDuplicate = { onDuplicateNote(note) }
                        )
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    noteToDelete?.let { note ->
        AlertDialog(
            onDismissRequest = { noteToDelete = null },
            title = { Text("নোট মুছে ফেলার নিশ্চিতকরণ") },
            text = { Text("'${note.title.ifEmpty { "শিরোনামহীন" }}' নোটটি স্থায়ীভাবে মুছে ফেলতে চান?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteNote(note)
                        noteToDelete = null
                        Toast.makeText(context, "নোটটি মুছে ফেলা হয়েছে", Toast.LENGTH_SHORT).show()
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("মুছুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { noteToDelete = null }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@Composable
fun NoteCardItem(
    note: Note,
    onClick: () -> Unit,
    onTogglePin: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    val cardColor = try {
        Color(android.graphics.Color.parseColor(note.colorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.surfaceContainerLow
    }

    val dateStr = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(Date(note.updatedAt))

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Card Header: Title & Pin/Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = note.title.ifEmpty { "শিরোনামহীন নোট" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Row {
                    IconButton(
                        onClick = onTogglePin,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            if (note.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            contentDescription = "Pin Note",
                            tint = if (note.isPinned) MaterialTheme.colorScheme.primary else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "More",
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("কপি টু ক্লিপবোর্ড") },
                                leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null) },
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Note Content", "${note.title}\n\n${note.content}")
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "টেক্সট কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("গুগল ডকুমেন্টে পাঠান (Google Docs)") },
                                leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
                                onClick = {
                                    DocumentExportUtil.shareToGoogleDocs(context, note)
                                    showMenu = false
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
                                    showMenu = false
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
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("ডুপ্লিকেট নোট") },
                                onClick = {
                                    onDuplicate()
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("মুছে ফেলুন", color = Color.Red) },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) },
                                onClick = {
                                    onDelete()
                                    showMenu = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Preview Content
            if (note.content.isNotEmpty()) {
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontProvider.getFontFamily(note.fontName),
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                )
            }

            // Drawing Thumbnail
            if (!note.drawingDataJson.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                DrawingDisplayView(
                    jsonData = note.drawingDataJson ?: "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer: Tag Pill & Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                ) {
                    Text(
                        text = note.tag,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = dateStr,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
