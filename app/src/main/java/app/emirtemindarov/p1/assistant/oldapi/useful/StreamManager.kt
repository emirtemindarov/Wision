package app.emirtemindarov.p1.assistant.oldapi.useful

/*
class StreamManager(
    private val apiKey: String,
    private val assistantId: String
) {
    private val _streamFlow = MutableSharedFlow<String>(extraBufferCapacity = 64)
    val streamFlow: SharedFlow<String> = _streamFlow

    private val client = OkHttpClient()

    fun startStreaming(threadId: String) {
        Log.d("STREAM", "startStreaming called with threadId=$threadId, assistantId=$assistantId")

        val url = "https://api.openai.com/v1/threads/$threadId/runs"
        Log.d("STREAM", "Streaming URL: $url")

        val schemaJson = """
        {
          "name": "folder_graph",
          "strict": true,
          "schema": {
            "type": "object",
            "properties": {
              "nodes": {
                "type": "array",
                "items": {
                  "type": "object",
                  "properties": {
                    "id": {
                      "type": "string"
                    },
                    "label": {
                      "type": "string"
                    },
                    "type": {
                      "type": "string"
                    },
                    "subtype": {
                      "type": [
                        "string",
                        "null"
                      ]
                    },
                    "color": {
                      "type": [
                        "string",
                        "null"
                      ]
                    },
                    "description": {
                      "type": [
                        "string",
                        "null"
                      ]
                    },
                    "inferred": {
                      "type": [
                        "boolean",
                        "null"
                      ]
                    },
                    "properties": {
                      "type": [
                        "object",
                        "null"
                      ],
                      "additionalProperties": false,
                      "properties": {
                        "path": {
                          "type": [
                            "string",
                            "null"
                          ]
                        },
                        "lines": {
                          "type": [
                            "integer",
                            "null"
                          ]
                        },
                        "signature": {
                          "type": [
                            "string",
                            "null"
                          ]
                        },
                        "visibility": {
                          "type": [
                            "string",
                            "null"
                          ]
                        },
                        "typeName": {
                          "type": [
                            "string",
                            "null"
                          ]
                        },
                        "children_ids": {
                          "type": [
                            "array",
                            "null"
                          ],
                          "items": {
                            "type": "string"
                          }
                        }
                      },
                      "required": [
                        "path",
                        "lines",
                        "signature",
                        "visibility",
                        "typeName",
                        "children_ids"
                      ]
                    }
                  },
                  "required": [
                    "id",
                    "label",
                    "type",
                    "subtype",
                    "color",
                    "description",
                    "inferred",
                    "properties"
                  ],
                  "additionalProperties": false
                }
              },
              "edges": {
                "type": "array",
                "items": {
                  "type": "object",
                  "properties": {
                    "from": {
                      "type": "string"
                    },
                    "to": {
                      "type": "string"
                    },
                    "type": {
                      "type": "string"
                    },
                    "properties": {
                      "type": [
                        "object",
                        "null"
                      ],
                      "additionalProperties": false,
                      "properties": {
                        "line": {
                          "type": [
                            "integer",
                            "null"
                          ]
                        },
                        "context": {
                          "type": [
                            "string",
                            "null"
                          ]
                        },
                        "inferred": {
                          "type": [
                            "boolean",
                            "null"
                          ]
                        }
                      },
                      "required": [
                        "line",
                        "context",
                        "inferred"
                      ]
                    }
                  },
                  "required": [
                    "from",
                    "to",
                    "type",
                    "properties"
                  ],
                  "additionalProperties": false
                }
              }
            },
            "required": [
              "nodes",
              "edges"
            ],
            "additionalProperties": false
          }
        }
        """.trimIndent()

        val requestBody = """
        {
          "assistant_id": "$assistantId",
          "stream": true,
          "response_format": {
            "type": "json_schema",
            "json_schema": $schemaJson
          }
        }
        """.trimIndent().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("OpenAI-Beta", "assistants=v2")
            .post(requestBody)
            .build()

        Log.d("STREAM", "Executing request: $request")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("STREAM", "HTTP failure: ${e.message}", e)
                _streamFlow.tryEmit("[ERROR] ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("STREAM", "HTTP response: code=${response.code}")
                if (!response.isSuccessful) {
                    Log.e("STREAM", "Unsuccessful response: ${response.body?.string()}")
                    _streamFlow.tryEmit("[ERROR] ${response.code}")
                    return
                }

                val source = response.body?.source()
                if (source == null) {
                    Log.e("STREAM", "Response body is null")
                    _streamFlow.tryEmit("[ERROR] Empty body")
                    return
                }

                try {
                    while (!source.exhausted()) {
                        val line = source.readUtf8Line() ?: continue
                        Log.d("STREAM_RAW", line)
                        if (line.startsWith("data:")) {
                            val data = line.removePrefix("data:").trim()
                            if (data == "[DONE]") {
                                _streamFlow.tryEmit("[DONE]")
                                break
                            } else {
                                _streamFlow.tryEmit(data)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("STREAM", "Exception while reading stream: ${e.message}", e)
                    _streamFlow.tryEmit("[ERROR] ${e.message}")
                }
            }
        })
    }
}
*/
