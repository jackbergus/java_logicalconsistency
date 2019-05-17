package org.ufl.hypogator.jackb.utils;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;

public class ArgMaxCollector<T, F> {

	private List<T> max = new ArrayList<>();
	private F score = null;
	private final Function<T, F> fun;
	private Comparator<? super F> comparator;

	private ArgMaxCollector(Function<T, F> fun, Comparator<? super F> comparator ) {
		this.fun = fun;
		this.comparator = comparator;
	}

	public void accept( T element ) {
		F toCompare = fun.apply(element);
		int cmp = score == null ? -1 : comparator.compare( score, toCompare );
		if ( cmp < 0 ) {
			score = toCompare;
			max.clear();
			max.add( element);
		} else if ( cmp == 0 )
			max.add(element);
	}

	public void combine( ArgMaxCollector<T, F> other ) {
		int cmp = comparator.compare( score, other.score );
		if ( cmp < 0 ) {
			max = other.max;
			score = other.score;
		} else if ( cmp == 0 ) {
			max.addAll( other.max );
		}
	}

	public Pair<F, List<T>> get() {
		return new Pair<>(score, max);
	}
	
	public static <T, F> Collector<T, ArgMaxCollector<T, F>, Pair<F, List<T>>> collector(Function<T, F> fun,  Comparator<? super F> comparator ) {
		return Collector.of(
			() -> new ArgMaxCollector<>(fun, comparator),
				ArgMaxCollector::accept,
			( a, b ) ->{ a.combine(b); return a; },
				ArgMaxCollector::get
		);
	}
}