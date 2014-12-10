package alex_henry.interfaces;

import java.util.Set;

import org.openimaj.feature.FeatureVector;

/*
 * Calculates vocabulary of means from a set of feature vectors. argument k specifies size of vocabulary
 * */
public interface KMeans<F extends FeatureVector> {
	public Set<F> getMeans(final int k, Set<F> vectors);
}
