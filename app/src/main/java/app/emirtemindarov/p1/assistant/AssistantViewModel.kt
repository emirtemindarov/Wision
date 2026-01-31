package app.emirtemindarov.p1.assistant

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.emirtemindarov.p1.BuildConfig
import app.emirtemindarov.p1.Environment
/*import app.emirtemindarov.p1.assistant.api.AddMessageRequest
import app.emirtemindarov.p1.assistant.api.ContentItemRequest
import app.emirtemindarov.p1.assistant.api.RetrofitClient
import app.emirtemindarov.p1.assistant.api.StreamManager*/
import app.emirtemindarov.p1.assistant.data.GraphModel
import app.emirtemindarov.p1.assistant.responsesapi.GraphSchema
import app.emirtemindarov.p1.assistant.responsesapi.InputContent
import app.emirtemindarov.p1.assistant.responsesapi.InputItem
import app.emirtemindarov.p1.assistant.responsesapi.ResponseRequest
import app.emirtemindarov.p1.assistant.responsesapi.ResponsesRetrofitClient
import app.emirtemindarov.p1.assistant.responsesapi.TextConfig
import app.emirtemindarov.p1.room.GraphDao
import app.emirtemindarov.p1.room.GraphEntity
import app.emirtemindarov.p1.room.GraphLoadMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.UUID

