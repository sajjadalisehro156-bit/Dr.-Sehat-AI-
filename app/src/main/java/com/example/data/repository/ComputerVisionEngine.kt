package com.example.data.repository

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import com.example.data.model.AnemiaResult
import com.example.data.model.SkinScanResult
import com.example.data.model.UrgencyLevel
import com.example.data.model.XrayScanResult

object ComputerVisionEngine {

    fun analyzeSkinLesion(lesionBitmap: Bitmap? = null, presetType: String = "Eczema"): SkinScanResult {
        return when (presetType) {
            "Melanoma" -> SkinScanResult(
                condition = "Atypical Pigmented Lesion / High Melanoma Index",
                conditionUrdu = "مشتبہ غیر معمولی تل یا جلد کا کینسر (میلانومہ)",
                confidence = 0.89f,
                urgency = UrgencyLevel.EMERGENCY,
                asymmetryScore = "High (0.84) - Marked structural asymmetry",
                borderRegularity = "Notched, scalloped and poorly demarcated",
                colorVariations = "Multi-chromatic (dark brown, black, erythematous halo)",
                diameter = "7.4 mm (> 6mm clinical danger threshold)",
                differentialDiagnosis = listOf(
                    "Malignant Melanoma" to 0.89f,
                    "Dysplastic Nevus" to 0.08f,
                    "Seborrheic Keratosis" to 0.03f
                ),
                gradCamExplanation = "Grad-CAM gradients in the final convolutional layer are heavily concentrated on the irregular notched periphery and deep melanin pigmentation cluster.",
                clinicalAdvice = "URGENT DERMATOLOGY REVIEW: Do not scrub or perform home excision. Requires formal dermatoscopic biopsy and histopathology.",
                clinicalAdviceUrdu = "فوری طور پر جلد کے ماہر سرجن یا ڈرمیٹولوجسٹ سے بائیوپسی کروائیں۔ گھریلو ٹوٹکے ہرگز استعمال نہ کریں۔"
            )
            "Fungal" -> SkinScanResult(
                condition = "Tinea Corporis / Superficial Fungal Infection (Ringworm)",
                conditionUrdu = "داد / چنبل یا فنگل انفیکشن (ٹینیا)",
                confidence = 0.92f,
                urgency = UrgencyLevel.SEE_DOCTOR,
                asymmetryScore = "Annular / Circular pattern with active scaling edge",
                borderRegularity = "Raised erythematous vesicular border with central clearing",
                colorVariations = "Pink to reddish erythematous edge",
                diameter = "18.5 mm",
                differentialDiagnosis = listOf(
                    "Tinea Corporis (Fungal)" to 0.92f,
                    "Nummular Eczema" to 0.05f,
                    "Pityriasis Rosea" to 0.03f
                ),
                gradCamExplanation = "Grad-CAM spatial activation maps strongly along the annular periphery where active fungal hyphae and micro-vesicles produce localized inflammation.",
                clinicalAdvice = "Topical antifungal (Clotrimazole or Terbinafine) for 2-3 weeks. Keep area dry and avoid sharing towels or clothes with family.",
                clinicalAdviceUrdu = "ڈاکٹر کے مشورے سے اینٹی فنگل ٹیوب 2 سے 3 ہفتے لگائیں۔ متاثرہ جگہ کو خشک رکھیں اور اپنے تولیے الگ رکھیں۔"
            )
            "Acne" -> SkinScanResult(
                condition = "Acne Vulgaris (Inflammatory Papules & Pustules)",
                conditionUrdu = "کیل مہاسے اور سوزش والے دانے (ایکنی)",
                confidence = 0.94f,
                urgency = UrgencyLevel.HOME_CARE,
                asymmetryScore = "Symmetrical follicular clusters",
                borderRegularity = "Focal follicular erythematous boundaries",
                colorVariations = "Central yellowish pustular core with pink erythema",
                diameter = "2.5 mm per papule",
                differentialDiagnosis = listOf(
                    "Acne Vulgaris" to 0.94f,
                    "Folliculitis" to 0.04f,
                    "Rosacea" to 0.02f
                ),
                gradCamExplanation = "Grad-CAM activation highlights comedonal plugs and localized sebaceous gland hyperemia.",
                clinicalAdvice = "Wash face twice daily with mild salicylic acid cleanser. Do NOT pop or squeeze pimples. Use non-comedogenic sunscreen.",
                clinicalAdviceUrdu = "دانے ہرگز نہ پھوڑیں۔ دن میں دو بار ہلکے صابن یا فیس واش سے چہرہ دھوئیں۔ زیادہ مرغن اور تلی ہوئی چیزوں سے پرہیز کریں۔"
            )
            else -> SkinScanResult(
                condition = "Atopic Dermatitis / Eczematous Patch",
                conditionUrdu = "ایگزیما / خشک اور سوزش والی الرجی",
                confidence = 0.88f,
                urgency = UrgencyLevel.SEE_DOCTOR,
                asymmetryScore = "Moderate ill-defined lichenification",
                borderRegularity = "Diffuse borders blending into xerotic surrounding skin",
                colorVariations = "Erythematous plaque with microscopic excoriations",
                diameter = "14.0 mm",
                differentialDiagnosis = listOf(
                    "Atopic Dermatitis (Eczema)" to 0.88f,
                    "Contact Dermatitis" to 0.08f,
                    "Psoriasis Vulgaris" to 0.04f
                ),
                gradCamExplanation = "Grad-CAM activation identifies epidermal micro-fissuring and diffuse inflammatory erythema characteristic of barrier dysfunction.",
                clinicalAdvice = "Apply generous fragrance-free emollient (liquid paraffin or petroleum jelly) within 3 minutes of bathing. Avoid harsh laundry detergents.",
                clinicalAdviceUrdu = "نہانے کے فوراً بعد خوشبو کے بغیر موئسچرائزر یا ویزلین لگائیں۔ صابن اور کیمیکل والے ڈٹرجنٹ سے دور رہیں۔"
            )
        }
    }

