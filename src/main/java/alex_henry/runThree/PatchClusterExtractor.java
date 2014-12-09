package alex_henry.runThree;

import java.util.HashSet;
import java.util.Set;

import org.openimaj.feature.FloatFV;
import org.openimaj.image.FImage;

import alex_henry.runTwo.KMeansFloatFV;
import alex_henry.runTwo.PatchExtractor;

public class PatchClusterExtractor implements VectorExtractor<FloatFV> {

	@Override
	public Set<FloatFV> getVectors(Set<FImage> images) {
		PatchExtractor extractor = new PatchExtractor();
		Set<FloatFV> vectors = new HashSet<FloatFV>();
		int patched = 0;
		int nanVectors = 0;
		for(FImage f : images){
			for(FImage patch : extractor.getPatches(f)){
				FloatFV vector = extractor.getVector(patch);
				if(isNaNy(vector)){
					nanVectors++;
				} else {
					vectors.add(vector);
				}
			}
			patched++;
			if(patched%100 == 0){
				System.out.println(patched + " images patched");
			}
		}
		System.out.println("NaN containing vectors: " + nanVectors);
		//System.out.println("Patching complete");
		System.out.println(vectors.size() + " vectors created");
		
		int k = 500;
		//figure out the k means
		Set<FloatFV> vocabulary = new KMeansFloatFV().getMeans(k, vectors);
		return vocabulary;
	}
	
	private boolean isNaNy(FloatFV vector){
		for(float f : vector.values){
			if(Float.isNaN(f)){
				return true;
			}
		}
		return false;
	}
}
