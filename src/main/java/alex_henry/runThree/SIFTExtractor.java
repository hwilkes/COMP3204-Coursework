package alex_henry.runThree;

import java.util.HashSet;
import java.util.Set;

import org.openimaj.feature.ByteFV;
import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.image.FImage;
import org.openimaj.image.feature.local.engine.DoGSIFTEngine;
import org.openimaj.image.feature.local.keypoints.Keypoint;

public class SIFTExtractor implements VectorExtractor<ByteFV> {

	@Override
	public Set<ByteFV> getVectors(Set<FImage> images) {
		DoGSIFTEngine engine = new DoGSIFTEngine();
		Set<ByteFV> vectors = new HashSet<ByteFV>();
		int sifted = 0;
		for(FImage f : images){
			//for each sift feature
			//get the keypoints
			
			LocalFeatureList<Keypoint> queryKeypoints = engine.findFeatures(f);
			for(Keypoint point : queryKeypoints)
			{
				vectors.add(point.getFeatureVector());
			}
			
			sifted++;
			if(sifted%100 == 0){
				System.out.println(sifted + " images sifted");
			}
		}
		//System.out.println("Patching complete");
		System.out.println(vectors.size() + " vectors created");
		
		int k = 500;
		//figure out the k means
		Set<ByteFV> vocabulary = new KMeansByte().getMeans(k, vectors);
		System.out.println("I did it!");
		return vocabulary;
	}

}
