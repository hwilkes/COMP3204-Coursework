package alex_henry.common;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;

public class ImageLoader {
	
	public static TrainingData loadTrainingImages(){
		
		Map<String,Map<String,FImage>> imageClasses = new HashMap<String,Map<String,FImage>>();
		
		File folder = new File("./images/training");
		//for each subfolder
		for(File subFolder : folder.listFiles()){
			if(subFolder.isDirectory()){
				Map<String,FImage> classImages = new HashMap<String,FImage>();
				//load each image, mapped by file name
				for(File f : subFolder.listFiles()){
					FImage img;
					try {
						img = ImageUtilities.readF(f);
					} catch (IOException e) {
						break;
					}
					classImages.put(f.getName(),img);
				}
				//add the class, mapped by class name
				imageClasses.put(subFolder.getName(), classImages);
			}
		}
		
		
		return new TrainingData(imageClasses);
	}
	
	public static Map<String,FImage> loadTestingImages(){
		Map<String,FImage> images = new HashMap<String,FImage>();
		
		File folder = new File("./images/testing");
		
		for(File f : folder.listFiles()){
			FImage img;
			try {
				img = ImageUtilities.readF(f);
			} catch (IOException e) {
				break;
			}
			images.put(f.getName(),img);
		}
		
		return null;
	}
	
}
