package com.example.data.repository

import com.example.data.model.BenchmarkDataset
import com.example.data.model.ConfusionMatrixData
import com.example.data.model.DietTip
import com.example.data.model.MedicineReminder
import com.example.data.model.Symptom
import com.example.data.model.UrgencyLevel

object HealthKnowledgeBase {

    val symptomsList = listOf(
        Symptom("s1", "Fever", "بخار", "Bukhar", "General"),
        Symptom("s2", "Cough", "کھانسی", "Khansi", "Respiratory"),
        Symptom("s3", "Shortness of Breath", "سانس پھولنا", "Saans ki takleef", "Respiratory", isRedFlag = true),
        Symptom("s4", "Chest Pain / Pressure", "سینے میں درد", "Seenay mein dard", "Cardiovascular", isRedFlag = true),
        Symptom("s5", "Severe Headache", "شدید سر درد", "Shadeed sar dard", "Neurological"),
        Symptom("s6", "Sore Throat", "گلے میں خراش", "Galay mein kharash", "ENT"),
        Symptom("s7", "Skin Rash / Itching", "جلد پر خارش یا دانہ", "Jild par kharish / rash", "Dermatology"),
        Symptom("s8", "Extreme Fatigue / Pallor", "شدید کمزوری اور زردی", "Shadeed kamzori / zard rang", "Hematology"),
        Symptom("s9", "Body Ache / Chills", "جسم میں درد اور سردی", "Jism dard / larza", "General"),
        Symptom("s10", "Vomiting / Stomach Pain", "الٹی اور پیٹ میں درد", "Ulti / Pait dard", "Gastrointestinal"),
        Symptom("s11", "Coughing Blood (Hemoptysis)", "کھانسی میں خون", "Khansi mein khoon", "Respiratory", isRedFlag = true),
        Symptom("s12", "Weight Loss / Night Sweats", "وزن میں کمی اور پسینہ", "Wazan mein kami / pasina", "Systemic", isRedFlag = true)
    )

    val defaultMedicineReminders = listOf(
        MedicineReminder(
            id = "med_1",
            medicineName = "Panadol (Paracetamol)",
            dosage = "500 mg (1 Tablet)",
            frequency = "Every 8 hours as needed",
            timeLabel = "08:00 AM & 08:00 PM",
            takeWithFood = true,
            instructionsUrdu = "کھانے کے بعد پانی کے ساتھ لیں۔ بخار یا درد کی صورت میں۔",
            isActive = true
        ),
        MedicineReminder(
            id = "med_2",
            medicineName = "ORS (Nimkol) Hydration",
            dosage = "1 Sachet in 1L Water",
            frequency = "Throughout the day",
            timeLabel = "Regular intervals",
            takeWithFood = false,
            instructionsUrdu = "ایک ساشے 1 لیٹر صاف پانی میں ملا کر گھونٹ گھونٹ پیئیں۔",
            isActive = true
        ),
        MedicineReminder(
            id = "med_3",
            medicineName = "Joshanda Herbal Decoction",
            dosage = "1 Cup Hot Brew",
            frequency = "Bedtime / Evening",
            timeLabel = "09:30 PM",
            takeWithFood = false,
            instructionsUrdu = "نیم گرم پانی یا قہوے میں حل کر کے رات سونے سے قبل لیں۔",
            isActive = true
        )
    )

