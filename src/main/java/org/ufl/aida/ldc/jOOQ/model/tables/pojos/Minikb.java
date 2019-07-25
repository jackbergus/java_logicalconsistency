/*
 * This file is generated by jOOQ.
 */
package org.ufl.aida.ldc.jOOQ.model.tables.pojos;


import javax.annotation.Generated;
import java.io.Serializable;


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
public class Minikb implements Serializable {

    private static final long serialVersionUID = 439805614;

    private String kbId;
    private String topicId;
    private String category;
    private String handle;
    private String description;

    public Minikb() {}

    public Minikb(Minikb value) {
        this.kbId = value.kbId;
        this.topicId = value.topicId;
        this.category = value.category;
        this.handle = value.handle;
        this.description = value.description;
    }

    public Minikb(
        String kbId,
        String topicId,
        String category,
        String handle,
        String description
    ) {
        this.kbId = kbId;
        this.topicId = topicId;
        this.category = category;
        this.handle = handle;
        this.description = description;
    }

    public String getKbId() {
        return this.kbId;
    }

    public void setKbId(String kbId) {
        this.kbId = kbId;
    }

    public String getTopicId() {
        return this.topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getHandle() {
        return this.handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Minikb (");

        sb.append(kbId);
        sb.append(", ").append(topicId);
        sb.append(", ").append(category);
        sb.append(", ").append(handle);
        sb.append(", ").append(description);

        sb.append(")");
        return sb.toString();
    }
}