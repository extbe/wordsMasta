package by.extbe.wordsmasta.dto

data class WordForTranslation(
    val word: String,
    val translation: String,
    val translationChoices: List<String>
)