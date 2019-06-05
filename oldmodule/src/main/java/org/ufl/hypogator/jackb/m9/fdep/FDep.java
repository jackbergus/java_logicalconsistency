package org.ufl.hypogator.jackb.m9.fdep;

import java.util.ArrayList;


/**
 * This class parses the functonal dependencies for each possible element
 */
public class FDep extends ArrayList<FDepBody_or_Key> {

    public FDep(String fromString) {
        fromString = fromString.trim();
        int fdepSep = fromString.indexOf("=>");
        while (fdepSep > 0) {
            String body = fromString.substring(0, fdepSep);
            if (!body.isEmpty()) {
                FDepBody_or_Key elem = new FDepBody_or_Key(body);
                if (!elem.isEmpty()) this.add(elem);
            }
            fromString = fromString.substring(fdepSep+2).trim();
            fdepSep = fromString.indexOf("=>");
        }
        fromString = fromString.replace("=>", "").trim();
        if (!fromString.isEmpty()) {
            FDepBody_or_Key elem = new FDepBody_or_Key(fromString);
            if (!elem.isEmpty()) this.add(elem);
        }
    }

    public static void main(String args[]) {
        //System.out.println(new FDep("=> c, d"));
        System.out.println(new FDep("a, b => c, d => "));
    }


}
