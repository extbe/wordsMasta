package by.extbe.wordsmasta.constant

enum class RequiredLanguage(val title: String, val code: String) {
    RUSSIAN("Russian","ru"),
    ENGLISH("English","en")
}

val DEFAULT_SOURCE_LANGUAGE = RequiredLanguage.ENGLISH
val DEFAULT_TARGET_LANGUAGE = RequiredLanguage.RUSSIAN

enum class ImportStatus(val description: String) {
    NO_DATA("No data to import"),
    IN_PROGRESS("Import in progress"),
    COMPLETED("Import completed"),
    ERROR("Import error")
}