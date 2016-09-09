package uk.ac.soton.ecs.jsh2.picalert;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.imageio.ImageIO;

import org.openimaj.feature.FeatureVector;
import org.openimaj.feature.FeatureVectorProvider;
import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.analysis.algorithm.EdgeDirectionCoherenceVector;
import org.openimaj.image.colour.Transforms;
import org.openimaj.image.feature.global.AvgBrightness;
import org.openimaj.image.feature.global.HueStats;
import org.openimaj.image.feature.global.Naturalness;
import org.openimaj.image.feature.global.Sharpness;
import org.openimaj.image.feature.local.engine.DoGSIFTEngine;
import org.openimaj.image.feature.local.keypoints.Keypoint;
import org.openimaj.image.pixel.statistics.BlockHistogramModel;
import org.openimaj.image.pixel.statistics.HistogramModel;
//import org.openimaj.image.processing.algorithm.EdgeDirectionCoherenceVector;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.FaceDetectorFeatures;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.image.processing.resize.ResizeProcessor;
import org.openimaj.io.IOUtils;
import org.openimaj.ml.clustering.ByteCentroidsResult;
import org.openimaj.ml.clustering.assignment.HardAssigner;
import org.openimaj.util.pair.IntFloatPair;

import sz.de.l3s.features.FeatureExtractor;

/**
 * @author Jonathon Hare <jsh2@ecs.soton.ac.uk>
 * small updates by s.zerr@soton.ac.uk
 * 
 */
public class ByteWriter implements FeatureExtractor {

	private static final int MAX_IMAGE_SIZE = 640;

	// Loading and optimising a quantiser takes some time, but only needs to be
	// done once
	protected static final String[] QUANTISERS = { "12",  "6", "3"
	}; // EDIT THIS TO REMOVE SIFT FEATURES
	protected static final ByteCentroidsResult[] QUANT = new ByteCentroidsResult[QUANTISERS.length];

	static {
		for (int i = 0; i < QUANTISERS.length; i++)
			QUANT[i] = loadQuantiser("pubpriv-dog-sift-fkm" + QUANTISERS[i] + "K-rnd1M.voc.byte");
	}

	

	/**
	 * load a quantiser resource for the sift features
	 * 
	 * @param name
	 *            name of the resource
	 * @return
	 */
	protected static ByteCentroidsResult loadQuantiser(String name) {
		try {
			// InputStream bis = ByteWriter.class.getResourceAsStream(name);
			/*
File bytesfile=new File("/home/zerr/git/ImagePrivacyFeatures/FeatureExtraction/src/main/resources/uk/ac/soton/ecs/jsh2/picalert/",name);
			
byte[][] c=deserialize(bytesfile, byte[][].class);
			 File bycfile=new File(bytesfile.getParentFile(),name+".bcr");
			 */
			ByteCentroidsResult quant = new ByteCentroidsResult();
			/*
			quant.centroids=c;
			PrintWriter writer;
			quant.writeASCII(writer=new PrintWriter(new FileWriter(bycfile)));
			writer.close();
			quant = new ByteCentroidsResult();
			*/
			Scanner sc;
			
			InputStream bis = ByteWriter.class.getResourceAsStream(name+".bcr");
			quant.readASCII(sc=new Scanner(bis));
			sc.close();
			//IOUtils.read(bis, ByteCentroidsResult.class);
					
					//deserialize(bis, ByteCentroidsResult.class);//IOUtils.read(bis, ByteCentroidsResult.class);

			// byte[][] clusters = quant.getClusters();
			return quant;
		} catch (IOException e) {
			e.printStackTrace();
			
		} 
		return null;
	}

	/**
	 * Compute the quantised sift features for a given quantiser
	 * 
	 * @param keys
	 * @param quant
	 * @return
	 */
	protected String computeQuantisedSIFTFeature(LocalFeatureList<Keypoint> keys, ByteCentroidsResult quant) {
		StringBuffer sb = new StringBuffer();

		sb.append(keys.size() + "\n" + quant.numClusters() + "\n");

		for (Keypoint k : keys) {

			HardAssigner<byte[], float[], IntFloatPair> assigner = quant.defaultHardAssigner();

			int id = assigner.assign(k.getFeatureVector().getVector());

			sb.append(String.format("%4.2f %4.2f %4.2f %4.3f %d\n", k.y, k.x, k.scale, k.ori, id));
		}

		return sb.toString();
	}

