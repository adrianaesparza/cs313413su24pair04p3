package edu.luc.etl.cs313.android.shapes.model;

/**
 * A shape visitor for calculating the bounding box, that is, the smallest
 * rectangle containing the shape. The resulting bounding box is returned as a
 * rectangle at a specific location.
 */
public class BoundingBox implements Visitor<Location> {

    @Override
    public Location onCircle(final Circle c) {
        final int radius = c.getRadius();
        return new Location(-radius, -radius, new Rectangle(2 * radius, 2 * radius));
    }

    @Override
    public Location onFill(final Fill f) {
        Location bbox = f.getShape().accept(this);
        if (bbox == null) {
            throw new IllegalStateException("Null bounding box returned from Fill shape: " +
                    f.getShape().getClass().getName());
        }
        return bbox;
    }

    @Override
    public Location onGroup(final Group g) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Shape s : g.getShapes()) {
            Location bbox = s.accept(this);
            if (bbox == null) {
                throw new IllegalStateException("Null bbox for shape in Group: " + s.getClass().getName());
            }
            int x = bbox.getX();
            int y = bbox.getY();
            Rectangle r = (Rectangle) bbox.getShape();
            int right = x + r.getWidth();
            int bottom = y + r.getHeight();

            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (right > maxX) maxX = right;
            if (bottom > maxY) maxY = bottom;
        }

        int width = maxX - minX;
        int height = maxY - minY;

        return new Location(minX, minY, new Rectangle(width, height));
    }

    @Override
    public Location onLocation(final Location l) {
        Shape innerShape = l.getShape();
        if (innerShape == null) {
            throw new IllegalStateException("Location contains null inner shape!");
        }
        Location innerBBox = innerShape.accept(this);
        if (innerBBox == null) {
            throw new IllegalStateException("Bounding box null from shape inside Location: " +
                    innerShape.getClass().getName());
        }
        return new Location(
                innerBBox.getX() + l.getX(),
                innerBBox.getY() + l.getY(),
                innerBBox.getShape()
        );
    }

    @Override
    public Location onRectangle(final Rectangle r) {
        return new Location(0, 0, new Rectangle(r.getWidth(), r.getHeight()));
    }

    @Override
    public Location onStrokeColor(final StrokeColor c) {
        Shape innerShape = c.getShape();
        if (innerShape == null) {
            throw new IllegalStateException("StrokeColor contains null inner shape!");
        }
        Location bbox = innerShape.accept(this);
        if (bbox == null) {
            throw new IllegalStateException("Bounding box null from shape inside StrokeColor: " +
                    innerShape.getClass().getName());
        }
        return bbox;
    }

    @Override
    public Location onOutline(final Outline o) {
        Shape innerShape = o.getShape();
        if (innerShape == null) {
            throw new IllegalStateException("Outline contains null inner shape!");
        }
        Location bbox = innerShape.accept(this);
        if (bbox == null) {
            throw new IllegalStateException("Bounding box null from shape inside Outline: " +
                    innerShape.getClass().getName());
        }
        return bbox;
    }

    @Override
    public Location onPolygon(final Polygon s) {
        if (s.getPoints().isEmpty()) {
            return new Location(0, 0, new Rectangle(0, 0));
        }

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Point p : s.getPoints()) {
            int x = p.getX();
            int y = p.getY();
            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
        }

        int width = maxX - minX;
        int height = maxY - minY;

        return new Location(minX, minY, new Rectangle(width, height));
    }


    @Override
    public Location onPoint(final Point p) {
        return new Location(p.getX(), p.getY(), new Rectangle(0, 0));
    }
}
