package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanLight
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.UrgencyDoctorYellow
import com.example.ui.theme.UrgencyEmergencyRed
import kotlin.math.sin

@Composable
fun SpectrogramView(
    isRecording: Boolean = false,
    coughType: String = "Productive Cough",
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "fft")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Spectrogram",
                        tint = CyanLight,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "MEL-SPECTROGRAM ACOUSTIC BARS",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .background(
                            if (isRecording) UrgencyEmergencyRed.copy(alpha = 0.2f) else Color(0xFF1E293B),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (isRecording) "● LISTENING (MFCC)" else "ANALYSIS READY",
                        color = if (isRecording) UrgencyEmergencyRed else EmeraldLight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Spectrogram Bar Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(Color(0xFF060B12), RoundedCornerShape(10.dp))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                    .padding(8.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxWidth().height(94.dp)) {
                    val barCount = 28
                    val barWidth = (size.width / barCount) * 0.7f
                    val spacing = (size.width - (barCount * barWidth)) / (barCount + 1)

                    val gradientBrush = Brush.verticalGradient(
                        colors = listOf(
                            UrgencyEmergencyRed,
                            UrgencyDoctorYellow,
                            CyanLight,
                            EmeraldLight
                        ),
                        startY = 0f,
                        endY = size.height
                    )

                    for (i in 0 until barCount) {
                        val baseHeightFraction = if (isRecording) {
                            val wave1 = (sin(phase + i * 0.35f) + 1f) / 2f
                            val wave2 = (sin(phase * 1.5f + i * 0.6f) + 1f) / 2f
                            (wave1 * 0.6f + wave2 * 0.4f).coerceIn(0.12f, 0.95f)
                        } else {
                            // Stable spectral profile
                            val centerBell = 1f - kotlin.math.abs((i - barCount / 2f) / (barCount / 2f))
                            (centerBell * 0.75f + 0.15f).coerceIn(0.1f, 0.9f)
                        }

                        val barHeight = size.height * baseHeightFraction
                        val left = spacing + i * (barWidth + spacing)
                        val top = size.height - barHeight

                        drawRoundRect(
                            brush = gradientBrush,
                            topLeft = Offset(left, top),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Acoustic Profile: $coughType",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "128 Mel-Bands / 16kHz",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp
                )
            }
        }
    }
}
