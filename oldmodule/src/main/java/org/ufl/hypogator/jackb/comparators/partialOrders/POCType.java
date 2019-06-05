package org.ufl.hypogator.jackb.comparators.partialOrders;

import org.ufl.hypogator.jackb.disambiguation.dimension.Direction;

public enum POCType {
    /**
     * The two elements are either the same or similar
     */
    Equal,

    /**
     * The first element precedes (or implies) the second one
     */
    Lesser,

    /**
     * The second element succeeds (or implies) the second one
     */
    Greater,

    /**
     * The two elements cannot be compared and inconsistent
     * TODO: add another case of uncomparable and consistent, as in Directions
     */
    Uncomparable,
    ;

    public Direction asDirection() {
        switch (this) {
            case Equal:
                return Direction.BOTH;
            case Lesser:
                return Direction.RIGHT_TYPE_LEFT_SUBTYPE;
            case Greater:
                return Direction.LEFT_TYPE_RIGHT_SUBTYPE;
            default:
                return Direction.NONE;
        }
    }

    public POCType invert() {
        switch (this) {
            case Lesser:
                return Greater;
            case Greater:
                return Lesser;
            default:
                return this;
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case Equal:
                return " == ";
            case Lesser:
                return " < ";
            case Greater:
                return " > ";
            default:
                return " n.c. ";
        }
    }
}
