package alex_henry.common;

import java.util.Map;
import java.util.Set;

import org.openimaj.image.FImage;

public class TrainingData {
	
	Map<String,Map<String,FImage>> data;
	
	TrainingData(Map<String,Map<String,FImage>> data){
		this.data = data;
	}
	
	public Set<String> getClassNames(){
		return data.keySet();
	}
	
	public Map<String,FImage> getClass(String className){
		return data.get(className);
	}
	
}
