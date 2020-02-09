package by.extbe.wordsmasta.persistence.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    tableName = "words",
    foreignKeys = [
        ForeignKey(
            entity = Language::class,
            parentColumns = ["id"],
            childColumns = ["language_id"],
            onDelete = CASCADE
        )
    ]
)
data class Word(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "language_id", index = true)
    val languageId: Long,

    @ColumnInfo(name = "value")
    val value: String
) {
    constructor(languageId: Long, value: String) : this(0, languageId, value)
}