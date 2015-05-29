package com.dolphin.thegigisup.helpers;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;
import com.dolphin.thegigisup.R;

/**
 * A custom ScrollView that has a parallax effect on it
 *
 * @author Team Dolphin
 */
public class CustomParallaxScrollView extends ScrollView {

    private static final float DEFAULT_SCROLL_FACTOR =  0.6f;
    private float scrollFactor = DEFAULT_SCROLL_FACTOR;

    private int backgroundResID;
    private View backgroundView;

    // Initialise default constructors for the view
    public CustomParallaxScrollView(Context context) {
        super(context);
        initView(context, null, 0);
    }

    public CustomParallaxScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public CustomParallaxScrollView(Context context,
                                    AttributeSet attrs,
                                    int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs, defStyle);
    }

    /**
     * Get the additional attributes that are needed to make this class
     *
     * @param context  The context used to create this view.
     * @param attrs    The attribute to be retrieved.
     * @param defStyle Resource identifier for default values. 0 if none.
     */

    private void initView(Context context, AttributeSet attrs, int defStyle) {

        if (isInEditMode()) {
            return;
        }

        if (attrs != null) {

            TypedArray values = context.obtainStyledAttributes(attrs,
                    R.styleable.CustomParallaxScrollView, defStyle, 0);
            backgroundResID = values.getResourceId(
                    R.styleable.CustomParallaxScrollView_backgroundView, 0);
            scrollFactor = values.getFloat(
                    R.styleable.CustomParallaxScrollView_scrollFactor,
                    DEFAULT_SCROLL_FACTOR);
        }

        setVerticalFadingEdgeEnabled(false);

    }

    /**
     * Set the view that will be subject to the parallax effect
     *
     * @param resID Identifer for the resource
     */

    public void setBackgroundResID(int resID) {
        backgroundView = findViewById(resID);
    }

    /**
     * Define the pace the view scrolls relative to the scrollview
     *
     * @param Factor A factor defining the scroll pace
     */
    public void setScrollFactor(float Factor) {
        scrollFactor = Factor;
    }

    /**
     * Override the onLayout method to make the scroll offset smoother
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            // On layout changes scroll offset might have changed.
            // Setting a new Y here removes any background view hiccups.
            translateBackgroundView(getScrollY());
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        // If resource set in XML,child view will be available after view attach
        // to the view hierarchy.
        if (backgroundResID > 0 && backgroundView == null) {
            backgroundView = findViewById(backgroundResID);
            backgroundResID = 0;
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        translateBackgroundView(t);

    }

    /**
     * Translate the background view using a y value and the scroll factor
     *
     * @param y An integer scroll value y
     */
    private void translateBackgroundView(int y) {
        if (backgroundView != null) {
            int translationY = (int) (y * scrollFactor);
                backgroundView.setTranslationY(translationY);
        }
    }

}
