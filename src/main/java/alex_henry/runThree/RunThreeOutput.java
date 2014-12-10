package alex_henry.runThree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.vfs2.FileSystemException;
import org.openimaj.data.dataset.VFSListDataset;
import org.openimaj.feature.ByteFV;
import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.feature.local.engine.BasicGridSIFTEngine;
import org.openimaj.image.feature.local.keypoints.Keypoint;
import org.openimaj.ml.annotation.Annotated;
import org.openimaj.ml.annotation.AnnotatedObject;
import org.openimaj.ml.annotation.ScoredAnnotation;

public class RunThreeOutput {
	public static void main(String[] args)
	{
		int k = 500;
	
		Set<FImage> trainingImages = new HashSet<FImage>();
		List<Annotated<FImage,String>> trainingAnnotations = new ArrayList<Annotated<FImage,String>>();
		File testingFolder = new File("./images/testing");
		File trainingFolder = new File("./images/training");
		
		Map<String,FImage> teImages = new HashMap<String,FImage>();
		for(File f : testingFolder.listFiles()){
			try {
				teImages.put(f.getName(),ImageUtilities.readF(f));
			} catch (IOException e) {
				System.err.println("Unable to read image "+f.getName());
				
			}
		}
	
		for(File subFolder : trainingFolder.listFiles())
		{
			
			VFSListDataset<FImage> trImages;
			try {
				trImages = new VFSListDataset<FImage>(subFolder.getAbsolutePath(), ImageUtilities.FIMAGE_READER);
			} catch (FileSystemException e) {
				e.printStackTrace();
				break;
			}
			
			


			
		}
		//get ALL the vectors!
		Set<ByteFV> vectors = new HashSet<ByteFV>();
		for(FImage f : trainingImages)
		{
			BasicGridSIFTEngine engine = new BasicGridSIFTEngine(false);

			LocalFeatureList<Keypoint> featurePoints = engine.findFeatures(f);
			
			for(Keypoint point : featurePoints){

				//build a sift descriptor, add to the list of sift descriptors
				vectors.add(point.getFeatureVector());
			}
		}
		
		
		

		//figure out the k means
		Set<ByteFV> vocabulary = new KMeansByte().getMeans(k, vectors);
		/*
		 * KMeans class produces a bag-of-visual-words feature using the patches produced by the PatchExtractor
		 * */
		ByteFV[] array = new ByteFV[vocabulary.size()];
		ClassifierByteFV classifier = new ClassifierByteFV(Arrays.asList(vocabulary.toArray(array)));

		classifier.train(trainingAnnotations);
				
		File output = new File("./Output/RunThree.txt");
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
				if(anno.confidence > confidence)
				{
					bestPrediction = anno.annotation;
					confidence = anno.confidence;
				}
			}
			pWriter.println(key + " " + bestPrediction);
		}
		
	}
}
