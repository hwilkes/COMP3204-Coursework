package alex_henry.common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.openimaj.image.FImage;

import alex_henry.interfaces.RunClassifier;
import alex_henry.runOne.RunOne;
import alex_henry.runThree.RunThree;
import alex_henry.runTwo.RunTwo;

public class Output {
	public static void main(String[] args)
	{
		File runOneOutput = new File("./Output/runOne.txt");
		File runTwoOutput = new File("./Output/run2.txt");
		File runThreeOutput = new File("./Output/run3.txt");
		
		RunOne one = new RunOne();
		writeToFile(one,runOneOutput);
		RunTwo two = new RunTwo();
		writeToFile(two,runTwoOutput);
		RunThree three = new RunThree();
		writeToFile(three,runThreeOutput);
	}
	
	public static void writeToFile(RunClassifier classifier, File output)
	{
		Map<String,FImage> testImages =  ImageLoader.loadTestingImages();
		TrainingData trainingImages = ImageLoader.loadTrainingImages();
		
		classifier.giveData(trainingImages.data);
		
		Map<String,String> classifications = classifier.getClassifications(testImages);
		
		FileWriter fWriter = null;
		try {
			fWriter = new FileWriter(output);
		} catch (IOException e) {
			e.printStackTrace();
		}
		PrintWriter pWriter = new PrintWriter(fWriter);
		for(String key : classifications.keySet())
		{
			pWriter.println(key+" "+classifications.get(key));
		}
		pWriter.close();
	}
}
