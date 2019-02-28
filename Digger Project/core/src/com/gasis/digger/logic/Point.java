package com.gasis.digger.logic;

/**
 * Holds x and y coordinates
 */
public class Point implements Comparable<Point> {

    // coordinates
    private int x;
    private int y;

    /**
     * Default constructor
     */
    public Point() {}

    /**
     * Constructor with args
     * @param x
     * @param y
     */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Checks if this point is equal to another object
     * @param other another object
     * @return true if points are equal
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof Point) {
            Point p = (Point) other;

            if (p.getX() == x && p.getY() == y) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the hash code of the point
     * @return
     */
    @Override
    public int hashCode() {
        return x ^ 31 + y ^ 31;
    }

    /**
     * Compares this point to the specified one
     * @param other point to compare to
     * @return -1 if this point is less than the other, 0 if equal, 1 if greater
     */
    @Override
    public int compareTo(Point other) {
        if (equals(other)) {
            return 0;
        } else if (other.x + other.y > x + y) {
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * Gets x coordinate
     * @return
     */
    public int getX() {
        return x;
    }

    /**
     * Sets x coordinate
     * @param x new x
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Gets y coordinate
     * @return
     */
    public int getY() {
        return y;
    }

    /**
     * Sets y coordinate
     * @param y new y
     */
    public void setY(int y) {
        this.y = y;
    }
}
