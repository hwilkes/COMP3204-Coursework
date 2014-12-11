package alex_henry.interfaces;

import java.util.Map;

import org.openimaj.image.FImage;

public interface RunClassifier {
	public void giveData(Map<String,Map<String,FImage>> trainingimages);
	public Map<String,String> getClassifications(Map<String,FImage> testimages);
	public Double getTrainingError();
	public Double getClassificationError(Map<FImage,String> labeledTestImages);
}
