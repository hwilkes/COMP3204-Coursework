package alex_henry.runThree;

import org.openimaj.feature.FeatureExtractor;
import org.openimaj.feature.FeatureVector;
import org.openimaj.image.FImage;


public interface BOVWExtractor<F extends FeatureVector> extends FeatureExtractor<F, FImage> {
	public F extractFeature(FImage object);
}
