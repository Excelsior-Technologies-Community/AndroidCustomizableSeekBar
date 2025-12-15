# **AndroidCustomizableSeekBar**
---
A custom Android SeekBar library with advanced features, providing highly customizable **single-thumb** and **dual-thumb (range)** seek bars.
Includes **AdvancedSeekBar** for single progress selection and **RangeSeekBar** for range selection, with support for **bubbles, gradients, ticks, emojis, vertical orientation,** and more.
Fully configurable via XML — no code changes needed for styling!

---
✨ Features
- Single-thumb (AdvancedSeekBar) or dual-thumb (RangeSeekBar) modes
- Customizable track, progress (with optional gradient), and thumbs
- Numeric or emoji value bubbles (shown during drag)
- Tick marks for discrete steps
- Read-only mode
- Horizontal or vertical orientation
- Modern UI with shadows and customizable bubble design
- Fully XML-configurable attributes


# **Preview**
---
<p align="center">
  <img src="https://github.com/user-attachments/assets/cc1cdd9a-37be-42aa-8aeb-ec654661af20"
       alt="Demo GIF"
       width="200">


</p>

---

##  **⚡ Installation**

**Step 1:** Add JitPack repository to your root `build.gradle`:

```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

**Step 2:** Add dependency to your app module `build.gradle`:
```
dependencies {
	        implementation 'com.github.Excelsior-Technologies-Community:AndroidCustomizableSeekBar:1.0.0'

 }
```

## **📦 Usage**



**AdvancedSeekBar (Single Thumb)**

```
<com.ext.advance_seekbar.AdvancedSeekBar
    android:id="@+id/advancedSeekBar"
    android:layout_width="match_parent"
    android:layout_height="140dp"
    android:paddingStart="20dp"
    android:paddingEnd="20dp"
    android:paddingTop="20dp"
    android:paddingBottom="20dp"
    android:progress="60"
    app:trackColor="#E0E0E0"
    app:progressColor="#E91E63"
    app:progressColorEnd="#FF4081"
    app:trackThickness="14dp"
    app:thumbColor="#E91E63"
    app:thumbRadius="26dp"
    app:min="0"
    app:max="100"
    app:showBubble="true"
    app:readOnly="false"
    app:showTicks="true"
    app:tickColor="#B0B0B0"
    app:tickRadius="8dp"
    app:emojiMode="false"
    app:bubbleBackgroundColor="#333333"
    app:bubbleTextColor="#FFFFFF"
    app:bubbleTextSize="42sp"
    app:bubbleCornerRadius="12dp" />
```

 **RangeSeekBar (Dual Thumb)**
 
```
 <com.ext.advance_seekbar.RangeSeekBar
    android:id="@+id/rangeSeekBar"
    android:layout_width="match_parent"
    android:layout_height="140dp"
    android:paddingStart="20dp"
    android:paddingEnd="20dp"
    android:paddingTop="20dp"
    android:paddingBottom="20dp"
    app:rsb_min="0"
    app:rsb_max="100"
    app:rsb_leftProgress="20"
    app:rsb_rightProgress="80"
    app:rsb_trackColor="#E0E0E0"
    app:rsb_progressColor="#E91E63"
    app:rsb_progressColorEnd="#FF4081"
    app:rsb_thumbColor="#E91E63"
    app:rsb_thumbRadius="26dp"
    app:rsb_showBubble="true"
    app:rsb_bubbleBackgroundColor="#333333"
    app:rsb_bubbleTextColor="#FFFFFF"
    app:rsb_bubbleTextSize="42sp"
    app:rsb_bubbleCornerRadius="12dp" />
```

**In Kotlin code**
**AdvancedSeekBar**
```
val seekBar = findViewById<AdvancedSeekBar>(R.id.advancedSeekBar)

// Update programmatically
seekBar.progress = 75
val currentProgress = seekBar.progress
```

**RangeSeekBar**
```
Kotlinval rangeSeekBar = findViewById<RangeSeekBar>(R.id.rangeSeekBar)

val left = rangeSeekBar.leftProgress
val right = rangeSeekBar.rightProgress

rangeSeekBar.leftProgress = 30
rangeSeekBar.rightProgress = 70
```

**attrs file:**

```
<resources>

    <!-- AdvancedSeekBar Custom Attributes -->
    <declare-styleable name="AdvancedSeekBar">
        <attr name="min" format="integer"/>
        <attr name="max" format="integer"/>
        <attr name="step" format="integer"/>

        <attr name="trackColor" format="color"/>
        <attr name="trackThickness" format="dimension"/>

        <attr name="progressColor" format="color"/>
        <attr name="progressColorEnd" format="color"/>
        <attr name="useGradient" format="boolean"/>

        <attr name="thumbColor" format="color"/>
        <attr name="thumbRadius" format="dimension"/>
        <attr name="thumbIcon" format="reference"/>
        <attr name="thumbWidth" format="dimension" />
        <attr name="thumbHeight" format="dimension" />
        <attr name="emojiMode" format="boolean"/>
        <attr name="emojiLow" format="string"/>
        <attr name="emojiMid" format="string"/>
        <attr name="emojiHigh" format="string"/>
        <attr name="emojiTextSize" format="dimension"/>

        <attr name="showBubble" format="boolean"/>
        <attr name="readOnly" format="boolean"/>
        <attr name="isVertical" format="boolean"/>

        <attr name="showTicks" format="boolean"/>
        <attr name="tickColor" format="color"/>
        <attr name="tickRadius" format="dimension"/>

        <!-- Bubble Customization -->
        <attr name="bubbleBackgroundColor" format="color"/>
        <attr name="bubbleTextColor" format="color"/>
        <attr name="bubbleTextSize" format="dimension"/>
        <attr name="bubbleCornerRadius" format="dimension"/>
        <attr name="bubbleWidth" format="dimension"/>
        <attr name="bubbleHeight" format="dimension"/>
        <attr name="android:progress"/> 


    </declare-styleable>

    <!-- RangeSeekBar (Dual Thumb) Custom Attributes -->
    <declare-styleable name="RangeSeekBar">
        <attr name="rsb_min" format="integer"/>
        <attr name="rsb_max" format="integer"/>
        <attr name="rsb_leftProgress" format="integer"/>
        <attr name="rsb_rightProgress" format="integer"/>

        <attr name="rsb_trackColor" format="color"/>
        <attr name="rsb_progressColor" format="color"/>
        <attr name="rsb_progressColorEnd" format="color"/>
        <attr name="rsb_useGradient" format="boolean"/>

        <attr name="rsb_thumbColor" format="color"/>
        <attr name="rsb_thumbRadius" format="dimension"/>
        <attr name="rsb_showBubble" format="boolean"/>

        <!-- Bubble Customization for Range -->
        <attr name="rsb_bubbleBackgroundColor" format="color"/>
        <attr name="rsb_bubbleTextColor" format="color"/>
        <attr name="rsb_bubbleTextSize" format="dimension"/>
        <attr name="rsb_bubbleCornerRadius" format="dimension"/>
        <attr name="rsb_bubbleWidth" format="dimension"/>
        <attr name="rsb_bubbleHeight" format="dimension"/>
    </declare-styleable>

</resources>
```

## 📄 License
```
MIT License

Copyright (c) 2025 [Your Name or Organization]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
```
