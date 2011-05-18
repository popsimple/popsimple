package com.project.canvas.shared.data;

public class Rectangle2D {
    protected int _left = 0;
    protected int _top = 0;
    protected int _right = 0;
    protected int _bottom = 0;

    public Rectangle2D(int left, int top, int right, int bottom) {
        this._left = left;
        this._top = top;
        this._right = right;
        this._bottom = bottom;
    }

    public boolean isOverlapping(Rectangle2D rect) {
        if (this._right < rect._left) {
            return false;
        }
        if (this._left > rect._right) {
            return false;
        }
        if (this._bottom < rect._top) {
            return false;
        }
        if (this._top > rect._bottom) {
            return false;
        }
        return true;
    }
}
