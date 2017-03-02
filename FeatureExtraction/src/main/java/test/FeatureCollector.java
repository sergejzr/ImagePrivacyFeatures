package test;

import java.io.File;
import java.io.IOException;

import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;

import com.android.dx.command.annotool.Main;

public class FeatureCollector {
	
public static void main(String[] args) {
	File indir=new File("");
	
	for(File f:indir.listFiles())
	{
		if(f.getName().endsWith(".pgm"))
		{
			try {
				MBFImage query = ImageUtilities.readMBF(f);
				
				extractFeaturesFromFace(query);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			
			
		}
	}
}

private static void extractFeaturesFromFace(MBFImage query) {
	
	
}

}
