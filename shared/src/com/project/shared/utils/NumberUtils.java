package com.project.shared.utils;

import java.util.LinkedList;

public class NumberUtils {

    public static class MovingAverage
    {
        LinkedList<Double> points = new LinkedList<Double>();
        private int _size;

        public MovingAverage(int size) {
            this._size = size;
        }

        public void clear() {
            this.points.clear();
        }

        public void add(double point) {
            this.points.addLast(point);
            if (this.points.size() > this._size) {
                this.points.removeFirst();
            }
        }

        public double getAverage() {
            double result = 0;
            for (Double point : points) {
                result = result + point;
            }
            return result * (1.0 / this.points.size());
        }
    }
}