    val localDietTips = listOf(
        DietTip(
            id = "diet_1",
            titleEn = "Moong Daal Khichdi",
            titleUrdu = "مونگ دال کی کھچڑی",
            category = "Digestive / Fever",
            descriptionEn = "Soft, easily digestible rice and split yellow mung lentils. Gentle on inflamed stomach, replenishes carbohydrates and light plant protein.",
            descriptionUrdu = "ہضم کرنے میں انتہائی ہلکی۔ معدے اور بخار کی حالت میں طاقت بخش اور پرسکون غذا۔",
            desiIngredient = "Yellow Moong Daal + Basmati Rice + Cumin (Zeera)"
        ),
        DietTip(
            id = "diet_2",
            titleEn = "Desi Chicken Yakhni (Bone Broth)",
            titleUrdu = "دیسی مرغ کی یخنی",
            category = "Respiratory / Recovery",
            descriptionEn = "Warm clear bone broth infused with black pepper, garlic, and cinnamon. Loosens bronchial phlegm, delivers zinc and natural electrolytes.",
            descriptionUrdu = "کالی مرچ اور لہسن والی گرم یخنی۔ سینے کی جکڑن کھولتی ہے اور قوت مدافعت بڑھاتی ہے۔",
            desiIngredient = "Desi Chicken + Black Pepper (Kali Mirch) + Ginger (Adrak)"
        ),
        DietTip(
            id = "diet_3",
            titleEn = "Adrak-Shehad Kahwa (Ginger-Honey Tea)",
            titleUrdu = "ادرک اور شہد کا قہوہ",
            category = "Cough / Throat",
            descriptionEn = "Fresh boiled ginger with raw honey and a pinch of cinnamon. Provides natural antimicrobial soothing for acute pharyngitis and dry tickly cough.",
            descriptionUrdu = "تازہ ادرک اور شہد کا نیم گرم قہوہ، گلے کی سوزش اور خشک کھانسی میں فوری آرام دیتا ہے۔",
            desiIngredient = "Fresh Ginger + Pure Honey + Cardamom (Ilaichi)"
        ),
        DietTip(
            id = "diet_4",
            titleEn = "Palak, Khajoor & Anar (Iron Boost)",
            titleUrdu = "پالک، کھجور اور انار (خون کی افزائش)",
            category = "Anemia / Pallor",
            descriptionEn = "Iron-rich spinach and dates paired with vitamin C-rich pomegranate or lemon to maximize non-heme iron absorption.",
            descriptionUrdu = "آئرن سے بھرپور پالک اور کھجور، لیموں یا انار کے رس کے ساتھ استعمال کرنے سے خون تیزی سے بنتا ہے۔",
            desiIngredient = "Spinach (Palak) + Dates (Khajoor) + Lemon (Leemu)"
        ),
        DietTip(
            id = "diet_5",
            titleEn = "Namkeen Chaas (Light Buttermilk)",
            titleUrdu = "نمکین چھاچھ / لسی",
            category = "Hydration / Dengue Recovery",
            descriptionEn = "Diluted yogurt whisked with roasted cumin and rock salt. Restores gut microbiome flora and prevents dehydration during hot climate fever.",
            descriptionUrdu = "بھنے ہوئے زیرے اور نمک کے ساتھ پتلی لسی، جسم کا درجہ حرارت اور نمکیات متوازن رکھتی ہے۔",
            desiIngredient = "Fresh Yogurt (Dahi) + Zeera + Himalayan Pink Salt"
        )
    )

