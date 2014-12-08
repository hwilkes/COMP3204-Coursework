package alex_henry.runTwo;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.vfs2.FileSystemException;
import org.openimaj.data.dataset.VFSListDataset;
import org.openimaj.feature.FloatFV;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.ml.annotation.Annotated;
import org.openimaj.ml.annotation.AnnotatedObject;
import org.openimaj.ml.annotation.ScoredAnnotation;

public class AppTwo {
	
	public static void main(String args[])
	{
		
		//BagOfVisualWords bovw = new BagOfVisualWords(null);
		
		/*for each image : trainingSet
		 * 		vectors = getVectors()
		 * end
 * 			Set of vectors = get K means of all vectors
 * 			train classifier
		 * 		
		 * 
		 * 
		 * 
		 * */
		List<Annotated<FImage,String>> testingAnnotations = new ArrayList<Annotated<FImage,String>>();
		
		Set<FImage> trainingImages = new HashSet<FImage>();
		List<Annotated<FImage,String>> trainingAnnotations = new ArrayList<Annotated<FImage,String>>();
		//File folder = new File("./images/training");
		File folder = new File("./images/training");
		int subs = folder.listFiles().length - 1;
		int subsAdded = 0;
		for(File subFolder : folder.listFiles())
		{
			
			VFSListDataset<FImage> images;
			try {
				images = new VFSListDataset<FImage>(subFolder.getAbsolutePath(), ImageUtilities.FIMAGE_READER);
			} catch (FileSystemException e) {
				e.printStackTrace();
				break;
			}
			
			System.out.println(subsAdded++ + "/" + subs);
			
			int toUse = 2;
			if(toUse > images.size()){
				toUse = images.size();
			}
			
			for(int i = 0; i < toUse; i++)
			{
				FImage f = images.get(i);
				trainingImages.add(f);
				trainingAnnotations.add(new AnnotatedObject<FImage,String>(f,subFolder.getName()));
			}
			
			for(int i = toUse; i < images.size(); i++)
			{
				if(i == toUse)
				{
					testingAnnotations.add(new AnnotatedObject<FImage,String>(images.get(i),subFolder.getName()));
				}
				trainingAnnotations.add(new AnnotatedObject<FImage,String>(images.get(i),subFolder.getName()));
			}
			
		}
		//get ALL the vectors!
		PatchExtractor extractor = new PatchExtractor();
		Set<FloatFV> vectors = new HashSet<FloatFV>();
		int patched = 0;
		int nanVectors = 0;
		for(FImage f : trainingImages){
			for(FImage patch : extractor.getPatches(f)){
				FloatFV vector = extractor.getVector(patch);
				if(isNaNy(vector)){
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
		
		int k = 500;
		//figure out the k means
		Set<FloatFV> vocabulary = new KMeans().getMeans(k, vectors);
		System.out.println("I did it!");
		/*
		 * KMeans calss produces a bag-of-visual-words feature using the patches produced by the PatchExtractor
		 * */
		FloatFV[] array = new FloatFV[vocabulary.size()];
		Classifier classifier = new Classifier(Arrays.asList(vocabulary.toArray(array)));
		
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
		
			
		
		
		/*classify image {
		 * v = getVectors(image)
		 * 
		 * bag of words = 
		 * 
		 * */
	}
	
	public static boolean isNaNy(FloatFV vector){
		for(float f : vector.values){
			if(Float.isNaN(f)){
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unused")
	public static void testKMeans()
	{
		int k = 2;
		//figure out the k means
		Set<FloatFV> vectors = new HashSet<FloatFV>();
		float[][] pairs = new float[][]{{0.2f,0.1f},{0.1f,0.2f},{0.3f,0.2f},{0.2f,0.3f}, {0.7f,0.6f},{0.6f,0.7f},{0.8f,0.7f},{0.7f,0.8f}};
		
		for(float[] pair : pairs){
			vectors.add(new FloatFV(pair));
		}
		
		Set<FloatFV> means = new KMeans().getMeans(k, vectors);
		System.out.println("I did it!");
		
	}
}
