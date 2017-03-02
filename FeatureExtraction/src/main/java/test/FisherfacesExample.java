package test;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.vfs2.FileSystemException;
import org.openimaj.data.dataset.GroupedDataset;
import org.openimaj.data.dataset.ListDataset;
import org.openimaj.data.dataset.VFSGroupDataset;
import org.openimaj.experiment.dataset.split.GroupedRandomSplitter;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.model.FisherImages;

public class FisherfacesExample {

    public static void main(String[] args) {
        try {
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
            int components = 50;
            FisherImages fisher = new FisherImages(components);
            fisher.train(training);
            
            System.out.println(fisher.extractFeature(allfaces.get(0)));
            System.out.println(fisher.extractFeature(allfaces.get(1)));
            System.out.println(fisher.extractFeature(allfaces.get(2)));
            System.out.println(fisher.extractFeature(allfaces.get(3)));
            
            List<FImage> subfaces = new ArrayList<>();
            subfaces=allfaces.subList(0, 40);
            
           
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