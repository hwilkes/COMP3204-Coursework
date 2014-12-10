package alex_henry.runOne;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.vfs2.FileSystemException;
import org.openimaj.data.dataset.ListBackedDataset;
import org.openimaj.data.dataset.ListDataset;
import org.openimaj.data.dataset.VFSListDataset;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;

public class RunOneOutput {
	
	static final int kNearestNeighbours = 29;
	
	public static void main(String[] args){
		
		KNearestClassifier classifier = new KNearestClassifier();
		int K = 500; //TODO set by argument?
	
		File trainingFolder = new File("./images/training");
		File testingFolder = new File("./images/testing");
		File output = new File("./Output/RunOne.txt");
		
		Map<String,FImage> testingImages = new HashMap<String,FImage>();
		for(File f : testingFolder.listFiles()){
			try {
				testingImages.put(f.getName(),ImageUtilities.readF(f));
			} catch (IOException e) {
				System.err.println("Unable to read image "+f.getName());
				
			}
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

		FileWriter fWriter = null;
		try {
			fWriter = new FileWriter(output);
		} catch (IOException e) {
			e.printStackTrace();
		}
		PrintWriter pWriter = new PrintWriter(fWriter);
		
		for(String key : testingImages.keySet())
		{
			String prediction = classifier.classify(testingImages.get(key), kNearestNeighbours);
			pWriter.println(key +" "+prediction);
		}
	}
}
