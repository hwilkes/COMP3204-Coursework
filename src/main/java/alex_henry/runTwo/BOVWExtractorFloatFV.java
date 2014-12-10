package alex_henry.runTwo;

import java.util.List;
import java.util.Set;

import org.openimaj.feature.FloatFV;
import org.openimaj.feature.FloatFVComparison;
import org.openimaj.feature.SparseFloatFV;
import org.openimaj.image.FImage;

import alex_henry.interfaces.BOVWExtractor;

/*
 * Implementation of BOWVExtractor for extracting FloatFV bag-of-visual-word features
 * */

public class BOVWExtractorFloatFV implements BOVWExtractor<SparseFloatFV> {

	List<FloatFV> dictionary;
	PatchExtractor extractor;
	
	public BOVWExtractorFloatFV(List<FloatFV> dictionary)
	{
		this.dictionary = dictionary;
		extractor = new PatchExtractor();
	}
	
	@Override
	public SparseFloatFV extractFeature(FImage object) {
		//initialise word counts
		float[] wordCounts = new float[dictionary.size()];
		for(int i = 0; i < wordCounts.length; i++)
		{
			wordCounts[i] = 0f;
		}
		
		Set<FImage> patches = extractor.getPatches(object);
		//Find nearest word for each patch in image
		for(FImage patch : patches)
		{
			FloatFV vector = extractor.getVector(patch);
			if(AppTwo.isNaNy(vector))//Ignore vectors with NaN values
			{
				continue;
			}
			FloatFV nearestWord = null;
			double nearestDistance = Double.MAX_VALUE;
			//Find word is shortest distance to patch
			for(FloatFV word : dictionary){
				double distance = FloatFVComparison.EUCLIDEAN.compare(word, vector);
				if(distance < nearestDistance){
					nearestDistance = distance;
					nearestWord = word;
				}
			}
			
			//Increase word count for nearest word to vector
			wordCounts[dictionary.indexOf(nearestWord)] ++;
			
		}
		
		return new SparseFloatFV(wordCounts);
	}

}
