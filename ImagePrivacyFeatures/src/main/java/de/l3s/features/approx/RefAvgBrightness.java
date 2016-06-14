package de.l3s.features.approx;

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


import java.util.Date;

import org.openimaj.feature.DoubleFV;
import org.openimaj.feature.FeatureVectorProvider;
import org.openimaj.image.FImage;
import org.openimaj.image.Image;
import org.openimaj.image.MBFImage;
import org.openimaj.image.processor.ImageProcessor;


public class RefAvgBrightness implements ImageProcessor<MBFImage>, FeatureVectorProvider<DoubleFV> {
	private double brightness;
	private static long times=0;
	private static int cnt=0;
	@Override
	public void processImage(MBFImage image, Image<?,?>... otherimages) {
		Date d1=new Date();
		FImage R = image.getBand(0);
		FImage G = image.getBand(1);
		FImage B = image.getBand(2);

		if (otherimages.length > 0 && otherimages[0] != null) {
			FImage mask = (FImage) otherimages[0];
			for (int y=0; y<R.height; y++) {
				for (int x=0; x<R.width; x++) {
					if (mask.pixels[y][x] == 1)
						brightness += (0.299f * R.pixels[y][x] + 0.587f * G.pixels[y][x] + 0.114f * B.pixels[y][x]);
				}
			}
		} else {
			for (int y=0; y<R.height; y++)
				for (int x=0; x<R.width; x++)
					brightness += (0.299f * R.pixels[y][x] + 0.587f * G.pixels[y][x] + 0.114f * B.pixels[y][x]);
		}

		brightness /= (R.height*R.width);
		Date d2 = new Date();
		times+=(d2.getTime()-d1.getTime());
		
		if(cnt++%10==0)
		System.out.println("----REF----"+(times)+" for "+cnt+" pics");
	}

	public double getBrightness() {
		return brightness;
	}

	@Override
	public DoubleFV getFeatureVector() {
		return new DoubleFV(new double [] { brightness });
	}
}
