package edu.luc.etl.cs313.android.shapes.model;

/**
 * A point, implemented as a location without a shape.
 */
public class Point extends Location {

    public Point(final int x, final int y) {
        super(x, y, new Circle(0));
    }

    @Override
    public <T> T accept(final Visitor<T> v) {
        return v.onPoint(this);
    }
}
