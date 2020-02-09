package by.extbe.wordsmasta.persistence.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE

@Entity(
    tableName = "translations",
    primaryKeys = ["source_word_id", "target_word_id"],
    foreignKeys = [
        ForeignKey(
            entity = Word::class,
            parentColumns = ["id"],
            childColumns = ["source_word_id"],
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = Word::class,
            parentColumns = ["id"],
            childColumns = ["target_word_id"],
            onDelete = CASCADE
        )
    ]
)
data class Translation(
    @ColumnInfo(name = "source_word_id", index = true) val sourceWordId: Long,
    @ColumnInfo(name = "target_word_id", index = true) val targetWordId: Long
)