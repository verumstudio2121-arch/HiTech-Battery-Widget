package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BatteryInfo
import com.example.data.WidgetConfig
import com.example.data.WidgetStyle
import kotlin.math.sin

@Composable
fun WidgetInteractivePreview(
    config: WidgetConfig,
    batteryInfo: BatteryInfo,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave_anim")
    val waveOffset by if (config.animationsEnabled) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = (2 * Math.PI).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(2800, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "wave_offset"
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    }

    val pulseGlow by if (config.animationsEnabled && batteryInfo.isCharging) {
        infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_glow"
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(0.7f) }
    }

    val resolvedColors = config.resolveColors(batteryInfo)
    val primaryColor = Color(resolvedColors.primary)
    val secondaryColor = Color(resolvedColors.secondary)
    val glowColor = Color(resolvedColors.glow)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.85f),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0F172A), Color(0xFF080C16))
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0x66FFFFFF), Color(0x15FFFFFF), glowColor.copy(alpha = 0.4f))
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasW = size.width
                val canvasH = size.height
                val pct = batteryInfo.percentage.coerceIn(0, 100) / 100f

                when (config.style) {
                    WidgetStyle.LIQUID_GLASS -> {
                        // Liquid Glass Canvas
                        val liquidTop = canvasH - (canvasH * 0.82f * pct)

                        // Ambient glow
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(glowColor.copy(alpha = 0.4f * pulseGlow), Color.Transparent),
                                center = Offset(canvasW * 0.5f, canvasH * 0.9f),
                                radius = canvasW * 0.6f
                            ),
                            radius = canvasW * 0.6f,
                            center = Offset(canvasW * 0.5f, canvasH * 0.9f)
                        )

                        // Draw animated wave
                        val wavePath = Path().apply {
                            moveTo(0f, canvasH)
                            lineTo(0f, liquidTop)
                            val waveCount = 1.5
                            val waveHeight = 12.dp.toPx()
                            val step = 10f
                            var x = 0f
                            while (x <= canvasW) {
                                val y = liquidTop + (sin((x / canvasW * waveCount * 2 * Math.PI) + waveOffset) * waveHeight).toFloat()
                                lineTo(x, y)
                                x += step
                            }
                            lineTo(canvasW, canvasH)
                            close()
                        }

                        drawPath(
                            path = wavePath,
                            brush = Brush.verticalGradient(
                                colors = listOf(secondaryColor.copy(alpha = 0.85f), primaryColor.copy(alpha = 0.95f)),
                                startY = liquidTop,
                                endY = canvasH
                            )
                        )

                        // Wave surface highlight line
                        val crestPath = Path().apply {
                            val waveCount = 1.5
                            val waveHeight = 12.dp.toPx()
                            var x = 0f
                            val startY = liquidTop + (sin(waveOffset) * waveHeight).toFloat()
                            moveTo(0f, startY)
                            while (x <= canvasW) {
                                val y = liquidTop + (sin((x / canvasW * waveCount * 2 * Math.PI) + waveOffset) * waveHeight).toFloat()
                                lineTo(x, y)
                                x += 10f
                            }
                        }
                        drawPath(
                            path = crestPath,
                            color = Color.White.copy(alpha = 0.7f),
                            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Glass diagonal shine stripe
                        val shinePath = Path().apply {
                            moveTo(canvasW * 0.1f, 0f)
                            lineTo(canvasW * 0.35f, 0f)
                            lineTo(canvasW * 0.15f, canvasH)
                            lineTo(0f, canvasH)
                            close()
                        }
                        drawPath(
                            path = shinePath,
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0x22FFFFFF), Color.Transparent),
                                start = Offset(0f, 0f),
                                end = Offset(canvasW * 0.3f, canvasH)
                            )
                        )
                    }

                    WidgetStyle.WATER_BUBBLE -> {
                        // Water Bubble Preview
                        val radius = canvasH * 0.40f
                        val cx = canvasH * 0.50f
                        val cy = canvasH * 0.50f

                        // Outer bubble glow
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(glowColor.copy(alpha = 0.45f * pulseGlow), Color.Transparent),
                                center = Offset(cx, cy),
                                radius = radius * 1.3f
                            ),
                            radius = radius * 1.3f,
                            center = Offset(cx, cy)
                        )

                        // Sphere dark base
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF1E293B), Color(0xFF030712)),
                                center = Offset(cx - radius * 0.3f, cy - radius * 0.3f),
                                radius = radius * 1.1f
                            ),
                            radius = radius,
                            center = Offset(cx, cy)
                        )

                        // Clip water wave within bubble
                        val bubbleCircle = Path().apply {
                            addOval(Rect(cx - radius, cy - radius, cx + radius, cy + radius))
                        }

                        drawContext.canvas.nativeCanvas.save()
                        val clipNativePath = android.graphics.Path().apply {
                            addCircle(cx, cy, radius, android.graphics.Path.Direction.CW)
                        }
                        drawContext.canvas.nativeCanvas.clipPath(clipNativePath)

                        val waterY = (cy + radius) - (radius * 2f * pct)
                        val waterPath = Path().apply {
                            moveTo(cx - radius, cy + radius)
                            lineTo(cx - radius, waterY)
                            var x = cx - radius
                            val waveHeight = 8.dp.toPx()
                            while (x <= cx + radius) {
                                val y = waterY + (sin((x / (radius * 2f) * 2 * Math.PI) + waveOffset) * waveHeight).toFloat()
                                lineTo(x, y)
                                x += 8f
                            }
                            lineTo(cx + radius, cy + radius)
                            close()
                        }

                        drawPath(
                            path = waterPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(secondaryColor.copy(alpha = 0.85f), primaryColor.copy(alpha = 0.95f)),
                                startY = waterY,
                                endY = cy + radius
                            )
                        )

                        // Floating micro-bubbles
                        if (pct > 0.2f) {
                            val bY1 = (cy + radius * 0.35f).coerceAtLeast(waterY + 15f)
                            val bY2 = (cy + radius * 0.6f).coerceAtLeast(waterY + 20f)
                            drawCircle(Color(0x66FFFFFF), radius = 5.dp.toPx(), center = Offset(cx - radius * 0.3f, bY1))
                            drawCircle(Color.White.copy(alpha = 0.8f), radius = 5.dp.toPx(), center = Offset(cx - radius * 0.3f, bY1), style = Stroke(1.dp.toPx()))
                            drawCircle(Color(0x66FFFFFF), radius = 7.dp.toPx(), center = Offset(cx + radius * 0.35f, bY2))
                            drawCircle(Color.White.copy(alpha = 0.8f), radius = 7.dp.toPx(), center = Offset(cx + radius * 0.35f, bY2), style = Stroke(1.dp.toPx()))
                        }

                        // Glass sphere reflection crescent
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0x88FFFFFF), Color.Transparent),
                                center = Offset(cx - radius * 0.35f, cy - radius * 0.45f),
                                radius = radius * 0.5f
                            ),
                            radius = radius * 0.45f,
                            center = Offset(cx - radius * 0.35f, cy - radius * 0.45f)
                        )

                        drawContext.canvas.nativeCanvas.restore()

                        // Outer ring highlight
                        drawCircle(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xAAFFFFFF), Color(0x33FFFFFF), glowColor),
                                start = Offset(cx - radius, cy - radius),
                                end = Offset(cx + radius, cy + radius)
                            ),
                            radius = radius,
                            center = Offset(cx, cy),
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }

                    WidgetStyle.MINIMAL -> {
                        // Minimalist Layout Canvas
                        // Progress bar track
                        val padX = 24.dp.toPx()
                        val barY = canvasH * 0.72f
                        val barHeight = 8.dp.toPx()
                        val barWidth = canvasW - (padX * 2f)

                        drawRoundRect(
                            color = Color(0x25FFFFFF),
                            topLeft = Offset(padX, barY),
                            size = Size(barWidth, barHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                        )

                        drawRoundRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(primaryColor, secondaryColor)
                            ),
                            topLeft = Offset(padX, barY),
                            size = Size(barWidth * pct, barHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                        )
                    }
                }

                // Native Canvas Text Rendering for high fidelity parity
                val nativeCanvas = drawContext.canvas.nativeCanvas

                val textLeft = if (config.style == WidgetStyle.WATER_BUBBLE) {
                    canvasH * 0.95f
                } else {
                    24.dp.toPx()
                }

                // Brand text
                val brandPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                    typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                    textSize = 12.sp.toPx()
                    color = 0x99FFFFFF.toInt()
                    letterSpacing = 0.08f
                }
                nativeCanvas.drawText("HITECH BATTERY", textLeft, 32.dp.toPx(), brandPaint)

                // Large Percentage
                if (config.showPercentage) {
                    val pctPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                        typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                        textSize = 46.sp.toPx()
                        color = android.graphics.Color.WHITE
                        setShadowLayer(8f, 0f, 3f, 0xCC000000.toInt())
                    }
                    nativeCanvas.drawText("${batteryInfo.percentage}%", textLeft, 78.dp.toPx(), pctPaint)
                }

                // Status line
                if (config.showStatusText) {
                    val statusPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                        typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                        textSize = 14.sp.toPx()
                        color = when {
                            batteryInfo.isFullyCharged -> 0xFF34D399.toInt()
                            batteryInfo.isCharging -> 0xFF38BDF8.toInt()
                            batteryInfo.isExtreme -> 0xFFEF4444.toInt()
                            batteryInfo.isLow -> 0xFFFB923C.toInt()
                            else -> 0xFFCBD5E1.toInt()
                        }
                    }

                    val statusMsg = when {
                        batteryInfo.isFullyCharged -> "✓ Fully Charged"
                        batteryInfo.isCharging -> "⚡ Charging (${batteryInfo.plugged.displayText})"
                        batteryInfo.isExtreme -> "⚠ Critical Battery"
                        batteryInfo.isLow -> "Low Battery (${batteryInfo.percentage}%)"
                        else -> "On Battery (${batteryInfo.plugged.displayText})"
                    }
                    nativeCanvas.drawText(statusMsg, textLeft, 102.dp.toPx(), statusPaint)
                }
            }
        }
    }
}
