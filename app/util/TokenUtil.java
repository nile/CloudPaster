package util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.MMSeg;
import com.chenlb.mmseg4j.Seg;
import com.chenlb.mmseg4j.Word;

public class TokenUtil {
	public static String[] token(String str)  {
		Dictionary dic = Dictionary.getInstance();
		Seg seg = new ComplexSeg(dic);;
		MMSeg mmSeg = new MMSeg(new StringReader(str), seg );
		Word next = null;
		ArrayList<String> tokens = new ArrayList<String>();
		try {
			while((next = mmSeg.next())!=null) {
				tokens.add(next.getString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tokens.toArray(new String[tokens.size()]);
	}
	public static void main(String[] args) throws IOException {
		token("我爱北京天安门");
		/*List<String> cut = SmallSeg.cut("我爱北京天安门");
		long start = System.currentTimeMillis();
		cut = SmallSeg.cut("我爱北京天安门");
		System.out.println(SmallSeg.cut("我购买了道具和服装。草泥马"));
		for (String string : cut) {
			System.out.println(string);
		}
		System.out.println(System.currentTimeMillis() - start);*/
	}
}
