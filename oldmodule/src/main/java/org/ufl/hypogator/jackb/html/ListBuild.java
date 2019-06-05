package org.ufl.hypogator.jackb.html;

import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

public class ListBuild {

    List<String> as = new ArrayList<>();
    private final String tagElement;
    private final Element listContainer;

    private ListBuild(String tagList, String tagElement) {
        this.tagElement = tagElement;
        this.listContainer = new Element(tagList);
    }

    public static ListBuild unordered() {
        return new ListBuild("ul", "li");
    }

    public ListBuild add(String element) {
        listContainer.appendChild(new Element(tagElement).text(element == null ? "{null}" : element));
        return this;
    }

    public ListBuild add(Element element) {
        listContainer.appendChild(element);
        return this;
    }

    public ListBuild add(ListBuild element) {
        return add(element.build());
    }

    public Element build() {
        return listContainer;
    }

    public static ListBuild tableRowForHeader() {
        return new ListBuild("tr", "th");
    }

    public static ListBuild tableRowForRow() {
        return new ListBuild("tr", "td");
    }

}
