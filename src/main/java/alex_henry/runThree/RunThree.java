package alex_henry.runThree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openimaj.feature.ByteFV;
import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.image.FImage;
import org.openimaj.image.feature.dense.gradient.dsift.ByteDSIFTKeypoint;
import org.openimaj.image.feature.dense.gradient.dsift.DenseSIFT;
import org.openimaj.ml.annotation.Annotated;
import org.openimaj.ml.annotation.AnnotatedObject;
import org.openimaj.ml.annotation.ScoredAnnotation;

import alex_henry.interfaces.RunClassifier;

public class RunThree implements RunClassifier {

	protected ClassifierByteFV<DenseSIFT> classifier;
	protected Double trainingError = null;
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
			
			List<FImage> trImages = new ArrayList<FImage>(trainingimages.get(className).values());
			
			//Two images from each folder in the training set are used for generating the vocabulary
			int toUse = 2;
			if(toUse > trImages.size()){
				toUse = trImages.size();
			}

			for(int i = 0; i < toUse; i++)
			{
				FImage f = trImages.get(i);
				vocabLearningImages.add(f);
				trainingAnnotations.add(new AnnotatedObject<FImage,String>(f,className));
			}

			for(int i = toUse; i < trImages.size(); i++)
			{
				trainingAnnotations.add(new AnnotatedObject<FImage,String>(trImages.get(i),className));
			}
			
		}
		
		Set<ByteFV> vectors = new HashSet<ByteFV>();
		for(FImage f : vocabLearningImages)
		{
			DenseSIFT sifter = new DenseSIFT(16,16);

			//int sifted = 0;
			
			sifter.analyseImage(f);
			LocalFeatureList<ByteDSIFTKeypoint> featurePoints = sifter.getByteKeypoints();
			
			for(ByteDSIFTKeypoint point : featurePoints){
				//build a sift descriptor, add to the list of sift descriptors
				vectors.add(point.getFeatureVector());
			}
			/*sifted++;
			if(sifted%100 == 0){
				System.out.println(sifted + " images sifted");
			}*/
		}
		

		//figure out the k means
		System.out.println(vectors.size());
		Set<ByteFV> vocabulary = new KMeansByteFV().getMeans(k, vectors);
		/*
		 * KMeans class produces a bag-of-visual-words feature using the patches produced by the PatchExtractor
		 * */
		ByteFV[] array = new ByteFV[vocabulary.size()];
		classifier = new ClassifierByteFV<DenseSIFT>(Arrays.asList(vocabulary.toArray(array)),new DenseSIFT(16,16));
		//Training using entire training set
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
				//get highest confidence to get prediction
				if(anno.confidence > confidence)
				{
					bestPrediction = anno.annotation;
					confidence = anno.confidence;
				}
			}
			if(!bestPrediction.equals( f.getAnnotations().iterator().next()))
			{
				error++;
			}
			count++;
		}
		//calculate error
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
				//Finds prediction with best confidence
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