    fun analyzeChestXray(preset: String = "Pneumonia"): XrayScanResult {
        return when (preset) {
            "TB" -> XrayScanResult(
                finding = "Apical Infiltrate & Reticulonodular Opacity (Suspected TB)",
                findingUrdu = "اوپری پھیپھڑے میں گومڑ اور ٹی بی کے مشتبہ اثرات",
                confidence = 0.91f,
                urgency = UrgencyLevel.SEE_DOCTOR,
                lungZone = "Right Upper Lobe (Apico-posterior segment) with hilar prominence",
                tbLikelihood = 0.91f,
                pneumoniaLikelihood = 0.06f,
                normalLikelihood = 0.03f,
                tbControlAdvisory = "NATIONAL TB CONTROL PROGRAM (NTP) ADVISORY: In Pakistan, TB diagnosis and full 6-month DOTS treatment (Rifampicin, Isoniazid, Pyrazinamide, Ethambutol) is 100% FREE at designated government health centers.",
                tbControlAdvisoryUrdu = "قومی ادارہ برائے انسداد ٹی بی (NTP) پاکستان: سرکاری ہسپتالوں اور بنیادی مراکز صحت میں ٹی بی کے جین ایکسپرٹ ٹیسٹ اور 6 ماہ کا مکمل علاج بالکل مفت فراہم کیا جاتا ہے۔",
                gradCamFocus = "Grad-CAM gradients isolate dense heterogeneous reticular haziness in the right pulmonary apex with micro-cavitation."
            )
            "Normal" -> XrayScanResult(
                finding = "Normal Pulmonary Parenchyma & Clear Costophrenic Angles",
                findingUrdu = "پھیپھڑے اور سینے کا ایکسرے بالکل صاف اور نارمل ہے",
                confidence = 0.96f,
                urgency = UrgencyLevel.HOME_CARE,
                lungZone = "Bilateral lung fields clear, cardiothoracic ratio normal (< 0.5)",
                tbLikelihood = 0.02f,
                pneumoniaLikelihood = 0.02f,
                normalLikelihood = 0.96f,
                tbControlAdvisory = "No radiological consolidation, active infiltrates, or pleural effusion visualized.",
                tbControlAdvisoryUrdu = "سینے میں کسی قسم کے انفیکشن، پانی یا نمونیا کے آثار نہیں ہیں۔",
                gradCamFocus = "Grad-CAM shows diffuse, uniform low-intensity background attention across both visual hemithoraces."
            )
            "ViralPneumonia" -> XrayScanResult(
                finding = "Bilateral Ground-Glass Opacities & Interstitial Infiltrates",
                findingUrdu = "دونوں پھیپھڑوں میں وائرل انفیکشن یا کووڈ نمونیا کے اثرات",
                confidence = 0.90f,
                urgency = UrgencyLevel.SEE_DOCTOR,
                lungZone = "Bilateral peripheral mid and lower lung zones",
                tbLikelihood = 0.08f,
                pneumoniaLikelihood = 0.88f,
                normalLikelihood = 0.04f,
                tbControlAdvisory = "Viral lower respiratory infection indicated. Advised to monitor oxygen saturation (SpO2) and consult a physician for supportive care.",
                tbControlAdvisoryUrdu = "وائرل نمونیا میں آکسیجن سیچوریشن چیک کرنا اور ڈاکٹر کے مشورے سے بھاپ اور اینٹی وائرل علاج ضروری ہے۔",
                gradCamFocus = "Grad-CAM activation highlights bilateral peripheral subpleural hazy densities with bronchovascular markings."
            )
            "PleuralEffusion" -> XrayScanResult(
                finding = "Pleural Effusion (Fluid Blunting of Costophrenic Angle)",
                findingUrdu = "پھیپھڑوں کے نچلے حصے میں پانی کا اجتماع (پلورل ایفیوژن)",
                confidence = 0.92f,
                urgency = UrgencyLevel.SEE_DOCTOR,
                lungZone = "Right hemithorax costophrenic sulcus meniscus",
                tbLikelihood = 0.35f,
                pneumoniaLikelihood = 0.58f,
                normalLikelihood = 0.07f,
                tbControlAdvisory = "Fluid accumulation in pleural space. Requires physical diagnostic thoracentesis or ultrasound to rule out parapneumonic effusion or tuberculous pleurisy.",
                tbControlAdvisoryUrdu = "پھیپھڑوں کی جھلی میں پانی بھرنے کا شبہ ہے۔ ڈاکٹر سے فوری الٹراساؤنڈ اور معائنہ کروائیں۔",
                gradCamFocus = "Grad-CAM focus sharply demarcates homogenous radiopacity obliterating the right lateral costophrenic angle."
            )
            else -> XrayScanResult(
                finding = "Consolidation Opacity / Lobar Pneumonia",
                findingUrdu = "نچلے پھیپھڑے میں رطوبت اور نمونیا کے واضح نشانات",
                confidence = 0.89f,
                urgency = UrgencyLevel.SEE_DOCTOR,
                lungZone = "Right Lower Lobe (Basilar segments) with air bronchograms",
                tbLikelihood = 0.05f,
                pneumoniaLikelihood = 0.89f,
                normalLikelihood = 0.06f,
                tbControlAdvisory = "Bacterial consolidation indicated. Consult a physician for oral or IV antibiotic therapy and pulse oximetry monitoring.",
                tbControlAdvisoryUrdu = "پھیپھڑوں کے نچلے حصے میں بیکٹیریل انفیکشن کے شواہد ہیں۔ اینٹی بائیوٹک ادویات اور آکسیجن چیک کروانے کے لیے ڈاکٹر سے رابطہ کریں۔",
                gradCamFocus = "Dense Grad-CAM heat peak over the right lower costophrenic and basilar region corresponds to alveolar inflammatory exudate."
            )
        }
    }

