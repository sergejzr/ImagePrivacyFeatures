package test;

import java.io.File;
import java.io.IOException;

import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.feature.local.engine.DoGSIFTEngine;
import org.openimaj.image.feature.local.keypoints.Keypoint;
import org.openimaj.ml.clustering.ByteCentroidsResult;
import org.openimaj.ml.clustering.assignment.HardAssigner;
import org.openimaj.util.pair.IntFloatPair;

import com.android.dx.command.annotool.Main;

import uk.ac.soton.ecs.jsh2.picalert.ImageFeatureExtractor;

public class FeatureCollector {
	
public static void main(String[] args) {
	File indir=new File("/home/zerr/soft/Shore/Demo/CmdLine/faces/");
	ByteCentroidsResult quant = ImageFeatureExtractor.loadQuantiser("pubpriv-dog-sift-fkm" + 6 + "K-rnd1M.voc.byte");
	
	for(File f:indir.listFiles())
	{
		if(f.getName().endsWith(".png"))
		{
			try {
				MBFImage query = ImageUtilities.readMBF(f);
				
				extractFeaturesFromFace(query,quant);
				if(true) return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			
			
		}
	}
}

private static void extractFeaturesFromFace(MBFImage query, ByteCentroidsResult quant) {
	
	LocalFeatureList<Keypoint> keys = new DoGSIFTEngine().findFeatures(query.flatten());
	StringBuffer sb=new StringBuffer();
	for (Keypoint k : keys) {

		HardAssigner<byte[], float[], IntFloatPair> assigner = quant.defaultHardAssigner();

		int id = assigner.assign(k.getFeatureVector().getVector());

		sb.append(String.format("%4.2f %4.2f %4.2f %4.3f %d\n", k.y, k.x, k.scale, k.ori, id));
	}
	System.out.println(sb);
}

}
