package test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
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
import org.openimaj.image.model.FisherImages;
import org.openimaj.image.processing.resize.ResizeProcessor;

public class MyFisherFaces {

    public static void main(String[] args) {
        try {
            // getting a set of face images
            VFSGroupDataset<FImage> dataset = new VFSGroupDataset<FImage>(
                    "zip:D:/Adpack/Mine/Face Recognition/App/ImagePrivacyFeatures/FeatureExtraction/Data/zip/faces_small_set.zip", ImageUtilities.FIMAGE_READER);
           
            List<FImage> allfaces = new ArrayList<>();
            for(FImage f:dataset)
            {
            	allfaces.add(f);
            }
          //  DisplayUtilities.display("AllFaces", allfaces);
            
            ResizeProcessor resizeProcessor = new ResizeProcessor(100, 100, true);
            
            // forming training set & testing set
            int nTraining = 7;
            int nTesting = 2;
            GroupedRandomSplitter<String, FImage> splits = new GroupedRandomSplitter<String, FImage>(dataset, nTraining,
                    0, nTesting);
            GroupedDataset<String, ListDataset<FImage>, FImage> training = splits.getTrainingDataset();
            //GroupedDataset<String, ListDataset<FImage>, FImage> testing = splits.getTestDataset();

            // set number of components and train the training set of images
            int components = 50;
            FisherImages fisher = new FisherImages(components);
            fisher.train(training);
            
            // Write trained data
            try (DataOutputStream out = new DataOutputStream(new FileOutputStream("./Data/faces/fisherfacestrained") ) ) {
            	fisher.writeBinary(out);
            }
            catch (Exception e) {
            	e.printStackTrace();
            }
            
            // Read trained data
            // Start it by creating a new FisherImages object to be sure that nothing is messed up inside the class
            fisher = new FisherImages(components);
            try (DataInputStream in = new DataInputStream(new FileInputStream(".Data/faces/fisherfacestrained") ) ) {
            	fisher.readBinary(in);
            }
            catch (Exception e) {
            	e.printStackTrace();
            }
            
            int i = 0;
            for (FImage f: allfaces) {
            	DoubleFV feature = fisher.extractFeature(f);
            	try (PrintWriter pw = new PrintWriter(new File(".Data/faces/" + i++)) ) {
            		feature.writeASCII(pw);
            	}
            	catch (Exception e) {
            		e.printStackTrace();
            	}
            }
            
            /*
            System.out.println(fisher.extractFeature(allfaces.get(0)));
            
             //drawing the first 12 basis vectors
             List<FImage> fisherFaces = new ArrayList<FImage>();
             for (int i = 0; i < 38; i++) {
            	 
                 fisherFaces.add(fisher.visualise(i));
                 
             }
             DisplayUtilities.display("FisherFaces", fisherFaces);*/

        } catch (FileSystemException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}