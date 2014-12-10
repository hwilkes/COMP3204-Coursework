package alex_henry.runTwo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openimaj.feature.FloatFV;
import org.openimaj.image.FImage;

/*
 * Class for extracting set of patch samples from image, return patches in set
 * Patches are normalised before they are returned
 * */

public class PatchExtractor {
	
	protected int size; //Size of patches produce (size X size)
	protected int sampleSize; //Distance between samples
	
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
	
	//Returns unordered set of patches for image
	public Set<FImage> getPatches(FImage image)
	{
		Set<FImage> patches = new HashSet<FImage>();

		for(int x = 0; x < image.width-size; x += sampleSize)
		{
			for(int y = 0; y < image.height-size; y += sampleSize)
			{
				FImage patch = image.extractROI(x, y, size, size); //Extracts patch from image
				
				patches.add(patch.normalise()); //Normalising patch
			}
		}
		return patches;
	} 
	
	//Similar as above method, return patches in an ordered List of Columns
	public List<List<FImage>> getOrderedPatches(FImage image)
	{
		List<List<FImage>> patches = new ArrayList<List<FImage>>();
		
		for(int x = 0; x < image.width-size; x += sampleSize)
		{
			ArrayList<FImage> line = new ArrayList<FImage>();
			for(int y = 0; y < image.height-size; y += sampleSize)
			{
				FImage patch = image.extractROI(x, y, size, size);
				
				line.add(patch.normalise());
			}
			
			patches.add(line);
		}
		
		return patches;
	}
	
	/*
	 * Returns FloatFV feature vector for patch
	 * */
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
