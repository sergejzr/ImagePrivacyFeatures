package test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Hex;
import org.openimaj.feature.DoubleFVComparison;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.colour.Transforms;
import org.openimaj.image.processing.face.alignment.RotateScaleAligner;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.FaceDetector;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.image.processing.face.detection.keypoints.FKEFaceDetector;
import org.openimaj.image.processing.face.detection.keypoints.FacialKeypoint;
import org.openimaj.image.processing.face.detection.keypoints.KEDetectedFace;
import org.openimaj.image.processing.face.recognition.EigenFaceRecogniser;
import org.openimaj.image.processing.face.recognition.FaceRecognitionEngine;
import org.openimaj.math.geometry.point.Point2dImpl;
import org.openimaj.math.geometry.shape.Rectangle;
import org.openimaj.math.geometry.shape.Shape;



public class MyFaceCut {
enum gender{male,female, neutral};
	public static void main(String[] args) {
	
		File indir=new File("/media/ssddrive/faces/imageslist/");
File outdir=new File("/home/zerr/soft/Shore/Demo/CmdLine/resimages");
MessageDigest hasher=null;
try {
			 hasher = java.security.MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		};
for(File f:outdir.listFiles())
{
if(!f.getName().endsWith("txt")) continue;

byte[] encoded;
try {
	encoded = Files.readAllBytes(Paths.get(f.getAbsoluteFile().toString()));
	String cn = new String(encoded);
	Properties pf=new Properties();
	Rectangle facerect=null;
	gender cgender=null;
	Double age=null;
	Double AgeDeviation=null;
	Double Angry=null;
	Double Happy=null;
	Double Sad=null;
	Double Surprised=null;
	StringBuffer sb=new StringBuffer();
	for(String line: cn.split("\n"))
	{
		if(line.startsWith("= |---Region:"))
		{
			String pattern = ".*Left=([\\d\\.]+).*Top=([\\d\\.]+).*Right=([\\d\\.]+).*Bottom=([\\d\\.]+)";

		      // Create a Pattern object
		      Pattern r = Pattern.compile(pattern);

		      // Now create matcher object.
		      Matcher m = r.matcher(line);
		      if(m.find())
		      {
		    	  facerect=new Rectangle((float)Double.parseDouble(m.group(1)),
		    			 (float) Double.parseDouble(m.group(2)),
		    			 Math.abs((float) Double.parseDouble(m.group(3))-(float)Double.parseDouble(m.group(1))),
		    			 Math.abs((float) Double.parseDouble(m.group(4))- (float) Double.parseDouble(m.group(2))));
		    	  float scaleX = 1.5f;
					float scaleY = 2.f;
					float offsetX = 0f;
					float offsetY = -facerect.height*0.2f;
					facerect.x -= facerect.width *(scaleX - 1f) * 0.5f - offsetX;
					facerect.y -= facerect.height* (scaleY - 1f) * 0.5f - offsetY;							
					facerect.width *= scaleX;
					facerect.height *= scaleY;
		    	  /*
		    	  facerect=new Rectangle((float)Double.parseDouble(m.group(1)),
			    			 (float) Double.parseDouble(m.group(2)),
			    			 (float) Double.parseDouble(m.group(3)),
			    			 (float) Double.parseDouble(m.group(4)));
			 */
		    	 int l=0;
		    	 l++;
		      }
		}else if(line.startsWith("========="))
		{
			String fname;
			if(facerect==null) continue;
			try{
			 fname = f.getName().substring(5, f.getName().length()-4);
			}catch (Exception e) {
				e.printStackTrace();
				System.out.println("Bad File "+f);
				facerect=null;
				continue;
			}
			
			
			MBFImage curImage = ImageUtilities.readMBF(new File(indir,fname));
			MBFImage face = curImage.extractROI(facerect);
			byte[] neid = hasher.digest(face.toByteImage());
			String strid = new String(Hex.encodeHex(neid));
					pf.setProperty("gender", cgender.toString());
					pf.setProperty("file", fname);
					pf.setProperty("age", age.toString());
			pf.setProperty("AgeDeviation", AgeDeviation+"");
			pf.setProperty("Angry", Angry+"");
			pf.setProperty("Happy", Happy+"");
			pf.setProperty("Sad", Sad+"");
			pf.setProperty("Surprised", Surprised+"");
			
			ImageUtilities.write(face, new File(new File("/home/zerr/soft/Shore/Demo/CmdLine/faces"), strid+".png"));
			String comments="";
			FileWriter fw;
			pf.store(fw=new FileWriter(new File(new File("/home/zerr/soft/Shore/Demo/CmdLine/faces"),strid+".props")), comments);
			fw.close();
			
			facerect=null;
		} else if(line.contains("---Gender"))
		{
			if(line.contains("Male"))
			{
				cgender=gender.male;
			}else
			if(line.contains("Female"))
			{
				cgender=gender.female;
			}else
			{
				cgender=gender.neutral;
			}
		}
		else if(line.contains("---Age = "))
		{
			age=getDouble(line);
			
		}
		else if(line.contains("--AgeDeviation"))
		{
			AgeDeviation=getDouble(line);
			
		}
		else if(line.contains("--Angry"))
		{
			Angry=getDouble(line);
			
		}
		else if(line.contains("--Happy"))
		{
			Happy=getDouble(line);
			
		}
		else if(line.contains("--Sad"))
		{
			Sad=getDouble(line);
			
		}
		else if(line.contains("--Surprised"))
		{
			Surprised=getDouble(line);
			
		}
	}
	
} catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}


}

/*
		VideoCapture vc = new VideoCapture(320, 240);
		VideoDisplay<MBFImage> vd = VideoDisplay.createVideoDisplay(vc);
		vd.addVideoListener(new VideoDisplayListener<MBFImage>() {
			public void beforeUpdate(MBFImage frame) {
			}

			public void afterUpdate(VideoDisplay<MBFImage> display) {
			}
		});
		*/
		MBFImage query;
		try {
		//	query = ImageUtilities.readMBF(new File("/home/zerr/testim/crowd-002_small.jpg"));
			query = ImageUtilities.readMBF(new File("/media/zerr/SAMSUNG/Face Recognition/Shore150h_Android_x86_x64_gcc48/Demo/CmdLine/sample.pgm"));
			
			
			MBFImage frame=query;

			FaceDetector<DetectedFace, FImage> fd1 = new HaarCascadeDetector(20);
			FKEFaceDetector fd = new FKEFaceDetector(fd1);
			
			EigenFaceRecogniser<KEDetectedFace, Person> faceRecogniser = EigenFaceRecogniser.create(20, new RotateScaleAligner(), 1, DoubleFVComparison.CORRELATION, 0.9f);
		    final FaceRecognitionEngine<KEDetectedFace, Person> faceEngine = FaceRecognitionEngine.create(fd, faceRecogniser);

			
			
			Date d;
			System.out.println(d=new Date());
			List<KEDetectedFace> faces = fd.detectFaces(Transforms.calculateIntensity(frame));
			System.out.println(new Date().getTime()-d.getTime());
			
			System.out.println(d=new Date());
			faces = fd.detectFaces(Transforms.calculateIntensity(frame));
			System.out.println(new Date().getTime()-d.getTime());
			int i=0;
			for (KEDetectedFace face : faces) {
				frame.drawShape(face.getBounds(), RGBColour.RED);
				
				FacialKeypoint[] kpoints = face.getKeypoints();
				for(FacialKeypoint p: kpoints)
				{
					System.out.println(p.type);
					
					Shape poli=new Rectangle(new Point2dImpl(face.getBounds().x+p.position.x,face.getBounds().y+p.position.y),new Point2dImpl(face.getBounds().x+p.position.x+2,face.getBounds().y+p.position.y+2));
					frame.drawShape(poli, RGBColour.GREEN);
				}
				
				
				ImageUtilities.write(frame, new File(new File("/media/zerr/SAMSUNG/Face Recognition/Shore150h_Android_x86_x64_gcc48/Demo/CmdLine/output_java.bmp").getParentFile(), "gface_"+i+".png"));
			i++;
			}
			File outputFile = new File("/media/zerr/SAMSUNG/Face Recognition/Shore150h_Android_x86_x64_gcc48/Demo/CmdLine/output_java.bmp");
			
			//File outputFile = new File("/home/zerr/testim/crowd-002_res.jpg");
				       ImageUtilities.write(frame, outputFile);
				       
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	private static Double getDouble(String line) {
		// TODO Auto-generated method stub
		return Double.parseDouble(line.split(" = ")[1].trim());
	}
}
