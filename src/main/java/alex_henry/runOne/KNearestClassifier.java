package alex_henry.runOne;

import java.util.HashMap;

import org.openimaj.data.dataset.ListBackedDataset;
import org.openimaj.data.dataset.ListDataset;
import org.openimaj.feature.FloatFV;
import org.openimaj.image.FImage;

public class KNearestClassifier {
	
	HashMap<String,ListDataset<FloatFV>> classes; 
	
	public KNearestClassifier()
	{
		classes = new HashMap<String,ListDataset<FloatFV>>();
	}
	
	public String classify(FImage image,int kValue)
	{
		TinyImage tiny = new TinyImage(image,16,16);
		
		FloatFV v = tiny.getVector();
		
		
		
		return "POO";
	}
	
	public void addClassValues(ListDataset<TinyImage> group,String setName)
	{
		if(classes.containsKey(setName)){
			ListDataset<FloatFV> existing = classes.get(setName);
			for(TinyImage i : group){
				existing.add(i.getVector());
			}
		} else {
			ListDataset<FloatFV> vectors = new ListBackedDataset<FloatFV>();
			for(TinyImage i : group){
				vectors.add(i.getVector());
			}
			classes.put(setName, vectors);
		}
	}
	
}
