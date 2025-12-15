package com.ext.advance_seekbar

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class RangeSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var min = 0
    var max = 100

    var leftProgress = 20
    var rightProgress = 80

    var trackColor = Color.LTGRAY
    var progressColor = Color.BLUE
    var progressColorEnd: Int? = null

    var thumbColor = Color.DKGRAY
    var thumbRadius = 28f
    var showBubble = true
    var trackThickness = 16f

    // Bubble customization
    var bubbleBackgroundColor = Color.parseColor("#333333")
    var bubbleTextColor = Color.WHITE
    var bubbleTextSize = 42f
    var bubbleCornerRadius = 12f
    var bubbleWidth = 80f
    var bubbleHeight = 50f

    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bubbleTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var draggingLeft = false
    private var draggingRight = false

    init {
        attrs?.let {
            val a = context.obtainStyledAttributes(it, R.styleable.RangeSeekBar)

            min = a.getInt(R.styleable.RangeSeekBar_rsb_min, min)
            max = a.getInt(R.styleable.RangeSeekBar_rsb_max, max)
            leftProgress = a.getInt(R.styleable.RangeSeekBar_rsb_leftProgress, leftProgress)
            rightProgress = a.getInt(R.styleable.RangeSeekBar_rsb_rightProgress, rightProgress)

            trackColor = a.getColor(R.styleable.RangeSeekBar_rsb_trackColor, trackColor)
            progressColor = a.getColor(R.styleable.RangeSeekBar_rsb_progressColor, progressColor)
            thumbColor = a.getColor(R.styleable.RangeSeekBar_rsb_thumbColor, thumbColor)
            thumbRadius = a.getDimension(R.styleable.RangeSeekBar_rsb_thumbRadius, thumbRadius)
            showBubble = a.getBoolean(R.styleable.RangeSeekBar_rsb_showBubble, true)

            val endColor = a.getColor(R.styleable.RangeSeekBar_rsb_progressColorEnd, -1)
            if (endColor != -1) progressColorEnd = endColor

            // Bubble customization
            bubbleBackgroundColor = a.getColor(R.styleable.RangeSeekBar_rsb_bubbleBackgroundColor, bubbleBackgroundColor)
            bubbleTextColor = a.getColor(R.styleable.RangeSeekBar_rsb_bubbleTextColor, bubbleTextColor)
            bubbleTextSize = a.getDimension(R.styleable.RangeSeekBar_rsb_bubbleTextSize, bubbleTextSize)
            bubbleCornerRadius = a.getDimension(R.styleable.RangeSeekBar_rsb_bubbleCornerRadius, bubbleCornerRadius)
            bubbleWidth = a.getDimension(R.styleable.RangeSeekBar_rsb_bubbleWidth, bubbleWidth)
            bubbleHeight = a.getDimension(R.styleable.RangeSeekBar_rsb_bubbleHeight, bubbleHeight)

            a.recycle()
        }

        // Track paint with rounded caps
        trackPaint.color = trackColor
        trackPaint.strokeWidth = trackThickness
        trackPaint.strokeCap = Paint.Cap.ROUND
        trackPaint.style = Paint.Style.STROKE

        // Progress paint with rounded caps
        progressPaint.strokeWidth = trackThickness
        progressPaint.strokeCap = Paint.Cap.ROUND
        progressPaint.style = Paint.Style.STROKE

        // Thumb paint with shadow
        thumbPaint.color = thumbColor
        thumbPaint.style = Paint.Style.FILL
        thumbPaint.setShadowLayer(10f, 0f, 4f, Color.parseColor("#40000000"))

        // Bubble paint
        bubblePaint.style = Paint.Style.FILL
        bubblePaint.setShadowLayer(12f, 0f, 6f, Color.parseColor("#50000000"))

        // Bubble text paint
        bubbleTextPaint.textAlign = Paint.Align.CENTER
        bubbleTextPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight = 140

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> widthSize
            else -> 300
        }

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> min(desiredHeight, heightSize)
            else -> desiredHeight
        }

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val centerY = height / 2f
        val startPadding = paddingStart.toFloat()
        val endPadding = paddingEnd.toFloat()
        val effectiveWidth = w - startPadding - endPadding

        val leftX = startPadding + effectiveWidth * (leftProgress - min) / (max - min)
        val rightX = startPadding + effectiveWidth * (rightProgress - min) / (max - min)

        // Track (rounded)
        canvas.drawLine(startPadding, centerY, w - endPadding, centerY, trackPaint)

        // Progress with gradient (rounded)
        progressPaint.shader = if (progressColorEnd != null) {
            LinearGradient(
                leftX, 0f,
                rightX, 0f,
                progressColor,
                progressColorEnd!!,
                Shader.TileMode.CLAMP
            )
        } else {
            progressPaint.color = progressColor
            null
        }
        canvas.drawLine(leftX, centerY, rightX, centerY, progressPaint)

        // Thumbs with shadow and highlight
        drawThumb(canvas, leftX, centerY)
        drawThumb(canvas, rightX, centerY)

        // Bubbles
        if (showBubble && (draggingLeft || draggingRight)) {
            if (draggingLeft) {
                drawBubble(canvas, leftX, centerY - 95f, leftProgress.toString())
            }
            if (draggingRight) {
                drawBubble(canvas, rightX, centerY - 95f, rightProgress.toString())
            }
        }
    }

    private fun drawThumb(canvas: Canvas, x: Float, y: Float) {
        // Draw main thumb circle with shadow
        canvas.drawCircle(x, y, thumbRadius, thumbPaint)

        // Inner highlight for 3D effect
        val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            alpha = 80
            style = Paint.Style.FILL
        }
        canvas.drawCircle(x - thumbRadius/4, y - thumbRadius/4, thumbRadius/3, highlightPaint)
    }

    private fun drawBubble(canvas: Canvas, x: Float, y: Float, text: String) {
        val arrowSize = 12f

        // Bubble rectangle
        val bubbleRect = RectF(
            x - bubbleWidth / 2,
            y - bubbleHeight,
            x + bubbleWidth / 2,
            y
        )

        // Update bubble paint color
        bubblePaint.color = bubbleBackgroundColor

        // Draw bubble background
        canvas.drawRoundRect(bubbleRect, bubbleCornerRadius, bubbleCornerRadius, bubblePaint)

        // Draw arrow (triangle pointing down)
        val arrowPath = Path().apply {
            moveTo(x - arrowSize, y)
            lineTo(x, y + arrowSize)
            lineTo(x + arrowSize, y)
            close()
        }
        canvas.drawPath(arrowPath, bubblePaint)

        // Update bubble text paint
        bubbleTextPaint.color = bubbleTextColor
        bubbleTextPaint.textSize = bubbleTextSize

        // Draw text
        val textY = y - bubbleHeight / 2 + bubbleTextSize / 3
        canvas.drawText(text, x, textY, bubbleTextPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val w = width.toFloat()
        val startPadding = paddingStart.toFloat()
        val endPadding = paddingEnd.toFloat()
        val effectiveWidth = w - startPadding - endPadding

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val leftX = startPadding + effectiveWidth * (leftProgress - min) / (max - min)
                val rightX = startPadding + effectiveWidth * (rightProgress - min) / (max - min)

                draggingLeft = abs(event.x - leftX) < abs(event.x - rightX)
                draggingRight = !draggingLeft
                invalidate()
            }

            MotionEvent.ACTION_MOVE -> {
                val adjustedX = max(startPadding, min(w - endPadding, event.x))
                val value = ((adjustedX - startPadding) / effectiveWidth * (max - min) + min).toInt()

                if (draggingLeft) {
                    leftProgress = min(value, rightProgress)
                } else if (draggingRight) {
                    rightProgress = max(value, leftProgress)
                }
                invalidate()
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                draggingLeft = false
                draggingRight = false
                invalidate()
            }
        }
        return true
    }
}