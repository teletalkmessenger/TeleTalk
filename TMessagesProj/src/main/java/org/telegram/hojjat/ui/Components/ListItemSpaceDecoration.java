package org.telegram.hojjat.ui.Components;

import android.graphics.Rect;
import android.view.View;

import org.telegram.messenger.support.widget.RecyclerView;

public class ListItemSpaceDecoration extends RecyclerView.ItemDecoration {
    private int leftSpace;
    private int topSpace;
    private int rightSpace;
    private int bottomSpace;
    private boolean noBottomSpaceForLastItem;
    private boolean noTopSpaceForFirstItem;

    public ListItemSpaceDecoration(int leftSpace, int topSpace, int rightSpace, int downSpace) {
        this.leftSpace = leftSpace;
        this.topSpace = topSpace;
        this.rightSpace = rightSpace;
        this.bottomSpace = downSpace;
    }

    public void setNoBottomSpaceForLastItem(boolean noBottomSpaceForLastItem) {
        this.noBottomSpaceForLastItem = noBottomSpaceForLastItem;
    }

    public void setNoTopSpaceForFirstItem(boolean noTopSpaceForFirstItem) {
        this.noTopSpaceForFirstItem = noTopSpaceForFirstItem;
    }

    public void setLeftSpace(int leftSpace) {
        this.leftSpace = leftSpace;
    }

    public void setTopSpace(int topSpace) {
        this.topSpace = topSpace;
    }

    public void setRightSpace(int rightSpace) {
        this.rightSpace = rightSpace;
    }

    public void setBottomSpace(int bottomSpace) {
        this.bottomSpace = bottomSpace;
    }

    public int getLeftSpace() {
        return leftSpace;
    }

    public int getTopSpace() {
        return topSpace;
    }

    public int getRightSpace() {
        return rightSpace;
    }

    public int getBottomSpace() {
        return bottomSpace;
    }

    public boolean isNoBottomSpaceForLastItem() {
        return noBottomSpaceForLastItem;
    }

    public boolean isNoTopSpaceForFirstItem() {
        return noTopSpaceForFirstItem;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = leftSpace;
        outRect.right = rightSpace;
        if (!noBottomSpaceForLastItem || parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1)
            outRect.bottom = bottomSpace;
        if (!noTopSpaceForFirstItem || parent.getChildAdapterPosition(view) != 0)
            outRect.top = topSpace;

    }
}
