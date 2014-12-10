package alex_henry.runThree;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.vfs2.FileSystemException;
import org.openimaj.data.dataset.VFSListDataset;
import org.openimaj.feature.ByteFV;
import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.feature.dense.gradient.dsift.ByteDSIFTKeypoint;
import org.openimaj.image.feature.dense.gradient.dsift.DenseSIFT;
import org.openimaj.ml.annotation.Annotated;
import org.openimaj.ml.annotation.AnnotatedObject;
import org.openimaj.ml.annotation.ScoredAnnotation;

public class DenseSift {
	public static void main(String[] args)
	{

		List<Annotated<FImage,String>> testingAnnotations = new ArrayList<Annotated<FImage,String>>();

		Set<FImage> trainingImages = new HashSet<FImage>();
		List<Annotated<FImage,String>> trainingAnnotations = new ArrayList<Annotated<FImage,String>>();
		File folder = new File("./images/training");
		int subs = folder.listFiles().length - 1;
		int subsAdded = 0;
		for(File subFolder : folder.listFiles())
		{

			VFSListDataset<FImage> images;
			try {
				images = new VFSListDataset<FImage>(subFolder.getAbsolutePath(), ImageUtilities.FIMAGE_READER);
			} catch (FileSystemException e) {
				e.printStackTrace();
				break;
			}

			System.out.println(subsAdded++ + "/" + subs);

			int toUse = 2;
			if(toUse > images.size()){
				toUse = images.size();
			}

			for(int i = 0; i < toUse; i++)
			{
				FImage f = images.get(i);
				trainingImages.add(f);
				trainingAnnotations.add(new AnnotatedObject<FImage,String>(f,subFolder.getName()));
			}

			for(int i = toUse; i < images.size(); i++)
			{
				if(i == toUse)
				{
					testingAnnotations.add(new AnnotatedObject<FImage,String>(images.get(i),subFolder.getName()));
				}
				trainingAnnotations.add(new AnnotatedObject<FImage,String>(images.get(i),subFolder.getName()));
			}
		}

		Set<ByteFV> vectors = new HashSet<ByteFV>();
		for(FImage f : trainingImages)
		{
			DenseSIFT sifter = new DenseSIFT(16,16);

			//int sifted = 0;
			
			sifter.analyseImage(f);
			LocalFeatureList<ByteDSIFTKeypoint> featurePoints = sifter.getByteKeypoints();
			
			for(ByteDSIFTKeypoint point : featurePoints){
				//build a sift descriptor, add to the list of sift descriptors
				vectors.add(point.getFeatureVector());
			}
			/*sifted++;
			if(sifted%100 == 0){
				System.out.println(sifted + " images sifted");
			}*/
		}

		//System.out.println("Patching complete");
		System.out.println(vectors.size() + " vectors created");

		int k = 500;
		//figure out the k means
		Set<ByteFV> vocabulary = new KMeansByte().getMeans(k, vectors);
		System.out.println("I did it!");
		/*
		 * KMeans calss produces a bag-of-visual-words feature using the patches produced by the PatchExtractor
		 * */
		ByteFV[] array = new ByteFV[vocabulary.size()];
		ClassifierByteFV<DenseSIFT> classifier = new ClassifierByteFV<DenseSIFT>(Arrays.asList(vocabulary.toArray(array)),new DenseSIFT(16,16));
        
		classifier.train(trainingAnnotations);
        
        
		for(Annotated<FImage,String> anno : testingAnnotations)
		{
			List<ScoredAnnotation<String>> annotations = classifier.annotate(anno.getObject());
			System.out.println("Actual class:" + anno.getAnnotations() );
			System.out.println("Number of predicted annotations: "+annotations.size());
			for(ScoredAnnotation<String> anno2 : annotations)
			{
				System.out.println("Label: "+anno2.annotation+" Confidence: "+anno2.confidence);
			}
			System.out.println();
		}
	}
}
