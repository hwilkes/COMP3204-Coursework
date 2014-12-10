package alex_henry.interfaces;

import org.openimaj.feature.FeatureExtractor;
import org.openimaj.feature.FeatureVector;
import org.openimaj.image.FImage;

/*
 * Based on FeatureExtractor openimaj interface, return a bag-of-visual-words
 * feature for an image
 * */

public interface BOVWExtractor<F extends FeatureVector> extends FeatureExtractor<F, FImage> {
	public F extractFeature(FImage object);
}
