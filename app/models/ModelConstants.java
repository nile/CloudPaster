package models;

import java.io.File;


public interface ModelConstants {
	public final static String PASTER_SRC_WEB = "WEB";
	public final static String PASTER_SRC_MSN = "MSN";
	/**
	 * 上传图片存放路径
	 */
	public final static String KEY_UPLOAD_DIR = "upload.file.dir";
	public final static String DEFAULT_UPLOAD_DIR = "public" + File.separator + "upload" + File.separator;
	/**
	 * 缩略图前缀
	 */
	public static final String KEY_THUMBNAIL_PREX = "thumbnail.prefix";
}
