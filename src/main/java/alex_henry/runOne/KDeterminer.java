package alex_henry.runOne;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.apache.commons.vfs2.FileSystemException;
import org.openimaj.data.dataset.VFSListDataset;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.ml.annotation.Annotated;
import org.openimaj.ml.annotation.AnnotatedObject;

public class KDeterminer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// load the images
		List<Annotated<TinyImage,String>> labeledImages = new ArrayList<Annotated<TinyImage, String>>();
		File folder = new File("./images/training/");
		for(File subFolder : folder.listFiles())
		{
				
			VFSListDataset<FImage> images = null;
			try {
				images = new VFSListDataset<FImage>(subFolder.getAbsolutePath(), ImageUtilities.FIMAGE_READER);
			} catch (FileSystemException e) {
				e.printStackTrace();
				break;
			}
			
			for(FImage f : images)
			{
				labeledImages.add(new AnnotatedObject<TinyImage,String>(new TinyImage(f,16,16),subFolder.getName()));
			}
			
		}
		
		//split them into randomly ordered 10ths
		final int folds = 10;
		List<List<Annotated<TinyImage,String>>> imageSelections = new ArrayList<List<Annotated<TinyImage,String>>>();
		for(int i = 0; i < folds; i++){
			imageSelections.add(new ArrayList<Annotated<TinyImage,String>>());
		}
		int added = 0;
		Random rnd = new Random();
		while(! labeledImages.isEmpty()){
			int n = rnd.nextInt(labeledImages.size());
			List<Annotated<TinyImage,String>> list = imageSelections.get(added % folds);
			list.add(labeledImages.get(n));
			added++;
			labeledImages.remove(n);
		}
		//for values of k
		System.out.println("K,Max Accuracy,Mean Accuracy,Min Accuracy");
		for(int k = 1; k<100;k++){
			ArrayList<Double> accuracies = new ArrayList<Double>();
			//for each 10th
			for(List<Annotated<TinyImage,String>> testing : imageSelections){
				//build the training and test sets
				List<Annotated<TinyImage,String>> training = new ArrayList<Annotated<TinyImage,String>>();
				for(List<Annotated<TinyImage,String>> list : imageSelections){
					if(list != testing){
						training.addAll(list);
					}
				}
				//determine the accuracy of them
				KNearestClassifier classifer = new KNearestClassifier();
				for(Annotated<TinyImage,String> anno : training){
					classifer.addClassValue(anno.getObject(), anno.getAnnotations().iterator().next());
				}
				int correct = 0;
				int wrong = 0;
				for(Annotated<TinyImage,String> test : testing){
					String result = classifer.classify(test.getObject(), k);
					Collection<String> truth = test.getAnnotations();
					if(truth.contains(result)){
						correct++;
					} else {
						wrong++;
					}
				}
				accuracies.add(new Double((double)correct/(double)(correct+wrong)));
				
				//print that accuracy
			}
			double sum = 0;
			double lowest = Double.MAX_VALUE;
			double highest = Double.MIN_VALUE;
			for(Double d : accuracies){
				sum += d;
				if(lowest > d){
					lowest = d;
				}
				if(highest < d){
					highest = d;
				}
			}
			double mean = sum / accuracies.size();
			System.out.println(k + "," + highest + "," + mean + "," + lowest);
		}
	}

}
