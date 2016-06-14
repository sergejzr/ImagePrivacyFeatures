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

import java.io.IOException;
import java.net.URL;

import org.openimaj.feature.DoubleFV;
import org.openimaj.feature.FeatureVectorProvider;
import org.openimaj.image.FImage;
import org.openimaj.image.Image;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.Transforms;
import org.openimaj.image.processor.ImageProcessor;


public class HueStats implements ImageProcessor<MBFImage>, FeatureVectorProvider<DoubleFV> {
	double mean_x = 0;
	double m2_x = 0;
	double mean_y = 0;
	double m2_y = 0;
	int n = 0;
	
	@Override
	public void processImage(MBFImage image, Image<?,?>... otherimages) {
		FImage hue = Transforms.calculateHue(image);
		
		FImage mask = null;
		if (otherimages.length > 0 && otherimages[0] != null)
			mask = (FImage) otherimages[0];

		for (int j=0; j<hue.height; j++) {
			for (int i=0; i<hue.width; i++) {
				if (mask != null && mask.pixels[j][i] == 0)
					continue;
				
				double angle = hue.pixels[j][i];
				
				double x = Math.cos(2 * Math.PI * angle);
				double y = Math.sin(2 * Math.PI * angle);
				
				n++;
				double delta_x = x - mean_x;
				double delta_y = y - mean_y;
				mean_x += delta_x / n;
				mean_y += delta_y / n;
				
				m2_x += delta_x * (x - mean_x);
				m2_y += delta_y * (y - mean_y);
			}
		}
	}

	public double getMeanHue() {
		return Math.atan2(mean_y, mean_x);
	}
	
	public double getHueVariance() {
		double var_x = m2_x / n;
		double var_y = m2_y / n;
		
		return var_y*var_x;
	}
	
	public enum ToneAttr {
		SEPIA,
		BLACK_AND_WHITE,
		COLOR;
		
		public static ToneAttr getAttr(double mean, double var) {
			if (var < 5e-4) {
				if (mean > -0.1 && mean < 0.1) return BLACK_AND_WHITE;
				if (mean > 0.6 && mean < 0.8) return SEPIA;
			}
			return COLOR;
		}
	}
	
	public ToneAttr getTone() {
		return ToneAttr.getAttr(getMeanHue(), getHueVariance());
	}
	
	@Override
	public DoubleFV getFeatureVector() {
		return new DoubleFV(new double[]{ getMeanHue(), getHueVariance() });
	}
	
	public static void main(String [] args) throws IOException {
//		MBFImage image = ImageUtilities.readMBF(new File("/Users/jsh2/Desktop/test.jpg"));
//		MBFImage image = ImageUtilities.readMBF(new File("/Users/jsh2/Desktop/testsep.jpg"));
//		MBFImage image = ImageUtilities.readMBF(new File("/Users/jsh2/Pictures/08-earth_shuttle1.jpg"));
//		MBFImage image = ImageUtilities.readMBF(new File("/Users/jsh2/Pictures/mandolux-ca-l-1280.jpg"));
		MBFImage image = ImageUtilities.readMBF(new URL("http://farm4.static.flickr.com/3067/2612399892_7df428d482.jpg"));
		HueStats cf = new HueStats();
		image.process(cf);
		
		System.out.println(cf.getMeanHue());
		System.out.println(cf.getHueVariance());
		System.out.println(cf.getTone());
	}
}
