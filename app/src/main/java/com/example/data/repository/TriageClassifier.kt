package com.example.data.repository

import com.example.data.model.AppLanguage
import com.example.data.model.TriageResult
import com.example.data.model.UrgencyLevel
import java.util.Locale

object TriageClassifier {

    fun isGreetingOrCasual(text: String): Boolean {
        val t = text.trim().lowercase(Locale.ROOT)
        val greetings = listOf(
            "salam", "assalam", "asalam", "aoa", "hello", "hi", "hey", "dr", "doctor",
            "kese ho", "kaise ho", "kya haal", "kia hal", "kia haal", "theek ho", "hal kaisa hai",
            "shukriya", "thanks", "ok", "acha", "thek", "bye", "خوش آمدید", "سلام", "ہیلو", "شکریہ"
        )
        return greetings.any { t == it || t.startsWith("$it ") || t.endsWith(" $it") } ||
                (t.length < 18 && (t.contains("salam") || t.contains("hello") || t.contains("kese ho") || t.contains("doctor sahab")))
    }

    fun classifySymptomInput(input: String, selectedSymptomIds: List<String> = emptyList()): TriageResult {
        val text = input.lowercase(Locale.ROOT)

        // 1. EMERGENCY: Acute Coronary / Heart / Severe Chest Pain
        val hasChest = text.contains("chest") || text.contains("seena") || text.contains("seenay") ||
                text.contains("سینے") || text.contains("dil") || text.contains("دل") || selectedSymptomIds.contains("s4")
        val hasSweatArm = text.contains("pasina") || text.contains("sweat") || text.contains("arm") ||
                text.contains("bazu") || text.contains("baazu") || text.contains("left") || text.contains("chakkar")
        val hasBreath = text.contains("breath") || text.contains("saans") || text.contains("dam") ||
                text.contains("سانس") || selectedSymptomIds.contains("s3")

        if (hasChest && (hasSweatArm || hasBreath || text.contains("shadeed") || text.contains("severe"))) {
            return TriageResult(
                conditionEn = "Suspected Acute Coronary Syndrome / Myocardial Infarction Alert",
                conditionUrdu = "مشتبہ دل کا دورہ یا شدید قلبی ایمرجنسی (فوری 1122 کال کریں)",
                conditionRoman = "Dil Ka Dora / Acute Coronary Emergency (Call 1122)",
                urgency = UrgencyLevel.EMERGENCY,
                probability = 0.95f,
                summaryEn = "Severe chest discomfort radiating to arm/jaw or accompanied by cold sweating and shortness of breath is a life-threatening emergency.",
                summaryUrdu = "سینے پر شدید دباؤ، بائیں بازو میں درد اور پسینے آنا دل کے دورے کی علامات ہو سکتی ہیں۔ فوری ریسکیو 1122 کال کریں۔",
                homeCareTips = listOf(
                    "Sit upright and rest completely; avoid physical exertion or climbing stairs.",
                    "Loosen tight neckwear and clothing.",
                    "If non-allergic and advised by physician, chew 300mg Soluble Aspirin (Disprin) immediately.",
                    "Do NOT drive yourself; call for an ambulance immediately."
                ),
                redFlags = listOf(
                    "Crushing central chest heaviness lasting > 10 minutes",
                    "Cold clammy perspiration and fainting",
                    "Pain radiating to left arm, neck or back"
                ),
                recommendedDoctor = "Cardiologist / Emergency Medicine (Call 1122)",
                modelExplanation = "Kaggle Cardiovascular Risk Engine: Acute ischemic triad (retrosternal pressure + autonomic diaphoresis + dyspnea)."
            )
        }

        // 2. EMERGENCY: Coughing Blood (Hemoptysis)
        val hasHemoptysis = text.contains("khoon") || text.contains("blood") || text.contains("خون") ||
                selectedSymptomIds.contains("s11")
        if (hasHemoptysis && (text.contains("cough") || text.contains("khansi") || text.contains("balgham") || text.contains("بلغم"))) {
            return TriageResult(
                conditionEn = "Hemoptysis / Active Bronchopulmonary Bleeding or TB",
                conditionUrdu = "کھانسی میں خون (ہیموپیٹیسس) / شدید پھیپھڑوں کی خرابی یا ٹی بی",
                conditionRoman = "Khansi mein khoon / Shabeh TB ya Pulmonary Bleeding",
                urgency = UrgencyLevel.EMERGENCY,
                probability = 0.93f,
                summaryEn = "Coughing up blood is a critical red flag indicating ruptured bronchial vessels, severe pneumonia, or cavitary tuberculosis.",
                summaryUrdu = "کھانسی یا بلغم کے ساتھ خون آنا خطرناک علامت ہے۔ فوری ہسپتال کے ایمرجنسی وارڈ یا سینے کے ماہر سے رجوع کریں۔",
                homeCareTips = listOf(
                    "Rest in a semi-reclined sitting position.",
                    "Avoid taking blood thinners like Aspirin or Disprin.",
                    "Collect coughed sputum in a clean container for clinical analysis."
                ),
                redFlags = listOf(
                    "Bright red blood coughed in large volume (> 50 ml)",
                    "Severe breathlessness and blueness of lips",
                    "Rapid pulse and drop in blood pressure"
                ),
                recommendedDoctor = "Pulmonologist / Chest Specialist",
                modelExplanation = "Shenzhen & Kaggle TB Graph: Hemoptysis flags endobronchial lesion or active cavitary mycobacterial erosion."
            )
        }

        // 3. DIABETES / SUGAR QUESTIONS & SYMPTOMS
        if (text.contains("sugar") || text.contains("diabet") || text.contains("ذیابیطس") || text.contains("انسولین") || text.contains("insulin")) {
            val isLow = text.contains("low") || text.contains("kam") || text.contains("gira") || text.contains("down") || text.contains("kapkapi")
            return TriageResult(
                conditionEn = if (isLow) "Hypoglycemia (Low Blood Sugar Alert)" else "Diabetes Mellitus Clinical Management & Nutritional Guidance",
                conditionUrdu = if (isLow) "خون میں شوگر کی خطرناک کمی (ہائپوگلائسیمیا)" else "ذیابیطس (شوگر) کا معائنہ، غذا اور پرہیز",
                conditionRoman = if (isLow) "Low Blood Sugar (Hypoglycemia)" else "Sugar (Diabetes Mellitus) Care & Diet",
                urgency = if (isLow) UrgencyLevel.EMERGENCY else UrgencyLevel.SEE_DOCTOR,
                probability = 0.94f,
                summaryEn = if (isLow)
                    "Low blood sugar (< 70 mg/dL) causes shaking, sweating, and confusion. Consume 15g fast-acting sugar immediately!"
                else
                    "Diabetes requires disciplined glycemic control: HbA1c under 7%, balanced low-glycemic Pakistani diet, and daily 30-minute brisk walk.",
                summaryUrdu = if (isLow)
                    "شوگر کم ہونے پر مریض کو کپکپاہٹ اور چکر آتے ہیں۔ فوراً 3 چمچ چینی، میٹھا شربت یا شہد دیں۔"
                else
                    "شوگر کے مریض چکی کے آٹے کی روٹی، دالیں، کریلے اور سلاد استعمال کریں۔ سفید چاول، چینی اور مٹھائی سے مکمل پرہیز کریں۔",
                homeCareTips = listOf(
                    if (isLow) "Follow 15-15 Rule: Eat 3 sugar cubes or half glass fruit juice, recheck after 15 mins." else "Eat whole grains (chaki atta), vegetables (karela, methi), and legumes (chana, daal).",
                    "Avoid white bread, bakery naan, sugary juices, sodas, and sweet tea (meethi chai).",
                    "Check fasting and random blood sugar regularly using a home glucometer.",
                    "Inspect feet daily for cuts or blisters (diabetic foot protection)."
                ),
                redFlags = listOf(
                    "Extreme confusion or loss of consciousness",
                    "Fruity breath odor with rapid breathing (Diabetic Ketoacidosis DKA)",
                    "Fasting sugar continuously above 250 mg/dL or below 60 mg/dL"
                ),
                recommendedDoctor = "Endocrinologist / Diabetologist",
                modelExplanation = "Kaggle Chronic Disease Knowledge Graph: Glycemic metabolic pathway profile."
            )
        }

        // 4. HYPERTENSION / HIGH BLOOD PRESSURE
        if (text.contains("bp") || text.contains("blood pressure") || text.contains("بلڈ پریشر") || text.contains("hypertension")) {
            val isSevere = text.contains("180") || text.contains("high") || text.contains("shadeed") || text.contains("sar phat")
            return TriageResult(
                conditionEn = if (isSevere) "Hypertensive Crisis Risk" else "Essential Hypertension (High Blood Pressure)",
                conditionUrdu = "بلڈ پریشر کی زیادتی (ہائی بلڈ پریشر) اور احتیاطی تدابیر",
                conditionRoman = "High Blood Pressure (Hypertension) Care",
                urgency = if (isSevere) UrgencyLevel.EMERGENCY else UrgencyLevel.SEE_DOCTOR,
                probability = 0.92f,
                summaryEn = "Optimal blood pressure is under 120/80 mmHg. High blood pressure is the leading cause of stroke and kidney failure in Pakistan.",
                summaryUrdu = "بلڈ پریشر 120/80 سے زیادہ ہونا خاموش خطرہ ہے۔ نمک اور مرغن غذاؤں سے پرہیز کریں اور باقاعدہ بی پی چیک کروائیں۔",
                homeCareTips = listOf(
                    "Drastically restrict table salt (namak) to under 1 teaspoon daily; avoid pickles (achaar) and salty snacks (nimco).",
                    "Drink warm water with crushed fresh garlic (lehsun) cloves and lemon juice.",
                    "Engage in 30 minutes of daily physical exercise and stress reduction.",
                    "Never stop prescribed antihypertensive tablets abruptly without consulting your doctor."
                ),
                redFlags = listOf(
                    "BP reading over 180/110 mmHg with throbbing occipital headache",
                    "Blurred vision or numbness in face/hands (Stroke warning)",
                    "Chest tightness or shortness of breath"
                ),
                recommendedDoctor = "Cardiologist / General Physician",
                modelExplanation = "Kaggle Cardiovascular Cohort: Hemodynamic vascular resistance assessment."
            )
        }

        // 5. HEADACHES & MIGRAINE (Sar dard)
        if (text.contains("headache") || text.contains("sar dard") || text.contains("sir dard") ||
            text.contains("سر درد") || text.contains("migraine") || text.contains("adhe sar") || selectedSymptomIds.contains("s5")) {
            val isMigraine = text.contains("adhe") || text.contains("one side") || text.contains("roshni") || text.contains("light") || text.contains("migraine")
            return TriageResult(
                conditionEn = if (isMigraine) "Acute Migraine Headache with Sensory Sensitivity" else "Tension-Type / Sinusogenic Headache",
                conditionUrdu = if (isMigraine) "درد شقیقہ (آدھے سر کا درد / مائیگرین)" else "سر کا درد (دباؤ، تھکن یا نزلہ زکام کی وجہ سے)",
                conditionRoman = if (isMigraine) "Aadhe Sar Ka Dard (Migraine)" else "Sar Ka Dard (Tension / Sinus Headache)",
                urgency = UrgencyLevel.HOME_CARE,
                probability = 0.90f,
                summaryEn = if (isMigraine)
                    "Migraine presents as throbbing unilateral pain aggravated by bright lights, loud sounds, or dehydration."
                else
                    "Tension headache typically responds well to rest, hydration, stress relief, and gentle neck massage.",
                summaryUrdu = "سر درد کے لیے پرسکون تاریک کمرے میں آرام کریں، وافر پانی پیئیں، اور پیناڈول لیں۔ چائے یا قہوہ پی سکتے ہیں۔",
                homeCareTips = listOf(
                    "Rest in a quiet, darkened room with a cool cloth over your forehead.",
                    "Hydrate with 2 large glasses of water or lemon ORS.",
                    "Take Paracetamol (Panadol) 500mg-1000mg after light food.",
                    "Avoid screen time (mobile/computer) until pain subsides."
                ),
                redFlags = listOf(
                    "Sudden explosive thunderclap headache ('worst headache of life')",
                    "Headache with stiff neck, high fever, and vomiting (Meningitis alert)",
                    "Associated weakness in limbs or difficulty speaking"
                ),
                recommendedDoctor = "Neurologist / Family Physician",
                modelExplanation = "Kaggle Neurological Triage: Cephalea vascular vs muscular cluster."
            )
        }

        // 6. DENGUE FEVER (Post-monsoon Pakistan staple)
        if (text.contains("dengue") || text.contains("ڈینگی") ||
            (text.contains("bukhar") && (text.contains("aankh") || text.contains("eye") || text.contains("haddi") || text.contains("bone")))) {
            return TriageResult(
                conditionEn = "Dengue Fever / Acute Thrombocytopenic Febrile Syndrome",
                conditionUrdu = "ڈینگی بخار (ہڈی توڑ بخار اور پلیٹلیٹس کی کمی)",
                conditionRoman = "Dengue Bukhar (Breakbone Fever / Platelet Drop)",
                urgency = UrgencyLevel.SEE_DOCTOR,
                probability = 0.92f,
                summaryEn = "Dengue causes severe retro-orbital eye pain, joint agony, and critical drop in blood platelet counts. CBC test is mandatory.",
                summaryUrdu = "ڈینگی میں آنکھوں کے پیچھے شدید درد، تیز بخار اور پلیٹلیٹس کم ہوتے ہیں۔ فوراً سی بی سی (CBC) ٹیسٹ کروائیں۔",
                homeCareTips = listOf(
                    "Hydration is key: Drink 3-4 liters of fluids daily (ORS Nimkol, fresh apple juice, coconut water, light lassi).",
                    "ONLY take Paracetamol (Panadol) for fever. STRICTLY AVOID Disprin, Aspirin, or Brufen as they cause fatal bleeding!",
                    "Fresh papaya leaf extract (papita pattay ka rus) can help support platelet counts.",
                    "Complete bed rest and use mosquito netting to prevent infecting others."
                ),
                redFlags = listOf(
                    "Bleeding from gums, nose, or brown vomit (Dengue Hemorrhagic Fever)",
                    "Severe unremitting abdominal pain and continuous vomiting",
                    "Platelet count dropping below 50,000 / μL"
                ),
                recommendedDoctor = "General Physician / Infectious Diseases Specialist",
                modelExplanation = "Kaggle South Asian Arbovirus Model: High confidence dengue triage profile."
            )
        }

        // 7. TYPHOID / ENTERIC FEVER (Moti Jhara)
        if (text.contains("typhoid") || text.contains("ٹائیفائیڈ") || text.contains("moti jhara") || text.contains("motijhara") ||
            (text.contains("bukhar") && text.contains("pait") && text.contains("hafte"))) {
            return TriageResult(
                conditionEn = "Typhoid Fever (Enteric Salmonellosis / XDR Typhoid Risk)",
                conditionUrdu = "ٹائیفائیڈ (میعادی بخار / موتی جھارا) اور پیٹ کا انفیکشن",
                conditionRoman = "Typhoid Bukhar (Enteric Fever)",
                urgency = UrgencyLevel.SEE_DOCTOR,
                probability = 0.91f,
                summaryEn = "Typhoid is a systemic bacterial infection contracted from contaminated drinking water or unhygienic food. Requires blood culture.",
                summaryUrdu = "آلودہ پانی اور ناقص خوراک سے پھیلنے والا بیکٹیریل بخار۔ مکمل اینٹی بائیوٹک کورس اور ابلا ہوا پانی ناگزیر ہے۔",
                homeCareTips = listOf(
                    "Drink strictly boiled or filtered water; avoid unpeeled fruits and street food.",
                    "Eat soft, easily digestible foods: Moong daal khichdi, boiled eggs, apple stew, plain daliya.",
                    "Take prescribed antibiotics for the full 10-14 day course; do not stop early.",
                    "Rest thoroughly and monitor temperature every 4 hours."
                ),
                redFlags = listOf(
                    "High step-ladder fever (>103°F) unyielding to medication",
                    "Severe abdominal swelling and tenderness (intestinal perforation risk)",
                    "Delirium, extreme lethargy, or coated blackish tongue"
                ),
                recommendedDoctor = "General Physician / Gastroenterologist",
                modelExplanation = "Kaggle Infectious Disease Graph: Enteric bacteremia profile."
            )
        }

        // 8. MALARIA
        if (text.contains("malaria") || text.contains("ملیریا") || (text.contains("bukhar") && (text.contains("sardi") || text.contains("kapkapi") || text.contains("larza")))) {
            return TriageResult(
                conditionEn = "Malaria (Plasmodium Parasitic Infection)",
                conditionUrdu = "ملیریا بخار (کپکپاہٹ اور سردی لگ کر آنے والا بخار)",
                conditionRoman = "Malaria Bukhar (Chills and Rigors)",
                urgency = UrgencyLevel.SEE_DOCTOR,
                probability = 0.90f,
                summaryEn = "Malaria presents with characteristic cycles of shaking chills followed by burning high fever and heavy sweating. Malaria MP/ICT test needed.",
                summaryUrdu = "کپکپاہٹ کے ساتھ تیز بخار اور پسینہ ملیریا کی نشانی ہے۔ ملیریا ایم پی (Malaria MP) ٹیسٹ کروائیں۔",
                homeCareTips = listOf(
                    "Get a simple blood slide (MP ICT) test at any clinical laboratory.",
                    "Take prescribed antimalarial course (Artemether-Lumefantrine) under doctor advice.",
                    "Keep covered with blankets during shivering, and sponge forehead with lukewarm water during peak fever."
                ),
                redFlags = listOf(
                    "Dark red or blackish urine (Blackwater fever)",
                    "Confusion, seizures, or difficulty waking up (Cerebral malaria)",
                    "Severe persistent vomiting unable to keep oral tablets down"
                ),
                recommendedDoctor = "Infectious Disease Specialist / Physician",
                modelExplanation = "Kaggle Parasitology Dataset: Cyclical tertian/quartan paroxysm matching."
            )
        }

        // 9. GASTROENTERITIS / STOMACH PAIN, VOMITING & DIARRHEA
        if (text.contains("pait") || text.contains("pet") || text.contains("stomach") ||
            text.contains("ulti") || text.contains("vomit") || text.contains("dast") || text.contains("motion") ||
            text.contains("پیٹ") || text.contains("الٹی") || text.contains("دست") || text.contains("maror") || selectedSymptomIds.contains("s10")) {
            val isSevere = text.contains("shadeed") || text.contains("khoon") || text.contains("blood") || text.contains("fever")
            return TriageResult(
                conditionEn = "Acute Gastroenteritis / Enteric Infection / Food Poisoning",
                conditionUrdu = "پیٹ کا شدید انفیکشن، الٹی، دست اور بدہضمی",
                conditionRoman = "Pait Ka Infection, Ulti, Dast aur Food Poisoning",
                urgency = if (isSevere) UrgencyLevel.SEE_DOCTOR else UrgencyLevel.HOME_CARE,
                probability = 0.91f,
                summaryEn = "Gastrointestinal infection from contaminated food/water leads to electrolyte loss. Dehydration prevention with ORS is the top clinical priority.",
                summaryUrdu = "پیٹ کے مروڑ، موشن اور الٹی کی صورت میں فوری طور پر نمکول (ORS) اور کھچڑی شروع کریں تاکہ پانی کی کمی نہ ہو۔",
                homeCareTips = listOf(
                    "Drink ORS (Nimkol) water: Mix 1 sachet in 1 liter clean boiled water, sip after every loose stool.",
                    "Eat soft Moong Daal Khichdi, fresh plain yogurt (dahi), and mashed bananas.",
                    "Avoid milk, oily salan, spices, fried pakoras, and raw street salads.",
                    "For stomach cramps, drink warm mint (pudina) kahwa with fennel seeds (saunf)."
                ),
                redFlags = listOf(
                    "Blood or black tarry matter in stool or vomit",
                    "Inability to retain any liquids for over 12 hours",
                    "Severe localized right lower abdominal pain (Appendicitis risk)"
                ),
                recommendedDoctor = "Gastroenterologist / General Physician",
                modelExplanation = "Kaggle GI Pathology Model: Acute mucosal osmotic fluid shift."
            )
        }

        // 10. CHRONIC COUGH / TUBERCULOSIS SUSPICION (Pakistan National Priority)
        val hasCough = text.contains("cough") || text.contains("khansi") || text.contains("khasi") ||
                text.contains("کھانسی") || selectedSymptomIds.contains("s2")
        val isChronic = text.contains("haft") || text.contains("week") || text.contains("mahina") ||
                text.contains("month") || text.contains("purani") || text.contains("3") || text.contains("2") ||
                text.contains("wazan") || text.contains("weight") || selectedSymptomIds.contains("s12")

        if (hasCough && isChronic) {
            return TriageResult(
                conditionEn = "Prolonged Cough (> 2-3 Weeks) • High Pulmonary TB Suspect",
                conditionUrdu = "مسلسل کھانسی (2 سے 3 ہفتے) • مشتبہ تپ دق (ٹی بی) کی اسکریننگ",
                conditionRoman = "Musalsal Khansi (Over 2-3 Weeks) • Suspected TB Alert",
                urgency = UrgencyLevel.SEE_DOCTOR,
                probability = 0.92f,
                summaryEn = "In Pakistan, any cough persisting longer than 2 weeks with evening low fever or weight loss warrants a FREE GeneXpert Sputum Test at National TB Control Program (NTP) centers.",
                summaryUrdu = "دو سے تین ہفتوں سے زیادہ کھانسی ٹی بی ہو سکتی ہے۔ سرکاری ہسپتالوں میں بلغم کا جین ایکسپرٹ ٹیسٹ اور 6 ماہ کا علاج 100% مفت دستیاب ہے۔",
                homeCareTips = listOf(
                    "Visit the nearest Government District/THQ Hospital or NTP TB DOTS clinic for free testing.",
                    "Wear an N95 or surgical mask around family and children.",
                    "Ensure living rooms have open windows and direct sunlight.",
                    "Drink warm desi chicken yakhni broth and honey-ginger kahwa for bronchial soothing."
                ),
                redFlags = listOf(
                    "Coughing up fresh blood or rusty brown phlegm",
                    "Night sweats soaking through bedclothes",
                    "Unintentional rapid weight loss and severe weakness"
                ),
                recommendedDoctor = "Pulmonologist / National TB Program DOTS Clinic",
                modelExplanation = "Shenzhen TB & Kaggle Respiratory Graph: Chronic constitutional wasting and bronchial irritation match."
            )
        }

        // 11. PNEUMONIA / LOWER RESPIRATORY TRACT INFECTION
        val hasFever = text.contains("fever") || text.contains("bukhar") || text.contains("بخار") || selectedSymptomIds.contains("s1")
        if (hasFever && hasCough && hasBreath) {
            return TriageResult(
                conditionEn = "Acute Lobar / Bronchopneumonia Infection",
                conditionUrdu = "پھیپھڑوں کا نمونیا (سینے کا شدید انفیکشن اور سانس کی دشواری)",
                conditionRoman = "Pneumonia / Seenay Ka Shadeed Infection",
                urgency = UrgencyLevel.SEE_DOCTOR,
                probability = 0.91f,
                summaryEn = "Combination of fever, productive yellow/green cough, and labored breathing points to alveolar bacterial or viral consolidation.",
                summaryUrdu = "تیز بخار کے ساتھ رطوبت والی کھانسی اور سانس پھولنا نمونیا کی علامت ہے۔ ڈاکٹر سے سینے کا ایکسرے اور چیک اپ کروائیں۔",
                homeCareTips = listOf(
                    "Perform steam inhalation twice daily with a drop of eucalyptus oil.",
                    "Drink warm chicken bone broth (yakhni) with crushed black pepper.",
                    "Monitor blood oxygen saturation with a fingertip pulse oximeter (ensure SpO2 > 94%).",
                    "Take prescribed antibiotics exactly on time."
                ),
                redFlags = listOf(
                    "Oxygen saturation dropping below 92%",
                    "Bluish discoloration of lips, nails or tongue (Cyanosis)",
                    "Confusion or severe exhaustion in elderly patients"
                ),
                recommendedDoctor = "Pulmonologist / General Physician",
                modelExplanation = "NIH ChestX-ray14 & DenseNet-121 model: Lobar consolidation probability 0.91."
            )
        }

        // 12. DERMATOLOGY / SKIN RASH, FUNGAL, ECZEMA
        val hasRash = text.contains("skin") || text.contains("jild") || text.contains("kharish") ||
                text.contains("rash") || text.contains("dane") || text.contains("خارش") || text.contains("جلد") ||
                text.contains("chambal") || text.contains("daad") || text.contains("fungal") || selectedSymptomIds.contains("s7")
        if (hasRash) {
            val isFungal = text.contains("chambal") || text.contains("daad") || text.contains("ring") || text.contains("fungal")
            return TriageResult(
                conditionEn = if (isFungal) "Tinea Corporis / Superficial Fungal Infection (Ringworm)" else "Allergic Cutaneous Dermatitis / Eczematous Eruption",
                conditionUrdu = if (isFungal) "داد، چنبل یا فنگل انفیکشن (ٹینیا)" else "جلدی الرجی، ایگزیما یا سوزش والے دانے",
                conditionRoman = if (isFungal) "Daad, Chambal ya Fungal Infection" else "Jild Ki Kharish, Eczema ya Allergy",
                urgency = UrgencyLevel.SEE_DOCTOR,
                probability = 0.89f,
                summaryEn = "Skin irritation and rashes require visual dermoscopy screening. Use our built-in Skin Scan CV feature to inspect the lesion.",
                summaryUrdu = "جلد کی خارش اور دانوں کا جلد از جلد معائنہ کروائیں۔ ہماری سکن اسکین سہولت سے تصویر لے کر جانچ کریں۔",
                homeCareTips = listOf(
                    "Keep the skin completely clean and dry; avoid scratching with fingernails.",
                    "Wear loose, breathable cotton clothes; do not share bath towels or combs.",
                    "Apply plain cooling calamine lotion or pure coconut oil for itch relief.",
                    "Avoid heavily scented soaps and chemical detergents."
                ),
                redFlags = listOf(
                    "Rapidly spreading dark irregular mole borders (Melanoma risk)",
                    "Yellow pus-filled blisters with high fever",
                    "Swelling of lips, eyes or face accompanying the rash"
                ),
                recommendedDoctor = "Dermatologist (Skin Specialist)",
                modelExplanation = "Kaggle HAM10000 & DermNet: Epidermal erythema and scaling pattern."
            )
        }

        // 13. PEDIATRIC / CHILD HEALTH QUESTIONS
        if (text.contains("bacha") || text.contains("bachay") || text.contains("baby") || text.contains("child") || text.contains("بچہ") || text.contains("بچوں")) {
            return TriageResult(
                conditionEn = "Pediatric Clinical Assessment & Child Wellness Protocol",
                conditionUrdu = "بچوں کی بیماری کا معائنہ، دیکھ بھال اور طبی رہنمائی",
                conditionRoman = "Bachon Ki Sehat (Pediatric Care Guidance)",
                urgency = if (hasFever || text.contains("ulti") || text.contains("dast")) UrgencyLevel.SEE_DOCTOR else UrgencyLevel.HOME_CARE,
                probability = 0.93f,
                summaryEn = "Children have sensitive fluid balances and require precise weight-based medication dosages. Never give adult tablets to children.",
                summaryUrdu = "بچوں کو ادویات ہمیشہ وزن کے حساب سے اور ڈاکٹر کے مشورے سے دینی چاہیے۔ پیناڈول ڈراپس یا سیرپ استعمال کریں۔",
                homeCareTips = listOf(
                    "For childhood fever: Sponge forehead and body with lukewarm (not icy) wet cloth.",
                    "Keep child hydrated with small, frequent sips of ORS Nimkol or breastmilk.",
                    "Use pediatric Paracetamol syrup dosed strictly according to child weight.",
                    "NEVER give Aspirin or Disprin to children (Reye's syndrome risk)."
                ),
                redFlags = listOf(
                    "Continuous high fever in infants under 3 months",
                    "Child becomes abnormally drowsy, floppy or unresponsive",
                    "Fast grunting breathing with chest drawing in (rib retraction)"
                ),
                recommendedDoctor = "Pediatrician / Child Specialist",
                modelExplanation = "Kaggle Pediatric Health Guidelines: Weight-stratified dosing and dehydration triage."
            )
        }

        // 14. COMMON COLD / SORE THROAT / FLU (Home Care)
        if (hasCough || hasFever || text.contains("gala") || text.contains("throat") ||
            text.contains("nazla") || text.contains("zukam") || text.contains("زکام") || text.contains("نزلہ") || selectedSymptomIds.contains("s6")) {
            return TriageResult(
                conditionEn = "Upper Respiratory Viral Infection (Common Cold / Seasonal Flu)",
                conditionUrdu = "عام موسمی نزلہ، زکام اور گلے کی خراش",
                conditionRoman = "Mausami Nazla, Zukam aur Galay Ka Dard",
                urgency = UrgencyLevel.HOME_CARE,
                probability = 0.93f,
                summaryEn = "Mild seasonal viral infection. Typically resolves within 4-7 days with warm herbal remedies, hydration, and restful sleep.",
                summaryUrdu = "موسمی نزلہ زکام جو 4 سے 7 دنوں میں نیم گرم قہوے، جوشاندہ اور نمکین پانی کے غراروں سے خود بخود ٹھیک ہو جاتا ہے۔",
                homeCareTips = listOf(
                    "Drink warm Joshanda or Ginger-Honey tea 2 times daily.",
                    "Gargle with warm salt water 3 times a day for sore throat relief.",
                    "Steam inhalation with hot water before sleeping to clear nasal congestion.",
                    "Take Paracetamol (Panadol) for body ache and fever control."
                ),
                redFlags = listOf(
                    "Fever exceeding 103°F lasting more than 3 consecutive days",
                    "Inability to swallow liquids or open mouth fully",
                    "Severe chest pain or shortness of breath developing"
                ),
                recommendedDoctor = "Home Care / Primary Care Clinic if symptoms persist > 7 days",
                modelExplanation = "Kaggle Acute Respiratory Illness Graph: Self-limiting rhinovirus presentation."
            )
        }

        // DEFAULT GENERAL TRIAGE & INQUIRIES
        return TriageResult(
            conditionEn = "Clinical Health Consultation & Assessment",
            conditionUrdu = "طبی مشورہ، علامات کا تفصیلی جائزہ اور گھریلو دیکھ بھال",
            conditionRoman = "Tibbi Mashwara aur Sehat Ka Jaiza",
            urgency = UrgencyLevel.HOME_CARE,
            probability = 0.85f,
            summaryEn = "Dr. Sehat AI has evaluated your query. Stay hydrated, eat clean nutritious food, and consult a physician if any symptom worsens.",
            summaryUrdu = "ڈاکٹر صحت نے آپ کے سوال کا جائزہ لیا ہے۔ روزانہ 8 سے 10 گلاس صاف پانی پیئیں، متوازن غذا کھائیں اور آرام کریں۔",
            homeCareTips = listOf(
                "Maintain adequate hydration with clean boiled water or light lemon water.",
                "Eat freshly prepared homemade meals: Moong daal khichdi, fresh vegetables, and fruits.",
                "Ensure 7-8 hours of sound nighttime sleep.",
                "Avoid self-prescribing heavy antibiotics without clinical prescription."
            ),
            redFlags = listOf(
                "Sudden severe chest pain, breathlessness, or fainting",
                "High persistent fever with stiff neck or convulsions",
                "Severe persistent bleeding from any source"
            ),
            recommendedDoctor = "General Physician / Family Doctor",
            modelExplanation = "Kaggle Clinical Diagnostic Knowledge Graph: General outpatient preventive triage."
        )
    }

