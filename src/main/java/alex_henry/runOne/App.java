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
 * OpenIMAJ Hello world!
 *
 */
public class App {
	public static void main( String[] args ) {

		KNearestClassifier classifier = new KNearestClassifier();


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
			ListDataset<TinyImage> tinys = new ListBackedDataset<TinyImage>();
			for(FImage f : images)
			{
				tinys.add(new TinyImage(f,16,16));
			}
			classifier.addClassValues(tinys, subFolder.getName());

		}
		FImage testImage;
		try
		{
			testImage = ImageUtilities.readF(new File("/home/hw17g12/Downloads/training/TallBuilding/15.jpg"));

		}
		catch (IOException e) {
			e.printStackTrace();
			return;
		}

		String imageClass = classifier.classify(testImage, 9);
		DisplayUtilities.display(testImage, imageClass);
		
	}
}