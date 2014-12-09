package alex_henry.runThree;

import java.util.List;

import org.openimaj.feature.ByteFV;
import org.openimaj.feature.ByteFVComparison;
import org.openimaj.feature.FeatureExtractor;
import org.openimaj.feature.SparseIntFV;
import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.image.FImage;
import org.openimaj.image.feature.local.engine.DoGSIFTEngine;
import org.openimaj.image.feature.local.keypoints.Keypoint;


public class BOVWExtractorByte implements BOVWExtractor<SparseIntFV> {

	List<ByteFV> dictionary;
	DoGSIFTEngine engine;
	
	public BOVWExtractorByte(List<ByteFV> dictionary)
	{
		this.dictionary = dictionary;
		engine = new DoGSIFTEngine();
	}
	
	@Override
	public SparseIntFV extractFeature(FImage object) {
		int[] wordCounts = new int[dictionary.size()];
		for(int i = 0; i < wordCounts.length; i++)
		{
			wordCounts[i] = 0;
		}
		
		LocalFeatureList<Keypoint> keypoints  = engine.findFeatures(object);
		for(Keypoint keypoint: keypoints)
		{
			ByteFV vector = keypoint.getFeatureVector();

			ByteFV nearestWord = null;
			double nearestDistance = Double.MAX_VALUE;
			
			for(ByteFV word : dictionary){
				double distance = ByteFVComparison.EUCLIDEAN.compare(word, vector);
				if(distance < nearestDistance){
					nearestDistance = distance;
					nearestWord = word;
				}
			}
			
			wordCounts[dictionary.indexOf(nearestWord)] ++;
			
		}
		
		return new SparseIntFV(wordCounts);
	}

}
