/**
 * Copyright (c) 2011, The University of Southampton and the individual contributors.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *   * 	Redistributions of source code must retain the above copyright notice,
 * 	this list of conditions and the following disclaimer.
 *
 *   *	Redistributions in binary form must reproduce the above copyright notice,
 * 	this list of conditions and the following disclaimer in the documentation
 * 	and/or other materials provided with the distribution.
 *
 *   *	Neither the name of the University of Southampton nor the names of its
 * 	contributors may be used to endorse or promote products derived from this
 * 	software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.l3s.features.approx.other;

import java.util.List;

import org.openimaj.feature.DoubleFV;
import org.openimaj.feature.FeatureVectorProvider;
import org.openimaj.image.Image;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.processing.CIEDE2000;
import org.openimaj.image.pixel.ConnectedComponent;
import org.openimaj.image.pixel.Pixel;
import org.openimaj.image.processor.ImageProcessor;
import org.openimaj.image.segmentation.FelzenszwalbHuttenlocherSegmenter;

/**
 * Implementation of the color contrast feature described in:
 * 
 * Che-Hua Yeh, Yuan-Chen Ho, Brian A. Barsky, Ming Ouhyoung.
 * Personalized photograph ranking and selection system.
 * In Proceedings of ACM Multimedia'2010. pp.211~220
 * 
 * The feature is calculated by performing a weighted average
 * of the average colour difference of all the segments in the
 * image.
 * 
 * @author Jonathon Hare <jsh2@ecs.soton.ac.uk>
 *
 */
public class ColourContrast implements ImageProcessor<MBFImage>, FeatureVectorProvider<DoubleFV> {
	FelzenszwalbHuttenlocherSegmenter<MBFImage> segmenter;
	double contrast;
	
	public ColourContrast() {
		segmenter = new FelzenszwalbHuttenlocherSegmenter<MBFImage>();
	}
	
	public ColourContrast(float sigma, float k, int minSize) {
		segmenter = new FelzenszwalbHuttenlocherSegmenter<MBFImage>(sigma, k, minSize);
	}
	
	@Override
	public DoubleFV getFeatureVector() {
		return new DoubleFV(new double[] { contrast });
	}

	@Override
	public void processImage(MBFImage image, Image<?, ?>... otherimages) {
		List<ConnectedComponent> ccs = segmenter.segment(image);
		MBFImage labImage = ColourSpace.convert(image, ColourSpace.CIE_Lab);
		float[][] avgs = new float[ccs.size()][3];
		int w = image.getWidth();
		int h = image.getHeight();
		
		//calculate patch average colours
		for (int i=0; i<avgs.length; i++) {
			for (Pixel p : ccs.get(i).pixels) {
				Float[] v = labImage.getPixel(p);
				
				avgs[i][0] += v[0];
				avgs[i][0] += v[1];
				avgs[i][0] += v[2];
			}
			int sz = ccs.get(i).pixels.size();
			avgs[i][0] /= sz;
			avgs[i][1] /= sz;
			avgs[i][2] /= sz;
		}
		
		for (int i=0; i<avgs.length; i++) {
			for (int j=i+1; j<avgs.length; j++) {
				ConnectedComponent ci = ccs.get(i);
				ConnectedComponent cj = ccs.get(i);
				float C = CIEDE2000.calculateDeltaE(avgs[i], avgs[j]);
				
				contrast += (1 - distance(ci, cj, w, h)) * ( C / ( ci.calculateArea() * cj.calculateArea() ) ); 
			}
		}
	}
	
	float distance(ConnectedComponent c1, ConnectedComponent c2, int w, int h) {
		double [] cen1 = c1.calculateCentroid();
		double [] cen2 = c2.calculateCentroid();
		
		double dx = (cen1[0] - cen2[0]) / w;
		double dy = (cen1[1] - cen2[1]) / h;
		
		return (float)( Math.sqrt(dx*dx + dy*dy) / Math.sqrt(2) );
	}
}
