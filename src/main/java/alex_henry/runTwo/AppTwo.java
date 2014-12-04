package alex_henry.runTwo;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.vfs2.FileSystemException;
import org.openimaj.data.dataset.VFSListDataset;
import org.openimaj.feature.FloatFV;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;

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
	
		
		Set<FImage> trainingImages = new HashSet<FImage>();
		
		File folder = new File("/home/hw17g12/Downloads/training/");
		for(File subFolder : folder.listFiles())
		{
			VFSListDataset<FImage> images;
			try {
				images = new VFSListDataset<FImage>(subFolder.getAbsolutePath(), ImageUtilities.FIMAGE_READER);
			} catch (FileSystemException e) {
				e.printStackTrace();
				break;
			}
			
			int toUse = 3;
			if(toUse > images.size()){
				toUse = images.size();
			}
			
			for(int i = 0; i < toUse; i++)
			{
				FImage f = images.get(i);
				trainingImages.add(f);
			}
		}
		//get ALL the vectors!
		PatchExtractor extractor = new PatchExtractor();
		Set<FloatFV> vectors = new HashSet<FloatFV>();
		int patched = 0;
		for(FImage f : trainingImages){
			for(FImage patch : extractor.getPatches(f)){
				vectors.add(extractor.getVector(patch));
			}
			patched++;
			if(patched%100 == 0){
				System.out.println(patched + " images patched");
			}
		}
		System.out.println("Patching complete");
		System.out.println(vectors.size() + " patches created");
		int k = 50;
		//figure out the k means
		Set<FloatFV> means = new KMeans().getMeans(k, vectors);
		System.out.println("I did it!");
		
		
		
		
		/*classify image {
		 * v = getVectors(image)
		 * 
		 * bag of words = 
		 * 
		 * */
	}
	
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