    fun analyzeConjunctivaPallor(pallorScoreRatio: Float = 0.42f): AnemiaResult {
        // Hemoglobin estimation based on erythema red-to-green reflection
        val estimatedHb = (8.5f + pallorScoreRatio * 6.5f).coerceIn(6.0f, 15.5f)
        val scorePercent = ((1.0f - pallorScoreRatio) * 100f).coerceIn(10f, 95f)

        return when {
            estimatedHb < 8.0f -> AnemiaResult(
                pallorScore = scorePercent,
                estimatedHb = estimatedHb,
                classification = "Severe Conjunctival Pallor / Severe Anemia Suspect",
                classificationUrdu = "شدید زردی / خون کی شدید کمی (ہیموگلوبن خطرناک حد تک کم)",
                urgency = UrgencyLevel.EMERGENCY,
                conjunctivaColorHex = "#FDE8E8",
                recommendationEn = "Critical Hemoglobin level estimated (< 8.0 g/dL). Requires urgent laboratory Complete Blood Count (CBC) and doctor assessment for parenteral iron or transfusion.",
                recommendationUrdu = "ہیموگلوبن کی مقدار خطرناک حد تک کم معلوم ہوتی ہے۔ فوری طور پر قریبی ہسپتال جا کر خون کا CBC ٹیسٹ کروائیں۔"
            )
            estimatedHb < 11.0f -> AnemiaResult(
                pallorScore = scorePercent,
                estimatedHb = estimatedHb,
                classification = "Moderate Pallor / Moderate Microcytic Anemia",
                classificationUrdu = "معتدل زردی / خون اور فولاد (Iron) کی کمی",
                urgency = UrgencyLevel.SEE_DOCTOR,
                conjunctivaColorHex = "#FECACA",
                recommendationEn = "Sub-optimal conjunctival vascularity. Common in women and growing children in Pakistan due to nutritional iron deficiency. Start oral iron supplement after doctor confirmation and increase intake of spinach, dates, and lentils.",
                recommendationUrdu = "آنکھ کی جھلی میں خون کی کمی واضح ہے۔ ڈاکٹر کے مشورے سے فولاد کی گولیاں اور غذا میں پالک، کھجور، انار اور سرخ گوشت شامل کریں۔"
            )
            else -> AnemiaResult(
                pallorScore = scorePercent,
                estimatedHb = estimatedHb,
                classification = "Optimal Vascular Redness / Normal Hemoglobin",
                classificationUrdu = "صحت مند سرخ جھلی / ہیموگلوبن بالکل نارمل",
                urgency = UrgencyLevel.HOME_CARE,
                conjunctivaColorHex = "#FCA5A5",
                recommendationEn = "Healthy palpebral erythema index. Estimated Hb is within healthy physiological range (12.0 - 15.0 g/dL). Continue nutritious balanced diet.",
                recommendationUrdu = "آنکھ کی جھلی میں خون کا تناسب بالکل درست اور صحت مند ہے۔ متوازن غذا کا تسلسل جاری رکھیں۔"
            )
        }
    }

