package util;

import java.util.Date;

import play.templates.JavaExtensions;

public class CPExtensions extends JavaExtensions {
	public static String freindly(Date date) {
		return FriendlyTime.friendlyTime(date);
	}
}
