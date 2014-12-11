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
	
	private TrainingData trainingImages;
	private Map<String,FImage> testImages;
	
	public Output(){
		testImages =  ImageLoader.loadTestingImages();
		trainingImages = ImageLoader.loadTrainingImages();
	}
	
	public static void main(String[] args)
	{
		Output output = new Output();
		
		File runOneOutput = new File("./Output/runOne.txt");
		File runTwoOutput = new File("./Output/run2.txt");
		File runThreeOutput = new File("./Output/run3.txt");
		
		RunOne one = new RunOne();
		output.writeToFile(one,runOneOutput);
		RunTwo two = new RunTwo();
		output.writeToFile(two,runTwoOutput);
		RunThree three = new RunThree();
		output.writeToFile(three,runThreeOutput);
	}
	
	public void writeToFile(RunClassifier classifier, File output)
	{
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
