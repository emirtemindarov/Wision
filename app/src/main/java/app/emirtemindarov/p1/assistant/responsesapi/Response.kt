package app.emirtemindarov.p1.assistant.responsesapi

data class ResponseRequest(
    val model: String,
    val input: List<InputItem>,
    val text: TextConfig? = null,
    val temperature: Double = 0.01
)

data class TextConfig(
    val format: Map<String, Any>
)

data class InputItem(
    val role: String,
    val content: List<InputContent>
)

data class InputContent(
    val type: String,
    val text: String
)

data class ResponseResponse(
    val output: List<ResponseOutput>,
    val usage: ResponsesUsage,
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

data class ResponsesUsage(
    val input_tokens: Long,
    val output_tokens: Long,
    val total_tokens: Long,
    val output_tokens_details: ReasoningTokens,
)

data class ReasoningTokens(
    val reasoning_tokens: Long,
)