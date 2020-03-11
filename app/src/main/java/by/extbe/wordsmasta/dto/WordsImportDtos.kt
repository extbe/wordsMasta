package by.extbe.wordsmasta.dto

data class WordGroupsImportDto(val groups: List<WordGroupDto> = listOf())

data class WordGroupDto(
    val groupName: String = "",
    val sourceLanguageCode: String = "",
    val targetLanguageCode: String = "",
    val words: Map<String, String> = mapOf()
)