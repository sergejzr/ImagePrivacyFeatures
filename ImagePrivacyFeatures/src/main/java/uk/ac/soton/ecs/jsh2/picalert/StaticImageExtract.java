package uk.ac.soton.ecs.jsh2.picalert;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import uk.ac.soton.ecs.jsh2.picalert.ImageFeatureExtractor;

public class StaticImageExtract {
public static void main(String[] args) {
	ImageFeatureExtractor ife=new ImageFeatureExtractor();
	File cur=new File("/media/zerr/BA0E0E3E0E0DF3E3/darkskiesimgs/000/000/0IS/S04/5-E/-97/032/0000000ISS045-E-97032.jpg");
	BufferedImage test=null;
	try{
		test = ImageIO.read(cur);
	}catch(Exception e)
	{
		System.out.println("Photo could not be parsed imageIO: "+cur);
		//e.printStackTrace();
		//return;
	}
Exception e = ife.getException();
if(e!=null)
{
	e.printStackTrace();
	}
	System.out.println(ife.extractFrom(test));
}
}
