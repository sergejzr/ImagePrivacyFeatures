/**
 * 
 */
package test;

import java.io.File;

/**
 * @author rvlasov
 *
 */
public class DataPreparator {
	
	static public void deleteFoldersWithLessFiles(String folder, int minNumFilesInFolder) {
		try {
			File dir = new File(folder);
			
			for (File f : dir.listFiles()) {
				if (!f.isDirectory())
					continue;
				
				File[] subDirFiles = f.listFiles();
				if (subDirFiles.length < minNumFilesInFolder) {
					for (File fInner: subDirFiles) {
						fInner.delete();
					}
					f.delete();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		deleteFoldersWithLessFiles("./Data/fisherset/150faces", 10);
	}
}
