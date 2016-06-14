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

import java.io.File;
import java.io.IOException;

import org.openimaj.feature.DoubleFV;
import org.openimaj.feature.FeatureVectorProvider;
import org.openimaj.image.FImage;
import org.openimaj.image.Image;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.Transforms;
import org.openimaj.image.processor.ImageProcessor;


public class Naturalness implements ImageProcessor<MBFImage>, FeatureVectorProvider<DoubleFV> {
	private final static double grassLower = 95.0 / 360.0;
	private final static double grassUpper = 135.0 / 360.0;
	private final static double skinLower = 25.0 / 360.0;
	private final static double skinUpper = 70.0 / 360.0;
	private final static double skyLower = 185.0 / 360.0;
	private final static double skyUpper = 260.0 / 360.0;
	
	private final static double satLower = 0.1;
	
	private final static double lightnessLower = 20.0 / 100.0;
	private final static double lightnessUpper = 80.0 / 100.0;
	
	private double skyMean = 0;
	private int skyN = 0;
	
	private double skinMean = 0;
	private int skinN = 0;
	
	private double grassMean = 0;
	private int grassN = 0; 
	
	private int nPixels = 0;
	
	@Override
	public void processImage(MBFImage image, Image<?,?>... otherimages) {
		MBFImage hsl = Transforms.RGB_TO_HSL(image);
		
		FImage H = (hsl).getBand(0);
		FImage S = (hsl).getBand(1);
		FImage L = (hsl).getBand(2);
		
		nPixels = H.height * H.width;
		
		FImage mask = null;
		if (otherimages.length > 0 && otherimages[0] != null)
			mask = (FImage) otherimages[0];
		
		for (int y=0; y<H.height; y++) {
			for (int x=0; x<H.width; x++) {
				if (mask != null && mask.pixels[y][x] == 0)
					continue;
				
				if (lightnessLower <= L.pixels[y][x] && L.pixels[y][x] <= lightnessUpper && S.pixels[y][x] > satLower) {
					double hue = H.pixels[y][x];
					double sat = S.pixels[y][x];
					
					if (skyLower <= hue && hue <= skyUpper) {
						skyMean += sat;
						skyN++;
					}
					
					if (skinLower <= hue && hue <= skinUpper) {
						skinMean += sat;
						skinN++;
					}
					
					if (grassLower <= hue && hue <= grassUpper) {
						grassMean += sat;
						grassN++;
					}
				}
			}
		}
		
		if (skyN != 0) skyMean /= skyN;
		if (skinN != 0) skinMean /= skinN;
		if (grassN != 0) grassMean /= grassN;
	}

	public double getNaturalness() {
		double NSkin 	= Math.exp(-0.5 * Math.pow((skinMean - 0.76) / (0.52), 2));
		double NGrass 	= Math.exp(-0.5 * Math.pow((grassMean - 0.81) / (0.53), 2));
		double NSky 	= Math.exp(-0.5 * Math.pow((skyMean - 0.43) / (0.22), 2));
		
		double wSkin = (double)skinN / (double)nPixels;
		double wGrass = (double)grassN / (double)nPixels;
		double wSky = (double)skyN / (double)nPixels;
		
		return wSkin*NSkin + wGrass*NGrass + wSky*NSky;
	}
	
	@Override
	public DoubleFV getFeatureVector() {
		return new DoubleFV(new double[] { getNaturalness() });
	}

	public static void main(String [] args) throws IOException {
//		MBFImage image = ImageUtilities.readMBF(new File("/Users/jsh2/Desktop/test.jpg"));
//		MBFImage image = ImageUtilities.readMBF(new File("/Users/jsh2/Desktop/testsep.jpg"));
//		MBFImage image = ImageUtilities.readMBF(new File("/Users/jsh2/Pictures/08-earth_shuttle1.jpg"));
//		MBFImage image = ImageUtilities.readMBF(new File("/Users/jsh2/Pictures/la-v3-l-1280.jpg"));
//		MBFImage image = ImageUtilities.readMBF(new URL("http://farm4.static.flickr.com/3067/2612399892_7df428d482.jpg"));
		MBFImage image = ImageUtilities.readMBF(new File("/Users/jsh2/Pictures/mandolux-ca-l-1280.jpg"));
		Naturalness cf = new Naturalness();
		image.process(cf);
		
		System.out.println(cf.getNaturalness());
	}
}
