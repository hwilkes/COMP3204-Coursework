package alex_henry.runTwo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.openimaj.feature.FloatFV;
import org.openimaj.feature.FloatFVComparison;
import org.openimaj.util.function.Operation;
import org.openimaj.util.parallel.Parallel;

public class KMeans {

	public Set<FloatFV> getMeans(final int k, Set<FloatFV> vectors)
	{
		if(k > vectors.size())
			throw new IllegalArgumentException("K is greater than vector size");

		//assume k means to using existing items
		Set<FloatFV> means = new HashSet<FloatFV>();
		Iterator<FloatFV> iter = vectors.iterator();
		for(int i = 0; i < k; i++)
		{
			means.add(iter.next());
		}

		Map<FloatFV, List<FloatFV>> prevMap = null;
		Map<FloatFV, List<FloatFV>> map = null;
		
		int loops = 0;
		
		do{
			prevMap = map;
			//mean to collection of nearests
			//final Map<FloatFV, List<FloatFV>> finalMap = new HashMap<FloatFV,List<FloatFV>>();
			final Map<FloatFV, List<FloatFV>> finalMap = Collections.synchronizedMap( new HashMap<FloatFV,List<FloatFV>>());
			map = finalMap;
			
			final Set<FloatFV> meanClone = new HashSet<FloatFV>();
			for(FloatFV f : means)
			{
				meanClone.add(f);
			}
			for(FloatFV mean : means){
				map.put(mean, Collections.synchronizedList(new ArrayList<FloatFV>()));
			}
			
			//for each vector
			Parallel.forEach(vectors, new Operation<FloatFV>(){
				
				@Override
				public void perform(FloatFV object) {
					
					VectorComparison comp = new VectorComparison();
					comp.setTarget(object);
					//get the nearest mean
					PriorityQueue<FloatFV> q = new PriorityQueue<FloatFV>(k,comp);
					//assign it to the nearest mean
					for(FloatFV mean : meanClone){
						q.add(mean);
					}
					FloatFV mean = q.poll();
					//add this vector as a mapping
					finalMap.get(mean).add(object);
				}
				
			});
			System.out.println("loop " + loops + " Assignment complete");
			//recalculate the mean to be the average of its assigned values
			final Set<FloatFV> updatedMeans = Collections.synchronizedSet(new HashSet<FloatFV>());
			//final int updated = 0;
			final Map<FloatFV, List<FloatFV>> mapCopy = map;
			Parallel.forEach(means,new Operation<FloatFV>()
					{

						@Override
						public void perform(FloatFV object) {
							Double[] array = new Double[object.length()];
							for(int i = 0; i < object.length(); i++){
								array[i] = 0.0;
							}
							List<FloatFV> nears = mapCopy.get(object);
							
							if(nears.isEmpty())
								return;
							
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
								
								if(array[i] == 0 && nears.size() == 0){
									System.out.println("NaN at index:"+i);
								}
							}
							
							
							FloatFV newMean = new FloatFV(fArray);
							updatedMeans.add(newMean);
//							updated++;
//							if(updated%10 == 0){
//								System.out.println("loop" + loops + " updated means:" + updated);
//							}

						}
				
					});
			

			means = updatedMeans;
			System.out.println("updates completed for loop" + loops);
			for(FloatFV f : means)
			{
				System.out.println(f.toString());;
			}
			System.out.println("means " + means.size() + "\n");

			
			loops++;
		}while(!map.equals(prevMap) );
		
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
