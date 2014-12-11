package alex_henry.runOne;

import alex_henry.common.ImageLoader;
import alex_henry.common.KFoldCrossValidation;
import alex_henry.common.KFoldCrossValidation.KFoldResult;
import alex_henry.common.TrainingData;
import alex_henry.runThree.RunThree;

public class KDeterminer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println("K,Max Error,Mean Error,Lowest Error");
		
		TrainingData data = ImageLoader.loadTrainingImages();
		KFoldResult result = KFoldCrossValidation.getAccuracy(3,data,new RunThree());
		System.out.println("Run Three"+","+result.highestError+","+result.meanError+","+result.lowestError);
//		for(int k = 1; k < 100; k++){
//			KFoldResult result = KFoldCrossValidation.getAccuracy(10, ImageLoader.loadTrainingImages(), new RunOne(k));
//			System.out.println(k + "," + result.highestError + "," + result.meanError + "," + result.lowestError);
//		}	
	}

}
