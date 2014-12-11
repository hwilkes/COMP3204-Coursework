package alex_henry.interfaces;

import java.util.Map;

import org.openimaj.image.FImage;

/*
 * Interfaces for classes which implement the classifier described for their run
 * Includes methods for training classifiers, and annotating sets of training data
 * Can also test error rates of classifier
 * */

public interface RunClassifier {
	//Trains classifier NOTE: This generally needs to be called before any other method in interface to initialise classifier
	//Argumene is a map of image classes to a map of image filenames to the images themselves
	public void giveData(Map<String,Map<String,FImage>> trainingimages);
	//Classifies set of training images, argument is map of training images filenames to the images themselves
	//Returns map of filenames to classifications
	public Map<String,String> getClassifications(Map<String,FImage> testimages);
	//Gets error on training set, should be calculated in giveData method
	public Double getTrainingError();
	//Gets classification error on a labeled set of test images
	public Double getClassificationError(Map<FImage,String> labeledTestImages);
}
