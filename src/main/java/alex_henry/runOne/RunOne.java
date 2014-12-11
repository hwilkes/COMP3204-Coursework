package alex_henry.runOne;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openimaj.data.dataset.ListBackedDataset;
import org.openimaj.data.dataset.ListDataset;
import org.openimaj.image.FImage;
import org.openimaj.ml.annotation.Annotated;
import org.openimaj.ml.annotation.AnnotatedObject;

import alex_henry.interfaces.RunClassifier;

/*
 * Implements RunClassifier to use the K-Nearest Neighbours classifier from Run One. 
 * Has both constructors for both passing the value for K and for using our recommended default value 
 * */
public class RunOne implements RunClassifier {

	private static final int DEFAULT_K = 29;
	
	private int kNearestNeighbours;
	private KNearestClassifier classifier = null;
	private Double trainingerror = null;
	private Map<String,String> classifications = null;

	public RunOne(int k)
	{
		kNearestNeighbours = k;
	}
	
	public RunOne()
	{
		kNearestNeighbours = DEFAULT_K;
	}
	
	@Override
	public void giveData(Map<String, Map<String, FImage>> trainingimages) {
		classifier = new KNearestClassifier();
		List<Annotated<FImage,String>> trainingAnnotations = new ArrayList<Annotated<FImage,String>>();

		for(String className : trainingimages.keySet())
		{
			Map<String,FImage> classImages = trainingimages.get(className);
			
			for(FImage f : classImages.values()){
				trainingAnnotations.add(new AnnotatedObject<FImage,String>(f,className));
			}
			//Generate TinyImages for each image in training set
			ListDataset<TinyImage> tinys = new ListBackedDataset<TinyImage>();
			for(FImage f : classImages.values())
			{
				tinys.add(new TinyImage(f,16,16));
			}
			//train classifier with TinyImages
			classifier.addClassValues(tinys, className);
		}

		/*
		 * Calculate error from applying classifier to training set
		 * */
		double error = 0;
		int count = 0;
		for(Annotated<FImage,String> f : trainingAnnotations)
		{
			String prediction = classifier.classify(f.getObject(),kNearestNeighbours);
			
			if(!prediction.equals(f.getAnnotations().iterator().next()))
			{
				error++;
			}
			count++;
		}
		error = (error/count)*100;


		trainingerror = error;
	}

	@Override
	public Map<String, String> getClassifications(Map<String, FImage> testimages) {
		classifications = new HashMap<String,String>();
		for(String key : testimages.keySet())
		{
			String prediction = classifier.classify(testimages.get(key), kNearestNeighbours);
			classifications.put(key, prediction);
		}
		return classifications;
	}

	@Override
	public Double getTrainingError() {
		return trainingerror;
	}

	@Override
	public Double getClassificationError(Map<FImage, String> labeledTestImages) {
		double error = 0;
		int count = 0;
		for(FImage f : labeledTestImages.keySet())
		{
			String prediction = classifier.classify(f,kNearestNeighbours);
			
			if(!prediction.equals(labeledTestImages.get(f)))
			{
				error++;
			}
			count++;
		}
		error = (error/count)*100;
		return error;
	}

}
