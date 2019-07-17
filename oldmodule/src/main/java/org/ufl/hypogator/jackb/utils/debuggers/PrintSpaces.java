package org.ufl.hypogator.jackb.utils.debuggers;

import com.bericotech.clavin.ClavinException;
import org.ufl.hypogator.jackb.disambiguation.dimension.space.DimLocation;
import org.ufl.hypogator.jackb.disambiguation.dimension.space.DisambiguatedSpace;
import org.ufl.hypogator.jackb.disambiguation.dimension.space.DisambiguatorForSpace;
import org.ufl.hypogator.jackb.traversers.conceptnet.ConceptNetTraverser;

import java.util.Scanner;

public class PrintSpaces {

    public static void main(String[] args) throws ClavinException {
        Scanner s = new Scanner(System.in);
        DisambiguatorForSpace t = DisambiguatorForSpace.getInstance(DimLocation.locationElements);
        while (true) {
            System.out.print("Insert the term to exactly match in ConceptNet5: ");
            String term = s.nextLine();
            System.out.println(t.detextWithClavinNerd(term));
        }
    }

}
