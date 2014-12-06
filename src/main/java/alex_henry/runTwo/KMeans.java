package alex_henry.runTwo;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.openimaj.feature.FloatFV;
import org.openimaj.feature.FloatFVComparison;
import org.openimaj.util.function.Operation;
import org.openimaj.util.parallel.Parallel;

public class KMeans {

	private Random r = new Random();
	
	public Set<FloatFV> getMeans(final int k, Set<FloatFV> vectors)
	{
		if(k > vectors.size())
			throw new IllegalArgumentException("K is greater than vector size");

		//assume k means to using existing items
		Set<FloatFV> means = new HashSet<FloatFV>();
//		Iterator<FloatFV> iter = vectors.iterator();
//		for(int i = 0; i < k; i++)
//		{
//			means.add(iter.next());
//		}
		Vector vec = new Vector(Arrays.asList(vectors.toArray()));
		
		for(int i = 0; i < k; i++)
		{
			FloatFV toAdd = (FloatFV) vec.remove(r.nextInt(vec.size()));
			means.add(toAdd);
			System.out.println(vectors.size());
		}

		Map<FloatFV, Set<FloatFV>> prevMap = null;
		Map<FloatFV, Set<FloatFV>> map = null;
		
		int loops = 0;
		
		do{
			prevMap = map;
			//mean to collection of nearests
			//final Map<FloatFV, List<FloatFV>> finalMap = new HashMap<FloatFV,List<FloatFV>>();
			map = new HashMap<FloatFV, Set<FloatFV>>();
			final Map<FloatFV, Set<FloatFV>> finalMap = Collections.synchronizedMap(map);
			
			final Set<FloatFV> meanClone = new HashSet<FloatFV>();
			for(FloatFV f : means)
			{
				meanClone.add(f);
			}
			for(FloatFV mean : means){
				map.put(mean, Collections.synchronizedSet(new HashSet<FloatFV>()));
			}
			
			//for each vector
			Parallel.forEach(vectors, new Operation<FloatFV>(){
				FloatFVComparison euc = FloatFVComparison.EUCLIDEAN;
				@Override
				public void perform(FloatFV object) {

					double nearestDistance = Double.MAX_VALUE;
					FloatFV nearestMean = null;

					//get the nearest mean
					for(FloatFV mean : meanClone)
					{
						double distance = euc.compare(mean, object);
						if(distance < nearestDistance)
						{
							nearestDistance = distance;
							nearestMean = mean;
						}
					}
					//assign it to the nearest mean
					finalMap.get(nearestMean).add(object);
				}
			});

			System.out.println("loop " + loops + " Assignment complete");
			//recalculate the mean to be the average of its assigned values

			final Set<FloatFV> updatedMeans = Collections.synchronizedSet(new HashSet<FloatFV>());
			//final int updated = 0;
			Parallel.forEach(means,new Operation<FloatFV>(){

						@Override
						public void perform(FloatFV object) {
							Set<FloatFV> nears = finalMap.get(object);
							
							if(nears.isEmpty())
								return;
							
							Double[] array = new Double[object.length()];
							for(int i = 0; i < object.length(); i++){
								array[i] = 0.0;
							}
							
							for(FloatFV n : nears)
							{
								for(int i = 0; i < n.length(); i++)
								{
									array[i] += (double) n.values[i];
								}				
							}
			
							float[] fArray = new float[object.length()];
							for(int i = 0; i < array.length; i++)
							{
								fArray[i] = (float) (array[i]/nears.size());
							}
							
							
							FloatFV newMean = new FloatFV(fArray);
							updatedMeans.add(newMean);

						}
				
					});
			

			means = updatedMeans;
			System.out.println("updates completed for loop:" + loops + " means:" + means.size() + "\n");
			loops++;
			
		}while(!map.equals(prevMap) );
		
		for(FloatFV f : means)
		{
			System.out.println(f.toString());;
		}
		return means;
	}


	static class VectorComparison implements Comparator<FloatFV>{

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

}
