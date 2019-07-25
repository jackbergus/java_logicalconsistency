/*
 * This file is generated by jOOQ.
 */
package org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.3"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class NodePrefixes implements Serializable {

    private static final long serialVersionUID = -1480238590;

    private Integer nodeId;
    private Integer prefixId;

    public NodePrefixes() {}

    public NodePrefixes(NodePrefixes value) {
        this.nodeId = value.nodeId;
        this.prefixId = value.prefixId;
    }

    public NodePrefixes(
        Integer nodeId,
        Integer prefixId
    ) {
        this.nodeId = nodeId;
        this.prefixId = prefixId;
    }

    public Integer getNodeId() {
        return this.nodeId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    public Integer getPrefixId() {
        return this.prefixId;
    }

    public void setPrefixId(Integer prefixId) {
        this.prefixId = prefixId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("NodePrefixes (");

        sb.append(nodeId);
        sb.append(", ").append(prefixId);

        sb.append(")");
        return sb.toString();
    }
}
