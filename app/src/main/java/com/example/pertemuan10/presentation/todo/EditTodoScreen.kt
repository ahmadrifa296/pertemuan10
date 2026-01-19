package com.example.pertemuan10.presentation.todo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pertemuan10.data.model.Todo

// --- TACTICAL DARK PALETTE ---
val DarkBg = Color(0xFF000000)
val DarkSurface = Color(0xFF121212)
val NeonCrimson = Color(0xFFFF003C)
val TacticalGray = Color(0xFF2C2C2C)
val TextHigh = Color(0xFFFFFFFF)
val TextLow = Color(0xFF757575)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTodoScreen(
    todo: Todo,
    onSave: (String, String) -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf(todo.title) }
    var priority by remember { mutableStateOf(todo.priority) }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg),
                title = {
                    Text(
                        "MODIFY TARGET", // Judul lebih tegas
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = TextHigh,
                            letterSpacing = 2.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = null, tint = NeonCrimson)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize()
        ) {
            // Header Indikator
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.WarningAmber, null, tint = NeonCrimson, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(12.dp))
                Text(
                    "COMMAND CENTER / EDIT MODE",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = NeonCrimson,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 1. Input Judul (Tactical Style)
            Text(
                "TASK DESCRIPTION",
                style = MaterialTheme.typography.labelSmall.copy(color = TextLow, letterSpacing = 1.sp),
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, TacticalGray)
            ) {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Enter task objective...", color = TextLow) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = NeonCrimson,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = NeonCrimson
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextHigh
                    )
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 2. Priority Selector (Industrial Radio Cards)
            Text(
                "THREAT LEVEL",
                style = MaterialTheme.typography.labelSmall.copy(color = TextLow, letterSpacing = 1.sp),
                modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("High", "Medium", "Low").forEach { p ->
                    val isSelected = priority == p
                    val pColor = when(p) {
                        "High" -> Color(0xFFFF1744)
                        "Medium" -> Color(0xFFFFEA00)
                        else -> Color(0xFF00E676)
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { priority = p },
                        color = if (isSelected) pColor.copy(alpha = 0.1f) else DarkSurface,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) pColor else TacticalGray
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { priority = p },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = pColor,
                                    unselectedColor = TacticalGray
                                )
                            )
                            Text(
                                text = p.uppercase(),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = if (isSelected) pColor else TextLow,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 3. Button Simpan (Aggressive Action)
            Button(
                onClick = { onSave(title, priority) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                enabled = title.isNotBlank(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCrimson,
                    contentColor = Color.White,
                    disabledContainerColor = TacticalGray
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    "CONFIRM CHANGES",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                )
            }
        }
    }
}