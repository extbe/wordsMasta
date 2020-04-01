package by.extbe.wordsmasta.constant

enum class DefaultLanguage(val title: String, val code: String) {
    RUSSIAN("Russian","ru"),
    ENGLISH("English","en")
}

val DEFAULT_SOURCE_LANGUAGE = DefaultLanguage.ENGLISH
val DEFAULT_TARGET_LANGUAGE = DefaultLanguage.RUSSIAN

enum class DefaultGroup(val title: String) {
    ALL_WORDS("All words")
}

val DEFAULT_WORD_GROUP = DefaultGroup.ALL_WORDS

enum class ImportStatus(val description: String) {
    NO_DATA("No data to import"),
    IN_PROGRESS("In progress"),
    COMPLETED("Completed"),
    ERROR("Error")
}