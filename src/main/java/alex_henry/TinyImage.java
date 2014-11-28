package alex_henry;

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
			System.out.println("TEST");
			int toDrop = original.height - original.width;
			int topDrop = toDrop/2;
			int bottomDrop = toDrop-topDrop;

			for(int i = topDrop; i < original.height - bottomDrop; i++ )
			{
				newPixels[i] = original.pixels[i].clone();
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
					System.out.print(newPixels[j][i-leftDrop]);
					
				}
				System.out.println();
			}
		}
		else
		{
			newPixels = original.pixels.clone();
			System.out.println("TEST2");
		}
		square = new FImage(newPixels);
		processed = ResizeProcessor.resample(new FImage(newPixels), x, y);

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
