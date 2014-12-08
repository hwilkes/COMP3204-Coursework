package alex_henry.runTwo;

import java.util.Comparator;

import org.openimaj.feature.FloatFV;
import org.openimaj.feature.FloatFVComparison;

public class FloatFVComparator implements Comparator<FloatFV>{

	FloatFV target;

	public void setTarget(FloatFV target){
		this.target = target;
	}

	@Override
	public int compare(FloatFV arg0, FloatFV arg1) {
		FloatFVComparison comp = FloatFVComparison.EUCLIDEAN;
		Double dist0 = comp.compare(arg0, target);
		Double dist1 = comp.compare(arg1, target);
		return dist0.compareTo(dist1);
	}

}
