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
import java.util.Random;

import org.openimaj.feature.DoubleFV;
import org.openimaj.feature.FeatureVectorProvider;
import org.openimaj.image.FImage;
import org.openimaj.image.Image;
import org.openimaj.image.MBFImage;
import org.openimaj.image.processor.ImageProcessor;


public class ApproxAvgBrightness_bak implements ImageProcessor<MBFImage>, FeatureVectorProvider<DoubleFV> {
	private double brightness;
	private static long times=0;
	private static int cnt=0;
	
	private final static int MATRIXSIZE=8;
	
	@Override
	public void processImage(MBFImage image, Image<?,?>... otherimages) {/*
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
		*/
		Date d1=new Date();
		
		FImage R = image.getBand(0);
		FImage G = image.getBand(1);
		FImage B = image.getBand(2);
		brightness=randomSampling(new ImageSummary(R,G,B), 0.1,  0.90,
				2);
		Date d2 = new Date();
		times+=(d2.getTime()-d1.getTime());
		if(cnt++%50==0)
		System.out.println("----APPROX---"+(times));
	}
	
	class ImageSummary
	{
		FImage R;
		FImage G;
		FImage B;
		public FImage getR() {
			return R;
		}

		public void setR(FImage r) {
			R = r;
		}

		public FImage getG() {
			return G;
		}

		public void setG(FImage g) {
			G = g;
		}

		public FImage getB() {
			return B;
		}

		public void setB(FImage b) {
			B = b;
		}

		private int size;
		private int perRow;
		private int perColumn;
		
		public ImageSummary(FImage r, FImage g, FImage b) {
			super();
			R = r;
			G = g;
			B = b;
			  perRow = r.getWidth()/MATRIXSIZE;
			   perColumn = r.getHeight()/MATRIXSIZE;
			 size = perRow*perColumn; 
		}

		public int size() {
			// TODO Auto-generated method stub
			return size;
		}

		
		public int getX(int a)
		{
			return (a%perRow)*MATRIXSIZE;
		}
		public int getY(int a)
		{
			return (a/perRow)*MATRIXSIZE;
		}
		
		
		
		
	}
	
	double randomSampling(ImageSummary octets, double epsilon, double delta,
			int W) {

		double rdj = 0.0;
		int r1 = 0;
		int r2 = (int) Math.ceil(Math.log(1 / delta) / Math.log(2));
		double jsSum[] = new double[(int) r2];
		Random r = new Random(124);
r=new Random();
		double abs_error;
		do {
			for (int i = 0; i < r2; i++) {
				for (int j = 1; j <= W; j++) {
					// randomly pick 2 distinct docs
					int a = r.nextInt(octets.size());
					int b = r.nextInt(octets.size());

					while (a == b)
						b = r.nextInt(octets.size());

					jsSum[i] += pairBrightness(
							octets,a,b);
					/*
					 * 
					 * Jaccard.jaccardSimilarity(docs.elementAt(a),
					 * docs.elementAt(b));
					 */
				}
			}

			r1 += W;

			// get the current estimate (average of the r2 jsSum[])

			for (int i = 0; i < r2; i++) {
				rdj += jsSum[i];
			}

			rdj = (rdj / ((double) r2)) / ((double) r1);

			// get the current estimate (median of the r2 jsSum[])
			rdj = Median.median(jsSum) / r1;

			// estimate the absolute error bound
			abs_error = 1 / Math.sqrt((double) r1);

		} while (abs_error / Math.abs(rdj - abs_error) > epsilon);

		return rdj;
	}
	private double pairBrightness(ImageSummary octets, int a, int b) {
		
		FImage R = octets.getR();
		FImage G = octets.getG();
		FImage B = octets.getB();
		
		octets.size();
		int xp1 = octets.getX(a);
		int yp1 = octets.getY(a);
		int xp2 = octets.getX(b);
		int yp2 = octets.getY(b);
		
		double b1 = brightness(R,G,B,xp1,yp1);
	//	double b2 = brightness(R,G,B,xp2,yp2);
	//	return (b1+b2)/2.;
		return b1;
		

		
		
	}

	private double brightness(FImage R, FImage G, FImage B, int xp, int yp) {

		double br=0;
		//System.out.println("xp=\t"+xp);
	//	System.out.println("yp=\t"+yp);
		
		for (int y=0; y<MATRIXSIZE; y++)
			for (int x=0; x<MATRIXSIZE; x++)
				br += (0.299f * R.pixels[y+yp][x+xp] + 0.587f * G.pixels[y+yp][x+xp] + 0.114f * B.pixels[y+yp][x+xp]);
	
		
		
	 return br/(MATRIXSIZE*MATRIXSIZE);
	}
	public double getBrightness() {
		return brightness;
	}

	@Override
	public DoubleFV getFeatureVector() {
		return new DoubleFV(new double [] { brightness });
	}
}
