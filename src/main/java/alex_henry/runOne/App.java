package alex_henry.runOne;

import java.net.URL;


import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;


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
