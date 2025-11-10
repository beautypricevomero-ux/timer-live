package com.example.timerlive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timerlive.ui.theme.TimerLiveTheme
import kotlinx.coroutines.delay
import kotlin.math.roundToLong

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TimerLiveTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TimerLiveApp()
                }
            }
        }
    }
}

private data class TimerPreset(
    val minutes: Int,
    val headline: String,
    val subline: String,
    val accentStart: Color,
    val accentEnd: Color,
    val accentSecondary: Color
) {
    val durationMillis: Long = minutes * 60_000L
    val label: String = "${minutes} minuti"
}

@Composable
private fun TimerLiveApp() {
    val presets = remember {
        listOf(
            TimerPreset(
                minutes = 3,
                headline = "Flash Glam 3'",
                subline = "Promo istantanea per stupire la chat",
                accentStart = Color(0xFFFFB347),
                accentEnd = Color(0xFFFF6B9C),
                accentSecondary = Color(0xFFFF8C42)
            ),
            TimerPreset(
                minutes = 5,
                headline = "Glow Rush 5'",
                subline = "Tempo perfetto per raccontare il boost",
                accentStart = Color(0xFF74E1FF),
                accentEnd = Color(0xFF5B7CFF),
                accentSecondary = Color(0xFF3FA9F5)
            ),
            TimerPreset(
                minutes = 7,
                headline = "Luxury Drop 7'",
                subline = "Tenuta lunga per le offerte top",
                accentStart = Color(0xFF8BFFDA),
                accentEnd = Color(0xFF3CEFFF),
                accentSecondary = Color(0xFF2ED8D3)
            ),
            TimerPreset(
                minutes = 10,
                headline = "Mega Deal 10'",
                subline = "Countdown completo per il bundle wow",
                accentStart = Color(0xFFFFC778),
                accentEnd = Color(0xFFFF4D8D),
                accentSecondary = Color(0xFFFF7A45)
            )
        )
    }

    var activePreset by remember { mutableStateOf<TimerPreset?>(null) }

    if (activePreset == null) {
        LandingScreen(
            presets = presets,
            onSelectPreset = { activePreset = it }
        )
    } else {
        TimerScreen(
            preset = activePreset!!,
            onBack = { activePreset = null }
        )
    }
}

@Composable
private fun LandingScreen(
    presets: List<TimerPreset>,
    onSelectPreset: (TimerPreset) -> Unit
) {
    val backgroundBrush = Brush.linearGradient(
        colors = listOf(Color(0xFFF8FBFF), Color(0xFFEEF3FF)),
        start = Offset.Zero,
        end = Offset(1400f, 900f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 32.dp, vertical = 24.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Column {
                Text(
                    text = "Offerte bomba live",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color(0xFF101438),
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Text(
                    text = "Seleziona il tempo perfetto e lascia che il countdown faccia esplodere l'urgenza!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF4E547A),
                    lineHeight = 28.sp
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                presets.chunked(2).forEach { columnPresets ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        columnPresets.forEach { preset ->
                            TimerPresetButton(preset = preset, onClick = { onSelectPreset(preset) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimerPresetButton(
    preset: TimerPreset,
    onClick: () -> Unit
) {
    val buttonBrush = Brush.linearGradient(listOf(preset.accentStart, preset.accentEnd))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .shadow(elevation = 20.dp, shape = RoundedCornerShape(36.dp), clip = false)
            .clip(RoundedCornerShape(36.dp))
            .background(Color.White.copy(alpha = 0.65f))
            .padding(1.dp)
            .clip(RoundedCornerShape(36.dp))
            .background(buttonBrush)
    ) {
        ElevatedButton(
            onClick = onClick,
            shape = RoundedCornerShape(36.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = preset.headline,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = preset.subline,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = preset.label.uppercase(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
private fun TimerScreen(
    preset: TimerPreset,
    onBack: () -> Unit
) {
    val totalMillis = preset.durationMillis
    var remainingMillis by remember(preset) { mutableStateOf(totalMillis) }
    var isRunning by remember(preset) { mutableStateOf(true) }

    LaunchedEffect(preset, isRunning) {
        if (!isRunning) return@LaunchedEffect
        val tick = 100L
        while (remainingMillis > 0L) {
            delay(tick)
            remainingMillis = (remainingMillis - tick).coerceAtLeast(0L)
        }
        isRunning = false
    }

    val progress = (remainingMillis.toFloat() / totalMillis.toFloat()).coerceIn(0f, 1f)
    val urgencyTriggered = remainingMillis <= (totalMillis * 0.4f).roundToLong()

    val timerColor by animateColorAsState(
        targetValue = if (urgencyTriggered) Color(0xFFFF2D55) else Color.White,
        animationSpec = tween(durationMillis = 300),
        label = "timerColor"
    )

    val pulseScale by animateFloatAsState(
        targetValue = if (urgencyTriggered) 1.08f else 1f,
        animationSpec = if (urgencyTriggered) {
            infiniteRepeatable(
                animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        } else {
            tween(durationMillis = 400)
        },
        label = "pulseScale"
    )

    val backgroundBrush = Brush.linearGradient(
        colors = listOf(preset.accentStart, preset.accentEnd),
        start = Offset.Zero,
        end = Offset(1200f, 800f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 32.dp, vertical = 24.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = "Torna indietro",
                tint = Color.White
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = preset.headline,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White.copy(alpha = 0.92f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = preset.subline,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 24.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .aspectRatio(1f)
                        .shadow(24.dp, RoundedCornerShape(360.dp), clip = false)
                        .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(360.dp))
                        .padding(24.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = size.minDimension * 0.06f
                        drawArc(
                            color = Color.White.copy(alpha = 0.12f),
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = strokeWidth,
                                cap = StrokeCap.Round
                            )
                        )
                        drawArc(
                            brush = Brush.sweepGradient(listOf(Color.White, preset.accentSecondary, Color.White)),
                            startAngle = -90f,
                            sweepAngle = progress * 360f,
                            useCenter = false,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = strokeWidth,
                                cap = StrokeCap.Round
                            )
                        )
                    }

                    val minutes = (remainingMillis / 60_000L)
                    val seconds = (remainingMillis % 60_000L) / 1000L
                    val formatted = String.format("%d:%02d", minutes, seconds)

                    Text(
                        text = formatted,
                        color = timerColor,
                        fontSize = 112.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1).sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(pulseScale)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val statusText = when {
                    remainingMillis <= 0L -> "Tempo scaduto! Lancia la prossima bomba."
                    urgencyTriggered -> "Ci siamo quasi! Spingi sull'urgenza."
                    else -> "Countdown in corso, tieni alta l'energia!"
                }
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.92f),
                    textAlign = TextAlign.Center
                )

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(
                        onClick = {
                            remainingMillis = totalMillis
                            isRunning = true
                        },
                        border = BorderStroke(2.dp, Color.White),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White,
                            containerColor = Color.Transparent
                        )
                    ) {
                        Icon(imageVector = Icons.Rounded.Refresh, contentDescription = "Reset")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Reset")
                    }
                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = preset.accentEnd)
                    ) {
                        Text(text = "Nuova offerta")
                    }
                }
            }
        }
    }
}

private fun Modifier.scale(scale: Float): Modifier = this.then(
    androidx.compose.ui.Modifier.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
)
