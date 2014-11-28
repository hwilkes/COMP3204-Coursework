package alex_henry.runOne;


import org.openimaj.feature.FloatFV;
import org.openimaj.image.FImage;
import org.openimaj.image.processing.resize.ResizeProcessor;

public class TinyImage  {

	protected FImage original;
	protected FImage processed;
	protected FImage square;

	
	public TinyImage(FImage original, int x, int y)
	{
		this.original = original;
		float[][] newPixels;

		if(original.height > original.width)
		{
			newPixels = new float[original.width][original.width];
			int toDrop = original.height - original.width;
			int topDrop = toDrop/2;
			int bottomDrop = toDrop-topDrop;

			for(int i = topDrop; i < original.height - bottomDrop; i++ )
			{
				newPixels[i-topDrop] = original.pixels[i].clone();
			}

		}
		else if(original.height < original.width)
		{
			newPixels = new float[original.height][original.height];

			int toDrop = original.width - original.height;
			int leftDrop = toDrop/2;
			int rightDrop = toDrop-leftDrop;

			for(int j = 0; j < original.height; j++)
			{
				for(int i = leftDrop; i < original.width - rightDrop; i++)
				{
					newPixels[j][i-leftDrop] = original.pixels[j][i];
					
				}
			}
		}
		else
		{
			newPixels = original.pixels.clone();
		}
		
		square = new FImage(newPixels);
		processed = ResizeProcessor.resample(new FImage(newPixels), x, y);

		double sum = 0.0;
		float smallest = Float.MAX_VALUE;
		float biggest = Float.MIN_VALUE;
		
		for(float[] row : processed.pixels)
		{
			for(float pixel : row)
			{
				sum =+ pixel;			
				if(pixel > biggest)
				{
					biggest = pixel;
				}
				if(pixel < smallest)
				{
					smallest = pixel;
				}
			}
		}
		float scale = 1/(biggest - smallest);
		float mean = (float)(sum/(processed.width*processed.height));
		/*
		 * Set image to have a zero mean
		 * 
		 * */

		
		for(int i = 0; i < processed.height; i++)
		{
			for(int j = 0; j < processed.width; j++)
			{
				processed.pixels[i][j] = scale*(processed.pixels[i][j] - mean); 
			}
		}
		
		
	}
	
	public FloatFV getVector()
	{
		float[] v = new float[processed.width*processed.height];
		
		for(int x = 0; x < processed.height; x++)
		{
			for(int y = 0; y < processed.width; y++)
			{
				v[x*processed.width+y] = processed.pixels[x][y];	
			}
		}
		
		return new FloatFV(v);
	}
	
	public FImage getSquare()
	{
		return square;
	}

	public FImage getOriginal()
	{
		return original;
	}
	
	public FImage getProcessed()
	{
		return processed;
	}

}
