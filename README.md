# 🩺 Dr. Sehat AI (ڈاکٹر صحت)
**Intelligent Virtual Health Screening & Triage Android Application**

Dr. Sehat AI is a comprehensive AI-powered clinical screening and health companion developed natively for Android using Jetpack Compose and Kotlin. It combines cloud-powered Gemini AI with local medical datasets (Kaggle clinical triage knowledge) for high-accuracy health diagnosis and triage even in low-bandwidth environments.

### ✨ Key Features:
- 💬 **Dr. Sehat AI Consultation:** Real-time health Q&A and medical reasoning in Urdu, Roman Urdu, and English with voice dictation & speech support.
- 🩻 **Chest X-Ray Screening:** Computer vision detection for pulmonary conditions (Tuberculosis, Lobar Pneumonia, Viral/COVID-19 Infiltrates, Pleural Effusion, and Normal Lungs) with universal gallery and camera upload.
- 🔬 **Skin Lesion Scanner:** AI visual dermatological assessment for rashes, eczema, melanoma, and infections.
- ❤️ **rPPG Contactless Vitals:** Camera-based pulse, SpO2, and respiratory rate estimation.
- 🗣️ **Urdu & Multilingual Voice Triage:** Native Urdu voice consultation and symptom categorization tailored for local health contexts.
- 📄 **Doctor Handoff & PDF Referral:** Generates structured triage summaries and medical referral slips for clinics and emergency departments.

### 🛠️ Tech Stack:
- **Language:** Kotlin
- **UI Toolkit:** Jetpack Compose (Material Design 3)
- **Architecture:** Clean MVVM with Coroutines & StateFlow
- **AI Engines:** Google Gemini 2.5 Flash API + Kaggle Clinical Offline Knowledge Base
- **Computer Vision:** Image processing with native Android Graphics & Color Tensors
