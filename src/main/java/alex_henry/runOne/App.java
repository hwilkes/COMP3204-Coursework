package alex_henry;

import java.net.URL;

import javax.swing.JFileChooser;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.processing.convolution.FGaussianConvolve;
import org.openimaj.image.typography.hershey.HersheyFont;


/**
 * OpenIMAJ Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
    	MBFImage image = null;
    	
    	try{
    		image = ImageUtilities.readMBF(new URL("http://comp3204.ecs.soton.ac.uk/images/johnny5.png"));
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		return;
    	}
        TinyImage tinyImage = new TinyImage(image.flatten(),16,16);
        
        //Display the image
        DisplayUtilities.display(image);
        DisplayUtilities.display(tinyImage.getSquare());
        DisplayUtilities.display(tinyImage.getProcessed());
    }
}
