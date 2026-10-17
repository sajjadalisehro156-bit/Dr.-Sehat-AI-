package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.ComputerVisionEngine
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.UrgencyEmergencyRed

@Composable
fun GradCamOverlayView(
    baseImageRes: Int? = null,
    baseBitmap: Bitmap? = null,
    heatFocusX: Float = 0.5f,
    heatFocusY: Float = 0.5f,
    heatRadiusRatio: Float = 0.38f,
    label: String = "AI Activation Explainability",
    onFocusChange: ((Float, Float) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var heatmapAlpha by remember { mutableFloatStateOf(0.65f) }
    var currentFocusX by remember(heatFocusX) { mutableFloatStateOf(heatFocusX) }
    var currentFocusY by remember(heatFocusY) { mutableFloatStateOf(heatFocusY) }

    val heatmapBitmap = remember(currentFocusX, currentFocusY, heatRadiusRatio) {
        ComputerVisionEngine.generateGradCamHeatmap(
            width = 320,
            height = 320,
            focusX = currentFocusX,
            focusY = currentFocusY,
            radiusRatio = heatRadiusRatio
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Explainability",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (baseBitmap != null) {
                        Box(
                            modifier = Modifier
                                .background(EmeraldLight.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "USER IMAGE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 9.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Grad-CAM: ${(heatmapAlpha * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Composite Visual Viewport with Interactive Tap to Inspect Zones
            Box(
                modifier = Modifier
                    .size(270.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .background(Color.Black)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val fx = (offset.x / size.width).coerceIn(0.1f, 0.9f)
                            val fy = (offset.y / size.height).coerceIn(0.1f, 0.9f)
                            currentFocusX = fx
                            currentFocusY = fy
                            onFocusChange?.invoke(fx, fy)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                // Priority: User uploaded bitmap first, then drawable resource
                if (baseBitmap != null) {
                    Image(
                        bitmap = baseBitmap.asImageBitmap(),
                        contentDescription = "User Uploaded Scan",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else if (baseImageRes != null) {
                    Image(
                        painter = painterResource(id = baseImageRes),
                        contentDescription = "Medical Scan Base Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Grad-CAM Heatmap Layer
                Image(
                    bitmap = heatmapBitmap.asImageBitmap(),
                    contentDescription = "Grad-CAM Heatmap Overlay",
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(heatmapAlpha),
                    contentScale = ContentScale.Crop
                )

                // Reticle centered on current focus position
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(
                            start = (270 * currentFocusX - 24).coerceAtLeast(0f).dp,
                            top = (270 * currentFocusY - 24).coerceAtLeast(0f).dp
                        )
                        .size(48.dp)
                        .border(1.5.dp, Color.White.copy(alpha = 0.9f), CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.TouchApp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Tap anywhere on scan to relocate Grad-CAM attention inspection hotspot",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Slider Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Raw Scan",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Slider(
                    value = heatmapAlpha,
                    onValueChange = { heatmapAlpha = it },
                    valueRange = 0.0f..1.0f,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = "AI Heatmap",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = UrgencyEmergencyRed
                )
            }

            // Heatmap Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendChip(color = Color(0xFFDC2626), text = "High Focus")
                LegendChip(color = Color(0xFFF59E0B), text = "Moderate")
                LegendChip(color = Color(0xFF06B6D4), text = "Periphery")
                LegendChip(color = Color.Transparent, text = "Clear", isClear = true)
            }
        }
    }
}

@Composable
private fun LegendChip(color: Color, text: String, isClear: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(if (isClear) Color.Gray.copy(alpha = 0.3f) else color, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
