package org.ufl.hypogator.jackb.disambiguation.dimension;

import org.ufl.hypogator.jackb.comparators.partialOrders.POCType;

public enum Direction {
    LEFT_TYPE_RIGHT_SUBTYPE,
    RIGHT_TYPE_LEFT_SUBTYPE,
    BOTH,
    NONE;

    public Direction reverse() {
        switch (this) {
            case LEFT_TYPE_RIGHT_SUBTYPE:
                return RIGHT_TYPE_LEFT_SUBTYPE;
            case RIGHT_TYPE_LEFT_SUBTYPE:
                return LEFT_TYPE_RIGHT_SUBTYPE;
            default:
                return this;
        }
    }

    public POCType toDirectionType() {
        switch (this) {
            case LEFT_TYPE_RIGHT_SUBTYPE:
                return POCType.Greater;
            case RIGHT_TYPE_LEFT_SUBTYPE:
                return POCType.Lesser;
            case BOTH:
                return POCType.Equal;
            default:
                return POCType.Uncomparable;
        }
    }
}
