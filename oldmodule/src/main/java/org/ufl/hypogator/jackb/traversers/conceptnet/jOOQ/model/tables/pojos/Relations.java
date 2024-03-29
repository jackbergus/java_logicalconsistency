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
public class Relations implements Serializable {

    private static final long serialVersionUID = -1711172591;

    private Integer id;
    private String  uri;
    private Boolean directed;

    public Relations() {}

    public Relations(Relations value) {
        this.id = value.id;
        this.uri = value.uri;
        this.directed = value.directed;
    }

    public Relations(
        Integer id,
        String  uri,
        Boolean directed
    ) {
        this.id = id;
        this.uri = uri;
        this.directed = directed;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Boolean getDirected() {
        return this.directed;
    }

    public void setDirected(Boolean directed) {
        this.directed = directed;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Relations (");

        sb.append(id);
        sb.append(", ").append(uri);
        sb.append(", ").append(directed);

        sb.append(")");
        return sb.toString();
    }
}
