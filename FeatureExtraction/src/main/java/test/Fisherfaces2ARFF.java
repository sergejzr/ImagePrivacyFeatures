package test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.vfs2.FileSystemException;
import org.openimaj.data.dataset.GroupedDataset;
import org.openimaj.data.dataset.ListDataset;
import org.openimaj.data.dataset.VFSGroupDataset;
import org.openimaj.experiment.dataset.split.GroupedRandomSplitter;
import org.openimaj.feature.DoubleFV;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class Fisherfaces2ARFF {

    public static void main(String[] args) {
        try {
        	 int components = 50;
        	MyFisherImages fisher = new MyFisherImages(components);
        	  File fisherser=new File("fisher.out");
        	  
        	  
        	  
        	  
            
            // getting a set of face images
            VFSGroupDataset<FImage> dataset = new VFSGroupDataset<FImage>(
                    "zip:file:///home/zerr/faces_1000_100perfolder.zip", ImageUtilities.FIMAGE_READER);
           
            List<FImage> allfaces = new ArrayList<>();
            for(FImage f:dataset)
            {
            	allfaces.add(f);
            }
          //  DisplayUtilities.display("AllFaces", allfaces);
            
            // forming training set & testing set
            int nTraining = 80;
            int nTesting = 5;
            
            
            GroupedRandomSplitter<String, FImage> splits = new GroupedRandomSplitter<String, FImage>(dataset, nTraining,
                    0, nTesting);
            GroupedDataset<String, ListDataset<FImage>, FImage> training = splits.getTrainingDataset();
            GroupedDataset<String, ListDataset<FImage>, FImage> testing = splits.getTestDataset();

            // set number of components and train the training set of images
           
            
           
            try {
              	if(!fisherser.exists()){
          
            		 fisher.train(training);
				fisher.serialize(fisherser);
            	}
				fisher=fisher.deserialize(fisherser);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            DoubleFV f;
			System.out.println(f=fisher.extractFeature(allfaces.get(0)));
           
            
            List<FImage> subfaces = new ArrayList<>();
            subfaces=allfaces.subList(0, 40);
            
            
            
            FastVector atts = new FastVector();
            for(int i=0;i<f.values.length;i++){
            atts.addElement(new Attribute("f"+i)); 	
            }
            
            Instances arfdataset = new Instances("faces", atts, 0);
           
            
            for(FImage im:allfaces)
            {
            	f=fisher.extractFeature(im);
            	 double[] values = new double[arfdataset.numAttributes()]; 
            	 for(int i=0;i<f.length();i++)
            	 {
            		 values[i]=f.get(i);
            	 }
            	 arfdataset.add(new Instance(1.0, values));
            	
            }
           
             //drawing the first 12 basis vectors
             List<FImage> fisherFaces = new ArrayList<FImage>();
             for (int i = 0; i < 10; i++) {
            	 
                 fisherFaces.add(fisher.visualise(i));
                 
             }
             
            // DisplayUtilities.display("FisherFaces", fisherFaces);

        } catch (FileSystemException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}