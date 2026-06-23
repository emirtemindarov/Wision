package app.emirtemindarov.p1.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.emirtemindarov.p1.render.NodeType
import app.emirtemindarov.p1.render.colorForType
import app.emirtemindarov.p1.ui.theme.CustomTextStyles

@Composable
fun CodeTextWithLineNumbers(
    text: String,
    modifier: Modifier = Modifier
) {
    val lines = remember(text) {
        text.split("\n")
    }

    Row(
        modifier = modifier.height(IntrinsicSize.Min)
    ) {

        Column(
            modifier = Modifier.padding(end = 12.dp),
            horizontalAlignment = Alignment.End
        ) {
            lines.forEachIndexed { index, _ ->
                Text(
                    text = "${index + 1}",
                    fontFamily = CustomTextStyles.Code,
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(end = 4.dp)
                .width(1.dp)
                .background(Color.LightGray)
        )

        Column {
            lines.forEach { line ->
                HighlightedCodeLine(line)
            }
        }
    }
}

@Composable
fun HighlightedCodeLine(line: String) {
    val annotated = remember(line) {
        buildAnnotatedString {
            val regex = Regex("\\w+|\\W+") // слова И все остальное (включая пробелы)

            regex.findAll(line).forEach { match ->
                val token = match.value
                val type = keywordMap[token.lowercase()]

                if (type != null) {
                    withStyle(
                        style = SpanStyle(color = colorForType(type))
                    ) {
                        append(token)
                    }
                } else {
                    append(token)
                }
            }
        }
    }

    Text(
        text = annotated,
        fontFamily = CustomTextStyles.Code,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium
    )
}

val keywordGroups: Map<NodeType, Set<String>> = mapOf(

    // Файлы
    NodeType.FILE to setOf(
        "import", "require", "include", "using"
    ),

    /*// Папки
    NodeType.FOLDER to setOf(
        // package уже в OBJECT
    ),*/

    // Классы / типы
    NodeType.CLASS to setOf(
        "class", "struct", /*"record",*/ /*"data"*/
    ),

    // Интерфейсы / контракты
    NodeType.INTERFACE to setOf(
        "interface", "protocol", "trait"
    ),

    // Функции / методы
    NodeType.FUNCTION to setOf(
        "fun", "function", "def", "fn", "lambda"
    ),

    // Переменные / объявления
    NodeType.VARIABLE to setOf(
        "var", "val", "let", "const", "final", "mutable", "immutable",
        "public", "private", "protected", "internal",
        "static", "abstract", "open", "sealed", "readonly"
    ),

    // Объекты / модули / пространства имён
    NodeType.OBJECT to setOf(
        "object", "module", "namespace", "package"
    ),

    // Управляющие конструкции
    NodeType.BLOCK to setOf(
        "if", "else", "when", "switch", "case",
        "for", "while", "do", "foreach",
        "try", "catch", "finally",
        "return", "yield", "break", "continue", "throw"
    )
)

val keywordMap: Map<String, NodeType> =
    keywordGroups.flatMap { (type, words) ->
        words.map { it to type }
    }.toMap()