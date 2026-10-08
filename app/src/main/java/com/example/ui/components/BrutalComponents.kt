package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * Modifier modifier.brutalShadow(offsetY = 4.dp, color = PositivusDark, cornerRadius = 26.dp)
 * Draws a sharp neo-brutalist solid shadow underneath the element.
 */
fun Modifier.brutalShadow(
  offsetY: Dp = 4.dp,
  offsetX: Dp = 0.dp,
  color: Color = PositivusBorder,
  cornerRadius: Dp = 26.dp
): Modifier = this.drawBehind {
  val cornerRadiusPx = cornerRadius.toPx()
  drawRoundRect(
    color = color,
    topLeft = Offset(offsetX.toPx(), offsetY.toPx()),
    size = size,
    cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadiusPx, cornerRadiusPx)
  )
}

/**
 * Positivus Neo-Brutalist Card with solid 1.5px border and sharp solid shadow.
 */
@Composable
fun BrutalCard(
  modifier: Modifier = Modifier,
  backgroundColor: Color = Color.White,
  borderColor: Color = PositivusBorder,
  shadowOffsetY: Dp = 4.dp,
  cornerRadius: Dp = 26.dp,
  contentPadding: PaddingValues = PaddingValues(18.dp),
  onClick: (() -> Unit)? = null,
  content: @Composable ColumnScope.() -> Unit
) {
  val shape = RoundedCornerShape(cornerRadius)

  Box(
    modifier = modifier.brutalShadow(
      offsetY = shadowOffsetY,
      color = borderColor,
      cornerRadius = cornerRadius
    )
  ) {
    Surface(
      shape = shape,
      color = backgroundColor,
      border = BorderStroke(1.5.dp, borderColor),
      modifier = Modifier
        .fillMaxWidth()
        .then(
          if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
        )
    ) {
      Column(
        modifier = Modifier.padding(contentPadding),
        content = content
      )
    }
  }
}

/**
 * Brutal Pill Tag
 */
@Composable
fun BrutalPill(
  text: String,
  backgroundColor: Color = PositivusGreen,
  textColor: Color = PositivusDark,
  borderColor: Color = PositivusBorder,
  icon: (@Composable () -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier.brutalShadow(
      offsetY = 2.dp,
      color = borderColor,
      cornerRadius = 99.dp
    )
  ) {
    Surface(
      shape = RoundedCornerShape(99.dp),
      color = backgroundColor,
      border = BorderStroke(1.2.dp, borderColor)
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        if (icon != null) {
          icon()
        }
        Text(
          text = text,
          color = textColor,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.3.sp
        )
      }
    }
  }
}
