package org.ufl.hypogator.jackb.server.handlers.concrete;

import com.google.common.collect.HashMultimap;
import org.ufl.hypogator.jackb.ontology.data.RawEventRelationship;
import org.ufl.hypogator.jackb.streamutils.collectors.CollectAndStream;
import org.ufl.hypogator.jackb.streamutils.iterators.IteratorWithOperations;

import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

public class MatchEventRelWithAIDAResolvedargs extends CollectAndStream<RawEventRelationship, HashMultimap<String, RawEventRelationship>, Void> {

    HashMultimap<String, RawEventRelationship> el;

    @Override
    protected IteratorWithOperations<Void> restream(HashMultimap<String, RawEventRelationship> input) {
        return null;
    }

    public MatchEventRelWithAIDAResolvedargs copy() {
        return new MatchEventRelWithAIDAResolvedargs(isParallel());
    }

    public MatchEventRelWithAIDAResolvedargs putAll(MatchEventRelWithAIDAResolvedargs map) {
        el.putAll(map.el);
        return this;
    }

    public MatchEventRelWithAIDAResolvedargs(boolean doParallelize) {
        super(doParallelize);
        el = HashMultimap.create();
    }

    @Override
    public Supplier<CollectAndStream<RawEventRelationship, HashMultimap<String, RawEventRelationship>, Void>> supplier() {
        return this::copy;
    }

    @Override
    public BiConsumer<CollectAndStream<RawEventRelationship, HashMultimap<String, RawEventRelationship>, Void>, RawEventRelationship> accumulator() {
        return (left, x) -> ((MatchEventRelWithAIDAResolvedargs) left).updateWith(x);
    }

    private void updateWith(RawEventRelationship x) {
        el.put(x.getSubType(), x);
    }

    @Override
    public BinaryOperator<CollectAndStream<RawEventRelationship, HashMultimap<String, RawEventRelationship>, Void>> combiner() {
        return (l, r) -> copy().putAll((MatchEventRelWithAIDAResolvedargs) l).putAll((MatchEventRelWithAIDAResolvedargs) r);
    }

    @Override
    public Function<CollectAndStream<RawEventRelationship, HashMultimap<String, RawEventRelationship>, Void>, HashMultimap<String, RawEventRelationship>> finisher() {
        return x -> ((MatchEventRelWithAIDAResolvedargs) x).el;
    }
}
