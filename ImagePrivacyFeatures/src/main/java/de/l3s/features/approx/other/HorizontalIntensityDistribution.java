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
import java.net.MalformedURLException;
import java.net.URL;

import org.openimaj.feature.DoubleFV;
import org.openimaj.feature.FeatureVectorProvider;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.Image;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processor.ImageProcessor;

/**
 * Produce a feature vector that describes the average intensity
 * distribution across the from left to right. 
 * 
 * @author Jonathon Hare <jsh2@ecs.soton.ac.uk>
 *
 */
public class HorizontalIntensityDistribution implements ImageProcessor<FImage>, FeatureVectorProvider<DoubleFV> {
	DoubleFV fv;
	int nbins = 10;
	
	public HorizontalIntensityDistribution() {
	}
	
	public HorizontalIntensityDistribution(int nbins) {
		this.nbins = nbins;
	}
	
	@Override
	public DoubleFV getFeatureVector() {
		return fv;
	}

	@Override
	public void processImage(FImage image, Image<?, ?>... otherimages) {
		fv = new DoubleFV(nbins);
		int [] counts = new int [nbins]; 
		
		float stripWidth = (float)image.width / (float)nbins;
		for (int y=0; y<image.height; y++) {
			for (int x=0; x<image.width; x++) {
				int bin = (int)(x / stripWidth);
				
				fv.values[bin] += image.pixels[y][x];
				counts[bin]++;
			}
		}
		
		for (int i=0; i<nbins; i++)
			fv.values[i] /= counts[i];
	}
	
	public static void main(String [] args) throws MalformedURLException, IOException {
		HorizontalIntensityDistribution s = new HorizontalIntensityDistribution();
//		FImage image = ImageUtilities.readF(new URL("http://farm1.static.flickr.com/8/9190606_8024996ff7.jpg"));
//		FImage image = ImageUtilities.readF(new URL("http://farm7.static.flickr.com/6201/6051101476_57afb46324.jpg"));
		FImage image = ImageUtilities.readF(new URL("http://farm5.static.flickr.com/4076/4905664253_17e7195206.jpg"));
		DisplayUtilities.display(image);
		image.process(s);
		System.out.println(s.getFeatureVector());
	}
}

