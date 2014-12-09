package alex_henry.runThree;

import java.util.Set;

import org.openimaj.feature.FeatureVector;

public interface KMeans<F extends FeatureVector> {
	public Set<F> getMeans(final int k, Set<F> vectors);
}
