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
                    "zip:http://datasets.openimaj.org/att_faces.zip", ImageUtilities.FIMAGE_READER);
           
            List<FImage> allfaces = new ArrayList<>();
            for(FImage f:dataset)
            {
            	allfaces.add(f);
            }
          //  DisplayUtilities.display("AllFaces", allfaces);
            
            // forming training set & testing set
            int nTraining = 5;
            int nTesting = 2;
            GroupedRandomSplitter<String, FImage> splits = new GroupedRandomSplitter<String, FImage>(dataset, nTraining,
                    0, nTesting);
            GroupedDataset<String, ListDataset<FImage>, FImage> training = splits.getTrainingDataset();
            GroupedDataset<String, ListDataset<FImage>, FImage> testing = splits.getTestDataset();

            // set number of components and train the training set of images
            int components = 50;
            FisherImages fisher = new FisherImages(components);
            fisher.train(training);
            
            System.out.println(fisher.extractFeature(allfaces.get(0)));
            
             //drawing the first 12 basis vectors
             List<FImage> fisherFaces = new ArrayList<FImage>();
             for (int i = 0; i < 38; i++) {
            	 
                 fisherFaces.add(fisher.visualise(i));
                 
             }
             DisplayUtilities.display("FisherFaces", fisherFaces);

        } catch (FileSystemException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}