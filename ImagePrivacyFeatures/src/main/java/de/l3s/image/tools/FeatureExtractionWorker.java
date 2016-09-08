package de.l3s.image.tools;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import de.l3s.features.VisualFeatureGroup;
import uk.ac.soton.ecs.jsh2.picalert.ImageFeatureExtractor;


public class FeatureExtractionWorker implements TTask {

	private File cur;
	private FeatureListener listener;
	private String[] features;
	
	public FeatureExtractionWorker(File curfile, String[] features, FeatureListener l) {
	cur=curfile;
	listener=l;
	this.features=features;
	}
	
	
	static HashSet<String> readyids=new HashSet<String>(200000);
public static void fillReadyIds(HashSet<String> fromdb)
{
	readyids.addAll(fromdb);
}
	@Override
	public void execute() {
		try {
		
					
					int endidx = cur.getName().indexOf('.');
					int i=0;
					for( i=0;i<cur.getName().length();i++)
					{
						if(cur.getName().charAt(i)!='0'){break;}
						
					}
					
					
					
					
					
					String id=cur.getName().substring(i,endidx);
					
					if(readyids.contains(id)){return;}
					readyids.add(id);	
							ImageFeatureExtractor fe = new ImageFeatureExtractor();
					BufferedImage test;
					try{
						test = ImageIO.read(cur);
					}catch(Exception e)
					{
						log().error("Photo could not be parsed imageIO: "+cur);
						//e.printStackTrace();
						return;
					}
					if(test==null)
					{
						log().error("Photo could not be parsed Unknownerror: "+cur);
					}
					
					Hashtable<String, String> fts = null;
					
					File featuresfile=new File(cur.toString()+".ftr");
					
					boolean needtostore=true;
					if(featuresfile.exists()&&featuresfile.length()>0)
					{
					
						try {
							fts =  de.l3s.util.file.FileUtils.deserialize(featuresfile, Hashtable.class);
							int y=0;
									y++;
									needtostore=false;
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					
					}
					
				
					
					
					HashSet<String> st= null;
					
					if(features!=null)
					{
						st=new HashSet<String>();
						for(String f:features){st.add(f);}	
					}
					
					if(st!=null)
					{
						if(fts==null){
						 fts = fe.extractFrom(test,st);	
						}
						 if(fts.size()==0)
							{
								log().error("Photo could not be parsed:\t"+cur);
								//fe.getException().printStackTrace();
							}
							if(fts.size()!=fe.getAvailableFeatures().size()){
								
								log().error("Some features are missing:\t"+cur);
							}
					}else
					{
						
						if(fts==null){
						fts = fe.extractFrom(test);
						}
						if(fts.size()==0)
						{
							log().error("Photo could not be parsed:\t"+cur);
						//	fe.getException().printStackTrace();
						}
						else
						if(features!=null&&fts.size()!=features.length)
						{
							log().error("Some features are missing:\t"+cur);
						//	fe.getException().printStackTrace();
						}
					}
					if(!featuresfile.exists());
					de.l3s.util.file.FileUtils.serialize(featuresfile, fts);
					listener.featuresRead(id,fts);
		
					
				//System.out.println(fe.extractFrom(test));
				
				
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	private Logger log() {
		return Logger.getLogger(FeatureExtractionWorker.class);
		
	}

	

}
