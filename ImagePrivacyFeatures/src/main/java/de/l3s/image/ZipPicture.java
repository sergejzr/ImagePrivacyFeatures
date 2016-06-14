package de.l3s.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;



import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

import de.l3s.util.encoding.MD5;
import de.l3s.util.image.FileDistributer;
import de.l3s.utils.tools.ZipReader;

public class ZipPicture extends Picture {

	private File picfile;
	private String contentbasedid;
	private byte[] zipdata;

	public ZipPicture(ZipInputStream zfile, ZipEntry zentry) {
		super("contentbased");
		try {
			zipdata = ZipReader.readFromZEntry(zfile, zentry);

			try {
				contentbasedid = MD5.encode(imageURL.getBytes("UTF-8")).substring(0, 9);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			super.setId(contentbasedid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public BufferedImage downloadImage() throws MalformedURLException,
			IOException {
		return ImageIO.read(new ByteInputStream(zipdata,0));
	}

	@Override
	protected File getImagePathIn(File directory) {
		FileDistributer fd = new FileDistributer(contentbasedid, directory, true);
		 return fd.extendFile(".jpg");
	}

	@Override
	public String getStorageId() {
		return contentbasedid;
	}

	@Override
	public boolean exists(File imagedir) {
		
		return true;
	}

}
