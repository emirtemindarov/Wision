package app.emirtemindarov.p1.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import app.emirtemindarov.p1.R

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

object CustomTextStyles {

    val Standard = FontFamily(
        Font(R.font.inter_24pt_regular, FontWeight.Normal),
        Font(R.font.inter_24pt_medium, FontWeight.Medium),
        Font(R.font.inter_24pt_bold, FontWeight.Bold),
        Font(resId = R.font.inter_24pt_italic, style = FontStyle.Italic),
        Font(R.font.inter_24pt_light, FontWeight.Light),
        Font(R.font.inter_24pt_thin, FontWeight.Thin),
    )

    val Code = FontFamily(
        Font(R.font.jetbrainsmono_regular, FontWeight.Normal),
        Font(R.font.jetbrainsmono_medium, FontWeight.Medium),
        Font(R.font.jetbrainsmono_bold, FontWeight.Bold),
        Font(resId = R.font.jetbrainsmono_italic, style = FontStyle.Italic),
        Font(R.font.jetbrainsmono_light, FontWeight.Light),
        Font(R.font.jetbrainsmono_thin, FontWeight.Thin),
    )

    val GraphNode = FontFamily(
        Font(R.font.firacode_regular, FontWeight.Normal),
        Font(R.font.firacode_medium, FontWeight.Medium),
        Font(R.font.firacode_bold, FontWeight.Bold),
        Font(R.font.firacode_light, FontWeight.Light),
        Font(R.font.firacode_semibold, FontWeight.SemiBold),
    )
}