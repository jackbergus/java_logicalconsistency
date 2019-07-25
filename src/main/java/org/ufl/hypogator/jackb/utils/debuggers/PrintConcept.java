package org.ufl.hypogator.jackb.utils.debuggers;

import org.ufl.hypogator.jackb.traversers.conceptnet.ConceptNetTraverser;

import java.util.Scanner;

public class PrintConcept {

    public static void main(String[] args) {
        ConceptNetTraverser t = new ConceptNetTraverser();
        Scanner s = new Scanner(System.in);
        System.out.print("Insert the term to exactly match in ConceptNet5: ");
        String term = s.nextLine();
        System.out.println(t.resolveTerm(term));
    }

}
