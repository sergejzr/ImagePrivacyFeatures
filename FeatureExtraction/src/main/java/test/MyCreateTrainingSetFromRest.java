package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
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

public class MyCreateTrainingSetFromRest {

	public static void main(String[] args) {

		// File testimagedir=new File("/home/zerr/faces31000/faces/");
		File testimagedir = new File("./Data/trainingset/raw_faces__601_1200");
		int cntfemale = 0, cntmale = 0;
		int max = Integer.MAX_VALUE;
		// ResizeProcessor resizeProcessor = new ResizeProcessor(92, 112,
		// false);
		for (File tf : testimagedir.listFiles()) {
			if (tf.getName().endsWith(".png")) {
				
				File fileInFaces440 = new File("./Data/trainingset/faces__601_1200/" + tf.getName());
				if (fileInFaces440.exists())
					continue;
				
				try {
					Properties properties = new Properties();
					String propsAbsFileName = tf.getAbsoluteFile().toString().substring(0,
							tf.getAbsoluteFile().toString().length() - 4) + ".props";
					FileInputStream input = new FileInputStream(propsAbsFileName);
					properties.load(input);

					String destFolder = "./Data/trainingset/faces__601_1200_rest/";

					if (properties.getProperty("gender").equals("male")) {
						destFolder = destFolder + "gender/female/";
						properties.setProperty("gender", "female");
					} else {
						destFolder = destFolder + "gender/male/";
						properties.setProperty("gender", "male");
					}
					FileUtils.copyFile(tf, new File(destFolder + tf.getName()));
					FileWriter fw;
					String comments = "";
					properties.store(fw = new FileWriter(new File(destFolder + tf.getName().toString().substring(0,
							tf.getName().toString().length() - 4) + ".props")), comments);
					fw.close();
					input.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}
}