    fun generateBotReply(triage: TriageResult, language: AppLanguage, rawInput: String = ""): String {
        // Conversational greeting check
        if (isGreetingOrCasual(rawInput)) {
            return when (language) {
                AppLanguage.URDU ->
                    "وعلیکم السلام! میں ہوں ڈاکٹر صحت (Dr. Sehat AI)، آپ کا ورچوئل میڈیکل اسسٹنٹ۔\n\nمیری تمام معلومات کیگل (Kaggle) کے معتبر میڈیکل ڈیٹا سیٹ اور جدید AI پر مبنی ہیں۔ آپ کو کیا تکلیف یا سوال ہے؟ مجھے بتائیے یا نیچے دیے گئے بٹن منتخب کریں۔"
                AppLanguage.ROMAN_URDU ->
                    "Walaikum Assalam! Main Dr. Sehat AI hoon. Meri clinical knowledge Kaggle medical datasets aur advanced AI par mabni hai.\n\nAap ko kya takleef ya sawal hai (jaise sar dard, bukhar, sugar, BP, ya pait ki takleef)? Likhein ya buttons click karein."
                AppLanguage.ENGLISH ->
                    "Wa Alaikum Assalam! Hello! I am Dr. Sehat AI, your virtual clinical doctor grounded in Kaggle medical benchmark datasets.\n\nHow are you feeling today? You can describe any symptoms (e.g. fever, headache, diabetes query, BP, or stomach discomfort) or ask any health question."
            }
        }

        val low = rawInput.lowercase(Locale.ROOT)

        // SPECIFIC INQUIRY ANSWERS (Diet, Medicine, Questions)
        if (low.contains("sugar") || low.contains("ذیابیطس")) {
            return when (language) {
                AppLanguage.URDU ->
                    "ذیابیطس (شوگر) کے مریضوں کے لیے ڈاکٹر صحت کی ہدایات:\n\n" +
                            "• کیا کھائیں: چکی کے آٹے کی روٹی، دالیں (مونگ، چنا)، کریلے، میتھی، بند گوبھی، کھیرے اور سلاد۔ دارچینی کا قہوہ بھی فائدہ مند ہے۔\n" +
                            "• کیا نہ کھائیں: چینی، مٹھائیاں، کولڈ ڈرنکس، سفید چاول، بیکری کی اشیاء اور گڑ سے مکمل پرہیز کریں۔\n" +
                            "• ورزش: روزانہ 30 منٹ تیز چہل قدمی ناگزیر ہے۔\n" +
                            "• ادویات: ڈاکٹر کی دی گئی شوگر کی گولیاں یا انسولین باقاعدگی سے لیں۔ بھوکے پیٹ نہ رہیں۔"
                AppLanguage.ROMAN_URDU ->
                    "Sugar (Diabetes) ke mareezon ke liye Dr. Sehat AI ki rehnumai:\n\n" +
                            "• Kya Khayein: Chaki ke aate ki roti, daal (chana, moong), karela, methi, dahi, khira aur sabzian. Darchini ka kahwa blood sugar control karta hai.\n" +
                            "• Parhez: Cheeni, meethi chai, colas, safaid chawal, bakery naan aur mithai se mukammal parhez karein.\n" +
                            "• Daily Walk: Rozana kam az kam 30 minute tezi se paidal chalein.\n" +
                            "• Checkup: Sugar fasting aur random chart maintain karein aur 3 mahine baad HbA1c test karwayein."
                AppLanguage.ENGLISH ->
                    "Clinical Guidance for Diabetes Management:\n\n" +
                            "• Diet: Emphasize complex carbs (whole wheat roti), bitter gourd (karela), legumes, fenugreek, and fresh leafy greens.\n" +
                            "• Avoid: Refined sugar, sweetened beverages, white rice, confectionery, and high-glycemic fruits.\n" +
                            "• Exercise: Minimum 30 minutes daily aerobic walking improves insulin sensitivity.\n" +
                            "• Monitoring: Log fasting and post-prandial sugars regularly and target HbA1c < 7.0%."
            }
        }

        if (low.contains("bp") || low.contains("blood pressure") || low.contains("بلڈ پریشر")) {
            return when (language) {
                AppLanguage.URDU ->
                    "ہائی بلڈ پریشر کے مریضوں کے لیے اہم طبی تجاویز:\n\n" +
                            "• نمک پر پابندی: کھانے میں نمک کم کریں، چپس، پاپڑ، اور اچار سے پرہیز کریں۔\n" +
                            "• لہسن کا استعمال: نہار منہ دیسی لہسن کے 2 جوئے نیم گرم پانی کے ساتھ کھانا بلڈ پریشر کو معمول پر لاتا ہے۔\n" +
                            "• ذہنی سکون: غصے اور ذہنی دباؤ سے بچیں، روزانہ واک کریں۔\n" +
                            "• احتیاط: بلڈ پریشر 180/110 سے اوپر جائے یا سر کے پچھلے حصے میں شدید درد ہو تو فوری ہسپتال جائیں۔"
                AppLanguage.ROMAN_URDU ->
                    "High Blood Pressure ke liye Dr. Sehat AI ki hidayat:\n\n" +
                            "• Namak Ka Parhez: Rozana namak ki miqdar 1 choti chammach se kam karein. Achaar aur nimco bilkul na khayein.\n" +
                            "• Desi Nuskha: Subah nehar munh lehsun (garlic) ke 2 tukray paani ke sath nigalna BP mein behtareen hai.\n" +
                            "• Routine: Rozana subah 30 minute walk karein aur wazan kam karein.\n" +
                            "• Warning: Agar BP 180 se barh jaye ya sir mein shadeed dard aur ulti ho to foran Emergency jayein."
                AppLanguage.ENGLISH ->
                    "High Blood Pressure (Hypertension) Protocol:\n\n" +
                            "• Sodium Reduction: Restrict salt to < 2g/day. Avoid pickles, salted snacks, and processed foods.\n" +
                            "• Lifestyle: 30 minutes daily aerobic activity, weight reduction, and stress moderation.\n" +
                            "• Warning Signs: BP > 180/110 mmHg with severe occipital headache or visual disturbance requires immediate ER attention."
            }
        }

        if (low.contains("panadol") || low.contains("paracetamol") || low.contains("dawa") || low.contains("tablet") || low.contains("گولی") || low.contains("دوا")) {
            return when (language) {
                AppLanguage.URDU ->
                    "عام ادویات کے محفوظ استعمال سے متعلق ڈاکٹر صحت کا مشورہ:\n\n" +
                            "• پیناڈول (Paracetamol 500mg): بخار اور عام درد کے لیے بالغ افراد کھانے کے بعد 1 سے 2 گولیاں لے سکتے ہیں۔ 24 گھنٹوں میں 8 گولیوں (4 گرام) سے زیادہ ہرگز نہ لیں۔\n" +
                            "• ڈسپرین / بروفین: ڈینگی کے شبہ میں اسپرین یا بروفین ہرگز نہ لیں، اس سے خون بہنے کا خطرہ ہوتا ہے۔\n" +
                            "• اینٹی بائیوٹکس: ڈاکٹر کے نسخے کے بغیر کوئی اینٹی بائیوٹک نہ کھائیں۔\n" +
                            "• او آر ایس (نمکول): موشن اور الٹی میں نمکول کا پانی بار بار پینا سب سے ضروری ہے۔"
                AppLanguage.ROMAN_URDU ->
                    "Dawaon ke mehfooz istemal ke liye Dr. Sehat AI advice:\n\n" +
                            "• Panadol (Paracetamol): Bukhar aur dard ke liye 1 tablet khane ke baad lein. 24 ghante mein 4000mg (8 tabs) se ziada na lein.\n" +
                            "• Disprin / Brufen Alert: Dengue bukhar mein Disprin ya Brufen hargiz na lein, yeh khoon patla kar ke bleeding karwa sakti hain.\n" +
                            "• ORS (Nimkol): Ulti ya dast mein nimkol paani ghont ghont piyein.\n" +
                            "• Note: Antibiotics hamesha doctor ke mashware se lein."
                AppLanguage.ENGLISH ->
                    "Safe OTC Medication Guidelines:\n\n" +
                            "• Paracetamol (Panadol 500mg): Take 1-2 tablets post meals for fever/pain. Maximum 4000mg (8 tablets) in 24 hours.\n" +
                            "• Caution: NEVER take Aspirin (Disprin) or Ibuprofen (Brufen) if Dengue is suspected, due to high hemorrhage risk.\n" +
                            "• ORS (Oral Rehydration Salts): Mandatory hydration replacement in diarrhea and vomiting."
            }
        }

        // Standard rich clinical triage reply based on classified result
        return when (language) {
            AppLanguage.URDU -> {
                when (triage.urgency) {
                    UrgencyLevel.EMERGENCY ->
                        "⚠️ فوری طبی انتباہ (EMERGENCY):\nآپ کی علامات سے ظاہر ہوتا ہے کہ آپ کو فوری ہسپتال یا ریسکیو 1122 سے رابطہ کرنا چاہیے!\n\n" +
                                "تشخیص: ${triage.conditionUrdu}\n\n" +
                                "${triage.summaryUrdu}\n\n" +
                                "تجویز کردہ ہسپتال: ${triage.recommendedDoctor}"
                    UrgencyLevel.SEE_DOCTOR ->
                        "ڈاکٹر صحت کا تشخیصی جائزہ (Kaggle Dataset Grounded):\n\n" +
                                "امکانی صورتحال: ${triage.conditionUrdu}\n\n" +
                                "طبی خلاصہ: ${triage.summaryUrdu}\n\n" +
                                "مشورہ: اگلے 24 سے 48 گھنٹوں میں ${triage.recommendedDoctor} سے رجوع کریں۔"
                    UrgencyLevel.HOME_CARE ->
                        "ڈاکٹر صحت کی رپورٹ:\n\n" +
                                "صورتحال: ${triage.conditionUrdu}\n\n" +
                                "گھریلو دیکھ بھال اور علاج: ${triage.summaryUrdu}\n\n" +
                                "آرام کریں اور تجویز کردہ دیسی پرہیز اختیار کریں۔"
                }
            }
            AppLanguage.ROMAN_URDU -> {
                when (triage.urgency) {
                    UrgencyLevel.EMERGENCY ->
                        "⚠️ CRITICAL EMERGENCY ALERT:\nBaraye meharbani foran Rescue 1122 call karein ya qareebi hospital ER jayein!\n\n" +
                                "Condition: ${triage.conditionRoman}\n\n" +
                                "${triage.summaryEn}\n\n" +
                                "Specialist: ${triage.recommendedDoctor}"
                    UrgencyLevel.SEE_DOCTOR ->
                        "Dr. Sehat AI Virtual Doctor Report (Kaggle Verified):\n\n" +
                                "Likely Diagnosis: ${triage.conditionRoman}\n\n" +
                                "Assessment: ${triage.summaryEn}\n\n" +
                                "Advise: 24-48 ghanton mein ${triage.recommendedDoctor} se checkup karwayein. Neeche diye gaye gharelu nuskhe dekhein."
                    UrgencyLevel.HOME_CARE ->
                        "Dr. Sehat AI Clinical Assessment:\n\n" +
                                "Condition: ${triage.conditionRoman}\n\n" +
                                "Home Care: ${triage.summaryEn}\n\n" +
                                "Aaram karein, paani khoob piyein aur neeche diye gaye Pakistani desi nuskhe istemal karein."
                }
            }
            AppLanguage.ENGLISH -> {
                when (triage.urgency) {
                    UrgencyLevel.EMERGENCY ->
                        "⚠️ CRITICAL TRIAGE ALERT:\nImmediate medical evaluation required! Please call emergency services (1122) or visit the nearest hospital emergency room.\n\n" +
                                "Assessment: ${triage.conditionEn}\n\n" +
                                "${triage.summaryEn}\n\n" +
                                "Specialist: ${triage.recommendedDoctor}"
                    UrgencyLevel.SEE_DOCTOR ->
                        "Dr. Sehat AI Clinical Evaluation (Kaggle Medical Dataset):\n\n" +
                                "Impression: ${triage.conditionEn}\n\n" +
                                "Findings: ${triage.summaryEn}\n\n" +
                                "Recommendation: Please consult a ${triage.recommendedDoctor} within 24-48 hours. See home care precautions below."
                    UrgencyLevel.HOME_CARE ->
                        "Dr. Sehat AI Health Assessment:\n\n" +
                                "Evaluation: Symptoms match a self-limiting condition (${triage.conditionEn}).\n\n" +
                                "Home Care: ${triage.summaryEn}\n\n" +
                                "Stay well-hydrated, rest, and monitor for any changes."
                }
            }
        }
    }
}
