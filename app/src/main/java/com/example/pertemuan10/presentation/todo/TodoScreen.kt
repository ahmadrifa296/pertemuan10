package com.example.pertemuan10.presentation.todo

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pertemuan10.data.UserData
import com.example.pertemuan10.data.model.Priority
import com.example.pertemuan10.data.model.Todo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(
    userData: UserData?,
    viewModel: TodoViewModel,
    onSignOut: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    var todoText by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }
    val todos by viewModel.todos.collectAsState()

    LaunchedEffect(key1 = userData?.userId) {
        userData?.userId?.let { viewModel.observeTodos(it) }
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = DarkBg),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Radar, null, tint = NeonCrimson, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "COMMAND CENTER",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = TextHigh,
                                letterSpacing = 2.sp
                            )
                        )
                    }
                },
                actions = {
                    userData?.let {
                        AsyncImage(
                            model = it.profilePictureUrl,
                            contentDescription = "Operator",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp)) // Square-ish profile
                                .clickable { onSignOut() }
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // 1. Mission Progress Dashboard
            TacticalDashboard(todos)

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                // 2. Search Protocol
                ModernSearchBar(viewModel)

                Spacer(modifier = Modifier.height(24.dp))

                // 3. Objective Input
                QuickAddCard(
                    text = todoText,
                    onTextChange = { todoText = it },
                    currentPriority = selectedPriority,
                    onPriorityChange = { selectedPriority = it },
                    onAddClick = {
                        if (todoText.isNotBlank()) {
                            userData?.userId?.let { viewModel.add(it, todoText, selectedPriority.name) }
                            todoText = ""
                        }
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    "ACTIVE TARGETS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = NeonCrimson,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.padding(start = 4.dp, bottom = 16.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(todos, key = { it.id }) { todo ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                if (it == SwipeToDismissBoxValue.EndToStart) {
                                    userData?.userId?.let { uid -> viewModel.delete(uid, todo.id) }
                                    true
                                } else false
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            backgroundContent = { SwipeDeleteBackground(dismissState) }
                        ) {
                            TaskItemPremium(todo, onNavigateToEdit) {
                                userData?.userId?.let { uid -> viewModel.toggle(uid, todo) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TacticalDashboard(todos: List<Todo>) {
    val completed = todos.count { it.isCompleted }
    val total = todos.size
    val progress = if (total > 0) completed.toFloat() / total else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1500, easing = LinearOutSlowInEasing), label = ""
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        color = DarkSurface,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, TacticalGray)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(24.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("MISSION STATUS", color = TextLow, style = MaterialTheme.typography.labelSmall, letterSpacing = 1.sp)
                Text(
                    if (progress == 1f) "OBJECTIVES CLEARED" else "$completed / $total TARGETS",
                    color = TextHigh,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                )
            }

            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    color = NeonCrimson,
                    strokeWidth = 6.dp,
                    trackColor = TacticalGray,
                    modifier = Modifier.size(60.dp)
                )
                Text(
                    "${(animatedProgress * 100).toInt()}%",
                    color = NeonCrimson,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun ModernSearchBar(viewModel: TodoViewModel) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DarkSurface,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, TacticalGray)
    ) {
        TextField(
            value = viewModel.searchQuery.value,
            onValueChange = {
                viewModel.searchQuery.value = it
                viewModel.updateFilteredList()
            },
            placeholder = { Text("INITIATE SEARCH...", color = TextLow, fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Rounded.Search, null, tint = NeonCrimson) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = NeonCrimson,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )
    }
}

@Composable
fun QuickAddCard(
    text: String,
    onTextChange: (String) -> Unit,
    currentPriority: Priority,
    onPriorityChange: (Priority) -> Unit,
    onAddClick: () -> Unit
) {
    Surface(
        color = DarkSurface,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, TacticalGray)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = text,
                    onValueChange = onTextChange,
                    placeholder = { Text("NEW DIRECTIVE...", color = TextLow) },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )

                IconButton(
                    onClick = onAddClick,
                    enabled = text.isNotBlank(),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = NeonCrimson,
                        contentColor = Color.White,
                        disabledContainerColor = TacticalGray
                    ),
                    modifier = Modifier.size(44.dp).clip(RoundedCornerShape(4.dp))
                ) {
                    Icon(Icons.Rounded.Add, null)
                }
            }

            Row(modifier = Modifier.padding(top = 12.dp)) {
                Priority.entries.forEach { p ->
                    val isSelected = currentPriority == p
                    val pColor = when(p) {
                        Priority.HIGH -> Color(0xFFFF1744)
                        Priority.MEDIUM -> Color(0xFFFFEA00)
                        else -> Color(0xFF00E676)
                    }
                    Surface(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable { onPriorityChange(p) },
                        color = if (isSelected) pColor.copy(alpha = 0.15f) else Color.Transparent,
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, if (isSelected) pColor else TacticalGray)
                    ) {
                        Text(
                            p.label.uppercase(),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isSelected) pColor else TextLow,
                                fontWeight = FontWeight.Black
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskItemPremium(todo: Todo, onEdit: (String) -> Unit, onToggle: () -> Unit) {
    val pColor = when(Priority.fromString(todo.priority)) {
        Priority.HIGH -> Color(0xFFFF1744)
        Priority.MEDIUM -> Color(0xFFFFEA00)
        else -> Color(0xFF00E676)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit(todo.id) },
        color = DarkSurface,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, TacticalGray)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Priority Indicator (Industrial Bar)
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(24.dp)
                    .background(pColor)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Tactical Square Checkbox
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .border (BorderStroke(2.dp, if (todo.isCompleted) pColor else TacticalGray), RoundedCornerShape(4.dp))
                    .background(if (todo.isCompleted) pColor.copy(alpha = 0.2f) else Color.Transparent)
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                if (todo.isCompleted) {
                    Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp), tint = pColor)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = todo.title.uppercase(),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium.copy(
                    textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else null,
                    color = if (todo.isCompleted) TextLow else TextHigh,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )

            Icon(Icons.Default.ChevronRight, null, tint = TacticalGray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeDeleteBackground(state: SwipeToDismissBoxState) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF330000))
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("TERMINATE", color = NeonCrimson, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black)
            Spacer(Modifier.width(12.dp))
            Icon(Icons.Default.DeleteSweep, "Terminate", tint = NeonCrimson)
        }
    }
}