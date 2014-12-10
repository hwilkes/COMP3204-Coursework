package alex_henry.runThree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
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

import alex_henry.common.ImageLoader;
import alex_henry.common.TrainingData;

/*
 * Based on code from alex_henry.runThree.DenseSift
 * 
 * */

public class RunThreeOutput {
	public static void main(String[] args)
	{
		/*
		 * Number of means to be generated for K-Means vocabulary
		 * */		
		int k = 500;
	
		Set<FImage> trainingImages = new HashSet<FImage>();
		List<Annotated<FImage,String>> trainingAnnotations = new ArrayList<Annotated<FImage,String>>();
		
		//map of training set to filenames
		Map<String,FImage> teImages = ImageLoader.loadTestingImages();
	
		TrainingData data = ImageLoader.loadTrainingImages();
		
		for(String className : data.getClassNames())
		{
			
			List<FImage> trImages = new ArrayList<FImage>(data.getClass(className).values());
			
			//Two images from each folder in the training set are used for generating the vocabulary
			int toUse = 2;
			if(toUse > trImages.size()){
				toUse = trImages.size();
			}

			for(int i = 0; i < toUse; i++)
			{
				FImage f = trImages.get(i);
				trainingImages.add(f);
				trainingAnnotations.add(new AnnotatedObject<FImage,String>(f,className));
			}

			for(int i = toUse; i < trImages.size(); i++)
			{
				trainingAnnotations.add(new AnnotatedObject<FImage,String>(trImages.get(i),className));
			}
			
		}
		
		Set<ByteFV> vectors = new HashSet<ByteFV>();
		for(FImage f : trainingImages)
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
		Set<ByteFV> vocabulary = new KMeansByte().getMeans(k, vectors);
		/*
		 * KMeans class produces a bag-of-visual-words feature using the patches produced by the PatchExtractor
		 * */
		ByteFV[] array = new ByteFV[vocabulary.size()];
		ClassifierByteFV<DenseSIFT> classifier = new ClassifierByteFV<DenseSIFT>(Arrays.asList(vocabulary.toArray(array)),new DenseSIFT(16,16));
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
		error = (error/count)*100;
		System.out.println("Percentage Error: "+error);
		
		
		//Setup to write error to file
		File errOutput = new File("./Output/run3Error.txt");
		FileWriter fEWriter = null;
		try {
			fEWriter = new FileWriter(errOutput);
		} catch (IOException e) {
			e.printStackTrace();
		}
		PrintWriter pEWriter = new PrintWriter(fEWriter);
		pEWriter.println("Percentage Error: "+error);
		
		//Setup to write results to file
		File output = new File("./Output/run3.txt");
		FileWriter fWriter = null;
		try {
			fWriter = new FileWriter(output);
		} catch (IOException e) {
			e.printStackTrace();
		}
		PrintWriter pWriter = new PrintWriter(fWriter);

		
		for(String key : teImages.keySet())
		{
			List<ScoredAnnotation<String>> predictions = classifier.annotate(teImages.get(key));
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
			System.out.println(key + " " + bestPrediction);
			pWriter.println(key + " " + bestPrediction);
		}
		pWriter.close();
	}
}