// TODO сделать глобальную защиту от двойных нажатий
// действия на сайте (кроме создания и удаления самого ассистента) не влияют на работу, все указывается в веб-запросе
class AssistantViewModel(
    private val graphDao: GraphDao
) : ViewModel() {
    private val _state = MutableStateFlow(AssistantState())
    val state = _state.asStateFlow()

    //private val api = RetrofitClient.apiService
    private val responsesApi = ResponsesRetrofitClient.apiService

    /*private val streamManager = StreamManager(
        BuildConfig.OPENAI_API_KEY,
        BuildConfig.OPENAI_ASSISTANT_ID
    )*/

    /*init {
        viewModelScope.launch {
            Log.d("ASSISTANT", "Stream collector started")
            streamManager.streamFlow.collect { delta ->
                handleStreamDelta(delta)
            }
        }
    }*/

    private var currentUserMessage: String? = null

    val jsonParser = Json { ignoreUnknownKeys = true }

    // пока не нужно сохранять ID треда
    //private var currentThreadId: String? = null

    /*fun askAssistant(userMessage: String) {
        viewModelScope.launch {
            try {
                Log.d("ASSISTANT", "=== askAssistant START ===")
                Log.d("ASSISTANT", "User message: $userMessage")

                _state.update { it.copy(stage = AssistantStage.Loading) }
                Log.d("ASSISTANT", "Stage set to Loading")

                // --- Шаг 1: создаём тред ---
                Log.d("ASSISTANT", "Creating new thread...")
                val threadResponse = api.createThread()
                val threadId = threadResponse.id
                Log.d("ASSISTANT", "Thread created: $threadId")

                // --- Шаг 2: добавляем сообщение ---
                Log.d("ASSISTANT", "Adding user message to thread $threadId")
                api.addMessage(
                    threadId = threadId,
                    body = AddMessageRequest(
                        "user",
                        listOf(ContentItemRequest(type = "text", text = userMessage))
                    )
                )
                Log.d("ASSISTANT", "User message added successfully")

                // сохранить контекст текущего запроса
                currentUserMessage = userMessage

                // --- --- Шаг 3: запускаем стрим ---
                Log.d("ASSISTANT", "Starting stream for thread $threadId")
                streamManager.startStreaming(threadId)

            } catch (e: Exception) {
                Log.e("ASSISTANT", "Exception in askAssistant: ${e.message}", e)
                _state.update {
                    it.copy(stage = AssistantStage.Error(e.message ?: "Неизвестная ошибка"))
                }
            }
        }
    }*/

    fun askResponse(userMessage: String) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(stage = AssistantStage.Loading) }

                // ---------- 1. Формируем запрос (ПУНКТ 4) ----------
                val request = ResponseRequest(
                    model = "gpt-4.1",
                    temperature = 0.01,
                    input = listOf(
                        // System instructions
                        InputItem(
                            role = "system",
                            content = listOf(
                                InputContent(
                                    type = "input_text",
                                    text = Environment.SYSTEM_INSTRUCTIONS                                )
                            )
                        ),

                        // Выбранный файл/папка
                        InputItem(
                            role = "user",
                            content = listOf(
                                InputContent(
                                    type = "input_text",
                                    text = userMessage
                                )
                            )
                        )
                    ),
                    text = TextConfig(
                        format = mapOf(
                            "type" to "json_schema",
                            "name" to "folder_graph",
                            "strict" to true,
                            "schema" to GraphSchema.schemaJson
                        )
                    )
                )

                Log.i("requestCreated", "Success")

                // ---------- 2. Вызов Responses API ----------
                val response = responsesApi.createResponse(request)

                Log.i("responseCreated", "Success")

                // ---------- 3. Извлечение результата (ПУНКТ 5) ----------
                val jsonText = response.output
                    .firstOrNull { it.type == "message" }
                    ?.content
                    ?.firstOrNull { it.type == "output_text" }
                    ?.text
                    ?: error("Empty response from model")

                Log.d("RESPONSES_API", "Final JSON length=${jsonText.length}")

                // ---------- 4. Парсинг графа ----------
                val graph: GraphModel =
                    jsonParser.decodeFromString(jsonText)

                // ---------- 5. Сохранение в БД ----------
                val graphId = UUID.randomUUID().toString()
                val time = System.currentTimeMillis()

                graphDao.insert(
                    GraphEntity(
                        graphId = graphId,
                        createdAt = time,
                        graphJson = jsonParser.encodeToString(graph),
                        graphSource = userMessage,
                        graphName = graph.nodes.firstOrNull()?.label ?: "Без названия",
                        lastModified = time
                    )
                )

                // ---------- 6. Обновление UI ----------
                _state.update {
                    it.copy(
                        stage = AssistantStage.Success(
                            graph = graph,
                            graphId = graphId
                        )
                    )
                }

            } catch (e: Exception) {
                Log.e("RESPONSES_API", "Error", e)
                _state.update {
                    it.copy(
                        stage = AssistantStage.Error(
                            e.message ?: "Unknown error"
                        )
                    )
                }
            }
        }
    }

    /*fun loadGraph(mode: GraphLoadMode) {
        when (mode) {

            is GraphLoadMode.NewAnalysis -> {
                askAssistant(mode.payload)    // ключевая строчка проекта
            }

            is GraphLoadMode.FromDatabase -> {
                loadFromDatabase(mode.graphId)
            }
        }
    }*/

    fun responsesLoadGraph(mode: GraphLoadMode) {
        when (mode) {
            is GraphLoadMode.NewAnalysis ->
                askResponse(mode.payload)

            is GraphLoadMode.FromDatabase ->
                responsesLoadFromDatabase(mode.graphId)
        }
    }


    /*private fun loadFromDatabase(graphId: String) {
        viewModelScope.launch {
            _state.update { it.copy(
                stage = AssistantStage.Loading
            ) }

            val entity = graphDao.getById(graphId)
                ?: run {
                    _state.update {
                        it.copy(stage = AssistantStage.Error("Graph not found"))
                    }
                    return@launch
                }

            val graph = jsonParser.decodeFromString<GraphModel>(entity.graphJson)

            _state.update {
                it.copy(
                    stage = AssistantStage.Success(
                        graph = graph,
                        graphId = graphId
                    )
                )
            }
        }
    }*/

    private fun responsesLoadFromDatabase(graphId: String) {
        viewModelScope.launch {
            _state.update { it.copy(stage = AssistantStage.Loading) }

            val entity = graphDao.getById(graphId)
                ?: run {
                    _state.update {
                        it.copy(stage = AssistantStage.Error("Graph not found"))
                    }
                    return@launch
                }

            val graph =
                jsonParser.decodeFromString<GraphModel>(entity.graphJson)

            _state.update {
                it.copy(
                    stage = AssistantStage.Success(
                        graph = graph,
                        graphId = graphId
                    )
                )
            }
        }
    }

    /*private suspend fun handleStreamDelta(delta: String) {
        when {
            // TODO заставить ассистента не отправлять всю дельту!!!!!
            delta == "[DONE]" -> {
                // DONE — ничего не делаем, финальный JSON мы поймали в thread.message
                Log.d("ASSISTANT", "Stream finished")
            }

            delta.startsWith("[ERROR]") -> {
                Log.e("ASSISTANT", "Stream error: $delta")
                _state.update { it.copy(stage = AssistantStage.Error(delta)) }
            }

            delta.startsWith("{") -> {
                try {
                    val json = Json.parseToJsonElement(delta).jsonObject
                    val objType = json["object"]?.jsonPrimitive?.contentOrNull

                    Log.d("STREAM_PARSE", "Получен объект: $objType")

                    when (objType) {
                        "thread.message.delta" -> {
                            // дельты по кускам — игнорируем
                            Log.d("STREAM_PARSE", "Игнорируем дельту")
                        }

                        "thread.message" -> {
                            val content = json["content"]?.jsonArray
                            val first = content?.getOrNull(0)?.jsonObject
                            val text = first
                                ?.get("text")?.jsonObject
                                ?.get("value")?.jsonPrimitive?.contentOrNull

                            if (!text.isNullOrEmpty()) {
                                Log.d("STREAM_PARSE", "Финальный JSON получен (length=${text.length})")
                                logLong("FINAL_JSON", text)

                                val graph: GraphModel = jsonParser.decodeFromString(text)

                                val graphId = UUID.randomUUID().toString()

                                val time = System.currentTimeMillis()

                                graphDao.insert(
                                    GraphEntity(
                                        graphId = graphId,
                                        createdAt = time,
                                        graphJson = jsonParser.encodeToString(graph),
                                        graphSource = currentUserMessage.orEmpty(),
                                        graphName = graph.nodes.firstOrNull()?.label ?: "Без названия",
                                        lastModified = time,
                                    )
                                )

                                _state.update {
                                    it.copy(
                                        stage = AssistantStage.Success(
                                            graph = graph,
                                            graphId = graphId
                                        )
                                    )
                                }
                            } else {
                                Log.e("STREAM_PARSE", "thread.message без текста!")
                            }
                        }

                        else -> {
                            Log.d("STREAM_PARSE", "Игнорируем объект: $objType")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("STREAM_PARSE", "Ошибка парсинга: ${e.message}", e)
                }
            }

            else -> {
                Log.d("ASSISTANT", "Ignored non-JSON delta: $delta")
            }
        }
    }*/


}
