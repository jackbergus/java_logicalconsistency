package org.ufl.hypogator.jackb.m9.fdep;

import java.util.ArrayList;

public class FDepBody_or_Key extends ArrayList<String> {

    public FDepBody_or_Key(String toSplit) {
        for (String x : toSplit.split(",")) {
            this.add(x.trim());
        }
    }

}