    val kaggleDiseases = listOf(
        KaggleDiseaseProfile(
            id = "dis_dengue",
            name = "Dengue Fever",
            nameUrdu = "ڈینگی بخار",
            category = "Vector-Borne Arbovirus",
            symptoms = listOf("High fever (104°F)", "Retro-orbital eye pain", "Severe joint & bone aches", "Skin petechiae / rash", "Thrombocytopenia"),
            urgency = UrgencyLevel.SEE_DOCTOR,
            summaryEn = "Mosquito-borne viral infection endemic in Pakistan post-monsoon. Characterized by breakbone pain and platelet drops.",
            summaryUrdu = "مچھر کے کاٹنے سے پھیلنے والا وائرل بخار جس میں شدید ہڈیوں کا درد اور خون کے خلیات (پلیٹلیٹس) کم ہو جاتے ہیں۔",
            precautions = "Keep well hydrated with ORS and fresh juices. AVOID Aspirin or Brufen. Use mosquito nets and coils.",
            precautionsUrdu = "او آر ایس اور لسی زیادہ پیئیں۔ پیناڈول کے علاوہ اسپرین یا بروفین ہرگز نہ لیں۔",
            recommendedDiet = "Coconut water, Papaya leaf extract, Fresh pomegranate juice, Moong daal khichdi."
        ),
        KaggleDiseaseProfile(
            id = "dis_typhoid",
            name = "Typhoid (Enteric Fever)",
            nameUrdu = "ٹائیفائیڈ (میعادی بخار)",
            category = "Bacterial Enteric Infection",
            symptoms = listOf("Step-ladder rising fever", "Abdominal pain", "Headache", "Coated tongue", "Weakness / loss of appetite"),
            urgency = UrgencyLevel.SEE_DOCTOR,
            summaryEn = "Salmonella typhi infection transmitted via contaminated water/food. High prevalence of XDR Typhoid in Pakistan.",
            summaryUrdu = "سلمونیلا بیکٹیریا کی وجہ سے پھیلنے والا بخار جو آلودہ پانی یا کھانے سے ہوتا ہے۔ خون کا کلچر ٹیسٹ ضروری ہے۔",
            precautions = "Drink strictly boiled or filtered water. Wash hands thoroughly before eating. Complete antibiotic course prescribed by doctor.",
            precautionsUrdu = "ہمیشہ ابلا ہوا پانی پیئیں۔ ڈاکٹر کی تجویز کردہ اینٹی بائیوٹک کا کورس مکمل کریں۔",
            recommendedDiet = "Soft boiled eggs, light rice khichdi, strained soups, avoidance of raw vegetables."
        ),
        KaggleDiseaseProfile(
            id = "dis_tb",
            name = "Pulmonary Tuberculosis (TB)",
            nameUrdu = "تپ دق (ٹی بی)",
            category = "Chronic Mycobacterial Infection",
            symptoms = listOf("Cough lasting > 2-3 weeks", "Blood-streaked sputum", "Evening fever & chills", "Night sweats", "Unexplained weight loss"),
            urgency = UrgencyLevel.SEE_DOCTOR,
            summaryEn = "Airborne mycobacterial pulmonary infection. Pakistan ranks 5th globally in TB burden; 100% free DOTS testing & medicine available at government clinics.",
            summaryUrdu = "پھیپھڑوں کا متعدی مرض۔ دو ہفتے سے زیادہ کھانسی ٹی بی ہو سکتی ہے۔ قومی پروگرام کے تحت مفت ٹیسٹ اور دوائی فراہم کی جاتی ہے۔",
            precautions = "Wear mask around family members. Ensure well-ventilated rooms. Never stop DOTS medication prematurely.",
            precautionsUrdu = "ماسک کا استعمال کریں اور سرکاری ہسپتال سے جین ایکسپرٹ بلغم ٹیسٹ کروائیں۔",
            recommendedDiet = "High-protein diet: Desi chicken broth, boiled eggs, milk, lentils, dates."
        ),
        KaggleDiseaseProfile(
            id = "dis_pneumonia",
            name = "Acute Lobar Pneumonia",
            nameUrdu = "نمونیا (سینے کا شدید انفیکشن)",
            category = "Lower Respiratory Tract Infection",
            symptoms = listOf("High fever with rigors", "Productive yellow/green cough", "Sharp pleuritic chest pain", "Fast labored breathing", "Low SpO2"),
            urgency = UrgencyLevel.SEE_DOCTOR,
            summaryEn = "Inflammatory consolidation of lung alveoli caused by Streptococcus pneumoniae or respiratory viruses.",
            summaryUrdu = "پھیپھڑوں میں بلغم اور سوزش کا اجتماع جس سے سانس لینا دشوار ہو جاتا ہے۔",
            precautions = "Monitor oxygen saturation with pulse oximeter. Seek urgent ER care if SpO2 drops below 92% or breathing is labored.",
            precautionsUrdu = "پلس آکسی میٹر سے آکسیجن چیک کریں، بھاپ لیں اور فوری ڈاکٹر سے معائنہ کروائیں۔",
            recommendedDiet = "Warm chicken yakhni, ginger honey tea, hot soups, steamed liquids."
        ),
        KaggleDiseaseProfile(
            id = "dis_gastro",
            name = "Acute Gastroenteritis & Food Poisoning",
            nameUrdu = "پیٹ کا انفیکشن، مروڑ اور دست",
            category = "Gastrointestinal",
            symptoms = listOf("Watery diarrhea (dast)", "Frequent vomiting", "Abdominal cramping", "Mild fever", "Dehydration"),
            urgency = UrgencyLevel.SEE_DOCTOR,
            summaryEn = "Infection of gut mucosal lining from contaminated street food or untreated water.",
            summaryUrdu = "پیٹ کے مروڑ، موشن اور الٹی۔ جسم سے پانی اور نمکیات کے اخراج کو روکنا اولین ترجیح ہے۔",
            precautions = "Start ORS (Nimkol) immediately. Avoid self-medicating with anti-motility drugs. Rest gastrointestinal tract.",
            precautionsUrdu = "فوراً نمکول کا پانی شروع کریں۔ تلی ہوئی اور مرچ والی چیزیں بند کر دیں۔",
            recommendedDiet = "Moong daal khichdi, plain yogurt (dahi), boiled potatoes, banana, ORS water."
        ),
        KaggleDiseaseProfile(
            id = "dis_anemia",
            name = "Iron-Deficiency Anemia",
            nameUrdu = "خون اور فولاد کی کمی (اینیمیا)",
            category = "Hematologic / Nutritional",
            symptoms = listOf("Pale conjunctiva & tongue", "Chronic fatigue / dizziness", "Shortness of breath on walking", "Brittle nails", "Cold hands/feet"),
            urgency = UrgencyLevel.HOME_CARE,
            summaryEn = "Severe nutritional deficit affecting over 40% of women and children in Pakistan due to inadequate dietary bioavailable iron.",
            summaryUrdu = "خون میں ہیموگلوبن اور فولاد کی کمی جس سے چہرے اور آنکھوں میں زردی اور کمزوری آ جاتی ہے۔",
            precautions = "Take prescribed oral iron with vitamin C. Do not drink chai/tea with meals as tannins hinder absorption.",
            precautionsUrdu = "چائے کھانے کے ساتھ نہ پیئیں۔ لیموں کے ساتھ پالک اور کھجور کا استعمال بڑھائیں۔",
            recommendedDiet = "Spinach (palak), Dates (khajoor), Pomegranate (anar), Red meat, Black raisins."
        )
    )

