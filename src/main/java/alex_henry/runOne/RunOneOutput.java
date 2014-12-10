package alex_henry.runOne;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.vfs2.FileSystemException;
import org.openimaj.data.dataset.ListBackedDataset;
import org.openimaj.data.dataset.ListDataset;
import org.openimaj.data.dataset.VFSListDataset;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.ml.annotation.Annotated;
import org.openimaj.ml.annotation.AnnotatedObject;

import alex_henry.common.ImageLoader;
import alex_henry.common.TrainingData;

/*
 * Based on code from alex_henry.runOne.App
 * */

public class RunOneOutput {
	
	//value of nearest neighbours used by KMeans classifier
	static final int kNearestNeighbours = 29;
	
	public static void main(String[] args){
		
		KNearestClassifier classifier = new KNearestClassifier();
	
		List<Annotated<FImage,String>> trainingAnnotations = new ArrayList<Annotated<FImage,String>>();

		File trainingFolder = new File("./images/training");
		File testingFolder = new File("./images/testing");
		File output = new File("./Output/run1.txt");
		/*
		*mapping test set images to their filenames
		*/
		Map<String,FImage> testingImages = ImageLoader.loadTestingImages();
		TrainingData trData = ImageLoader.loadTrainingImages();

		for(String className : trData.getClassNames())
		{
			Map<String,FImage> classImages = trData.getClass(className);
			
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
		System.out.println("Percentage Error: "+error);
		
		/*
		 * Write error to file
		 * */
		File errOutput = new File("./Output/RunOneError.txt");
		FileWriter fEWriter = null;
		try {
			fEWriter = new FileWriter(errOutput);
		} catch (IOException e) {
			e.printStackTrace();
		}
		PrintWriter pEWriter = new PrintWriter(fEWriter);
		pEWriter.println("Percentage Error: "+error);
		
		
		FileWriter fWriter = null;
		try {
			fWriter = new FileWriter(output);
		} catch (IOException e) {
			e.printStackTrace();
		}
		PrintWriter pWriter = new PrintWriter(fWriter);
		
		/*
		 * Get classifications for test set and write to file
		 * */
		for(String key : testingImages.keySet())
		{
			String prediction = classifier.classify(testingImages.get(key), kNearestNeighbours);
			pWriter.println(key +" "+prediction);
		}
	}
}
