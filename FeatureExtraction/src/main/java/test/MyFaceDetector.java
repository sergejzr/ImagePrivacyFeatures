package test;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

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
import org.openimaj.image.processing.face.recognition.FisherFaceRecogniser;
import org.openimaj.math.geometry.point.Point2dImpl;
import org.openimaj.math.geometry.shape.Rectangle;
import org.openimaj.math.geometry.shape.Shape;

public class MyFaceDetector {

	public static void main(String[] args) {

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
		//	query = ImageUtilities.readMBF(new File("/media/zerr/SAMSUNG/Face Recognition/Shore150h_Android_x86_x64_gcc48/Demo/CmdLine/sample.pgm"));
			query = ImageUtilities.readMBF(new File("./Data/353397003_1dca2e74c2_138_97443916@N00.jpg"));
			
			
			MBFImage frame=query;

			FaceDetector<DetectedFace, FImage> fd1 = new HaarCascadeDetector(20);
			FKEFaceDetector fd = new FKEFaceDetector(fd1);
			
			EigenFaceRecogniser<KEDetectedFace, Person> faceRecogniser = EigenFaceRecogniser.create(20, new RotateScaleAligner(), 1, DoubleFVComparison.CORRELATION, 0.9f);
		    final FaceRecognitionEngine<KEDetectedFace, Person> faceEngine = FaceRecognitionEngine.create(fd, faceRecogniser);

			
			
			Date d;
			System.out.println(d=new Date());
			List<KEDetectedFace> faces = fd.detectFaces(Transforms.calculateIntensity(frame));
			System.out.println(new Date().getTime()-d.getTime());
			
//			System.out.println(d=new Date());
//			faces = fd.detectFaces(Transforms.calculateIntensity(frame));
//			System.out.println(new Date().getTime()-d.getTime());
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
				
				
				ImageUtilities.write(frame, new File(new File("./Data/output_java.bmp").getParentFile(), "gface_"+i+".bmp"));
			i++;
			}
			File outputFile = new File("./Data/output_java.bmp");
			
			//File outputFile = new File("/home/zerr/testim/crowd-002_res.jpg");
				       ImageUtilities.write(frame, outputFile);
				       
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
}
