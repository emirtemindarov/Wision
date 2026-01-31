package app.emirtemindarov.p1.assistant.responsesapi

import kotlinx.serialization.descriptors.StructureKind

data class ResponseRequest(
    val model: String,
    val input: List<InputItem>,
    val text: TextConfig? = null,
    val temperature: Double = 0.01
)

data class TextConfig(
    val format: Map<String, Any>
)

/*data class JsonSchema(
    val strict: Boolean,
    val schema: StructureKind.OBJECT
)*/

data class InputItem(
    val role: String,
    val content: List<InputContent>
)

data class InputContent(
    val type: String,
    val text: String
)

data class ResponseResponse(
    val output: List<ResponseOutput>
)

data class ResponseOutput(
    val id: String,
    val type: String,
    val content: List<ResponseContent>
)

data class ResponseContent(
    val type: String,
    val text: String? = null
)
