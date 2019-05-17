package org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ;

import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.EdgeVertex;

public interface ConceptNet5Interface {

    /**
     * Creates a node using the nodeId from babelnet
     * @param node
     * @return
     */
    public EdgeVertex queryNode(boolean like, String node);
}