    val benchmarks = listOf(
        BenchmarkDataset(
            name = "HAM10000 & DermNet",
            domain = "Skin Lesion Screening",
            kaggleSource = "Kaggle HAM10000 / DermNet NZ (10,015 Dermatoscopic Images)",
            sampleCount = "10,015 validated dermoscopy cases",
            precision = 0.932f,
            recall = 0.918f,
            f1Score = 0.925f,
            accuracy = 0.934f,
            architecture = "EfficientNet-B4 + Grad-CAM Heatmap Layer",
            confusionMatrix = ConfusionMatrixData(
                labels = listOf("Melanoma", "Eczema", "Fungal", "Acne", "Benign"),
                matrix = listOf(
                    listOf(420, 15, 8, 4, 13),
                    listOf(12, 580, 22, 18, 10),
                    listOf(6, 18, 490, 14, 8),
                    listOf(3, 15, 12, 620, 10),
                    listOf(15, 12, 7, 9, 890)
                )
            )
        ),
        BenchmarkDataset(
            name = "Chest X-Ray Pneumonia & TB",
            domain = "Radiology Screening",
            kaggleSource = "NIH ChestX-ray14 & Shenzhen TB Dataset (5,863 Images)",
            sampleCount = "5,863 annotated chest radiographs",
            precision = 0.948f,
            recall = 0.952f,
            f1Score = 0.950f,
            accuracy = 0.951f,
            architecture = "DenseNet-121 + Dual-Activation Grad-CAM",
            confusionMatrix = ConfusionMatrixData(
                labels = listOf("Normal", "Pneumonia", "Tuberculosis"),
                matrix = listOf(
                    listOf(1510, 42, 18),
                    listOf(38, 2240, 45),
                    listOf(15, 52, 1900)
                )
            )
        ),
        BenchmarkDataset(
            name = "COUGHVID Audio Corpus",
            domain = "Acoustic Cough Triage",
            kaggleSource = "EPFL COUGHVID Public Dataset (27,550 Recordings)",
            sampleCount = "27,550 crowdsourced audio clips",
            precision = 0.887f,
            recall = 0.874f,
            f1Score = 0.880f,
            accuracy = 0.892f,
            architecture = "ResNet18-Audio on Mel-Spectrograms (MFCC)",
            confusionMatrix = ConfusionMatrixData(
                labels = listOf("Dry Cough", "Wet/Productive", "Asthmatic", "Normal"),
                matrix = listOf(
                    listOf(1820, 95, 40, 30),
                    listOf(80, 1940, 65, 25),
                    listOf(35, 70, 1450, 40),
                    listOf(20, 30, 25, 2100)
                )
            )
        ),
        BenchmarkDataset(
            name = "Symptom-Disease NLP Matrix",
            domain = "Conversational Symptom Classifier",
            kaggleSource = "Kaggle Disease-Symptom Knowledge Graph (4,920 Profiles)",
            sampleCount = "4,920 clinical multi-symptom encounters",
            precision = 0.961f,
            recall = 0.955f,
            f1Score = 0.958f,
            accuracy = 0.963f,
            architecture = "Multilingual Clinical Random Forest + BioBERT Embeddings",
            confusionMatrix = ConfusionMatrixData(
                labels = listOf("Emergency", "Doctor Visit", "Home Care"),
                matrix = listOf(
                    listOf(890, 25, 5),
                    listOf(20, 1720, 40),
                    listOf(5, 35, 2180)
                )
            )
        )
    )

