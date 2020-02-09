package by.extbe.wordsmasta.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import by.extbe.wordsmasta.constant.RequiredLanguage
import by.extbe.wordsmasta.persistence.dao.LanguageDao
import by.extbe.wordsmasta.persistence.dao.TranslationDao
import by.extbe.wordsmasta.persistence.dao.WordDao
import by.extbe.wordsmasta.persistence.entity.Language
import by.extbe.wordsmasta.persistence.entity.Translation
import by.extbe.wordsmasta.persistence.entity.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Database(version = 1, entities = [Word::class, Translation::class, Language::class])
abstract class WordsMastaDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun languageDao(): LanguageDao
    abstract fun translationDao(): TranslationDao

    companion object {
        @Volatile
        private var instance: WordsMastaDatabase? = null

        fun getDatabase(context: Context): WordsMastaDatabase {
            val i = instance
            if (i != null) {
                return i
            }

            return synchronized(this) {
                val i2 = instance
                if (i2 != null) {
                    i2
                } else {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        WordsMastaDatabase::class.java,
                        "words_masta_database"
                    ).addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            GlobalScope.launch(Dispatchers.IO) {
                                val requiredLanguages = RequiredLanguage.values()
                                    .asSequence()
                                    .map { Language(it.title, it.code) }
                                    .toList()
                                getDatabase(context).languageDao().insertAll(requiredLanguages)
                            }
                        }
                    }).build()
                    instance!!
                }
            }
        }
    }
}