package alex_henry.runThree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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

/**
 * Run 3 - classifier using visual words built from dense sift feature vectors
 * This is a test implementation which was moved into the RunThree class and RunClassifier interface
 */
public class DenseSift {
	
	public static final int SIZE = 16;
	
	public static void main(String[] args)
	{

		List<Annotated<FImage,String>> testingAnnotations = new ArrayList<Annotated<FImage,String>>();
		Set<FImage> trainingImages = new HashSet<FImage>();
		List<Annotated<FImage,String>> trainingAnnotations = new ArrayList<Annotated<FImage,String>>();
		
		TrainingData data = ImageLoader.loadTrainingImages();
		
		int subs = data.getClassNames().size();
		int subsAdded = 0;
		for(String className: data.getClassNames())
		{

			ArrayList<FImage> images = new ArrayList<FImage>(data.getClass(className).values());
			
			System.out.println(subsAdded++ + "/" + subs);
			
			int toUse = 2;
			if(toUse > images.size()){
				toUse = images.size();
			}
			
			for(int i = 0; i < images.size(); i++)
			{
				FImage image = images.get(i);
				if(i < toUse){
					trainingImages.add(image);
					trainingAnnotations.add(new AnnotatedObject<FImage,String>(image,className));
				} else if (i == toUse){
					testingAnnotations.add(new AnnotatedObject<FImage,String>(image,className));
				} else {
					trainingAnnotations.add(new AnnotatedObject<FImage,String>(image,className));
				}
				
			}
		}

		Set<ByteFV> vectors = new HashSet<ByteFV>();
		for(FImage f : trainingImages)
		{
			DenseSIFT sifter = new DenseSIFT(SIZE,SIZE);
			
			sifter.analyseImage(f);
			LocalFeatureList<ByteDSIFTKeypoint> featurePoints = sifter.getByteKeypoints();
			
			for(ByteDSIFTKeypoint point : featurePoints){
				//build a sift descriptor, add to the list of sift descriptors
				vectors.add(point.getFeatureVector());
			}
		}

		//System.out.println("Patching complete");
		System.out.println(vectors.size() + " vectors created");

		int k = 500;
		//figure out the k means
		Set<ByteFV> vocabulary = new KMeansByteFV().getMeans(k, vectors);
		System.out.println("I did it!");
		/*
		 * KMeans class produces a bag-of-visual-words feature using the patches produced by the PatchExtractor
		 * */
		ByteFV[] array = new ByteFV[vocabulary.size()];
		ClassifierByteFV<DenseSIFT> classifier = new ClassifierByteFV<DenseSIFT>(Arrays.asList(vocabulary.toArray(array)),new DenseSIFT(SIZE,SIZE));
        
		classifier.train(trainingAnnotations);
        
        
		for(Annotated<FImage,String> anno : testingAnnotations)
		{
			List<ScoredAnnotation<String>> annotations = classifier.annotate(anno.getObject());
			System.out.println("Actual class:" + anno.getAnnotations() );
			System.out.println("Number of predicted annotations: "+annotations.size());
			for(ScoredAnnotation<String> anno2 : annotations)
			{
				System.out.println("Label: "+anno2.annotation+" Confidence: "+anno2.confidence);
			}
			System.out.println();
		}
	}
}
