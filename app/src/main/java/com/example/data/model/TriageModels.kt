package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.UrgencyDoctorBg
import com.example.ui.theme.UrgencyDoctorYellow
import com.example.ui.theme.UrgencyEmergencyBg
import com.example.ui.theme.UrgencyEmergencyRed
import com.example.ui.theme.UrgencyHomeCareBg
import com.example.ui.theme.UrgencyHomeCareGreen

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    URDU("ur", "Urdu", "اردو"),
    ROMAN_URDU("ro", "Roman Urdu", "Roman Urdu")
}

enum class UrgencyLevel(
    val titleEn: String,
    val titleUr: String,
    val color: Color,
    val bgColor: Color,
    val iconName: String
) {
    HOME_CARE(
        titleEn = "Home Care / Self-limiting",
        titleUr = "گھریلو دیکھ بھال / آرام",
        color = UrgencyHomeCareGreen,
        bgColor = UrgencyHomeCareBg,
        iconName = "CheckCircle"
    ),
    SEE_DOCTOR(
        titleEn = "See a Doctor (24-48 hrs)",
        titleUr = "ڈاکٹر سے رجوع کریں (24-48 گھنٹے)",
        color = UrgencyDoctorYellow,
        bgColor = UrgencyDoctorBg,
        iconName = "Warning"
    ),
    EMERGENCY(
        titleEn = "Emergency Triage (Immediate / 1122)",
        titleUr = "فوری ایمرجنسی / 1122 پر رابطہ کریں",
        color = UrgencyEmergencyRed,
        bgColor = UrgencyEmergencyBg,
        iconName = "Emergency"
    )
}

data class Symptom(
    val id: String,
    val nameEn: String,
    val nameUrdu: String,
    val nameRoman: String,
    val category: String,
    val isRedFlag: Boolean = false
)

data class TriageResult(
    val conditionEn: String,
    val conditionUrdu: String,
    val conditionRoman: String,
    val urgency: UrgencyLevel,
    val probability: Float, // 0.0 to 1.0
    val summaryEn: String,
    val summaryUrdu: String,
    val homeCareTips: List<String>,
    val redFlags: List<String>,
    val recommendedDoctor: String,
    val modelExplanation: String
)

data class ChatMessage(
    val id: String,
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val language: AppLanguage = AppLanguage.ROMAN_URDU,
    val triageResult: TriageResult? = null,
    val suggestedChips: List<String> = emptyList()
)

data class SkinScanResult(
    val condition: String,
    val conditionUrdu: String,
    val confidence: Float,
    val urgency: UrgencyLevel,
    val asymmetryScore: String,
    val borderRegularity: String,
    val colorVariations: String,
    val diameter: String,
    val differentialDiagnosis: List<Pair<String, Float>>,
    val gradCamExplanation: String,
    val clinicalAdvice: String,
    val clinicalAdviceUrdu: String
)

data class XrayScanResult(
    val finding: String,
    val findingUrdu: String,
    val confidence: Float,
    val urgency: UrgencyLevel,
    val lungZone: String,
    val tbLikelihood: Float,
    val pneumoniaLikelihood: Float,
    val normalLikelihood: Float,
    val tbControlAdvisory: String,
    val tbControlAdvisoryUrdu: String,
    val gradCamFocus: String
)

data class VitalsData(
    val heartRateBpm: Int,
    val systolicBp: Int = 118,
    val diastolicBp: Int = 78,
    val spo2Percent: Int,
    val bodyTempF: Float,
    val signalQuality: String, // "Optimal", "Good", "Weak"
    val timestamp: Long = System.currentTimeMillis(),
    val source: String = "Smartphone Camera rPPG" // or "ESP32 Sensor Unit"
)

data class CoughAnalysisResult(
    val coughType: String,
    val coughTypeUrdu: String,
    val confidence: Float,
    val urgency: UrgencyLevel,
    val acousticDuration: String,
    val spectralPitch: String,
    val explosivePhases: Int,
    val potentialCauses: List<String>,
    val recommendationEn: String,
    val recommendationUrdu: String
)

data class AnemiaResult(
    val pallorScore: Float, // 0 to 100
    val estimatedHb: Float, // g/dL
    val classification: String,
    val classificationUrdu: String,
    val urgency: UrgencyLevel,
    val conjunctivaColorHex: String,
    val recommendationEn: String,
    val recommendationUrdu: String
)

data class MedicineReminder(
    val id: String,
    val medicineName: String,
    val dosage: String,
    val frequency: String,
    val timeLabel: String,
    val takeWithFood: Boolean,
    val instructionsUrdu: String,
    val isActive: Boolean = true
)

data class DietTip(
    val id: String,
    val titleEn: String,
    val titleUrdu: String,
    val category: String, // "Fever/Cold", "Digestive/Khichdi", "Respiratory", "Anemia/Blood"
    val descriptionEn: String,
    val descriptionUrdu: String,
    val desiIngredient: String
)

data class ConfusionMatrixData(
    val labels: List<String>,
    val matrix: List<List<Int>>
)

data class BenchmarkDataset(
    val name: String,
    val domain: String,
    val kaggleSource: String,
    val sampleCount: String,
    val precision: Float,
    val recall: Float,
    val f1Score: Float,
    val accuracy: Float,
    val architecture: String,
    val confusionMatrix: ConfusionMatrixData
)
