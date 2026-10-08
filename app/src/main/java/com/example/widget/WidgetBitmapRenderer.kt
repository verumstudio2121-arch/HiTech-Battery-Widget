package com.example.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import com.example.data.BatteryInfo
import com.example.data.ResolvedColors
import com.example.data.WidgetConfig
import com.example.data.WidgetSize
import com.example.data.WidgetStyle
import kotlin.math.sin

object WidgetBitmapRenderer {

    fun renderWidgetBitmap(
        context: Context,
        config: WidgetConfig,
        batteryInfo: BatteryInfo,
        size: WidgetSize = WidgetSize.MEDIUM,
        targetWidth: Int = 0,
        targetHeight: Int = 0
    ): Bitmap {
        val width = if (targetWidth > 0) targetWidth else when (size) {
            WidgetSize.SMALL -> 400
            WidgetSize.MEDIUM -> 800
            WidgetSize.LARGE -> 800
        }
        val height = if (targetHeight > 0) targetHeight else when (size) {
            WidgetSize.SMALL -> 400
            WidgetSize.MEDIUM -> 400
            WidgetSize.LARGE -> 750
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        when (config.style) {
            WidgetStyle.LIQUID_GLASS -> drawLiquidGlass(canvas, width, height, config, batteryInfo, size)
            WidgetStyle.WATER_BUBBLE -> drawWaterBubble(canvas, width, height, config, batteryInfo, size)
            WidgetStyle.MINIMAL -> drawMinimal(canvas, width, height, config, batteryInfo, size)
        }

        return bitmap
    }

    // =========================================================================
    // STYLE A: LIQUID GLASS
    // =========================================================================
    private fun drawLiquidGlass(
        canvas: Canvas,
        width: Int,
        height: Int,
        config: WidgetConfig,
        batteryInfo: BatteryInfo,
        size: WidgetSize
    ) {
        val colors = config.resolveColors(batteryInfo)
        val density = width / 400f
        val cornerRadius = 36f * density

        val rect = RectF(12f * density, 12f * density, width - 12f * density, height - 12f * density)

        // 1. Dark translucent backdrop
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            shader = LinearGradient(
                rect.left, rect.top, rect.right, rect.bottom,
                intArrayOf(0xCC0B132B.toInt(), 0xEE080C16.toInt()),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, bgPaint)

        // 2. Ambient glow from liquid
        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            shader = RadialGradient(
                rect.centerX(), rect.bottom,
                rect.width() * 0.75f,
                intArrayOf(colors.glow, 0x00000000),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, glowPaint)

        // 3. Liquid level fill
        val pct = batteryInfo.percentage.coerceIn(0, 100) / 100f
        val liquidTop = rect.bottom - (rect.height() * 0.85f * pct)

        val liquidPath = Path()
        liquidPath.moveTo(rect.left, rect.bottom)
        liquidPath.lineTo(rect.left, liquidTop)

        // Soft curved wave surface
        val waveAmplitude = 6f * density
        val waveMid = rect.centerX()
        liquidPath.cubicTo(
            rect.left + rect.width() * 0.25f, liquidTop - waveAmplitude,
            rect.left + rect.width() * 0.75f, liquidTop + waveAmplitude,
            rect.right, liquidTop
        )
        liquidPath.lineTo(rect.right, rect.bottom)
        liquidPath.close()

        canvas.save()
        // Clip to rounded card bounds
        val clipPath = Path().apply {
            addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW)
        }
        canvas.clipPath(clipPath)

        val liquidPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            shader = LinearGradient(
                rect.centerX(), liquidTop,
                rect.centerX(), rect.bottom,
                intArrayOf(
                    Color.argb(200, Color.red(colors.secondary), Color.green(colors.secondary), Color.blue(colors.secondary)),
                    Color.argb(235, Color.red(colors.primary), Color.green(colors.primary), Color.blue(colors.primary))
                ),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawPath(liquidPath, liquidPaint)

        // Liquid surface highlight line
        val waveLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 3f * density
            color = Color.argb(220, 255, 255, 255)
        }
        val waveLinePath = Path().apply {
            moveTo(rect.left, liquidTop)
            cubicTo(
                rect.left + rect.width() * 0.25f, liquidTop - waveAmplitude,
                rect.left + rect.width() * 0.75f, liquidTop + waveAmplitude,
                rect.right, liquidTop
            )
        }
        canvas.drawPath(waveLinePath, waveLinePaint)

        // Subtle specular diagonal glass shine stripe
        val shinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            shader = LinearGradient(
                rect.left, rect.top,
                rect.right * 0.8f, rect.bottom * 0.8f,
                intArrayOf(0x30FFFFFF, 0x05FFFFFF, 0x00FFFFFF),
                floatArrayOf(0f, 0.45f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, shinePaint)
        canvas.restore()

        // 4. Glass border with frosted highlight
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 2f * density
            shader = LinearGradient(
                rect.left, rect.top, rect.right, rect.bottom,
                intArrayOf(0x88FFFFFF.toInt(), 0x22FFFFFF, 0x4438BDF8.toInt()),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, borderPaint)

        // 5. Typography and indicators
        drawContentOverlay(canvas, rect, config, batteryInfo, colors, density, size)
    }

    // =========================================================================
    // STYLE B: WATER BUBBLE
    // =========================================================================
    private fun drawWaterBubble(
        canvas: Canvas,
        width: Int,
        height: Int,
        config: WidgetConfig,
        batteryInfo: BatteryInfo,
        size: WidgetSize
    ) {
        val colors = config.resolveColors(batteryInfo)
        val density = width / 400f

        if (size == WidgetSize.MEDIUM || size == WidgetSize.LARGE) {
            // Draw background card first for banner/dashboard layout
            val rect = RectF(12f * density, 12f * density, width - 12f * density, height - 12f * density)
            val cornerRadius = 36f * density

            val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.FILL
                shader = LinearGradient(
                    rect.left, rect.top, rect.right, rect.bottom,
                    intArrayOf(0xCC090E17.toInt(), 0xFA05080E.toInt()),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, bgPaint)

            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 1.5f * density
                shader = LinearGradient(
                    rect.left, rect.top, rect.right, rect.bottom,
                    intArrayOf(0x66FFFFFF.toInt(), 0x15FFFFFF, 0x3338BDF8.toInt()),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, borderPaint)

            // Left sphere bubble, right side metrics
            val bubbleRadius = (rect.height() * 0.40f)
            val bubbleCx = rect.left + bubbleRadius + 24f * density
            val bubbleCy = rect.centerY()

            drawSphereBubble(canvas, bubbleCx, bubbleCy, bubbleRadius, config, batteryInfo, colors, density)

            // Right side stats & text
            val rightLeft = bubbleCx + bubbleRadius + 28f * density
            drawSideMetrics(canvas, rightLeft, rect.top, rect.right - 20f * density, rect.bottom, config, batteryInfo, colors, density, size)
        } else {
            // SMALL: Centered sphere bubble
            val cx = width / 2f
            val cy = height / 2f
            val radius = (width.coerceAtMost(height) / 2f) - 16f * density

            drawSphereBubble(canvas, cx, cy, radius, config, batteryInfo, colors, density)
            drawBubbleCenteredText(canvas, cx, cy, radius, config, batteryInfo, colors, density)
        }
    }

    private fun drawSphereBubble(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float,
        config: WidgetConfig,
        batteryInfo: BatteryInfo,
        colors: ResolvedColors,
        density: Float
    ) {
        // 1. Outer bubble shadow / glow
        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            shader = RadialGradient(
                cx, cy, radius * 1.2f,
                intArrayOf(colors.glow, 0x00000000),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(cx, cy, radius * 1.15f, glowPaint)

        // 2. Dark spherical body
        val sphereBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            shader = RadialGradient(
                cx - radius * 0.3f, cy - radius * 0.3f, radius * 1.2f,
                intArrayOf(0xEE1E293B.toInt(), 0xFF0B132B.toInt(), 0xFF030712.toInt()),
                floatArrayOf(0f, 0.6f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(cx, cy, radius, sphereBg)

        // 3. Clip to sphere circle for water fill
        val circlePath = Path().apply {
            addCircle(cx, cy, radius - 2f * density, Path.Direction.CW)
        }
        canvas.save()
        canvas.clipPath(circlePath)

        // Water fill calculation
        val pct = batteryInfo.percentage.coerceIn(0, 100) / 100f
        val waterY = (cy + radius) - (radius * 2f * pct)

        val waterPath = Path()
        waterPath.moveTo(cx - radius, cy + radius)
        waterPath.lineTo(cx - radius, waterY)

        // Wave crest
        val waveAmp = 5f * density
        waterPath.cubicTo(
            cx - radius * 0.5f, waterY - waveAmp,
            cx + radius * 0.5f, waterY + waveAmp,
            cx + radius, waterY
        )
        waterPath.lineTo(cx + radius, cy + radius)
        waterPath.close()

        val waterPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            shader = LinearGradient(
                cx, waterY, cx, cy + radius,
                intArrayOf(
                    Color.argb(210, Color.red(colors.secondary), Color.green(colors.secondary), Color.blue(colors.secondary)),
                    Color.argb(245, Color.red(colors.primary), Color.green(colors.primary), Color.blue(colors.primary))
                ),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawPath(waterPath, waterPaint)

        // Floating micro bubbles in water
        val bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = 0x55FFFFFF
        }
        val bubbleStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 1f * density
            color = 0xAAFFFFFF.toInt()
        }
        val bY1 = (cy + radius * 0.4f).coerceAtLeast(waterY + 10f * density)
        val bY2 = (cy + radius * 0.65f).coerceAtLeast(waterY + 15f * density)
        val bY3 = (cy + radius * 0.15f).coerceAtLeast(waterY + 8f * density)

        if (pct > 0.25f) {
            canvas.drawCircle(cx - radius * 0.35f, bY1, 4f * density, bubblePaint)
            canvas.drawCircle(cx - radius * 0.35f, bY1, 4f * density, bubbleStroke)
            canvas.drawCircle(cx + radius * 0.4f, bY2, 5.5f * density, bubblePaint)
            canvas.drawCircle(cx + radius * 0.4f, bY2, 5.5f * density, bubbleStroke)
            canvas.drawCircle(cx + radius * 0.1f, bY3, 3f * density, bubblePaint)
        }

        // Top sphere specular reflection (glass shine crescent)
        val reflectionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            shader = RadialGradient(
                cx - radius * 0.35f, cy - radius * 0.45f, radius * 0.5f,
                intArrayOf(0x88FFFFFF.toInt(), 0x00FFFFFF),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(cx - radius * 0.3f, cy - radius * 0.4f, radius * 0.4f, reflectionPaint)

        canvas.restore()

        // 4. Glass sphere outer border
        val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 3f * density
            shader = LinearGradient(
                cx - radius, cy - radius, cx + radius, cy + radius,
                intArrayOf(0xAAFFFFFF.toInt(), 0x33FFFFFF, 0x6638BDF8.toInt()),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(cx, cy, radius, ringPaint)
    }

    private fun drawBubbleCenteredText(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float,
        config: WidgetConfig,
        batteryInfo: BatteryInfo,
        colors: ResolvedColors,
        density: Float
    ) {
        val pctPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            textSize = radius * 0.52f
            color = Color.WHITE
            setShadowLayer(8f * density, 0f, 2f * density, 0xCC000000.toInt())
        }

        if (config.showPercentage) {
            canvas.drawText("${batteryInfo.percentage}%", cx, cy + (pctPaint.textSize * 0.35f), pctPaint)
        }

        // Icon indicator
        if (batteryInfo.isCharging && config.showChargingIcon) {
            val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
                textSize = radius * 0.28f
                color = 0xFFFACC15.toInt() // Vibrant electric yellow
            }
            canvas.drawText("⚡", cx, cy - radius * 0.38f, iconPaint)
        } else if (batteryInfo.isFullyCharged && config.showStatusText) {
            val fullPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
                textSize = radius * 0.22f
                color = 0xFF4ADE80.toInt()
            }
            canvas.drawText("✓ FULL", cx, cy + radius * 0.65f, fullPaint)
        }
    }

    // =========================================================================
    // STYLE C: MINIMAL
    // =========================================================================
    private fun drawMinimal(
        canvas: Canvas,
        width: Int,
        height: Int,
        config: WidgetConfig,
        batteryInfo: BatteryInfo,
        size: WidgetSize
    ) {
        val colors = config.resolveColors(batteryInfo)
        val density = width / 400f
        val rect = RectF(12f * density, 12f * density, width - 12f * density, height - 12f * density)
        val cornerRadius = 32f * density

        // 1. Pure minimalist matte dark surface
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = 0xFF090D16.toInt()
        }
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, bgPaint)

        // Subtle clean hairline border
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 1.2f * density
            color = 0x22FFFFFF
        }
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, borderPaint)

        if (size == WidgetSize.SMALL) {
            // SMALL: Large percentage + sleek bottom progress pill
            val pctPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
                textSize = 72f * density
                color = Color.WHITE
            }

            val cy = rect.centerY()
            if (config.showPercentage) {
                canvas.drawText("${batteryInfo.percentage}%", rect.centerX(), cy + 12f * density, pctPaint)
            }

            // Minimal charging icon
            if (batteryInfo.isCharging && config.showChargingIcon) {
                val boltPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    textAlign = Paint.Align.CENTER
                    textSize = 28f * density
                    color = 0xFFFACC15.toInt()
                }
                canvas.drawText("⚡", rect.centerX(), cy - 48f * density, boltPaint)
            }

            // Sleek progress bar at bottom
            val barMargin = 32f * density
            val barRect = RectF(rect.left + barMargin, rect.bottom - 42f * density, rect.right - barMargin, rect.bottom - 32f * density)
            val barBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.FILL
                color = 0x22FFFFFF
            }
            canvas.drawRoundRect(barRect, 5f * density, 5f * density, barBgPaint)

            val fillWidth = barRect.width() * (batteryInfo.percentage / 100f)
            val fillRect = RectF(barRect.left, barRect.top, barRect.left + fillWidth, barRect.bottom)
            val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.FILL
                shader = LinearGradient(
                    fillRect.left, fillRect.top, fillRect.right, fillRect.bottom,
                    intArrayOf(colors.primary, colors.secondary),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRoundRect(fillRect, 5f * density, 5f * density, fillPaint)
        } else {
            // MEDIUM or LARGE: Sleek horizontal layout with modern typography
            val padX = 36f * density
            val pctPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.LEFT
                textSize = 76f * density
                color = Color.WHITE
            }

            val yTop = rect.top + 90f * density
            if (config.showPercentage) {
                canvas.drawText("${batteryInfo.percentage}%", rect.left + padX, yTop, pctPaint)
            }

            // Status label
            val statusPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                textAlign = Paint.Align.LEFT
                textSize = 20f * density
                color = if (batteryInfo.isCharging) 0xFF34D399.toInt() else 0xFF94A3B8.toInt()
            }
            val statusText = when {
                batteryInfo.isFullyCharged -> "✓ Fully Charged"
                batteryInfo.isCharging -> "⚡ Charging (${batteryInfo.plugged.displayText})"
                batteryInfo.isExtreme -> "⚠ Critical Battery"
                batteryInfo.isCritical -> "⚠ Very Low Battery"
                batteryInfo.isLow -> "Low Battery"
                else -> "On Battery"
            }
            canvas.drawText(statusText, rect.left + padX, yTop + 34f * density, statusPaint)

            // Sleek horizontal progress bar
            val barY = yTop + 65f * density
            val barRect = RectF(rect.left + padX, barY, rect.right - padX, barY + 12f * density)
            val barBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.FILL
                color = 0x25FFFFFF
            }
            canvas.drawRoundRect(barRect, 6f * density, 6f * density, barBg)

            val fillWidth = barRect.width() * (batteryInfo.percentage / 100f)
            val fillRect = RectF(barRect.left, barRect.top, barRect.left + fillWidth, barRect.bottom)
            val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.FILL
                shader = LinearGradient(
                    fillRect.left, fillRect.top, fillRect.right, fillRect.bottom,
                    intArrayOf(colors.primary, colors.secondary),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRoundRect(fillRect, 6f * density, 6f * density, fillPaint)

            // Telemetry badges (Temp, Voltage, Health)
            if (size == WidgetSize.LARGE || height > 450) {
                val statsY = barY + 45f * density
                drawTelemetryBadges(canvas, rect.left + padX, statsY, rect.right - padX, batteryInfo, density)
            }
        }
    }

    // =========================================================================
    // SHARED OVERLAY & BADGES
    // =========================================================================
    private fun drawContentOverlay(
        canvas: Canvas,
        rect: RectF,
        config: WidgetConfig,
        batteryInfo: BatteryInfo,
        colors: ResolvedColors,
        density: Float,
        size: WidgetSize
    ) {
        val cx = rect.centerX()
        val cy = rect.centerY()

        if (size == WidgetSize.SMALL) {
            // Percentage
            if (config.showPercentage) {
                val pctPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    textAlign = Paint.Align.CENTER
                    textSize = 68f * density
                    color = Color.WHITE
                    setShadowLayer(10f * density, 0f, 3f * density, 0xCC000000.toInt())
                }
                canvas.drawText("${batteryInfo.percentage}%", cx, cy + 20f * density, pctPaint)
            }

            // Charging bolt
            if (batteryInfo.isCharging && config.showChargingIcon) {
                val boltPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    textAlign = Paint.Align.CENTER
                    textSize = 28f * density
                    color = 0xFFFACC15.toInt()
                }
                canvas.drawText("⚡", cx, cy - 40f * density, boltPaint)
            } else if (batteryInfo.isFullyCharged && config.showStatusText) {
                val statusPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    textAlign = Paint.Align.CENTER
                    textSize = 18f * density
                    color = 0xFF4ADE80.toInt()
                }
                canvas.drawText("✓ FULL", cx, rect.bottom - 24f * density, statusPaint)
            }
        } else {
            // MEDIUM or LARGE: Left side percentage, Right side telemetry/status
            val padX = 36f * density
            val left = rect.left + padX

            // Brand header badge
            val brandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.LEFT
                textSize = 15f * density
                color = 0x99FFFFFF.toInt()
                letterSpacing = 0.08f
            }
            canvas.drawText("HITECH BATTERY", left, rect.top + 42f * density, brandPaint)

            // Large percentage
            val pctPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.LEFT
                textSize = 76f * density
                color = Color.WHITE
                setShadowLayer(12f * density, 0f, 4f * density, 0xBB000000.toInt())
            }
            if (config.showPercentage) {
                canvas.drawText("${batteryInfo.percentage}%", left, rect.top + 118f * density, pctPaint)
            }

            // Status pill/text
            val statusY = rect.top + 155f * density
            val statusPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.LEFT
                textSize = 20f * density
                color = when {
                    batteryInfo.isFullyCharged -> 0xFF34D399.toInt()
                    batteryInfo.isCharging -> 0xFF38BDF8.toInt()
                    batteryInfo.isExtreme -> 0xFFEF4444.toInt()
                    batteryInfo.isCritical -> 0xFFF87171.toInt()
                    batteryInfo.isLow -> 0xFFFB923C.toInt()
                    else -> 0xFFE2E8F0.toInt()
                }
            }

            val statusText = when {
                batteryInfo.isFullyCharged -> "✓ Fully Charged"
                batteryInfo.isCharging -> "⚡ Charging (${batteryInfo.plugged.displayText})"
                batteryInfo.isExtreme -> "⚠ Critical Battery!"
                batteryInfo.isCritical -> "⚠ Low Battery (Plug in)"
                batteryInfo.isLow -> "Low Battery (${batteryInfo.percentage}%)"
                else -> "Discharging (${batteryInfo.plugged.displayText})"
            }
            canvas.drawText(statusText, left, statusY, statusPaint)

            // Additional details for Large widget
            if (size == WidgetSize.LARGE || rect.height() > 400f * density) {
                drawTelemetryBadges(canvas, left, statusY + 40f * density, rect.right - padX, batteryInfo, density)
            }
        }
    }

    private fun drawSideMetrics(
        canvas: Canvas,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        config: WidgetConfig,
        batteryInfo: BatteryInfo,
        colors: ResolvedColors,
        density: Float,
        size: WidgetSize
    ) {
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.LEFT
            textSize = 15f * density
            color = 0x88FFFFFF.toInt()
            letterSpacing = 0.08f
        }
        canvas.drawText("HITECH BATTERY", left, top + 42f * density, titlePaint)

        val pctPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.LEFT
            textSize = 68f * density
            color = Color.WHITE
            setShadowLayer(8f * density, 0f, 2f * density, 0x99000000.toInt())
        }
        if (config.showPercentage) {
            canvas.drawText("${batteryInfo.percentage}%", left, top + 112f * density, pctPaint)
        }

        val statusPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.LEFT
            textSize = 20f * density
            color = if (batteryInfo.isCharging) 0xFF38BDF8.toInt() else 0xFFE2E8F0.toInt()
        }
        val statusText = when {
            batteryInfo.isFullyCharged -> "✓ Fully Charged"
            batteryInfo.isCharging -> "⚡ Charging"
            batteryInfo.isExtreme -> "⚠ Critical"
            batteryInfo.isLow -> "Low Battery"
            else -> "On Battery"
        }
        canvas.drawText(statusText, left, top + 148f * density, statusPaint)

        // Telemetry row
        val metricsY = top + 185f * density
        val metricPillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textAlign = Paint.Align.LEFT
            textSize = 15f * density
            color = 0xAAFFFFFF.toInt()
        }
        val metricLine = "${batteryInfo.temperatureCelsius}°C  •  ${String.format("%.1f", batteryInfo.voltageVolts)}V  •  ${batteryInfo.health.displayText}"
        canvas.drawText(metricLine, left, metricsY, metricPillPaint)
    }

    private fun drawTelemetryBadges(
        canvas: Canvas,
        left: Float,
        top: Float,
        right: Float,
        batteryInfo: BatteryInfo,
        density: Float
    ) {
        val badgeHeight = 54f * density
        val badgeWidth = (right - left - (16f * density * 2)) / 3f

        val badges = listOf(
            Pair("TEMP", "${batteryInfo.temperatureCelsius}°C"),
            Pair("VOLTAGE", "${String.format("%.2f", batteryInfo.voltageVolts)}V"),
            Pair("HEALTH", batteryInfo.health.displayText)
        )

        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = 0x221E293B.toInt()
        }
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 1f * density
            color = 0x25FFFFFF
        }
        val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            textSize = 12f * density
            color = 0x88FFFFFF.toInt()
        }
        val valPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            textSize = 17f * density
            color = Color.WHITE
        }

        badges.forEachIndexed { i, badge ->
            val bLeft = left + (i * (badgeWidth + 16f * density))
            val bRect = RectF(bLeft, top, bLeft + badgeWidth, top + badgeHeight)
            canvas.drawRoundRect(bRect, 14f * density, 14f * density, bgPaint)
            canvas.drawRoundRect(bRect, 14f * density, 14f * density, borderPaint)

            canvas.drawText(badge.first, bRect.centerX(), top + 20f * density, labelPaint)
            canvas.drawText(badge.second, bRect.centerX(), top + 42f * density, valPaint)
        }
    }
}