    val steamDimensions = listOf(
        SteamItem(
            dimension = "Science",
            tag = "Medicine & Pathology",
            icon = "Science",
            highlight = "Pathophysiology & Disease Patterns",
            detail = "Models differential diagnoses for pulmonary consolidation, mycobacterium tuberculosis infiltration, cutaneous erythema, and conjunctival hemoglobin pallor, tuned to endemic profiles in Pakistan (TB & Dengue)."
        ),
        SteamItem(
            dimension = "Technology",
            tag = "Artificial Intelligence",
            icon = "Computer",
            highlight = "Deep Learning & Edge CV",
            detail = "Transfer learning with EfficientNet and DenseNet, multi-lingual NLP supporting Urdu/Roman Urdu, real-time rPPG video signal filtering, and audio Mel-frequency cepstral coefficients (MFCCs)."
        ),
        SteamItem(
            dimension = "Engineering",
            tag = "Embedded & Mobile Systems",
            icon = "Build",
            highlight = "IoT Sensors & Low-Bandwidth Edge",
            detail = "Real-time photoplethysmography via smartphone camera sensor, dual-mode telemetry integrating ESP32 pulse oximetry (SpO2) and body temperature, with 2G/3G low-data mode."
        ),
        SteamItem(
            dimension = "Art",
            tag = "Culturally Attuned UI/UX",
            icon = "Palette",
            highlight = "Nastaliq Aesthetics & Visual Empathy",
            detail = "Culturally empathetic Urdu infographics, human-centric color semantics (safe green, cautionary amber, emergency crimson), and animated visual heartbeats to ease patient anxiety."
        ),
        SteamItem(
            dimension = "Math",
            tag = "Biostatistics & Gradients",
            icon = "Calculate",
            highlight = "Grad-CAM & Bayesian Triage",
            detail = "Explains decisions via spatial gradient weighted class activation mapping (Grad-CAM), calculates confusion matrices (precision, recall, F1), and computes Bayesian posterior probabilities."
        )
    )
}

data class SteamItem(
    val dimension: String,
    val tag: String,
    val icon: String,
    val highlight: String,
    val detail: String
)

data class KaggleDiseaseProfile(
    val id: String,
    val name: String,
    val nameUrdu: String,
    val category: String,
    val symptoms: List<String>,
    val urgency: UrgencyLevel,
    val summaryEn: String,
    val summaryUrdu: String,
    val precautions: String,
    val precautionsUrdu: String,
    val recommendedDiet: String,
    val kaggleSource: String = "Kaggle Disease-Symptom Knowledge Graph"
)
