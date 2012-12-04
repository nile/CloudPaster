package util;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import models.BinaryFile;
import models.ModelConstants;

import org.apache.commons.io.FileUtils;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import play.Logger;
import play.Play;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class PasterUtil {
	public static String cleanUpAndConvertImages(String orgin, String email) {
		Document doc = Jsoup.parse(orgin);
		Elements imgs = doc.getElementsByTag("img");
		Iterator<Element> iterator = imgs.iterator();
		while (iterator.hasNext()) {
			Element img = iterator.next();
			String src = img.attr("src");
			processImg(doc, img, src, email);
		}
		String res = Jsoup.clean(
				doc.body().html(),
				Whitelist
						.simpleText()
						.addTags("img", "a","ul","ol","li","blockquote","p","br")
						.addAttributes("a", "href","target")
						.addAttributes("img", "align", "alt", "height", "src",
								"title", "width"));
		return res;
	}

	private static void processImg(Document doc, Element img, String src,
			String email) {
		File originImg = null;
		BinaryFile file = null;
		byte[] imgdata = null;
		Element parent = img.parent();
		img.remove();
		try {
			if (src.startsWith("data:image/")) {
				int dataofffset = src.indexOf(";base64,");
				if (dataofffset > -1) {
					String data = src.substring(dataofffset
							+ ";base64,".length());
					imgdata = Base64.decode(data);
				}
			} else if (src.startsWith("http://") || src.startsWith("https://")) {
				Response request = Jsoup
						.connect(src)
						.referrer(src)
						.userAgent(
								"Mozilla/5.0 (Windows NT 5.1; rv:2.0b6) Gecko/20100101 Firefox/4.0b6")
						.execute();
				imgdata = request.bodyAsBytes();
			}
			file = BinaryFile.create(null, email);
			if (file == null) {
				return;
			}
			file.path = file.path+".png";
			originImg = new File(file.path);
			FileUtils.writeByteArrayToFile(originImg, imgdata);
		} catch (IOException e) {
			Logger.error(e, "process image faild");
			return;
		}
		String thumbName = Play.configuration.getProperty(
				ModelConstants.KEY_THUMBNAIL_PREX,
				ModelConstants.DEFAULT_THUMBNAIL_PREX)
				+ originImg.getName();
		File thumbFile = new File(originImg.getParentFile(), thumbName);
		String thumbUrl = ("/" + originImg.getParent() + "/" + thumbName)
				.replace('\\', '/');
		ImageUtil.scale(originImg, thumbFile);
		img.attr("src", thumbUrl);
		Element a = doc.createElement("a").attr("target", "_blank");
		a.attr("href", "/" + file.path.replace('\\', '/'));
		a.appendChild(img);
		parent.appendChild(a);
		file.save();
	}
}
