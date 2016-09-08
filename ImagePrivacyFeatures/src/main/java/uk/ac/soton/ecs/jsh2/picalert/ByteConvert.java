package uk.ac.soton.ecs.jsh2.picalert;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.imageio.ImageIO;

import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.Transforms;
import org.openimaj.image.feature.local.engine.DoGSIFTEngine;
import org.openimaj.image.feature.local.keypoints.Keypoint;
import org.openimaj.io.IOUtils;

import org.openimaj.ml.clustering.kmeans.fast.FastByteKMeansCluster;
import org.openimaj.util.pair.IntFloatPair;

public class ByteConvert {
public static void main(String[] args) {
	ByteConvert bc=new ByteConvert();
	
//bc.convert();
	try {
		bc.test();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
protected static final String[] QUANTISERS = { "12", "6", "3"
}; 
protected static final FastByteKMeansCluster[] QUANT = new FastByteKMeansCluster[QUANTISERS.length];
static {
	for (int i = 0; i < QUANTISERS.length; i++)
		QUANT[i] = loadQuantiser("pubpriv-dog-sift-fkm" + QUANTISERS[i]
				+ "K-rnd1M.voc");
}

protected static FastByteKMeansCluster loadQuantiser(String name) {
	try {
		InputStream bis = ImageFeatureExtractor.class
				.getResourceAsStream(name);
		
		FastByteKMeansCluster quant = IOUtils.read(bis,
				FastByteKMeansCluster.class);
		quant.optimize(true);
		return quant;
	} catch (IOException e) {
		return null;
	}
}

private void test() throws IOException {

	BufferedImage image=ImageIO.read(new File("/media/zerr/BA0E0E3E0E0DF3E3/darkskiesimgs/000/000/0IS/S02/2-E/-31/250/0000000ISS022-E-31250.jpg"));
	MBFImage rgbimg = null;// resizeMax(ImageUtilities.createMBFImage(image,
	// false), MAX_IMAGE_SIZE); //RGB

try {
if (image == null) {
System.out.println("img==null");
}
rgbimg = ImageUtilities.createMBFImage(image, false);
} catch (Exception e) {
e.printStackTrace();

}

MBFImage hsvimg = Transforms.RGB_TO_HSV(rgbimg); // HSV

FImage greyimg = Transforms.calculateIntensityNTSC(rgbimg); // grey



System.out.println("For oldlib");

if (QUANTISERS.length > 0) {
	LocalFeatureList<Keypoint> keys = new DoGSIFTEngine()
			.findFeatures(greyimg);
	for (int i = 0; i < QUANTISERS.length; i++) {
		System.out.println("For "+QUANTISERS[i]+" features");
		
		for (Keypoint k : keys) {
			int id = QUANT[i].push_one(k.getFeatureVector().getVector());

			System.out.println(String.format("%4.2f %4.2f %4.2f %4.3f %d\n", k.y, k.x,
					k.scale, k.ori, id));
		}
		
		
	}

}

}


private void convert() {

	
	for(int i=0;i<QUANT.length;i++) 
	{
		FastByteKMeansCluster c=QUANT[i];
		try {
			
	//c.optimize(true);
			serialize(new File(QUANTISERS[i]+".byte"), c.getClusters());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
public static void serialize(File vocfile, Object vocabulary) throws IOException {

	FileOutputStream fos = new FileOutputStream(vocfile);
	ObjectOutputStream oos = new ObjectOutputStream(fos);
	oos.writeObject(vocabulary);
	oos.flush();
	oos.close();
}


public  static <T> T deserialize(File file,Class<T> class1) throws IOException,
		ClassNotFoundException {

	FileInputStream fis = new FileInputStream(file);
	ObjectInputStream ois = new ObjectInputStream(fis);
	Object myDeserializedObject = ois.readObject();
	ois.close();
	return class1.cast(myDeserializedObject);

}

}
