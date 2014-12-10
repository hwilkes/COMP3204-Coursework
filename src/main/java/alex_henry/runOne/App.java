package alex_henry.runOne;

import java.io.File;
import java.io.IOException;

import org.apache.commons.vfs2.FileSystemException;
import org.openimaj.data.dataset.ListBackedDataset;
import org.openimaj.data.dataset.ListDataset;
import org.openimaj.data.dataset.VFSListDataset;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;


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

		//for each folder in the training data
		File folder = new File("./images/training/");
		for(File subFolder : folder.listFiles())
		{	
			//load its images
			VFSListDataset<FImage> images;
			try {
				
				images = new VFSListDataset<FImage>(subFolder.getAbsolutePath(), ImageUtilities.FIMAGE_READER);
			} catch (FileSystemException e) {
				e.printStackTrace();
				break;
			}
			//build tiny image features for each image
			ListDataset<TinyImage> tinys = new ListBackedDataset<TinyImage>();
			for(FImage f : images)
			{
				tinys.add(new TinyImage(f,16,16));
			}
			//add them to the classifiers data
			classifier.addClassValues(tinys, subFolder.getName());

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