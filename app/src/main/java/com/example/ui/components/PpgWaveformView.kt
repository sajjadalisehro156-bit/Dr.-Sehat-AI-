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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.VitalsEngine
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.UrgencyEmergencyRed

@Composable
fun PpgWaveformView(
    heartRateBpm: Int = 74,
    isMeasuring: Boolean = true,
    sensorSource: String = "Smartphone Camera rPPG",
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ppg")
    val timeOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (60000 / heartRateBpm.coerceAtLeast(40)), easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)), // Clinical Dark Monitor Style
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with Live Pulse & BPM
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(UrgencyEmergencyRed.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Pulse",
                            tint = UrgencyEmergencyRed,
                            modifier = Modifier
                                .size(18.dp)
                                .size((18 * if (isMeasuring) pulseScale else 1f).dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "PHOTOPLETHYSMOGRAPHY (rPPG)",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = sensorSource,
                            color = EmeraldLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // BPM readout
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = if (isMeasuring) "$heartRateBpm" else "--",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "BPM",
                        color = EmeraldLight,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Clinical Waveform Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(Color(0xFF060B12), RoundedCornerShape(10.dp))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                    .padding(8.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxWidth().height(114.dp)) {
                    val width = size.width
                    val height = size.height

                    // Draw Medical ECG Grid Lines
                    val gridSpacing = 24.dp.toPx()
                    var x = 0f
                    while (x < width) {
                        drawLine(
                            color = Color(0xFF0E2238),
                            start = Offset(x, 0f),
                            end = Offset(x, height),
                            strokeWidth = 1f
                        )
                        x += gridSpacing
                    }
                    var y = 0f
                    while (y < height) {
                        drawLine(
                            color = Color(0xFF0E2238),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1f
                        )
                        y += gridSpacing
                    }

                    if (isMeasuring) {
                        val path = Path()
                        val points = 80
                        val stepX = width / points

                        for (i in 0..points) {
                            val sampleTime = timeOffset + (i * 0.035f)
                            val normValue = VitalsEngine.calculatePpgSample(sampleTime, heartRateBpm)
                            // Invert for canvas coordinates (0 is top)
                            val canvasY = height - (normValue * (height * 0.8f)) - (height * 0.1f)
                            val currentX = i * stepX

                            if (i == 0) {
                                path.moveTo(currentX, canvasY)
                            } else {
                                path.lineTo(currentX, canvasY)
                            }
                        }

                        // Draw Glow Line
                        drawPath(
                            path = path,
                            color = Color(0xFF10B981).copy(alpha = 0.4f),
                            style = Stroke(width = 6.dp.toPx())
                        )
                        // Draw Crisp Main Line
                        drawPath(
                            path = path,
                            color = Color(0xFF34D399),
                            style = Stroke(width = 2.5.dp.toPx())
                        )
                    } else {
                        // Flatline idle
                        drawLine(
                            color = Color(0xFF475569),
                            start = Offset(0f, height / 2f),
                            end = Offset(width, height / 2f),
                            strokeWidth = 2f
                        )
                    }
                }

                // Live status badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(Color(0xFF064E3B).copy(alpha = 0.85f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isMeasuring) "LIVE SYNC" else "STANDBY",
                        color = EmeraldLight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Clinical interpretation footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = VitalsEngine.interpretHeartRate(heartRateBpm),
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
                Text(
                    text = "QRS/PPG Peak Locked",
                    color = EmeraldLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
