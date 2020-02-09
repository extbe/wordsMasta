package by.extbe.wordsmasta.persistence.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "languages")
data class Language(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "name", index = true)
    val name: String,

    @ColumnInfo(name = "code", index = true)
    val code: String
) {
    constructor(name: String, code: String) : this(0, name, code)
}