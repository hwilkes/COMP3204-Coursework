package alex_henry.runOne;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.openimaj.data.dataset.ListBackedDataset;
import org.openimaj.data.dataset.ListDataset;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;

import alex_henry.common.ImageLoader;
import alex_henry.common.TrainingData;


/**
 * Run One classifier using K nearest neighbours on TinyImage feature vectors
 *
 */
public class App {
	
	//The number of nearest neighbours to analyze.
	//Determined using output from KDeterminer class
	private static final int K = 29;
	
	public static void main( String[] args ) {
		
		KNearestClassifier classifier = new KNearestClassifier();
		
		//load the training data
		TrainingData data = ImageLoader.loadTrainingImages();
		//for each class in the training data
		for(String className : data.getClassNames())
		{	
			Map<String,FImage> imageClass = data.getClass(className);
			//build TinyImages of their images
			ListDataset<TinyImage> tinys = new ListBackedDataset<TinyImage>();
			for(FImage i : imageClass.values()){
				tinys.add(new TinyImage(i,16,16));
			}
			//add the TinyImages to the classifiers data
			classifier.addClassValues(tinys, className);

		}
		
		//load a test image from the tall building class
		FImage testImage;
		try
		{
			testImage = ImageUtilities.readF(new File("./images/training/TallBuilding/15.jpg"));
		}
		catch (IOException e) {
			e.printStackTrace();
			return;
		}
		//show the image in a window
		String imageClass = classifier.classify(testImage, K);
		DisplayUtilities.display(testImage, imageClass);
		
	}
}