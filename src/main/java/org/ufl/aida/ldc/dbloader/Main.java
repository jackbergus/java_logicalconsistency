package org.ufl.aida.ldc.dbloader;

import org.ufl.hypogator.jackb.fuzzymatching.ldc.LDCMatching;

/**
 * Main entrypoint for loading and grounding the LDC dataset.
 *
 * TODO: this has to be extended with the actual data pipeline. For the moment it considers LDC as both the
 * TODO: data resolver and the TA2KB data. These two need to be separated in a near future
 */
public class Main {

    public static void main(String[] args) {
        LDCMatching.loadLDCToDatabase();
    }

}
