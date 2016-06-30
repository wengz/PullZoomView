package com.ooxx.example;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

/**
 * 一个 ScrollView，当滑动到顶部时继续向下拉则放大 ScrollView 唯一的子View的第一个子View的高度
 */
public class PullZoomView extends ScrollView{

    private View mViewToScale;

    public PullZoomView(Context context) {
        super(context);
    }

    public PullZoomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullZoomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mViewToScale = ((ViewGroup)getChildAt(0)).getChildAt(0);
    }

    /**
     * 能否继续向上滑动
     */
    public boolean canScrollUp (){
        return canScrollVertically(-1);
    }


    private float mFraction = 0.6f;
    private float mInitX;
    private float mInitY;
    private float mCurX;
    private float mCurY;
    private int mOrigHeight;

    private ViewGroup.LayoutParams mLayoutParams;

    private Status mStatus = Status.normal;

    private enum Status {
        normal,
        pull_zoom
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!canScrollUp()){
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mInitX = ev.getX();
                    mInitY = ev.getY();
                    mOrigHeight = mViewToScale.getHeight();
                    mStatus = Status.pull_zoom;
                    return true;

                case MotionEvent.ACTION_MOVE:
                    if (mStatus != Status.pull_zoom){
                        break;
                    }

                    mCurX = ev.getX();
                    mCurY = ev.getY();
                    if ( mCurY - mInitY < 0){
                        mStatus = Status.normal;
                        break;
                    }
                    mLayoutParams = mViewToScale.getLayoutParams();
                    mLayoutParams.height = mOrigHeight + (int) ((mCurY - mInitY) * mFraction);
                    mViewToScale.setLayoutParams(mLayoutParams);
                    return true;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (mStatus != Status.pull_zoom){
                        break;
                    }
                    mLayoutParams = mViewToScale.getLayoutParams();
                    mLayoutParams.height = mOrigHeight;
                    mViewToScale.setLayoutParams(mLayoutParams);
                    mStatus = Status.normal;
                    return true;
            }
        }
        return super.onTouchEvent(ev);
    }
}
