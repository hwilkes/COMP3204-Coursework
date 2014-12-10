package alex_henry.interfaces;

import java.util.List;

import org.openimaj.feature.FeatureVector;
import org.openimaj.image.FImage;
import org.openimaj.ml.annotation.Annotated;
import org.openimaj.ml.annotation.ScoredAnnotation;

/*
 * Interface for Classifier classes
 * 
 * Are trained with a list of Feature Vector objects
 * Annotate method returns list of predictions with confidence for an image
 * */

public interface Classifier<F extends FeatureVector> {
	public void train(List<? extends Annotated<FImage,String>> data);
	public List<ScoredAnnotation<String>> annotate(FImage image);
}
