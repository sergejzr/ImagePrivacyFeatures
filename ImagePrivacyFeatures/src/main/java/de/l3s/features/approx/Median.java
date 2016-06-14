package de.l3s.features.approx;



import java.util.Arrays;

public class Median {
	static/*
		 * Given an array of doubles, where the 1st entry a[0] shows the size of
		 * the array, sort the array a[1] ... a[a[0]] and return the median;
		 * (the sorting doesn't consider much efficency issue, mainly for small
		 * array with say 10 enries, )
		 */
	public double median(double a[]) {

		if(a.length>2){
		Arrays.sort(a);
		
		int middle = a.length>>2;
	    if (a.length%2 == 1) {
	        return a[middle];
	    } else {
	        return (a[middle-1] + a[middle]) / 2.0;
	    }
		}else
			
			 return a[0];
			
	    
		
		/*
		double sum = 0;
		for (double cur : a) {
			sum += cur;
		}

		double mean = sum / a.length;
		double before = a[0];
		for (double cur : a) {
			if (cur > mean) {
				return (Math.abs(cur - mean) < Math.abs(cur - before)) ? cur
						: before;
			}
			before = cur;
		}
		return a[a.length - 1];
		*/
	}
	/*
	 * public static double median(double[] numberList) {
	 * Arrays.sort(numberList); int factor = numberList.length - 1; double[]
	 * first = new double[(int) factor / 2]; double[] last = new
	 * double[first.length]; double[] middleNumbers = new double[1]; for (int i
	 * = 0; i < first.length; i++) { first[i] = numberList[i]; } for (int i =
	 * numberList.length; i > last.length; i--) { last[i] = numberList[i]; } for
	 * (int i = 0; i <= numberList.length; i++) { if (numberList[i] != first[i]
	 * || numberList[i] != last[i]) middleNumbers[i] = numberList[i]; } if
	 * (numberList.length % 2 == 0) { double total = middleNumbers[0] +
	 * middleNumbers[1]; return total / 2; } else { return middleNumbers[0]; } }
	 */
}
