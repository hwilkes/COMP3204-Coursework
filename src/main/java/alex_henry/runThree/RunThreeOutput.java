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
import org.openimaj.image.feature.dense.gradient.dsift.ByteDSIFTKeypoint;
import org.openimaj.image.feature.dense.gradient.dsift.DenseSIFT;
import org.openimaj.image.feature.dense.gradient.dsift.PyramidDenseSIFT;
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

			for(int i = 0; i < trImages.size(); i++)
			{

				trainingAnnotations.add(new AnnotatedObject<FImage,String>(trImages.get(i),subFolder.getName()));
			}
			
		}
		Set<ByteFV> vectors = new HashSet<ByteFV>();
		PyramidDenseSIFT<FImage> pds = new PyramidDenseSIFT<FImage>(new DenseSIFT(), 0, 8,16,24,32);
		for(FImage f : trainingImages)
		{
			pds.analyseImage(f);
			//int sifted = 0;
			LocalFeatureList<ByteDSIFTKeypoint> featurePoints = pds.getByteKeypoints();

			for(ByteDSIFTKeypoint point : featurePoints){

				//build a sift descriptor, add to the list of sift descriptors
				vectors.add(point.getFeatureVector());
				/*sifted++;
				if(sifted%100 == 0){
					System.out.println(sifted + " images sifted");
				}*/
			}
		}
		
		
		

		//figure out the k means
		Set<ByteFV> vocabulary = new KMeansByte().getMeans(k, vectors);
		/*
		 * KMeans class produces a bag-of-visual-words feature using the patches produced by the PatchExtractor
		 * */
		ByteFV[] array = new ByteFV[vocabulary.size()];
		ClassifierByteFV<PyramidDenseSIFT<FImage>> classifier = new ClassifierByteFV<PyramidDenseSIFT<FImage>>(Arrays.asList(vocabulary.toArray(array)),new PyramidDenseSIFT<FImage>(new DenseSIFT(), 0, 8,16,24,32));

		classifier.train(trainingAnnotations);
				
		double error = 0;
		int count = 0;
		for(Annotated<FImage,String> f : trainingAnnotations)
		{
			List<ScoredAnnotation<String>> predictions = classifier.annotate(f.getObject());
			float confidence = 0f; String bestPrediction = null;
			for(ScoredAnnotation<String> anno : predictions)
			{
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
		error = (error/count)*100;
		System.out.println("Percentage Error: "+error);
		File errOutput = new File("./Output/RunThreeError.txt");
		FileWriter fEWriter = null;
		try {
			fEWriter = new FileWriter(errOutput);
		} catch (IOException e) {
			e.printStackTrace();
		}
		PrintWriter pEWriter = new PrintWriter(fEWriter);
		pEWriter.println("Percentage Error: "+error);
		
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
