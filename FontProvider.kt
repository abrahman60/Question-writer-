package com.example.util

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

data class CustomFontOption(
    val name: String,
    val displayNameBengali: String,
    val languageGroup: String, // "বাংলা", "উর্দু", "আরবি", "ইংরেজি"
    val fontFamily: FontFamily,
    val sampleText: String
)

object FontProvider {

    // Language Groups
    val fontOptions = listOf(
        // Urdu Fonts
        CustomFontOption(
            name = "Jameel Noori Nastaliq",
            displayNameBengali = "জামিল নূরী নাস্তালিক (Urdu)",
            languageGroup = "উর্দু",
            fontFamily = FontFamily.Serif,
            sampleText = "اردو جمیل نوری نستعلیق خط"
        ),
        CustomFontOption(
            name = "Nastaliq Standard",
            displayNameBengali = "নাস্তালিক স্ট্যান্ডার্ড (Urdu)",
            languageGroup = "উর্দু",
            fontFamily = FontFamily.Serif,
            sampleText = "نستعلیق خوشخطی نوٹ"
        ),
        CustomFontOption(
            name = "Gulzar Urdu",
            displayNameBengali = "গুলজার ফন্ট (Urdu)",
            languageGroup = "উর্দু",
            fontFamily = FontFamily.SansSerif,
            sampleText = "گلزار خط اردو"
        ),

        // Bengali Fonts
        CustomFontOption(
            name = "Sutonnymj",
            displayNameBengali = "শুটো নি এমজে (Sutonnymj)",
            languageGroup = "বাংলা",
            fontFamily = FontFamily.Serif,
            sampleText = "আমার সোনার বাংলা আমি তোমায় ভালোবাসি"
        ),
        CustomFontOption(
            name = "SolaimanLipi",
            displayNameBengali = "সোলাইমান লিপি (SolaimanLipi)",
            languageGroup = "বাংলা",
            fontFamily = FontFamily.SansSerif,
            sampleText = "সহজ ও সুন্দর বাংলা লিপি"
        ),
        CustomFontOption(
            name = "Kalpurush",
            displayNameBengali = "কালপুরুষ (Kalpurush)",
            languageGroup = "বাংলা",
            fontFamily = FontFamily.SansSerif,
            sampleText = "স্মার্ট ও স্পষ্ট বাংলা অক্ষর"
        ),
        CustomFontOption(
            name = "Kobiguru",
            displayNameBengali = "কবিগুরু ফন্ট (Kobiguru Handwriting)",
            languageGroup = "বাংলা",
            fontFamily = FontFamily.Cursive,
            sampleText = "হাতে লেখা কবিগুরু শৈলী"
        ),
        CustomFontOption(
            name = "Sharif Font",
            displayNameBengali = "শরিফ ফন্ট (Sharif)",
            languageGroup = "বাংলা",
            fontFamily = FontFamily.Serif,
            sampleText = "শরিফ বাংলা অক্ষর রূপ"
        ),
        CustomFontOption(
            name = "Tiro Bangla",
            displayNameBengali = "তিরো বাংলা (Tiro Bangla)",
            languageGroup = "বাংলা",
            fontFamily = FontFamily.Serif,
            sampleText = "বই ও পরীক্ষার প্রশ্ন শৈলী"
        ),

        // Arabic Fonts
        CustomFontOption(
            name = "Nastaliq Arabic",
            displayNameBengali = "নাস্তালিক নোট (Arabic)",
            languageGroup = "আরবি",
            fontFamily = FontFamily.Serif,
            sampleText = "بسم الله الرحمن الرحيم"
        ),
        CustomFontOption(
            name = "Amiri Arabic",
            displayNameBengali = "আমিরি / নাসখ (Amiri/Naskh)",
            languageGroup = "আরবি",
            fontFamily = FontFamily.Serif,
            sampleText = "خط الأميري العربي"
        ),
        CustomFontOption(
            name = "Scheherazade",
            displayNameBengali = "শেহেরেজাদ (Scheherazade)",
            languageGroup = "আরবি",
            fontFamily = FontFamily.Serif,
            sampleText = "الخط العربي الاصيل"
        ),
        CustomFontOption(
            name = "Cairo Arabic",
            displayNameBengali = "কায়রো ফন্ট (Cairo)",
            languageGroup = "আরবি",
            fontFamily = FontFamily.SansSerif,
            sampleText = "خط القاهرة الحديث"
        ),

        // English Fonts
        CustomFontOption(
            name = "Times New Roman",
            displayNameBengali = "টাইমস নিউ রোমান (Times New Roman)",
            languageGroup = "ইংরেজি",
            fontFamily = FontFamily.Serif,
            sampleText = "The quick brown fox jumps over the lazy dog"
        ),
        CustomFontOption(
            name = "Arial",
            displayNameBengali = "অ্যারিয়াল (Arial)",
            languageGroup = "ইংরেজি",
            fontFamily = FontFamily.SansSerif,
            sampleText = "Clean modern sans-serif typeface"
        ),
        CustomFontOption(
            name = "Georgia",
            displayNameBengali = "জর্জিয়া (Georgia)",
            languageGroup = "ইংরেজি",
            fontFamily = FontFamily.Serif,
            sampleText = "Classic editorial serif typeface"
        ),
        CustomFontOption(
            name = "Courier New",
            displayNameBengali = "কুরিয়ার নিউ (Courier New)",
            languageGroup = "ইংরেজি",
            fontFamily = FontFamily.Monospace,
            sampleText = "Monospaced typewriting style"
        ),
        CustomFontOption(
            name = "Trebuchet MS",
            displayNameBengali = "ট্রেবুচেট (Trebuchet MS)",
            languageGroup = "ইংরেজি",
            fontFamily = FontFamily.SansSerif,
            sampleText = "Humanist sans-serif typeface"
        ),
        CustomFontOption(
            name = "Roboto",
            displayNameBengali = "রোবোটো (Roboto)",
            languageGroup = "ইংরেজি",
            fontFamily = FontFamily.Default,
            sampleText = "Standard Android material font"
        ),
        CustomFontOption(
            name = "Pacifico",
            displayNameBengali = "প্যাসিফিকো (Handwriting Cursive)",
            languageGroup = "ইংরেজি",
            fontFamily = FontFamily.Cursive,
            sampleText = "Beautiful artistic handwriting"
        )
    )

    fun getFontFamily(fontName: String): FontFamily {
        return fontOptions.firstOrNull { it.name.equals(fontName, ignoreCase = true) }?.fontFamily
            ?: FontFamily.Default
    }
}
