/*
 * Copyright (C) 2016 Felipe Joglar Santos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fjoglar.etsitnoticias.view.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ProgressBar;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.fjoglar.etsitnoticias.R;

/**
 * This class generates a Material Design style ProgressBar for use in Android devices
 * pre Lollipop (v21).
 *
 * Custom view based on Mark Allison's article:
 * https://blog.stylingandroid.com/material-progressbar/
 */
public class MaterialProgressBar extends ProgressBar {
    private static final int INDETERMINATE_MAX = 1000;
    private static final String SECONDARY_PROGRESS = "secondaryProgress";
    private static final String PROGRESS = "progress";

    private Animator animator = null;

    private final int duration;

    public MaterialProgressBar(Context context) {
        this(context, null, -1);
    }

    public MaterialProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public MaterialProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MaterialProgressBar, defStyleAttr, 0);
        int backgroundColour;
        int progressColour;
        try {
            backgroundColour = ta.getColor(R.styleable.MaterialProgressBar_backgroundColour, 0);
            progressColour = ta.getColor(R.styleable.MaterialProgressBar_progressColour, 0);
            int defaultDuration = context.getResources().getInteger(android.R.integer.config_mediumAnimTime);
            duration = ta.getInteger(R.styleable.MaterialProgressBar_duration, defaultDuration);
        } finally {
            ta.recycle();
        }
        Resources resources = context.getResources();
        //noinspection deprecation
        setProgressDrawable(resources.getDrawable(android.R.drawable.progress_horizontal));
        createIndeterminateProgressDrawable(backgroundColour, progressColour);
        setMax(INDETERMINATE_MAX);
        super.setIndeterminate(false);
        this.setIndeterminate(true);
    }

    private void createIndeterminateProgressDrawable(@ColorInt int backgroundColour, @ColorInt int progressColour) {
        LayerDrawable layerDrawable = (LayerDrawable) getProgressDrawable();
        if (layerDrawable != null) {
            layerDrawable.mutate();
            layerDrawable.setDrawableByLayerId(android.R.id.background, createShapeDrawable(backgroundColour));
            layerDrawable.setDrawableByLayerId(android.R.id.progress, createClipDrawable(backgroundColour));
            layerDrawable.setDrawableByLayerId(android.R.id.secondaryProgress, createClipDrawable(progressColour));
        }
    }

    private Drawable createClipDrawable(@ColorInt int colour) {
        ShapeDrawable shapeDrawable = createShapeDrawable(colour);
        return new ClipDrawable(shapeDrawable, Gravity.START, ClipDrawable.HORIZONTAL);
    }

    private ShapeDrawable createShapeDrawable(@ColorInt int colour) {
        ShapeDrawable shapeDrawable = new ShapeDrawable();
        setColour(shapeDrawable, colour);
        return shapeDrawable;
    }

    private void setColour(ShapeDrawable drawable, int colour) {
        Paint paint = drawable.getPaint();
        paint.setColor(colour);
    }

    @Override
    public synchronized void setIndeterminate(boolean indeterminate) {
        if (isStarted()) {
            return;
        }
        animator = createIndeterminateAnimator();
        animator.setTarget(this);
        animator.start();
    }

    private boolean isStarted() {
        return animator != null && animator.isStarted();
    }

    private Animator createIndeterminateAnimator() {
        AnimatorSet set = new AnimatorSet();
        Animator progressAnimator = getAnimator(SECONDARY_PROGRESS, new DecelerateInterpolator());
        Animator secondaryProgressAnimator = getAnimator(PROGRESS, new AccelerateInterpolator());
        set.playTogether(progressAnimator, secondaryProgressAnimator);
        set.setDuration(duration);
        return set;
    }

    @NonNull
    private ObjectAnimator getAnimator(String propertyName, Interpolator interpolator) {
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(this, propertyName, 0, INDETERMINATE_MAX);
        progressAnimator.setInterpolator(interpolator);
        progressAnimator.setDuration(duration);
        progressAnimator.setRepeatMode(ValueAnimator.RESTART);
        progressAnimator.setRepeatCount(ValueAnimator.INFINITE);
        return progressAnimator;
    }
}
