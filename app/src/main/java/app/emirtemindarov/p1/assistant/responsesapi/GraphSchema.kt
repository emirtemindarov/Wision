package app.emirtemindarov.p1.assistant.responsesapi

object GraphSchema {
    val schemaJson: Map<String, Any> = mapOf(
        "type" to "object",
        "properties" to mapOf(

            // =========================
            // NODES
            // =========================

            "nodes" to mapOf(
                "type" to "array",
                "items" to mapOf(
                    "type" to "object",
                    "additionalProperties" to false,
                    "properties" to mapOf(

                        "id" to mapOf("type" to "string"),
                        "name" to mapOf("type" to "string"),

                        "type" to mapOf(
                            "enum" to listOf(
                                "FILE",
                                "FOLDER",
                                "CLASS",
                                "INTERFACE",
                                "FUNCTION",
                                "VARIABLE",
                                "BLOCK",
                                "OBJECT"
                            )
                        ),

                        "childrenIds" to mapOf(
                            "type" to "array",
                            "items" to mapOf("type" to "string")
                        )
                    ),
                    "required" to listOf(
                        "id",
                        "name",
                        "type",
                        "childrenIds"
                    )
                )
            ),

            // =========================
            // EDGES
            // =========================

            "edges" to mapOf(
                "type" to "array",
                "items" to mapOf(
                    "type" to "object",
                    "additionalProperties" to false,
                    "properties" to mapOf(

                        "from" to mapOf("type" to "string"),
                        "to" to mapOf("type" to "string"),

                        "type" to mapOf(
                            "enum" to listOf(
                                "USES",
                                "IMPLEMENTS",
                                "INHERITS"
                            )
                        )
                    ),
                    "required" to listOf(
                        "from",
                        "to",
                        "type"
                    )
                )
            )
        ),
        "required" to listOf("nodes", "edges"),
        "additionalProperties" to false
    )
}