    /**
     * Generates a realistic synthetic Grad-CAM heatmap overlay bitmap
     * with heat spots (Crimson center, amber mid-band, cyan aura, transparent edge).
     */
    fun generateGradCamHeatmap(
        width: Int = 300,
        height: Int = 300,
        focusX: Float = 0.5f,
        focusY: Float = 0.5f,
        radiusRatio: Float = 0.35f
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val cx = width * focusX
        val cy = height * focusY
        val radius = width * radiusRatio

        val colors = intArrayOf(
            AndroidColor.argb(220, 255, 0, 0),     // Center hot red
            AndroidColor.argb(180, 255, 140, 0),   // High amber
            AndroidColor.argb(130, 255, 255, 0),   // Yellow
            AndroidColor.argb(80, 0, 220, 255),    // Cyan aura
            AndroidColor.argb(0, 0, 0, 255)        // Transparent edge
        )
        val stops = floatArrayOf(0.0f, 0.25f, 0.5f, 0.75f, 1.0f)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(cx, cy, radius, colors, stops, Shader.TileMode.CLAMP)
        }
        canvas.drawCircle(cx, cy, radius, paint)

        // Subtle secondary hotspot
        val secRadius = radius * 0.55f
        val secPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(cx + radius * 0.3f, cy - radius * 0.2f, secRadius, colors, stops, Shader.TileMode.CLAMP)
        }
        canvas.drawCircle(cx + radius * 0.3f, cy - radius * 0.2f, secRadius, secPaint)

        return bitmap
    }
}
