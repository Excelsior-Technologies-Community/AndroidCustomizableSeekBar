package com.ext.adavancedseekbar

import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.ext.advance_seekbar.AdvancedSeekBar
import com.ext.advance_seekbar.RangeSeekBar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup bubble behavior for seekbars (show only when dragging)
        setupBubbleBehavior(findViewById(R.id.roundedSeekBar))
        setupBubbleBehavior(findViewById(R.id.verticalSeekBar))
        setupBubbleBehavior(findViewById(R.id.emojiSeekBar))
        setupBubbleBehavior(findViewById(R.id.iconSeekBar))
        setupBubbleBehavior(findViewById(R.id.tickSeekBar))

        // Bubble seekbars - show bubble on drag
        setupBubbleBehavior(findViewById(R.id.bubbleSeekBar))
        setupBubbleBehavior(findViewById(R.id.customBubbleSeekBar))

        // Read-only seekbar → no interaction, no bubble
        findViewById<AdvancedSeekBar>(R.id.readOnlySeekBar).apply {
            showBubble = false
        }

        // RangeSeekBar → handles its own bubble internally
        findViewById<RangeSeekBar>(R.id.rangeSeekBar)
    }

    /**
     * Setup bubble to show only when user is dragging
     */
    private fun setupBubbleBehavior(seekBar: AdvancedSeekBar) {
        val shouldShowBubble = seekBar.showBubble
        seekBar.showBubble = false // Start hidden

        seekBar.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN,
                MotionEvent.ACTION_MOVE -> {
                    if (shouldShowBubble) {
                        seekBar.showBubble = true
                        seekBar.invalidate()
                    }
                }

                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {
                    seekBar.showBubble = false
                    seekBar.invalidate()
                }
            }
            false // Let the seekbar handle the touch event
        }
    }
}