package org.ufl.hypogator.jackb.html;

import org.jsoup.nodes.Element;

public class Table {

    private final Element table = new Element("table");
    private final Element tableBody = new Element("tbody");

    public Table(String... header) {
        ListBuild th = ListBuild.tableRowForHeader();
        for (String x : header) {
            th.add(x);
        }
        table.appendChild(new Element("thead").appendChild(th.build()));
    }

    public Table addRow(String... row) {
        ListBuild tr  =  ListBuild.tableRowForRow();
        for (String x : row) {
            tr.add(x);
        }
        tableBody.append(tr.build().outerHtml());
        return this;
    }

    public Element build() {
        table.appendChild(tableBody);
        return table;
    }

    public static Table edgeVertexTable() {
        return new Table("id", "value", "lang", "term", "POS", "source");
    }

}
