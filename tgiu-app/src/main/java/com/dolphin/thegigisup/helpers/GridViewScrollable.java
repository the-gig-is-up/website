package com.dolphin.thegigisup.helpers;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * A scrollable gridview used to store selected seats
 *
 * @author Team Dolphin 19/04/2015.
 */
public class GridViewScrollable extends GridView {

    /**
     * Default gridView constructor
     *
     * @param context The current context
     */
    public GridViewScrollable(Context context) {
        super(context);
        init();
    }

    public GridViewScrollable(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GridViewScrollable(Context context,
                              AttributeSet attrs,
                              int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setSmoothScrollbarEnabled(true);
    }

    /**
     * Override the touch event on the scroll view to allow a scrollview
     * to be a parent to the gridScrollView
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev){
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }
}