/*
 * MVDRelaxationForTransfer.java
 * This file is part of KnowledgeBaseExpansion
 *
 * Copyright (C) 2019 - Giacomo Bergami
 *
 * KnowledgeBaseExpansion is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * KnowledgeBaseExpansion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with KnowledgeBaseExpansion. If not, see <http://www.gnu.org/licenses/>.
 */

 
package ref;

import algos.Substitute;
import types.*;

import java.util.*;

public class MVDRelaxationForTransfer {
    public Schema rewrittenRelation;
    public Schema originalEvent;
    public Set<String> notToExQuantifyVars;
    public HashMap<String, Substitute.SubPair<String, String>> relaxation;
    public HashMap<String, String> fixedToSwitch;

    /**
     * @return all the variables that are not represented into the minimal rewritten event/relationship, and that must be found within the db.
     */
    public Set<String> exQuantifiable() {
        if (originalEvent == null || originalEvent.arguments == null)
            return Collections.emptySet();

        HashSet<String> s = new HashSet<>(originalEvent.arguments);
        if (notToExQuantifyVars != null)
            s.removeAll(notToExQuantifyVars);
        return s;
    }

    public List<Proposition> prepareFixpointInit() {
        return Collections.singletonList(originalEvent.asProposition());
    }

    Substitute<String, Proposition> sprop = new Substitute<>(null);
    public List<Proposition> substitute(List<Proposition> toExpand, String arg, String newArg1, String newArg2) {
        List<Proposition> toReturn = new ArrayList<>(toExpand.size()*2);
        for (Proposition s : toExpand) {
            Substitute.SubPair<String, String> mustBeRelaxed = relaxation.get(arg);
            if (mustBeRelaxed != null) {
                Proposition left = s.copy();
                Proposition right = s.copy();
                swap(left, right, newArg1, newArg2, mustBeRelaxed.first);
                swap(left, right, newArg2, newArg1, mustBeRelaxed.second);
                toReturn.add(left);
                toReturn.add(right);
            } else {
                String fixedargument = fixedToSwitch.get(arg);
                Proposition left = s.copy();
                Proposition right = s.copy();
                swap(left, right, newArg2, newArg1, fixedargument);
                toReturn.add(left);
                toReturn.add(right);
            }
        }
        return toReturn;
    }

    private void swap(Proposition left, Proposition right, String newArg1, String newArg2, String m) {
        sprop.setSubstitute(m);
        sprop.accept(new Substitute.SubPair<>(left, newArg1));
        sprop.accept(new Substitute.SubPair<>(right, newArg2));
    }

    public Clause fromPropositionWithExProj(Proposition prop) {
        // Obtaining the variables to be used in the existential arguments. These shall be applied to all the already existing events
        Set<String> toProject = exQuantifiable();
        ArrayList<Quantifier> existProject = new ArrayList<>(toProject.size());
        for (String var : toProject) {
            existProject.add(Quantifier.exists(var, "?", new ArrayList<>()));
        }
        return new Clause(new ArrayList<Quantifier>(), existProject, false, prop, new ArrayList<>());
    }

}
