package com.ext.advance_seekbar

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.max
import kotlin.math.min

class AdvancedSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    /* ---------------- ATTRIBUTES ---------------- */
    var min = 0
    var max = 100
    var step = 1

    var trackColor = Color.LTGRAY
    var trackThickness = 20f

    var progressColor = Color.BLACK
    var progressColorEnd: Int? = null

    var thumbColor = Color.BLUE
    var thumbRadius = 28f
    var thumbIconRes: Int? = null

    var emojiMode = false
    var emojiLow = "😞"
    var emojiMid = "😐"
    var emojiHigh = "😄"
    var emojiTextSize = 48f

    var showBubble = false
    var readOnly = false
    var isVertical = false

    var showTicks = false
    var tickColor = Color.DKGRAY
    var tickRadius = 8f

    // Bubble customization
    var bubbleBackgroundColor = Color.parseColor("#333333")
    var bubbleTextColor = Color.WHITE
    var bubbleTextSize = 42f
    var bubbleCornerRadius = 12f
    var bubbleWidth = 80f
    var bubbleHeight = 50f

    /* ---------------- STATE ---------------- */
    var progress = 0
        set(value) {
            field = max(min, min(max, value))
            invalidate()
        }

    /* ---------------- PAINTS ---------------- */
    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bubbleTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var thumbBitmap: Bitmap? = null

    /* ---------------- INIT ---------------- */
    init {
        attrs?.let {
            val a = context.obtainStyledAttributes(it, R.styleable.AdvancedSeekBar)

            min = a.getInt(R.styleable.AdvancedSeekBar_min, min)
            max = a.getInt(R.styleable.AdvancedSeekBar_max, max)
            step = a.getInt(R.styleable.AdvancedSeekBar_step, step)

            trackColor = a.getColor(R.styleable.AdvancedSeekBar_trackColor, trackColor)
            trackThickness = a.getDimension(R.styleable.AdvancedSeekBar_trackThickness, trackThickness)

            progressColor = a.getColor(R.styleable.AdvancedSeekBar_progressColor, progressColor)
            val endColor = a.getColor(R.styleable.AdvancedSeekBar_progressColorEnd, -1)
            if (endColor != -1) progressColorEnd = endColor

            thumbColor = a.getColor(R.styleable.AdvancedSeekBar_thumbColor, thumbColor)
            thumbRadius = a.getDimension(R.styleable.AdvancedSeekBar_thumbRadius, thumbRadius)

            // Read android:progress attribute
            progress = a.getInt(R.styleable.AdvancedSeekBar_android_progress, progress)

            emojiMode = a.getBoolean(R.styleable.AdvancedSeekBar_emojiMode, false)
            emojiLow = a.getString(R.styleable.AdvancedSeekBar_emojiLow) ?: emojiLow
            emojiMid = a.getString(R.styleable.AdvancedSeekBar_emojiMid) ?: emojiMid
            emojiHigh = a.getString(R.styleable.AdvancedSeekBar_emojiHigh) ?: emojiHigh
            emojiTextSize = a.getDimension(R.styleable.AdvancedSeekBar_emojiTextSize, emojiTextSize)

            showBubble = a.getBoolean(R.styleable.AdvancedSeekBar_showBubble, false)
            readOnly = a.getBoolean(R.styleable.AdvancedSeekBar_readOnly, false)
            isVertical = a.getBoolean(R.styleable.AdvancedSeekBar_isVertical, false)

            showTicks = a.getBoolean(R.styleable.AdvancedSeekBar_showTicks, false)
            tickColor = a.getColor(R.styleable.AdvancedSeekBar_tickColor, tickColor)
            tickRadius = a.getDimension(R.styleable.AdvancedSeekBar_tickRadius, tickRadius)

            // Bubble attributes
            bubbleBackgroundColor = a.getColor(R.styleable.AdvancedSeekBar_bubbleBackgroundColor, bubbleBackgroundColor)
            bubbleTextColor = a.getColor(R.styleable.AdvancedSeekBar_bubbleTextColor, bubbleTextColor)
            bubbleTextSize = a.getDimension(R.styleable.AdvancedSeekBar_bubbleTextSize, bubbleTextSize)
            bubbleCornerRadius = a.getDimension(R.styleable.AdvancedSeekBar_bubbleCornerRadius, bubbleCornerRadius)
            bubbleWidth = a.getDimension(R.styleable.AdvancedSeekBar_bubbleWidth, bubbleWidth)
            bubbleHeight = a.getDimension(R.styleable.AdvancedSeekBar_bubbleHeight, bubbleHeight)

            val icon = a.getResourceId(R.styleable.AdvancedSeekBar_thumbIcon, -1)
            if (icon != -1) {
                thumbIconRes = icon
                try {
                    thumbBitmap = BitmapFactory.decodeResource(resources, icon)
                } catch (e: Exception) { e.printStackTrace() }
            }

            a.recycle()
        }

        // Track paint
        trackPaint.color = trackColor
        trackPaint.strokeWidth = trackThickness
        trackPaint.strokeCap = Paint.Cap.ROUND
        trackPaint.style = Paint.Style.STROKE

        // Progress paint
        progressPaint.strokeWidth = trackThickness
        progressPaint.strokeCap = Paint.Cap.ROUND
        progressPaint.style = Paint.Style.STROKE

        // Thumb paint
        thumbPaint.color = thumbColor
        thumbPaint.style = Paint.Style.FILL

        // Tick paint
        tickPaint.color = tickColor
        tickPaint.style = Paint.Style.FILL

        // Text paint
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.color = Color.BLACK

        // Bubble paint
        bubblePaint.color = bubbleBackgroundColor
        bubblePaint.style = Paint.Style.FILL
        bubblePaint.setShadowLayer(8f, 0f, 4f, Color.parseColor("#40000000"))

        bubbleTextPaint.color = bubbleTextColor
        bubbleTextPaint.textSize = bubbleTextSize
        bubbleTextPaint.textAlign = Paint.Align.CENTER
        bubbleTextPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    /* ---------------- DRAW ---------------- */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()
        val center = if (isVertical) w / 2 else h / 2

        val ratio = (progress - min).toFloat() / (max - min)
        val paddingAdjust = trackThickness / 2 + thumbRadius

        // Track
        if (isVertical) {
            canvas.drawLine(center, paddingAdjust, center, h - paddingAdjust, trackPaint)
        } else {
            canvas.drawLine(paddingAdjust, center, w - paddingAdjust, center, trackPaint)
        }

        // Progress
        val progressStart = if (isVertical) h - paddingAdjust else paddingAdjust
        val progressEnd = if (isVertical) h - paddingAdjust - ratio * (h - 2 * paddingAdjust)
        else paddingAdjust + ratio * (w - 2 * paddingAdjust)

        if (progressColorEnd != null) {
            progressPaint.shader = if (isVertical) {
                LinearGradient(0f, progressStart, 0f, progressEnd, progressColor, progressColorEnd!!, Shader.TileMode.CLAMP)
            } else {
                LinearGradient(progressStart, 0f, progressEnd, 0f, progressColor, progressColorEnd!!, Shader.TileMode.CLAMP)
            }
        } else {
            progressPaint.shader = null
            progressPaint.color = progressColor
        }

        if (isVertical) {
            canvas.drawLine(center, progressEnd, center, progressStart, progressPaint)
        } else {
            canvas.drawLine(progressStart, center, progressEnd, center, progressPaint)
        }

        // Ticks
        if (showTicks) {
            val count = (max - min) / step
            for (i in 0..count) {
                val tickPos = i.toFloat() / count
                val x = if (isVertical) center else paddingAdjust + tickPos * (w - 2 * paddingAdjust)
                val y = if (isVertical) h - paddingAdjust - tickPos * (h - 2 * paddingAdjust) else center
                canvas.drawCircle(x, y, tickRadius, tickPaint)
            }
        }

        // Thumb
        val tx = if (isVertical) center else progressEnd
        val ty = if (isVertical) progressEnd else center

        // Bubble / Emoji
        if (showBubble || emojiMode) {
            if (emojiMode) {
                textPaint.textSize = emojiTextSize
                val emoji = when {
                    progress <= max / 3 -> emojiLow
                    progress <= 2 * max / 3 -> emojiMid
                    else -> emojiHigh
                }
                canvas.drawText(emoji, tx, ty - 80f, textPaint)
            } else if (showBubble) {
                drawBubble(canvas, tx, ty - thumbRadius - 20f, progress.toString())
            }
        }

        // Draw thumb icon or circle
        thumbBitmap?.let { bitmap ->
            val thumbSize = (thumbRadius * 2).toInt()
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, thumbSize, thumbSize, true)
            canvas.drawBitmap(scaledBitmap, tx - thumbRadius, ty - thumbRadius, null)
        } ?: run {
            canvas.drawCircle(tx, ty, thumbRadius, thumbPaint)
        }
    }

    private fun drawBubble(canvas: Canvas, x: Float, y: Float, text: String) {
        val arrowSize = 10f

        val bubbleRect = RectF(
            x - bubbleWidth / 2,
            y - bubbleHeight,
            x + bubbleWidth / 2,
            y
        )

        canvas.drawRoundRect(bubbleRect, bubbleCornerRadius, bubbleCornerRadius, bubblePaint)

        val arrowPath = Path().apply {
            moveTo(x - arrowSize, y)
            lineTo(x, y + arrowSize)
            lineTo(x + arrowSize, y)
            close()
        }
        canvas.drawPath(arrowPath, bubblePaint)

        val textY = y - bubbleHeight / 2 + 16f
        canvas.drawText(text, x, textY, bubbleTextPaint)
    }

    /* ---------------- TOUCH ---------------- */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (readOnly) return false

        val w = width.toFloat()
        val h = height.toFloat()
        val paddingAdjust = trackThickness / 2 + thumbRadius

        when (event.action) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE -> {
                val raw = if (isVertical) {
                    val adjustedY = max(paddingAdjust, min(h - paddingAdjust, event.y))
                    ((h - adjustedY - paddingAdjust) / (h - 2 * paddingAdjust) * (max - min) + min).toInt()
                } else {
                    val adjustedX = max(paddingAdjust, min(w - paddingAdjust, event.x))
                    ((adjustedX - paddingAdjust) / (w - 2 * paddingAdjust) * (max - min) + min).toInt()
                }

                progress = ((raw - min) / step) * step + min
                invalidate()
            }
        }
        return true
    }
}
