package alex_henry.runThree;

import java.util.List;

import org.openimaj.feature.ByteFV;
import org.openimaj.image.FImage;
import org.openimaj.ml.annotation.Annotated;
import org.openimaj.ml.annotation.ScoredAnnotation;
import org.openimaj.ml.annotation.linear.LiblinearAnnotator;

public class ClassifierByte {

	private LiblinearAnnotator<FImage,String> annotator;
	
	public ClassifierByte(List<ByteFV> dictionary)
	{
		BOVWExtractorByte extractor = new BOVWExtractorByte(dictionary);
		annotator = new LiblinearAnnotator<FImage, String>(extractor,LiblinearAnnotator.Mode.MULTICLASS,
				de.bwaldvogel.liblinear.SolverType.MCSVM_CS,1.0,1.0);
	}
	
	public ClassifierByte(BOVWExtractorByte extractor)
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
