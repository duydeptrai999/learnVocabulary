package com.duyth10.learnvocabulary


import androidx.room.Entity
import androidx.room.PrimaryKey

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import kotlinx.parcelize.Parcelize


@Entity(tableName = "vocabulary_table")
@Parcelize
data class Vocabulary(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val eng: String,
    val vie: String,
    val description: String? = ""
): Parcelable

@Dao
interface VocabularyDao {
    @Insert
    suspend fun insert(vocabulary: Vocabulary)

    @Update
    suspend fun update(vocabulary: Vocabulary)

    @Delete
    suspend fun delete(vocabulary: Vocabulary)

    @Query("SELECT * FROM vocabulary_table")
    fun getAllVocabulary(): LiveData<List<Vocabulary>>

    @Query("SELECT * FROM vocabulary_table")
    fun getVocabularyList(): List<Vocabulary>

}


//@Database(entities = [Vocabulary::class], version = 1, exportSchema = false)
//abstract class VocabularyDatabase : RoomDatabase() {
//
//    abstract fun vocabularyDao(): VocabularyDao
//
//    companion object {
//        @Volatile
//        private var INSTANCE: VocabularyDatabase? = null
//
//        fun getDatabase(context: Context): VocabularyDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    VocabularyDatabase::class.java,
//                    "vocabulary_database"
//                ).build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
//}


@Database(entities = [Vocabulary::class], version = 1, exportSchema = false)
abstract class VocabularyDatabase : RoomDatabase() {

    abstract fun vocabularyDao(): VocabularyDao

    companion object {
        @Volatile
        private var INSTANCE: VocabularyDatabase? = null

        fun getDatabase(context: Context): VocabularyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VocabularyDatabase::class.java,
                    "vocabulary_database"
                )
                    .addCallback(VocabularyDatabaseCallback()) // No need to pass CoroutineScope explicitly
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class VocabularyDatabaseCallback : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Populate the database after it is created for the first time
            INSTANCE?.let { database ->
                // Use Dispatchers.IO as it's a background task for DB operations
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.vocabularyDao())
                }
            }
        }

        suspend fun populateDatabase(vocabularyDao: VocabularyDao) {
            vocabularyDao.insert(
                Vocabulary(
                    eng = "Apple",
                    vie = "Táo",
                    description = "A type of fruit"
                )
            )
            vocabularyDao.insert(
                Vocabulary(
                    eng = "Computer",
                    vie = "Máy tính",
                    description = "An electronic device"
                )
            )
            vocabularyDao.insert(
                Vocabulary(
                    eng = "House",
                    vie = "Nhà",
                    description = "A place where people live"
                )
            )
            vocabularyDao.insert(
                Vocabulary(
                    eng = "Car",
                    vie = "Xe hơi",
                    description = "A vehicle used for transportation"
                )
            )
//            vocabularyDao.insert(
//                Vocabulary(
//                    eng = "Book",
//                    vie = "Sách",
//                    description = "A written or printed work of fiction or non-fiction"
//                )
//            )
//            vocabularyDao.insert(
//                Vocabulary(
//                    eng = "Dog",
//                    vie = "Chó",
//                    description = "A common domesticated animal kept as a pet"
//                )
//            )
//            vocabularyDao.insert(
//                Vocabulary(
//                    eng = "Sun",
//                    vie = "Mặt trời",
//                    description = "The star at the center of our solar system"
//                )
//            )
//            vocabularyDao.insert(
//                Vocabulary(
//                    eng = "Computer",
//                    vie = "Máy tính",
//                    description = "An electronic device for processing data"
//                )
//            )
        }
    }
}

