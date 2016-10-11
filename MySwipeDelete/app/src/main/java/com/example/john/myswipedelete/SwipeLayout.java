package com.example.john.myswipedelete;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by John on 2016/9/27.
 */
public class SwipeLayout extends FrameLayout {
    private View contentView;
    private View deleteView;
    private int deleteHeight;
    private int deleteWidth;
    private int contentWidth;
    private ViewDragHelper viewDragHelper;
    private SwipeState currentState = SwipeState.Close;
    private float downX, downY;


    enum SwipeState {
        Open, Close;
    }
    public SwipeLayout(Context context) {
        super(context);
        init();
    }

    private void init() {
        viewDragHelper = ViewDragHelper.create(this, callback);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
		//交给ViewDraghelper处理看是否需要拦截
        boolean result = viewDragHelper.shouldInterceptTouchEvent(ev);
		//判断现在是否可以横向滑动
		//如果不是同一个对象则清除以前打开的对象
        if (!SwipeLayoutManager.getInstance().isShouldSwipe(this)) {
            SwipeLayoutManager.getInstance().clearCurrentLayout();
            result = true;
        }
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!SwipeLayoutManager.getInstance().isShouldSwipe(this)) {
            requestDisallowInterceptTouchEvent(true);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();
                float delatX = moveX - downX;
                float delatY = moveY - downY;
                if (Math.abs(delatX) > Math.abs(delatY)) {
                    requestDisallowInterceptTouchEvent(true);

                }
				
                downX = moveX;
                downY = moveY;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
		//可以处理的子View
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == contentView || child == deleteView;
        }
		//滑动范围
        @Override
        public int getViewHorizontalDragRange(View child) {
            return deleteWidth;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == contentView) {
                if (left > 0) {
                    left = 0;
                }
                if (left < -deleteWidth) {
                    left = -deleteWidth;
                }
            } else if (child == deleteView) {
                if (left > contentWidth) {
                    left = contentWidth;
                }
                if (left < (contentWidth - deleteWidth)) {
                    left = contentWidth - deleteWidth;
                }
            }
            return left;

        }
		
		//伴随滑动
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (changedView == contentView) {
                deleteView.layout(deleteView.getLeft() + dx, deleteView.getTop()+ dy, deleteView.getRight() + dx, deleteView.getBottom()+ dy);
            } else if (changedView == deleteView) {
                contentView.layout(contentWidth + dx, contentView.getTop() + dy, contentView.getRight() + dx, contentView.getBottom() + dy);
            }
            if (contentView.getLeft() == 0 && currentState != SwipeState.Close) {
                currentState = SwipeState.Close;
                if (listener != null) {
                    listener.onClose(getTag());
                }
                SwipeLayoutManager.getInstance().clearCurrentLayout();
            } else if (contentView.getLeft() == -deleteWidth && currentState == SwipeState.Close) {
                currentState = SwipeState.Open;
                if (listener != null) {
                    listener.onOpen(getTag());
                }
                SwipeLayoutManager.getInstance().setCurrentLayout(SwipeLayout.this);
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (contentView.getLeft() < -deleteWidth/ 2) {
                open();
            } else {
                close();
            }
        }
    };

    public void close() {
        viewDragHelper.smoothSlideViewTo(contentView, 0, contentView.getTop());
        ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
    }

    private void open() {
        viewDragHelper.smoothSlideViewTo(contentView, -deleteWidth, contentView.getTop());
        ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);

    }
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private OnSwipeSateChangelistener listener;

    public void setOnSwipeSateChangelistener(OnSwipeSateChangelistener listener) {
        this.listener = listener;
    }
    public interface OnSwipeSateChangelistener {
        void onOpen(Object tag);

        void onClose(Object tag);
    }


    public SwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentView = getChildAt(0);
        deleteView = getChildAt(1);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        deleteHeight = deleteView.getMeasuredHeight();
        deleteWidth = deleteView.getMeasuredWidth();
        contentWidth = contentView.getMeasuredWidth();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        contentView.layout(0, 0, contentWidth, deleteHeight);
        deleteView.layout(contentView.getRight(), 0, contentView.getRight() + deleteWidth, deleteHeight);
    }
}


