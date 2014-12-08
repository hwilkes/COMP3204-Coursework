package alex_henry.runTwo;

import java.util.HashSet;
import java.util.Set;

import org.openimaj.feature.FloatFV;
import org.openimaj.image.FImage;

public class PatchExtractor {
	
	protected int size;
	protected int sampleSize;
	
	public PatchExtractor(int size, int sampleSize)
	{
		this.size = size;
		this.sampleSize = sampleSize;
	}
	
	public PatchExtractor()
	{
		size = 8;
		sampleSize = 4;
	}
	
	public Set<FImage> getPatches(FImage image)
	{
		Set<FImage> patches = new HashSet<FImage>();

		for(int x = 0; x < image.width-size; x += sampleSize)
		{
			for(int y = 0; y < image.height-size; y += sampleSize)
			{
				FImage patch = image.extractROI(x, y, size, size);
				
				patches.add(patch.normalise());
			}
		}
		return patches;
	} 
	
	public FloatFV getVector(FImage patch)
	{
		float[] v = new float[patch.width*patch.height];
		
		for(int x = 0; x < patch.height; x++)
		{
			for(int y = 0; y < patch.width; y++)
			{
				v[x*patch.width+y] = patch.pixels[x][y];	
			}
		}
		
		return new FloatFV(v);
	}
	
}
