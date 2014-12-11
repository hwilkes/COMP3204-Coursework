package alex_henry.runTwo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.openimaj.feature.FloatFV;
import org.openimaj.image.FImage;
import org.openimaj.ml.annotation.Annotated;
import org.openimaj.ml.annotation.AnnotatedObject;
import org.openimaj.ml.annotation.ScoredAnnotation;

import alex_henry.interfaces.RunClassifier;

public class RunTwo implements RunClassifier {

	protected ClassifierFloatFV classifier;
	protected Double trainingError;
	
	@Override
	public void giveData(Map<String, Map<String, FImage>> trainingimages) {
		
		/*
		 * Number of means to be generated for K-Means vocabulary
		 * */
		int k = 500;
		
		Set<FImage> vocabLearningImages = new HashSet<FImage>();

		List<Annotated<FImage,String>> trainingAnnotations = new ArrayList<Annotated<FImage,String>>();
		
		for(String className : trainingimages.keySet())
		{
			Map<String,FImage> classImages = trainingimages.get(className);
			
			
			/*
			*Number of images to user. For generating the vocabulary only 2 images from each training folder are used
			*/
			int toUse = 2;
			if(toUse > classImages.size()){
				toUse = classImages.size();
			}
			Iterator<Entry<String, FImage>> iter = classImages.entrySet().iterator();
			/*
			 * Add remaining training images to trainingImages and trainingAnnotations
			 * */
			for(int i = 0; i < toUse; i++)
			{
				FImage f = iter.next().getValue();
				vocabLearningImages.add(f);
				trainingAnnotations.add(new AnnotatedObject<FImage,String>(f,className));
			}
			
			for(int i = toUse; i < classImages.size(); i++)
			{
				trainingAnnotations.add(new AnnotatedObject<FImage,String>(iter.next().getValue(),className));
			}
			
		}
		System.out.println("Getting Vectors");
		
		//get ALL the vectors!
		PatchExtractor extractor = new PatchExtractor();
		Set<FloatFV> vectors = new HashSet<FloatFV>();
		int patched = 0;
		int nanVectors = 0;
		for(FImage f : vocabLearningImages){
			for(FImage patch : extractor.getPatches(f)){
				FloatFV vector = extractor.getVector(patch);
				if(AppTwo.isNaNy(vector)){	//Some patches generate feature vectors with NaN values. These need to be tested for and removed
					nanVectors++;
				} else {
					vectors.add(vector);
				}
			}
			patched++;
			if(patched%100 == 0){
				System.out.println(patched + " images patched");
			}
		}
		System.out.println("NaN containing vectors: " + nanVectors);
		//System.out.println("Patching complete");
		System.out.println(vectors.size() + " vectors created");
		
		
		//figure out the k means
		Set<FloatFV> vocabulary = new KMeansFloatFV().getMeans(k, vectors);
		System.out.println("I did it!");
		/*
		 * KMeans class produces a bag-of-visual-words feature using the patches produced by the PatchExtractor
		 * */
		FloatFV[] array = new FloatFV[vocabulary.size()];
		classifier = new ClassifierFloatFV(Arrays.asList(vocabulary.toArray(array)));
		/*
		 * Training classifier using training images
		 * */
		classifier.train(trainingAnnotations);
		
		/*
		 * Calculate error from applying classifier to training set
		 * */
		double error = 0;
		int count = 0;
		for(Annotated<FImage,String> f : trainingAnnotations)
		{
			List<ScoredAnnotation<String>> predictions = classifier.annotate(f.getObject());
			float confidence = 0f; String bestPrediction = null;
			for(ScoredAnnotation<String> anno : predictions)
			{
				/*
				 * Best prediction is prediction with highest confidence
				 * */
				if(anno.confidence > confidence)
				{
					bestPrediction = anno.annotation;
					confidence = anno.confidence;
				}
			}
			if(!bestPrediction.equals(f.getAnnotations().iterator().next()))
			{
				error++;
			}
			count++;
		}
		
		trainingError = (error/count)*100;

	}

	@Override
	public Map<String, String> getClassifications(Map<String, FImage> testimages) {
		Map<String,String> classifications = new HashMap<String,String>();

		for(String key : testimages.keySet())
		{
			List<ScoredAnnotation<String>> predictions = classifier.annotate(testimages.get(key));
			float confidence = 0f; String bestPrediction = null;
			for(ScoredAnnotation<String> anno : predictions)
			{
				/*
				 * Best prediction is prediction with highest confidence
				 * */
				if(anno.confidence > confidence)
				{
					bestPrediction = anno.annotation;
					confidence = anno.confidence;
				}
			}

			classifications.put(key, bestPrediction);
		}
		
		return classifications;
	}

	@Override
	public Double getTrainingError() {
		return trainingError;
	}

	@Override
	public Double getClassificationError(Map<FImage, String> labeledTestImages) {
		Double error = 0.0;
		int count = 0;
		for(FImage img : labeledTestImages.keySet())
		{
			List<ScoredAnnotation<String>> predictions = classifier.annotate(img);
			float confidence = 0f; String bestPrediction = null;
			for(ScoredAnnotation<String> anno : predictions)
			{
				/*
				 * Best prediction is prediction with highest confidence
				 * */
				if(anno.confidence > confidence)
				{
					bestPrediction = anno.annotation;
					confidence = anno.confidence;
				}
			}

			if(!bestPrediction.equals(labeledTestImages.get(img)))
			{
				error++;
			}
			count++;
		}
		
		return (error/count)*100;
	}

}
