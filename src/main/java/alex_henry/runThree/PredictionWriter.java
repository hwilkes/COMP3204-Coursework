package alex_henry.runThree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFileChooser;

import org.apache.commons.vfs2.FileSystemException;
import org.openimaj.data.dataset.VFSListDataset;
import org.openimaj.feature.ByteFV;
import org.openimaj.feature.FloatFV;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.ml.annotation.Annotated;
import org.openimaj.ml.annotation.AnnotatedObject;
import org.openimaj.ml.annotation.ScoredAnnotation;

import alex_henry.interfaces.DenseSIFTExtractor;
import alex_henry.runTwo.ClassifierFloatFV;

public class PredictionWriter {
	
	public static void main(String[] args)
	{
		JFileChooser trainingChooser = new JFileChooser();
		JFileChooser testingChooser = new JFileChooser();
		trainingChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		testingChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		

	    int returnTrainingVal = trainingChooser.showOpenDialog(null);
	    int returnTestingVal = testingChooser.showOpenDialog(null);
	    if(returnTestingVal == JFileChooser.APPROVE_OPTION && returnTestingVal == JFileChooser.APPROVE_OPTION) {
	    	File trainingFolder = trainingChooser.getSelectedFile();
	    	File testingFolder = testingChooser.getSelectedFile();
	    	
	    	List<Annotated<FImage,String>> testingAnnotations = new ArrayList<Annotated<FImage,String>>();
			
			Set<FImage> trainingImages = new HashSet<FImage>();
			Set<FImage> testingImages = new HashSet<FImage>();
			List<Annotated<FImage,String>> trainingAnnotations = new ArrayList<Annotated<FImage,String>>();
			int subs = trainingFolder.listFiles().length - 1;
			int subsAdded = 0;
			
			VFSListDataset<FImage> teImages = null;
			try {
				teImages = new VFSListDataset<FImage>(testingFolder.getAbsolutePath(), ImageUtilities.FIMAGE_READER);
			} catch (FileSystemException e) {
				e.printStackTrace();
			}
			
			for(int i = 0; i < teImages.size(); i++)
			{
				testingImages.add(teImages.get(i));
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
				
				System.out.println(subsAdded++ + "/" + subs);
				
				int toUse = trImages.size();
				for(int i = 0; i < toUse; i++)
				{
					FImage f = trImages.get(i);
					trainingImages.add(f);
					trainingAnnotations.add(new AnnotatedObject<FImage,String>(f,subFolder.getName()));
				}				
			}
			
			PatchClusterExtractor patchExtractor = new PatchClusterExtractor();
			SIFTExtractor sift = new SIFTExtractor();
			DenseSIFTExtractor dSift = new DenseSIFTExtractor();
			
			Set<FloatFV> linearCluster = patchExtractor.getVectors(trainingImages);
			Set<ByteFV> siftCluster = sift.getVectors(trainingImages);
			Set<ByteFV> dsiftCluster = dSift.getVectors(trainingImages);
			
			ClassifierByteFV byteSiftClassifier = new ClassifierByteFV(Arrays.asList((siftCluster.toArray(new ByteFV[siftCluster.size()]))));
			ClassifierByteFV byteDSiftClassifier = new ClassifierByteFV(Arrays.asList((dsiftCluster.toArray(new ByteFV[siftCluster.size()]))));
			ClassifierFloatFV floatKMeansClassifier = new ClassifierFloatFV(Arrays.asList(linearCluster.toArray(new FloatFV[linearCluster.size()])));
			
			byteSiftClassifier.train(trainingAnnotations);
			byteDSiftClassifier.train(trainingAnnotations);
			floatKMeansClassifier.train(trainingAnnotations);
			
			List<Annotated<FImage,String>> linearPredictions = new ArrayList<Annotated<FImage,String>>();
			List<Annotated<FImage,String>> siftPredictions = new ArrayList<Annotated<FImage,String>>();
			List<Annotated<FImage,String>> dsiftPredictions = new ArrayList<Annotated<FImage,String>>();
			
			for(FImage image : testingImages)
			{
				
				List<ScoredAnnotation<String>> linearAnnotations = floatKMeansClassifier.annotate(image);
				List<ScoredAnnotation<String>> siftAnnotations = byteSiftClassifier.annotate(image);
				List<ScoredAnnotation<String>> dsiftAnnotations = byteDSiftClassifier.annotate(image);

				Annotated<FImage,String> linearClass = null; float linearConfidence = 0f;
				Annotated<FImage,String> siftClass = null; float siftConfidence = 0f;
				Annotated<FImage,String> dsiftClass = null; float dsiftConfidence = 0f;
				for(ScoredAnnotation<String> anno : linearAnnotations)
				{
					if(linearConfidence < anno.confidence)
						linearClass = new AnnotatedObject<FImage,String>(image,anno.annotation);
				}
				for(ScoredAnnotation<String> anno : siftAnnotations)
				{
					if(siftConfidence < anno.confidence)
						siftClass = new AnnotatedObject<FImage,String>(image,anno.annotation);
				}
				for(ScoredAnnotation<String> anno : dsiftAnnotations)
				{
					if(dsiftConfidence < anno.confidence)
						dsiftClass= new AnnotatedObject<FImage,String>(image,anno.annotation);
				}
				
				linearPredictions.add(linearClass);
				siftPredictions.add(siftClass);
				dsiftPredictions.add(dsiftClass);
			}
			
			File linearOutput = new File("./Output/linearTestingPredictions.txt");
			File siftOutput = new File("./Output/siftTestingPredictions.txt");
			File dsiftOutput = new File("./Output/dsiftTestingPredictions.txt");

			
			for(Annotated<FImage,String> anno : linearPredictions)
			{
				
			}
			for(Annotated<FImage,String> anno : siftPredictions){}
			for(Annotated<FImage,String> anno : dsiftPredictions){}
	    }
	}
}
