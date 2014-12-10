package alex_henry.runThree;

import java.util.List;

import org.openimaj.feature.ByteFV;
import org.openimaj.image.FImage;
import org.openimaj.image.feature.dense.gradient.dsift.AbstractDenseSIFT;
import org.openimaj.ml.annotation.Annotated;
import org.openimaj.ml.annotation.ScoredAnnotation;
import org.openimaj.ml.annotation.linear.LiblinearAnnotator;

import alex_henry.interfaces.Classifier;

public class ClassifierByteFV<SIFT extends AbstractDenseSIFT<FImage>> implements Classifier<ByteFV> {

	private LiblinearAnnotator<FImage,String> annotator;
	
	public ClassifierByteFV(List<ByteFV> dictionary, SIFT method)
	{
		BOVWExtractorByte extractor = new BOVWExtractorByte<SIFT>(dictionary,method);
		annotator = new LiblinearAnnotator<FImage, String>(extractor,LiblinearAnnotator.Mode.MULTICLASS,
				de.bwaldvogel.liblinear.SolverType.MCSVM_CS,1.0,1.0);
	}
	
	public ClassifierByteFV(BOVWExtractorByte extractor)
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
