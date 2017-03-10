/**
 * 
 */
package test;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.Transforms;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.FaceDetector;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.image.processing.face.detection.keypoints.FKEFaceDetector;
import org.openimaj.image.processing.face.detection.keypoints.KEDetectedFace;
import org.openimaj.image.processing.resize.ResizeProcessor;
import org.openimaj.math.geometry.shape.Rectangle;

/**
 * @author rvlasov
 *
 */
public class MyCreateFisherset {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File indir = new File("./Data/fisherset/raw_150faces");
		File outdir = new File("./Data/fisherset/150faces");

		MessageDigest hasher = null;
		try {
			hasher = java.security.MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ResizeProcessor resizeProcessor = new ResizeProcessor(92, 112, false);

		FaceDetector<DetectedFace, FImage> fd = new HaarCascadeDetector(20);
		// FKEFaceDetector fd = new FKEFaceDetector(fd1);

		// EigenFaceRecogniser<KEDetectedFace, Person> faceRecogniser =
		// EigenFaceRecogniser.create(20, new RotateScaleAligner(), 1,
		// DoubleFVComparison.CORRELATION, 0.9f);
		// final FaceRecognitionEngine<KEDetectedFace, Person> faceEngine =
		// FaceRecognitionEngine.create(fd, faceRecogniser);

		for (File folder : indir.listFiles()) {
			if (!folder.isDirectory())
				continue;

			for (File f : folder.listFiles()) {
				try {
					MBFImage curImage = ImageUtilities.readMBF(f);

					List<DetectedFace> faces = fd.detectFaces(Transforms.calculateIntensity(curImage));

					if (faces.size() == 0 || faces.size() > 1) {
						System.out.println("Image skipped: " + f.getName() + " - " + faces.size() + " faces");
						continue;
					}

					String outSubFolderFullName = outdir + "/" + folder.getName();
					File outSubFolder = new File(outSubFolderFullName);
					if (!outSubFolder.exists())
						outSubFolder.mkdir();

					int i = 0;
					for (DetectedFace face : faces) {
						Rectangle facerect = new Rectangle(face.getBounds());

						float ratio = 112f / 92f;
						float curRatio = facerect.height / facerect.width;
						float scaleX = 1f;
						float scaleY = 1f;
						if (curRatio >= ratio) {
							// Current height is even bigger than required compared to width. Therefore
							// increase the width
							scaleX = curRatio / ratio;
							scaleY = 1f;
						}
						else {
							// Current height less than than required compared to width. Therefore
							// increase the height
							scaleX = 1f;
							scaleY = ratio / curRatio;
						}
						scaleX *= 1.3f;
						scaleY *= 1.3f;
						
						// float scaleY = 2.f;
						float offsetX = 0f;
						float offsetY = 0f;
						// float offsetY = -facerect.height*0.2f;
						//float offsetY = -facerect.height * 0.15f * (curRatio >= ratio ? 1f : ratio/curRatio);
						rescaleFacerect(facerect, scaleX, scaleY);
						facerect.x -= - offsetX;
						facerect.y -= - offsetY;

						// Make sure that facerect is within the original image
						while (facerect.x < 0 || facerect.y < 0 ||
								facerect.x + facerect.width > curImage.getWidth()-1 ||
								facerect.y + facerect.height > curImage.getHeight()-1)
						{
							rescaleFacerect(facerect, 0.9f, 0.9f);
						}
						

						MBFImage faceImage = curImage.extractROI(facerect);
						faceImage.processInplace(resizeProcessor);

						ImageUtilities.write(Transforms.calculateIntensity(faceImage), new File(outSubFolderFullName,
								f.getName().substring(0, f.getName().length() - 4) + ".png"));

						i++;
					}
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		System.out.println("Program finished");
	}
	
	static public void rescaleFacerect(Rectangle facerect, float scaleX, float scaleY) {
		facerect.x -= facerect.width * (scaleX - 1f) * 0.5f;
		facerect.y -= facerect.height * (scaleY - 1f) * 0.5f;
		facerect.width *= scaleX;
		facerect.height *= scaleY;
	}
}
