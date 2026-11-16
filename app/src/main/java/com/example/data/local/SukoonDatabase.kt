package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.domain.SceneThemeIds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [AzkarEntity::class, ZikrSessionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SukoonDatabase : RoomDatabase() {
    abstract fun azkarDao(): AzkarDao

    companion object {
        @Volatile
        private var INSTANCE: SukoonDatabase? = null

        fun getInstance(context: Context): SukoonDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SukoonDatabase::class.java,
                    "sukoon_zikr.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.azkarDao()?.let { dao ->
                                    if (dao.getAzkarCount() == 0) {
                                        dao.insertAll(initialSeedAzkar())
                                    }
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun initialSeedAzkar(): List<AzkarEntity> {
            val now = System.currentTimeMillis()
            return listOf(
                AzkarEntity(
                    title = "SubhanAllah wa bihamdihi",
                    arabicText = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
                    transliteration = "Subḥānallāhi wa biḥamdihī",
                    urduTranslation = "اللہ پاک ہے اپنی تمام خوبیوں اور حمد کے ساتھ",
                    englishTranslation = "Glory be to Allah and praise be to Him.",
                    benefitsAndReflection = "Encourages quiet awe, gratitude, and stillness of heart as you reflect on the perfection and mercy of Allah.",
                    referenceText = "Sahih al-Bukhari 6405 · Sahih Muslim 2692",
                    isSampleContent = false,
                    targetCount = 100,
                    currentCount = 0,
                    themeId = SceneThemeIds.DEAD_TREE_TO_FRUITFUL,
                    isDailyZikr = true,
                    isPublished = true,
                    createdAt = now,
                    updatedAt = now
                ),
                AzkarEntity(
                    title = "Dua of Prophet Yunus (AS)",
                    arabicText = "لَا إِلَٰهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ",
                    transliteration = "Lā ilāha illā Anta subḥānaka innī kuntu minaẓ-ẓālimīn",
                    urduTranslation = "تیرے سوا کوئی معبود نہیں، تو پاک ہے، بے شک میں قصورواروں میں سے تھا",
                    englishTranslation = "There is no deity except You; exalted are You. Indeed, I have been of the wrongdoers.",
                    benefitsAndReflection = "A call of humility and quiet hope from moments of deep solitude, reminding the heart that light returns by Allah's mercy.",
                    referenceText = "Quran · Surah Al-Anbiya (21:87)",
                    isSampleContent = false,
                    targetCount = 100,
                    currentCount = 0,
                    themeId = SceneThemeIds.DARKNESS_TO_SUNRISE,
                    isDailyZikr = false,
                    isPublished = true,
                    createdAt = now - 1000,
                    updatedAt = now - 1000
                ),
                AzkarEntity(
                    title = "Istighfar of Return",
                    arabicText = "أَسْتَغْفِرُ اللَّهَ الْعَظِيمَ وَأَتُوبُ إِلَيْهِ",
                    transliteration = "Astaghfirullāhal-ʿAẓīma wa atūbu ilayh",
                    urduTranslation = "میں عظمت والے اللہ سے مغفرت طلب کرتا ہوں اور اسی کی طرف رجوع کرتا ہوں",
                    englishTranslation = "I seek forgiveness from Allah, the Almighty, and turn to Him in repentance.",
                    benefitsAndReflection = "Softens the heart through sincere repentance and gentle renewal, turning inward weight into calm reassurance.",
                    referenceText = "Sunan Abi Dawud 1517 (Sahih)",
                    isSampleContent = false,
                    targetCount = 100,
                    currentCount = 0,
                    themeId = SceneThemeIds.WILTED_TO_BLOOMING_FLOWER,
                    isDailyZikr = false,
                    isPublished = true,
                    createdAt = now - 2000,
                    updatedAt = now - 2000
                ),
                AzkarEntity(
                    title = "Tasbeeh, Tahmeed & Takbeer",
                    arabicText = "سُبْحَانَ اللَّهِ، وَالْحَمْدُ لِلَّهِ، وَاللَّهُ أَكْبَرُ",
                    transliteration = "Subḥānallāh, wal-ḥamdulillāh, wallāhu akbar",
                    urduTranslation = "اللہ پاک ہے، تمام تعریفیں اللہ کے لیے ہیں، اور اللہ سب سے بڑا ہے",
                    englishTranslation = "Glory be to Allah, all praise belongs to Allah, and Allah is the Greatest.",
                    benefitsAndReflection = "Unites wonder, thankfulness, and humility—anchoring the soul in remembrance throughout the day.",
                    referenceText = "Sahih Muslim 223",
                    isSampleContent = false,
                    targetCount = 99,
                    currentCount = 0,
                    themeId = SceneThemeIds.DRY_DESERT_TO_GARDEN,
                    isDailyZikr = false,
                    isPublished = true,
                    createdAt = now - 3000,
                    updatedAt = now - 3000
                ),
                AzkarEntity(
                    title = "Sample Gratitude Cycle (Demo)",
                    arabicText = "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ",
                    transliteration = "Al-ḥamdu lillāhi Rabbil-ʿālamīn",
                    urduTranslation = "تمام تعریفیں اللہ کے لیے ہیں جو تمام جہانوں کا رب ہے",
                    englishTranslation = "All praise and gratitude belong to Allah, Lord of all the worlds.",
                    benefitsAndReflection = "Demonstration entry showcasing a 33-bead contemplative cycle. Customize or add your own verified Azkar inside the Admin Studio.",
                    referenceText = "Sample Demonstration Entry (Al-Fatihah 1:2)",
                    isSampleContent = true,
                    targetCount = 33,
                    currentCount = 0,
                    themeId = SceneThemeIds.DRY_DESERT_TO_GARDEN,
                    isDailyZikr = false,
                    isPublished = true,
                    createdAt = now - 4000,
                    updatedAt = now - 4000
                )
            )
        }
    }
}
