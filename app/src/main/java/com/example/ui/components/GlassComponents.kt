package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * Liquid Glass Card Container with frosted translucent white gradient and specular rim highlight.
 */
@Composable
fun GlassCard(
  modifier: Modifier = Modifier,
  cornerRadius: Dp = 24.dp,
  contentPadding: PaddingValues = PaddingValues(18.dp),
  borderAlpha: Float = 0.78f,
  backgroundAlpha: Float = 0.94f,
  onClick: (() -> Unit)? = null,
  content: @Composable ColumnScope.() -> Unit
) {
  val shape = RoundedCornerShape(cornerRadius)
  val baseModifier = modifier
    .shadow(
      elevation = 8.dp,
      shape = shape,
      spotColor = Color(0x14000000),
      ambientColor = Color(0x0A000000)
    )
    .clip(shape)
    .background(
      brush = Brush.verticalGradient(
        colors = listOf(
          Color.White.copy(alpha = backgroundAlpha),
          Color(0xFFF7F8FC).copy(alpha = backgroundAlpha)
        )
      )
    )
    .then(
      if (onClick != null) {
        Modifier.clickable(onClick = onClick)
      } else Modifier
    )
    .padding(contentPadding)

  Surface(
    shape = shape,
    color = Color.Transparent,
    border = BorderStroke(1.dp, Color.White.copy(alpha = borderAlpha)),
    modifier = modifier
  ) {
    Column(
      modifier = baseModifier,
      content = content
    )
  }
}

/**
 * High-contrast tactile Master Ground CTA (Black pill button, Apple HIG style)
 */
@Composable
fun GroundCTAButton(
  text: String,
  subtext: String? = null,
  icon: ImageVector? = null,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true
) {
  Button(
    onClick = onClick,
    enabled = enabled,
    modifier = modifier
      .fillMaxWidth()
      .heightIn(min = 64.dp),
    shape = RoundedCornerShape(22.dp),
    colors = ButtonDefaults.buttonColors(
      containerColor = SecondaryDark,
      contentColor = Color.White,
      disabledContainerColor = Color(0xFFD0D0D5),
      disabledContentColor = Color(0xFF8E8E93)
    ),
    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 1.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = text,
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        )
        if (subtext != null) {
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = subtext,
            style = MaterialTheme.typography.labelMedium.copy(
              color = Color.White.copy(alpha = 0.72f)
            )
          )
        }
      }
      if (icon != null) {
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.18f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(22.dp)
          )
        }
      }
    }
  }
}

/**
 * Status Chip with rounded pill geometry
 */
@Composable
fun StatusPill(
  text: String,
  tintColor: Color = PrimaryBlue,
  backgroundColor: Color = tintColor.copy(alpha = 0.12f),
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(CircleShape)
      .background(backgroundColor)
      .padding(horizontal = 10.dp, vertical = 4.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = text,
      color = tintColor,
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold
      )
    )
  }
}
