package alex_henry.runThree;

import java.util.List;

import org.openimaj.feature.FeatureVector;
import org.openimaj.image.FImage;
import org.openimaj.ml.annotation.Annotated;
import org.openimaj.ml.annotation.ScoredAnnotation;

public interface Classifier<F extends FeatureVector> {
	public void train(List<? extends Annotated<FImage,String>> data);
	public List<ScoredAnnotation<String>> annotate(FImage image);
}
