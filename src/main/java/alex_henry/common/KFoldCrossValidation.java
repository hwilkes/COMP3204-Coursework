package alex_henry.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openimaj.image.FImage;

import alex_henry.interfaces.RunClassifier;

public class KFoldCrossValidation {

	public static Double getAccuracy(int k, TrainingData data, RunClassifier runner){
		List<Map<FImage,String>> labeledImages = new ArrayList<Map<FImage, String>>();

		for(int i=0; i<k; i++){
			labeledImages.add(new HashMap<FImage,String>());
		}

		//split the data into 10 random sets
		for(String className : data.getClassNames()){
			Map<String,FImage> classImages = data.getClass(className);
			List<String> randomNames = new ArrayList<String>(classImages.keySet());
			Collections.shuffle(randomNames);
			int i = 0;
			for(String imageName : randomNames){
				labeledImages.get(i % k).put(classImages.get(imageName), className);
				i++;
			}
		}


		ArrayList<Double> accuracies = new ArrayList<Double>();
		//using each 10th to test
		for(Map<FImage,String> testing : labeledImages){
			//build the training and test sets
			Map<String, Map<String,FImage>> training = new HashMap<String, Map<String,FImage>>();
			
			Integer addedImages = 0;
			
			for(Map<FImage,String> list : labeledImages){
				//build the training set using the other 10ths
				if(list != testing){
					for(FImage image : list.keySet()){
						String className = list.get(image);
						Map<String,FImage> theClass;
						//add a new class if it's not present
						if(training.containsKey(className)){
							theClass = training.get(className);
						} else {
							theClass = new HashMap<String,FImage>();
							training.put(className, new HashMap<String,FImage>());
						}
						theClass.put(addedImages.toString(), image);
					}
				}
			}
			//determine the accuracy of them
			runner.giveData(training);
			accuracies.add(runner.getClassificationError(testing));

			
		}
		//print that accuracy
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
		return mean;
	}

}
