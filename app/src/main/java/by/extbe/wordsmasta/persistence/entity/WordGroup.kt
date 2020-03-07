package by.extbe.wordsmasta.persistence.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "word_groups",
    primaryKeys = ["word_id", "group_id"],
    foreignKeys = [
        ForeignKey(
            entity = Word::class,
            parentColumns = ["id"],
            childColumns = ["word_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Group::class,
            parentColumns = ["id"],
            childColumns = ["group_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WordGroup(
    @ColumnInfo(name = "word_id") val wordId: Long,
    @ColumnInfo(name = "group_id", index = true) val groupId: Long
)