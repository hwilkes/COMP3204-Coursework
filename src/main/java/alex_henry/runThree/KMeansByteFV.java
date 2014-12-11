package alex_henry.runThree;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.openimaj.feature.ByteFV;
import org.openimaj.feature.ByteFVComparison;
import org.openimaj.util.function.Operation;
import org.openimaj.util.parallel.Parallel;

import alex_henry.interfaces.KMeans;

/*
 * Implementation of KMeans interface for ByteFV feature vector
 * */

public class KMeansByteFV implements KMeans<ByteFV>
{

	private Random r = new Random();
	
	public Set<ByteFV> getMeans(final int k, Set<ByteFV> vectors)
	{
		if(k > vectors.size()) //Must have enough vectors to produce K sized vocabulary
			throw new IllegalArgumentException("K is greater than vector size");

		//assume k means to using existing items
		Set<ByteFV> means = new HashSet<ByteFV>();
		ByteFV[] byteVectors = new ByteFV[vectors.size()];
		Vector<ByteFV> vec = new Vector<ByteFV>(Arrays.asList(vectors.toArray(byteVectors)));
		
		for(int i = 0; i < k; i++)
		{
			ByteFV toAdd = (ByteFV) vec.remove(r.nextInt(vec.size()));
			means.add(toAdd);
			System.out.println(vectors.size());
		}

		Map<ByteFV, Set<ByteFV>> prevMap = null;
		Map<ByteFV, Set<ByteFV>> map = null;
		
		int loops = 0;
		
		do{
			prevMap = map;
			//mean to collection of nearests
			//final Map<ByteFV, List<ByteFV>> finalMap = new HashMap<ByteFV,List<ByteFV>>();
			map = new HashMap<ByteFV, Set<ByteFV>>();
			final Map<ByteFV, Set<ByteFV>> finalMap = Collections.synchronizedMap(map);
			
			final Set<ByteFV> meanClone = new HashSet<ByteFV>();
			for(ByteFV f : means)
			{
				meanClone.add(f);
			}
			for(ByteFV mean : means){
				map.put(mean, Collections.synchronizedSet(new HashSet<ByteFV>()));
			}
			
			//for each vector
			Parallel.forEach(vectors, new Operation<ByteFV>(){
				ByteFVComparison euc = ByteFVComparison.EUCLIDEAN;
				@Override
				public void perform(ByteFV object) {

					double nearestDistance = Double.MAX_VALUE;
					ByteFV nearestMean = null;

					//get the nearest mean
					for(ByteFV mean : meanClone)
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

			final Set<ByteFV> updatedMeans = Collections.synchronizedSet(new HashSet<ByteFV>());
			//final int updated = 0;
			//Each parallel loop gets updated means for one vector
			Parallel.forEach(means,new Operation<ByteFV>(){

						@Override
						public void perform(ByteFV object) {
							Set<ByteFV> nears = finalMap.get(object);
							
							if(nears.isEmpty())
								return;
							
							Double[] array = new Double[object.length()];
							for(int i = 0; i < object.length(); i++){
								array[i] = 0.0;
							}
							
							for(ByteFV n : nears)
							{
								for(int i = 0; i < n.length(); i++)
								{
									array[i] += (double) n.values[i];
								}				
							}
			
							byte[] fArray = new byte[object.length()];
							for(int i = 0; i < array.length; i++)
							{
								fArray[i] = (byte) (array[i]/nears.size());
							}
							
							
							ByteFV newMean = new ByteFV(fArray);
							updatedMeans.add(newMean);

						}
				
					});
			

			means = updatedMeans;
			System.out.println("updates completed for loop:" + loops + " means:" + means.size() + "\n");
			loops++;
			
		}while(!map.equals(prevMap) );
		
		for(ByteFV f : means)
		{
			System.out.println(f.toString());;
		}
		return means;
	}


	static class VectorComparison implements Comparator<ByteFV>{

		ByteFV target;

		public void setTarget(ByteFV target){
			this.target = target;
		}
		
		@Override
		public int compare(ByteFV arg0, ByteFV arg1) {
			ByteFVComparison comp = ByteFVComparison.EUCLIDEAN;
			Double dist0 = comp.compare(arg0, target);
			Double dist1 = comp.compare(arg1, target);
			return dist0.compareTo(dist1);
		}

	}
}
