package by.extbe.wordsmasta.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import by.extbe.wordsmasta.constant.DefaultGroup
import by.extbe.wordsmasta.constant.DefaultLanguage
import by.extbe.wordsmasta.persistence.dao.*
import by.extbe.wordsmasta.persistence.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Database(
    version = 1,
    entities = [
        Word::class,
        Translation::class,
        Language::class,
        Group::class,
        WordGroup::class
    ]
)
abstract class WordsMastaDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun languageDao(): LanguageDao
    abstract fun translationDao(): TranslationDao
    abstract fun groupDao(): GroupDao
    abstract fun wordGroupDao(): WordGroupDao

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
                    ).addCallback(FillDatabaseWithDefaultDataCallback(context)).build()
                    instance!!
                }
            }
        }
    }

    class FillDatabaseWithDefaultDataCallback(private val context: Context) :
        RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            GlobalScope.launch(Dispatchers.IO) {
                val defaultLanguages = DefaultLanguage.values()
                    .asSequence()
                    .map { Language(it.title, it.code) }
                    .toList()
                val database = getDatabase(context)
                database.languageDao().insertAll(defaultLanguages)
                val defaultGroups = DefaultGroup.values()
                    .asSequence()
                    .map { Group(it.title) }
                    .toList()
                database.groupDao().insertAll(defaultGroups)
            }
        }
    }
}