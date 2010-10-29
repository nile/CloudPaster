import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.mortennobel.imagescaling.DimensionConstrain;
import com.mortennobel.imagescaling.ResampleOp;

public class TestScale {
	public static void main(String[] args) throws IOException {
		BufferedImage apples = ImageIO.read(new File("test/apples.jpg"));
		ResampleOp resampleOp = new ResampleOp(DimensionConstrain.createMaxDimension(600, 500));
		BufferedImage rescaled= resampleOp.filter(apples, null);
		ImageIO.write(rescaled, "png", new File("test/apples_out.png"));
	}
}
