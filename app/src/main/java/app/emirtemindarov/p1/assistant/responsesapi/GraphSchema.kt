package app.emirtemindarov.p1.assistant.responsesapi

object GraphSchema {
    val schemaJson: Map<String, Any> = mapOf(
        "type" to "object",
        "properties" to mapOf(
            "nodes" to mapOf(
                "type" to "array",
                "items" to mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "id" to mapOf("type" to "string"),
                        "label" to mapOf("type" to "string"),
                        "type" to mapOf("type" to "string"),
                        "subtype" to mapOf("type" to listOf("string", "null")),
                        "color" to mapOf("type" to listOf("string", "null")),
                        "description" to mapOf("type" to listOf("string", "null")),
                        "inferred" to mapOf("type" to listOf("boolean", "null")),
                        "properties" to mapOf(
                            "type" to listOf("object", "null"),
                            "additionalProperties" to false,
                            "properties" to mapOf(
                                "path" to mapOf("type" to listOf("string", "null")),
                                "lines" to mapOf("type" to listOf("integer", "null")),
                                "signature" to mapOf("type" to listOf("string", "null")),
                                "visibility" to mapOf("type" to listOf("string", "null")),
                                "typeName" to mapOf("type" to listOf("string", "null")),
                                "children_ids" to mapOf(
                                    "type" to listOf("array", "null"),
                                    "items" to mapOf("type" to "string")
                                )
                            ),
                            "required" to listOf(
                                "path", "lines", "signature",
                                "visibility", "typeName", "children_ids"
                            )
                        )
                    ),
                    "required" to listOf(
                        "id", "label", "type", "subtype",
                        "color", "description", "inferred", "properties"
                    ),
                    "additionalProperties" to false
                )
            ),
            "edges" to mapOf(
                "type" to "array",
                "items" to mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "from" to mapOf("type" to "string"),
                        "to" to mapOf("type" to "string"),
                        "type" to mapOf("type" to "string"),
                        "properties" to mapOf(
                            "type" to listOf("object", "null"),
                            "additionalProperties" to false,
                            "properties" to mapOf(
                                "line" to mapOf("type" to listOf("integer", "null")),
                                "context" to mapOf("type" to listOf("string", "null")),
                                "inferred" to mapOf("type" to listOf("boolean", "null"))
                            ),
                            "required" to listOf("line", "context", "inferred")
                        )
                    ),
                    "required" to listOf("from", "to", "type", "properties"),
                    "additionalProperties" to false
                )
            )
        ),
        "required" to listOf("nodes", "edges"),
        "additionalProperties" to false
    )



    /*val schemaJson = """
        {
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
                    "type": ["string", "null"]
                  },
                  "color": {
                    "type": ["string", "null"]
                  },
                  "description": {
                    "type": ["string", "null"]
                  },
                  "inferred": {
                    "type": ["boolean", "null"]
                  },
                  "properties": {
                    "type": ["object", "null"],
                    "additionalProperties": false,
                    "properties": {
                      "path": {
                        "type": ["string", "null"]
                      },
                      "lines": {
                        "type": ["integer", "null"]
                      },
                      "signature": {
                        "type": ["string", "null"]
                      },
                      "visibility": {
                        "type": ["string", "null"]
                      },
                      "typeName": {
                        "type": ["string", "null"]
                      },
                      "children_ids": {
                        "type": ["array", "null"],
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
                    "type": ["object", "null"],
                    "additionalProperties": false,
                    "properties": {
                      "line": {
                        "type": ["integer", "null"]
                      },
                      "context": {
                        "type": ["string", "null"]
                      },
                      "inferred": {
                        "type": ["boolean", "null"]
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
          "required": ["nodes", "edges"],
          "additionalProperties": false
        }
        """.trimIndent()*/
}