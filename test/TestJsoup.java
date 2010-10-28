import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class TestJsoup {
	public static void main(String[] args) throws IOException {
		String content = "<img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAIUAAAAYCAIAAABbQKn2AAAEjElEQVRoge2Y30siaxzG908JguiiC5lgEW8WObDgtEvFelEoItp2kB1haXFPbT+kzc0Lx70YNgd2FKHR2S0aj9hOdC40yZrQdC42f2yxakFkFyp1Kg6pMOdiwNOeTh6dsd1gfXivfPXheb6fmddh7hVbuku696MDtPSNWjy+t0qlUo3dFo/vrRaPu6UWj7ulFo+7pZ+Xx+n5pUTn7h7Cu4dwic59en75oxMVi03hcbXYbddLH51cHaIQK9dqfMK+mTk+yxyfTdg3XavxZoUUQroJPFyr8Td4OFe4qK7m1quK60luHGSOz+hUvnsI523FsqxE56YTORPBmAiG2j6U6NwsyzYlpxDSQnlwxZh04WPoAEKCEBJ0rWV8W9km1uNUKldAAzm3HKcTuSdTn6jtQyE86ERO+Zpi0gXxMC4exqntQ9BA0omc8JwCSQvlQSdy2tmV9NGJxZPkulk8ycXgXrPqVQUhwZfvN5h0QaonALXD6c8Aagdvt4Ep72JwD6PifWNk3xhpnI++fL8xMOUVnpMjndrPS3Ruic5NbR8+GvXWPwqhPLhi5MYBhARBAwkayKdwwOJJQkiwb4ysM8T/CqPi3OX8ZOrThH1TpMLG8R2RCuPnlitccPc0aCADsWwglpXqiZmFJKB25AoXAqMKJC2IB1csEMu+9X2R6olqtxGMGcEYkQoTXq9YLNKJnFRP0IkchATBkQ8sy3YNohAa6xpE+RmanKE3eNi1lhGpMJZlWZYVqTAIjT0a9ZqcIR6GbaC5oVXDShAPkzM0agtgVJybfqlcKZUrIhWmgCNaJPrguY9fvatKH50AaodvK2siGJEKK/75V7FY7BpEtUiUH4/LUgVQ2nxbWe3sSjWeyRkCDaQCjnQNopelSqOed4IHV+zd7zHjfHRgdr1TjlRXz6Rfbg73TPr51avq9PwSUNowKm7xJAGlLbWf5z7vlCNyc7hTjvDwXA7tDkx5nf6MVE9czSwexhVwBFA7lkO7/NI2hTR/HsuhXXDkg4lgIDT2wvHZ4kna/8iSGwd0Kk+n8jIjLTPSQuqVypWHz5wT9s056uvjV9TV2XUNojIjzY+HWGM3zkfH8Z0RjBnHd2YWkm+9e1zyF47PD577eP8t3URaonMr4Ej3EF7PKPjzEGvsT+EAhMa0SFQBRxRwRG4Oy81hjgS3hNTTzq4oX1Nz1FctEuWQz3z8Mkd9da1lXGsZmZFu74Ub9Uzt5wGlbRzfEWvs7b3wv5ZIhXGYqzdiQxJr7CaCqUG6ngdCnjxS+3nuEOfu8Zu68a6HLoalesLiSYIG8vr52ylHZEa69kH8n9JML4EGcmB2vaPfWip/c3qUypX2XviX3/z3f13QTC816vwP6WH8+jQAtaPOUfDkoZlekuoJBRzpmfS398I3dZMZaR71ArFspxyB0NjjV1RHv/X6+4Y20MyPx33lO46odX79+q51fp3bBZS2Rp1rk+7ot9ZJmicPQGmrXq21u/Go9/CZs/rb9NHJ9S909FvbQHNHv7Uh21tVs0j/RO93m/hUentq8WjxaOlm1ebxN+/NUZfXcKa2AAAAAElFTkSuQmCC\" alt=\"\" />";
		content+="<img src=http://static.adzerk.net/Advertisers/2417.png/>";
		Document parse = Jsoup.parse(content);
		Elements imgs = parse.getElementsByTag("img");
		Iterator<Element> iterator = imgs.iterator();
		while (iterator.hasNext()) {
			Element img = iterator.next();
			String src = img.attr("src");
			if (src.startsWith("data:image/")) {
				int dataofffset = src.indexOf(";base64,");
				if (dataofffset > -1) {
					String data = src.substring(dataofffset + ";base64,".length());
					bytesToImage(Base64.decode(data), "get.png");
					img.attr("src", "get.png");
				}
			}else if(src.startsWith("http://")||src.startsWith("https://")){
				Response request = Jsoup.connect("http://static.adzerk.net/Advertisers/2417.png").execute();
				bytesToImage(request.bodyAsBytes(), "down.png");
				img.attr("src", "down.png");
			}
		}
		String res = Jsoup.clean(parse.body().html(), Whitelist.basic().addTags("img")
                .addAttributes("img", "align", "alt", "height", "src", "title", "width")
                );
		System.out.println(res);
	}

	private static void bytesToImage(byte[] data, String tofile) throws IOException {
		FileUtils.writeByteArrayToFile(new File(tofile), data);
	}
}
