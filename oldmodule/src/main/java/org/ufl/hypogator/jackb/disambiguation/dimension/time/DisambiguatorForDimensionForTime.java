/*
 * TimeDisambiguator.java
 * This file is part of aida_scraper
 *
 * Copyright (C) 2018 giacomo
 *
 * aida_scraper is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * aida_scraper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aida_scraper. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.ufl.hypogator.jackb.disambiguation.dimension.time;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.TimeAnnotator;
import edu.stanford.nlp.util.CoreMap;

import org.ufl.hypogator.jackb.disambiguation.DisambiguationAlgorithm;
import org.ufl.hypogator.jackb.disambiguation.DisambiguatorForDimension;

import java.util.List;
import java.util.Properties;

public class DisambiguatorForDimensionForTime implements DisambiguatorForDimension<ResolvedTime, InformativeTime> {
    AnnotationPipeline pipeline;
    private String[] noArgs;

    /**
     * Initializes the stanford pipeline
     */
    public DisambiguatorForDimensionForTime(String[] noArgs) {
        this.noArgs = noArgs;
        pipeline = new AnnotationPipeline();
        pipeline.addAnnotator(new TokenizerAnnotator(false));
        pipeline.addAnnotator(new WordsToSentencesAnnotator(false));
        //pipeline.addAnnotator(new POSTaggerAnnotator(false));
        pipeline.addAnnotator(new TimeAnnotator("sutime", new Properties()));
    }

    /**
     * Provides an unique temporal representation
     *
     * @param str
     * @return
     */
    @Override
    public InformativeTime disambiguate(String str) {
        str = str.replaceAll("night","");
        str = str.replaceAll("day", "");
        str = str.replaceAll("morning", "");
        str = str.replaceAll("dawn", "");
        str = str.replaceAll("sunset", "");
        Annotation annotation = new Annotation(str);
        InformativeTime ti = new InformativeTime(str);
        pipeline.annotate(annotation);
        //System.out.println(annotation.get(CoreAnnotations.TextAnnotation.class));
        List<CoreMap> timexAnnsAll = annotation.get(TimeAnnotations.TimexAnnotations.class);
        for (CoreMap cm : timexAnnsAll) {
            List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
            ti.setDisambiguation(cm.toString(),
                    new ResolvedTime(cm.get(TimeAnnotations.TimexAnnotation.class).value(), cm.toString()), 1);
        }
        return ti;
    }

    @Override
    public DisambiguationAlgorithm<ResolvedTime, InformativeTime> getAlgorithm(double ignored) {
        return new DisambiguationAlgorithm<>(this, ignored, noArgs, allowReflexiveExpansion());
    }

    @Override
    public String[] allowedKBTypesForTypingExpansion() {
        return noArgs;
    }

    @Override
    public boolean allowReflexiveExpansion() {
        return false;
    }


}