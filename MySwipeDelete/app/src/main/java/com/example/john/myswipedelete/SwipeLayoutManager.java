package com.example.john.myswipedelete;

/**
 * Created by John on 2016/9/27.
 */
public class SwipeLayoutManager {
    private static SwipeLayoutManager mInstance = new SwipeLayoutManager();
    private SwipeLayout currentLayout;

    public SwipeLayoutManager() {
    }

    public static SwipeLayoutManager getInstance() {

        return mInstance;
    }

    public void setCurrentLayout(SwipeLayout swipeLayout) {
        this.currentLayout = swipeLayout;
    }

    public void clearCurrentLayout() {
        if (currentLayout != null) {
            currentLayout.close();
        }
    }



    public boolean isShouldSwipe(SwipeLayout swipeLayout) {
        if (currentLayout == null) {
            return false;
        } else {
            return currentLayout == swipeLayout;
        }
    }
}
