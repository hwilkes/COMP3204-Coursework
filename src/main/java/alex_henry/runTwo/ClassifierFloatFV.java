package alex_henry.runTwo;

import java.util.List;

import org.openimaj.feature.FloatFV;
import org.openimaj.image.FImage;
import org.openimaj.ml.annotation.Annotated;
import org.openimaj.ml.annotation.ScoredAnnotation;
import org.openimaj.ml.annotation.linear.LiblinearAnnotator;

import alex_henry.runThree.Classifier;

public class ClassifierFloatFV implements Classifier {

	private LiblinearAnnotator<FImage,String> annotator;
	
	public ClassifierFloatFV(List<FloatFV> dictionary)
	{
		BOVWExtractorFloatFV extractor = new BOVWExtractorFloatFV(dictionary);
		annotator = new LiblinearAnnotator<FImage, String>(extractor,LiblinearAnnotator.Mode.MULTICLASS,
				de.bwaldvogel.liblinear.SolverType.MCSVM_CS,1.0,1.0);
	}
	
	public ClassifierFloatFV(BOVWExtractorFloatFV extractor)
	{
		annotator = new LiblinearAnnotator<FImage, String>(extractor,LiblinearAnnotator.Mode.MULTICLASS,
				de.bwaldvogel.liblinear.SolverType.MCSVM_CS,1.0,1.0);
		
	}
	
	public void train(List<? extends Annotated<FImage,String>> data)
	{
		annotator.train(data);
	}
	
	public List<ScoredAnnotation<String>> annotate(FImage image)
	{
		return annotator.annotate(image);
	}
}
