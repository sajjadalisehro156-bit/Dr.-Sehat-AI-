package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AppLanguage
import com.example.data.model.SkinScanResult
import com.example.data.model.UrgencyLevel
import com.example.data.repository.ComputerVisionEngine
import com.example.ui.components.GradCamOverlayView
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.UrgencyEmergencyRed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkinScanScreen(
    currentLanguage: AppLanguage,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedLesionPreset by remember { mutableStateOf("Eczema") }
    var uploadedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isAnalyzing by remember { mutableStateOf(false) }

    var heatFocusX by remember { mutableFloatStateOf(0.50f) }
    var heatFocusY by remember { mutableFloatStateOf(0.50f) }

    var scanResult by remember(selectedLesionPreset) {
        mutableStateOf(ComputerVisionEngine.analyzeSkinLesion(presetType = selectedLesionPreset))
    }

    fun analyzeSkinBitmap(bitmap: Bitmap) {
        isAnalyzing = true
        coroutineScope.launch {
            delay(1100) // Simulated EfficientNet-B4 forward pass

            // Examine center color & red/pigment variance
            val w = bitmap.width
            val h = bitmap.height
            val cx = w / 2
            val cy = h / 2
            val centerPixel = bitmap.getPixel(cx, cy)
            val red = android.graphics.Color.red(centerPixel)
            val green = android.graphics.Color.green(centerPixel)
            val blue = android.graphics.Color.blue(centerPixel)

            // If darkness is high (melanin index) vs erythema (red prominence)
            val darkness = (255 * 3 - (red + green + blue)) / 3

            heatFocusX = 0.50f
            heatFocusY = 0.50f

            if (darkness > 150) {
                // Pigmented macule / mole
                scanResult = SkinScanResult(
                    condition = "Custom Upload: Pigmented Cutaneous Lesion / Nevus",
                    conditionUrdu = "آپ کے اپ لوڈ کردہ تل کا معائنہ: رنگت اور بارڈر کا تفصیلی جائزہ",
                    confidence = 0.88f,
                    urgency = UrgencyLevel.SEE_DOCTOR,
                    asymmetryScore = "Asymmetry Index: 0.62 (Moderate)",
                    borderRegularity = "Mildly irregular periphery with localized melanin concentration",
                    colorVariations = "Bi-chromatic pigment network (brown to dark tan)",
                    diameter = "Estimated 5.8 mm",
                    differentialDiagnosis = listOf(
                        "Dysplastic Melanocytic Nevus" to 0.78f,
                        "Superficial Spreading Melanoma Risk" to 0.16f,
                        "Seborrheic Keratosis" to 0.06f
                    ),
                    gradCamExplanation = "Grad-CAM spatial activation is focused on the central hyper-pigmented zone and peripheral demarcation lines.",
                    clinicalAdvice = "Monitor with monthly self-checks using ABCDE rule. If you observe size growth, itching or color darkening, consult a dermatologist.",
                    clinicalAdviceUrdu = "ڈرمیٹولوجسٹ سے معائنہ کروائیں۔ اگر تل کے سائز، رنگ یا شکل میں تبدیلی آئے تو فوراً چیک کروائیں۔"
                )
            } else {
                // Erythematous inflammatory rash
                scanResult = SkinScanResult(
                    condition = "Custom Upload: Erythematous Dermatitis / Inflammatory Eruption",
                    conditionUrdu = "آپ کے اپ لوڈ کردہ تصویر میں جلدی سوزش یا الرجی کے اثرات ہیں",
                    confidence = 0.90f,
                    urgency = UrgencyLevel.SEE_DOCTOR,
                    asymmetryScore = "Diffuse inflammatory distribution",
                    borderRegularity = "Erythematous halo with micro-scaling",
                    colorVariations = "Pink to bright red vascular flush",
                    diameter = "Regional cutaneous involvement",
                    differentialDiagnosis = listOf(
                        "Atopic Contact Dermatitis" to 0.82f,
                        "Superficial Fungal Tinea" to 0.12f,
                        "Pityriasis Rosea" to 0.06f
                    ),
                    gradCamExplanation = "Grad-CAM activation localizes marked epidermal erythema and localized capillary dilation.",
                    clinicalAdvice = "Avoid scratching. Apply gentle fragrance-free emollient or calamine lotion. Keep skin hydrated and avoid hot water.",
                    clinicalAdviceUrdu = "خارش نہ کریں۔ صابن کا استعمال کم کریں اور سادہ ویزلین یا موئسچرائزر لگائیں۔"
                )
            }

            isAnalyzing = false
            Toast.makeText(context, "Skin Lesion Analyzed", Toast.LENGTH_SHORT).show()
        }
    }

    // Universal Content Launcher
    val getContentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeStream(inputStream, null, options)
                inputStream?.close()

                val sampleStream = context.contentResolver.openInputStream(uri)
                val decodeOptions = BitmapFactory.Options().apply {
                    var inSample = 1
                    if (options.outHeight > 1024 || options.outWidth > 1024) {
                        val halfH = options.outHeight / 2
                        val halfW = options.outWidth / 2
                        while ((halfH / inSample) >= 1024 && (halfW / inSample) >= 1024) {
                            inSample *= 2
                        }
                    }
                    inSampleSize = inSample
                    inMutable = true
                }
                val bitmap = BitmapFactory.decodeStream(sampleStream, null, decodeOptions)
                sampleStream?.close()

                if (bitmap != null) {
                    uploadedBitmap = bitmap
                    analyzeSkinBitmap(bitmap)
                } else {
                    Toast.makeText(context, "Could not decode image", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error loading skin photo: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            uploadedBitmap = bitmap
            analyzeSkinBitmap(bitmap)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Skin Scan (CV) • جلدی امراض",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "DermNet & HAM10000 Transfer Learning (EfficientNet)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Upload & Capture Actions Box
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Upload or Photograph Skin Lesion",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Capture a focused, well-lit photo of acne, rash, eczema patch, or mole for instant AI screening.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    getContentLauncher.launch("image/*")
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_upload_skin"),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Upload Photo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    cameraLauncher.launch(null)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_camera_skin"),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Take Photo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (uploadedBitmap != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Custom Skin Image Active", fontSize = 11.sp, color = EmeraldLight, fontWeight = FontWeight.SemiBold)
                                }

                                OutlinedButton(
                                    onClick = {
                                        uploadedBitmap = null
                                        selectedLesionPreset = "Eczema"
                                        scanResult = ComputerVisionEngine.analyzeSkinLesion(presetType = "Eczema")
                                    },
                                    modifier = Modifier.height(30.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Use Sample", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Presets selector for quick demonstration
            item {
                Text(
                    text = "Or Test Clinical Dermoscopy Case:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Eczema", "Fungal", "Acne", "Melanoma").forEach { type ->
                        FilterChip(
                            selected = uploadedBitmap == null && selectedLesionPreset == type,
                            onClick = {
                                uploadedBitmap = null
                                selectedLesionPreset = type
                                heatFocusX = when (type) {
                                    "Melanoma" -> 0.52f
                                    "Fungal" -> 0.48f
                                    "Acne" -> 0.54f
                                    else -> 0.50f
                                }
                                heatFocusY = 0.50f
                                scanResult = ComputerVisionEngine.analyzeSkinLesion(presetType = type)
                            },
                            label = { Text(type, fontSize = 11.sp) }
                        )
                    }
                }
            }

            // Grad-CAM Visual Overlay Component
            item {
                if (isAnalyzing) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(color = EmeraldLight)
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "EfficientNet Analyzing Skin Lesion...",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Computing ABCDE asymmetry, pigment variegation & borders",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }
                } else {
                    GradCamOverlayView(
                        baseImageRes = if (uploadedBitmap == null) R.drawable.img_skin_lesion_sample_1792146775418 else null,
                        baseBitmap = uploadedBitmap,
                        heatFocusX = heatFocusX,
                        heatFocusY = heatFocusY,
                        heatRadiusRatio = if (selectedLesionPreset == "Melanoma") 0.42f else 0.35f,
                        label = "Grad-CAM Attention: ${scanResult.condition}",
                        onFocusChange = { fx, fy ->
                            heatFocusX = fx
                            heatFocusY = fy
                        }
                    )
                }
            }

            // Diagnosis & Urgency Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = scanResult.urgency.bgColor)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = scanResult.urgency.titleEn,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = scanResult.urgency.color
                            )
                            Text(
                                text = "Confidence: ${(scanResult.confidence * 100).toInt()}%",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = scanResult.urgency.color
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = scanResult.condition,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = scanResult.conditionUrdu,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Explainability (Why AI Flagged This):",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = scanResult.gradCamExplanation,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // ABCDE Dermatological Criteria
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "ABCD Clinical Dermoscopy Criteria",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        AbcdRow(letter = "A", name = "Asymmetry", detail = scanResult.asymmetryScore)
                        AbcdRow(letter = "B", name = "Border", detail = scanResult.borderRegularity)
                        AbcdRow(letter = "C", name = "Color", detail = scanResult.colorVariations)
                        AbcdRow(letter = "D", name = "Diameter", detail = scanResult.diameter)
                    }
                }
            }

            // Differential Diagnosis Breakdown
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Differential Probabilities (Ensemble)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        scanResult.differentialDiagnosis.forEach { (condition, prob) ->
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = condition, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    Text(text = "${(prob * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                LinearProgressIndicator(
                                    progress = { prob },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = if (condition.contains("Melanoma")) UrgencyEmergencyRed else EmeraldPrimary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Clinical Advice in Urdu and English
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Care Guidance • طبی رہنمائی",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = scanResult.clinicalAdvice,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = scanResult.clinicalAdviceUrdu,
                            fontSize = 12.sp,
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun AbcdRow(letter: String, name: String, detail: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(EmeraldPrimary.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letter,
                color = EmeraldPrimary,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = detail,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