	/**
	 * Resize the image if it is too big, otherwise just return it
	 * 
	 * @param image
	 *            the image to resize
	 * @param maxSide
	 *            the maximum allowed dimension
	 * @return the scaled image
	 */
	protected MBFImage resizeMax(MBFImage image, int maxSide) {
		int actualMax = Math.max(image.getHeight(), image.getWidth());

		if (actualMax < maxSide)
			return image;

		float scale = (float) maxSide / (float) actualMax;
		ResizeProcessor rp = new ResizeProcessor(scale);

		return image.processInplace(rp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.l3s.l3sws.picalert.FeatureExtractor#extractFrom(java.awt.image.
	 * BufferedImage)
	 */

	public Hashtable<String, String> extractFrom(BufferedImage image) {
		Hashtable<String, String> res = new Hashtable<String, String>();
		try {
			res = extractFrom(image, getAvailableFeatures());
		} catch (Exception e) {
			this.e = e;
			e.printStackTrace();
		}
		return res;
	}

	public Set<String> getAvailableFeatures() {
		HashSet<String> ret = new HashSet<String>();
		// TODO Auto-generated method stub
		String[] st = new String[] { "dog-sift-fkm12k-rnd1M",

				
		};
		for (String s : st) {
			ret.add(s);
		}
		return ret;
	}

	/**
	 * Convert a {@link FeatureVector} provided by a
	 * {@link FeatureVectorProvider} to the same form as it would be if written
	 * to a file in ASCII format
	 * 
	 * @param fvp
	 *            the {@link FeatureVectorProvider}
	 * @return the string encoding the features
	 */
	protected String fvProviderToString(FeatureVectorProvider<?> fvp) {
		return fvToString(fvp.getFeatureVector());
	}

	/**
	 * Convert a {@link FeatureVector} to the same form as it would be if
	 * written to a file in ASCII format
	 * 
	 * @param fv
	 *            the {@link FeatureVector}
	 * @return the string encoding the features
	 */
	protected String fvToString(FeatureVector fv) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			IOUtils.writeASCII(baos, fv);
			return baos.toString("UTF-8");
		} catch (IOException e) {
			return "";
		}
	}

	/**
	 * Just for testing
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		testaPic();
	}

	public static void testaPic() {

		// BufferedImage test = ImageIO.read(new
		// File("E:\\webservicefolder\\image\\flickr\\008\\284\\233\\821\\008284233821.jpg"));

		try {
			String[] fotos = new String[] {
					// "/data2/zerr/taggedimages_clean/002/167/644/838/002167644838.jpg",
					// "/data2/zerr/taggedimages_clean/000/424/785/153/000424785153.jpg"
					"/media/zerr/BA0E0E3E0E0DF3E3/darkskiesimgs/000/000/0IS/S04/5-E/-97/032/0000000ISS045-E-97032.jpg" };
			ByteWriter fe = new ByteWriter();
			for (String f : fotos) {

				BufferedImage test = ImageIO.read(new File(f));
				HashSet<String> hs = new HashSet<String>();
			
				hs.add("dog-sift-fkm12k-rnd1M");
				Hashtable<String, String> res = fe.extractFrom(test, hs);

				Set<String> features = fe.getAvailableFeatures();
				// hs.addAll(res.keySet());

				if (false && res.size() != features.size()) {
					System.out.println(f);
					fe.getException().printStackTrace();
				} else {
					System.out.println(res);
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	Exception e = null;

	public Hashtable<String, String> extractFrom(BufferedImage image, Set<String> features) throws Exception {
		Hashtable<String, String> data = new Hashtable<String, String>();

		e = null;

		// pre-calculate the image representations we'll need
		// we'll also limit the size of provided images to a maximum
		MBFImage rgbimg = null;// resizeMax(ImageUtilities.createMBFImage(image,
								// false), MAX_IMAGE_SIZE); //RGB

		try {
			if (image == null) {
				System.out.println("img==null");
			}
			rgbimg = resizeMax(ImageUtilities.createMBFImage(image, false), MAX_IMAGE_SIZE); // RGB
		} catch (Exception e) {
			e.printStackTrace();
			return data;
		}

		MBFImage hsvimg = Transforms.RGB_TO_HSV(rgbimg); // HSV

		FImage greyimg = Transforms.calculateIntensityNTSC(rgbimg); // grey

		for (String f : features) {

			if (f.startsWith("dog-sift-")) {
				// sift
				if (QUANTISERS.length > 0) {
					LocalFeatureList<Keypoint> keys = new DoGSIFTEngine().findFeatures(greyimg);
					for (int i = 0; i < QUANTISERS.length; i++) {
						data.put("dog-sift-fkm" + QUANTISERS[i] + "k-rnd1M",
								computeQuantisedSIFTFeature(keys, QUANT[i]));
					}

				}
			}

			

		}
		return data;
	}

	

	public Exception getException() {
		return e;

	}
	
}
