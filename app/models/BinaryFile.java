package models;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.persistence.Entity;

import org.apache.commons.io.FileUtils;

import play.Logger;
import play.Play;
import play.db.jpa.Model;
import util.CryptoUtil;
@Entity
public class BinaryFile extends Model {
	public String name;
	public String path;
	public String creator;
	public String skey;
	public Date createDate;
	public String src = ModelConstants.PASTER_SRC_WEB;

	public static BinaryFile create(File file, String email) {
		return createAndStore(file, null, email, null,false);
	}
	public static BinaryFile createAndStore(File file, String email) {
		return createAndStore(file, null, email, null,true);
	}

	/**
	 * 
	 * @param file
	 *            file 涓簄ull鏃跺�锛屼細杩斿洖涓�釜鏂扮殑鏂囦欢璺緞锛岀▼搴忎腑闇�鍐欏叆鍐呭锛屽亣濡傚啓鍏ュけ璐ワ紝鍒欏垹闄よ褰曘�
	 * @param name
	 * @param email
	 * @param src
	 * @return
	 */
	public static BinaryFile create(File file, String name, String email, String src) {
		return createAndStore(file,name,email,src,false);
	}
	public static BinaryFile createAndStore(File file, String name, String email, String src) {
		return createAndStore(file,name,email,src,true);
	}
	private static BinaryFile createAndStore(File file, String name, String email, String src,boolean save) {
		BinaryFile paster = new BinaryFile();
		paster.creator = email;
		paster.createDate = new Date();
		String randomstr = CryptoUtil.randomstr(24);
		while (getByKey(randomstr) != null) {
			randomstr = CryptoUtil.randomstr(24);
		}
		paster.skey = randomstr;
		
		String pathname = Play.configuration.getProperty(ModelConstants.KEY_UPLOAD_DIR,ModelConstants.DEFAULT_UPLOAD_DIR) + randomstr.charAt(0);
		if (file != null) {
			try {
				FileUtils.moveToDirectory(file, new File(pathname), true);
				paster.path = pathname + File.separator + file.getName();
			} catch (IOException e) {
				Logger.error(e, "error while creat file");
				return null;
			}
		} else {
			paster.path = pathname + File.separator + randomstr;
		}
		
		paster.name = randomstr;
		if(file!=null) {
			paster.name = file.getName();
		}
		if (name != null) {
			paster.name = name;
		}
		
		if (src != null) {
			paster.src = src;
		}
		if(save) {
			paster.store();
		}
		return paster;
	}

	public static BinaryFile getByKey(String key) {
		return BinaryFile.find("byKey", key).first();
	}
	public void store(){
		this.save();
	}
}
