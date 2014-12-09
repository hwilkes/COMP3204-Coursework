package alex_henry.runThree;

import java.util.Set;

import org.openimaj.feature.FeatureVector;
import org.openimaj.image.FImage;

public interface VectorExtractor<F extends FeatureVector> {
	public Set<F> getVectors(Set<FImage> images);
}
