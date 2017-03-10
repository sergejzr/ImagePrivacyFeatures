package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.vfs2.FileSystemException;
import org.openimaj.data.dataset.GroupedDataset;
import org.openimaj.data.dataset.ListDataset;
import org.openimaj.data.dataset.VFSGroupDataset;
import org.openimaj.experiment.dataset.split.GroupedRandomSplitter;
import org.openimaj.feature.DoubleFV;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.resize.ResizeProcessor;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

public class Fisherfaces2ARFF {

    public static void main(String[] args) {

    	
//   	 int components = 100;
//   	MyFisherImages fisher = new MyFisherImages(components);
//   	  File fisherser=new File("fisher.out");
//   	  
//   	  
//         try {
//           	if(!fisherser.exists()){ 
//   	  
//       
//       // getting a set of face images
//       VFSGroupDataset<FImage> dataset = new VFSGroupDataset<FImage>(
//               "zip:http://datasets.openimaj.org/att_faces.zip", ImageUtilities.FIMAGE_READER);
//      
//       List<FImage> allfaces = new ArrayList<>();
//       for(FImage f:dataset)
//       {
//       	allfaces.add(f);
//       }
//     //  DisplayUtilities.display("AllFaces", allfaces);
//       
//       // forming training set & testing set
//       int nTraining = 8;
//       int nTesting = 1;
//       
//       
//       GroupedRandomSplitter<String, FImage> splits = new GroupedRandomSplitter<String, FImage>(dataset, nTraining,
//               0, nTesting);
//       GroupedDataset<String, ListDataset<FImage>, FImage> training = splits.getTrainingDataset();
//       GroupedDataset<String, ListDataset<FImage>, FImage> testing = splits.getTestDataset();
//
//       // set number of components and train the training set of images
//      
//       
//      
//
//     
//       		 fisher.train(training);
//			fisher.serialize(fisherser);
//       	}
//			fisher=fisher.deserialize(fisherser);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//       

         Instances arfdataset=null;
       //File testimagedir=new File("/home/zerr/faces31000/faces/");
       File testimagedir=new File("./Data/trainingset/faces_500");
       int cntfemale=0,cntmale=0;
       int max=5000;
       //ResizeProcessor resizeProcessor = new ResizeProcessor(92, 112, false);
       for(File tf:testimagedir.listFiles())
       {
       	if(tf.getName().endsWith(".png")){
       		//if(max--<0) continue;
       	try {
       		Properties properties=new Properties();
       		String propsFileName = tf.getAbsoluteFile().toString().substring(0,tf.getAbsoluteFile().toString().length()-4)+".props";
           	FileInputStream input = new FileInputStream(propsFileName);
           	properties.load(input);
           	if(properties.getProperty("gender").equals("female"))
           	{
           		if(cntfemale++ > max)
           		continue;
           	}else
           	{
           		if(cntmale++ > max)
           			continue;
           	}
       		// load a properties file
           	
       		
       		
				FImage im=ImageUtilities.readF(tf);
				
				FImage sim = im;
				
				//FImage sim = im.process(resizeProcessor);

			
				
				  DoubleFV f;
				if(arfdataset==null)
				{
					 FastVector      attVals=new FastVector();
					 attVals.addElement("male");
					 attVals.addElement("female");
					System.out.println(f=fisher.extractFeature(sim));
		            FastVector atts = new FastVector();
		            for(int i=0;i<f.values.length;i++){
		            atts.addElement(new Attribute("f"+i)); 	
		            }
		            Attribute classAttr = new Attribute("class",attVals);
		            
		            atts.addElement(classAttr); 
		            
		            
		             arfdataset = new Instances("faces", atts, 0);
				}
				
				
				f=fisher.extractFeature(sim);
				double[] values = new double[arfdataset.numAttributes()]; 
           	 int i=0;
           	 for(;i<f.values.length;i++)
           	 {
           		 values[i]=f.get(i);
           	 }
           	//values[i]=properties.getProperty("gender").equals("female")?5:7;
           	
       		Instance instance = new Instance(1.0, values);
       		instance.setDataset(arfdataset);
           	 
       		instance.setValue(i, properties.getProperty("gender"));
            arfdataset.add(instance);
            
            String destFolder = "./Data/trainingset/faces_500/gender/";
            
            if (properties.getProperty("gender") == "male") {
            	destFolder = destFolder + "male/";
            }
            else {
            	destFolder = destFolder + "female/";
            }
            FileUtils.copyFile(tf, new File(destFolder + tf.getName()));
		}
       	catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
       	}
       }

       ArffSaver saver = new ArffSaver();
       saver.setInstances(arfdataset);
       try {
			saver.setFile(new File("./Data/trainingset/gender.arff"));
			
	          saver.writeBatch();
	          saver.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     

   
    }
}