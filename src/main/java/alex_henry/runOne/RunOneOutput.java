package alex_henry.runOne;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.vfs2.FileSystemException;
import org.openimaj.data.dataset.ListBackedDataset;
import org.openimaj.data.dataset.ListDataset;
import org.openimaj.data.dataset.VFSListDataset;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;

public class RunOneOutput {
	
	public static void main(String[] args){
		
		KNearestClassifier classifier = new KNearestClassifier();
		int K = 9; //TODO set by argument?
	
		File trainingFolder = new File("./images/training");
		File testingFolder = new File("./images/testing");
		
		List<String> fileNames = new ArrayList<String>();
		
		VFSListDataset<FImage> testingImages;
		try {
			testingImages = new VFSListDataset<FImage>(testingFolder.getAbsolutePath(), ImageUtilities.FIMAGE_READER);
			
		} catch (FileSystemException e) {
			e.printStackTrace();
			return;
		}
		for(File f : testingFolder.listFiles())
		{
			fileNames.add(f.getName());
		}
		
		
		for(File subFolder : trainingFolder.listFiles())
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

		for(int i = 0; i < testingImages.size(); i++)
		{
			String prediction = classifier.classify(testingImages.get(i), K);
			System.out.println(fileNames.get(i)+" "+prediction);

		}
	}
}
