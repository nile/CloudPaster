package util;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import play.Logger;

import com.mortennobel.imagescaling.DimensionConstrain;
import com.mortennobel.imagescaling.ResampleOp;

public class ImageUtil {
	public static boolean scale(File src, File dest) {
		try {
			BufferedImage apples = ImageIO.read(src);
			ResampleOp resampleOp = new ResampleOp(DimensionConstrain.createMaxDimension(600, 500,true));
			BufferedImage rescaled = resampleOp.filter(apples, null);
			ImageIO.write(rescaled, "png", dest);
			return true;
		} catch (Exception e) {
			Logger.error(e, "压缩图片出错");
		}
		return false;
	}
}
