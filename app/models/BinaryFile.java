package models;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import play.Logger;
import play.modules.mongo.MongoEntity;
import play.modules.mongo.MongoModel;
import play.modules.mongosearch.Field;
import play.modules.mongosearch.Indexed;
import play.modules.mongosearch.MongoSearch;
import util.CryptoUtil;

@MongoEntity("m_binary")
@Indexed
public class BinaryFile extends MongoModel {
	@Field(tokenize = true, stored = false)
	public String name;
	@Field(tokenize = true, stored = false)
	public String path;
	public String creator;
	public String key;
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
	 *            file 为null时候，会返回一个新的文件路径，程序中需要写入内容，假如写入失败，则删除记录。
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
		paster.key = randomstr;
		
		String pathname = ModelConstants.UPLOAD_DIR + randomstr.charAt(0);
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
		MongoSearch.index(this);
	}
}
