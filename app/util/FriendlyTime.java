package util;

import java.util.Date;

public class FriendlyTime {

	private static final long ONE_MINUTE = 60000L;
	private static final long ONE_HOUR = 3600000L;
	private static final long ONE_DAY = 86400000L;
	private static final long ONE_WEEK = 604800000L;

	public static String friendlyTime(Date date) {
		long delta = new Date().getTime() - date.getTime();
		if (delta < 1L * ONE_MINUTE) {
			return "刚刚";
		}
		if (delta < 2L * ONE_MINUTE) {
			return "1分钟前";
		}
		if (delta < 45L * ONE_MINUTE) {
			return toMinutes(delta) + "分钟前";
		}
		if (delta < 90L * ONE_MINUTE) {
			return "1小时前";
		}
		if (delta < 24L * ONE_HOUR) {
			return toHours(delta) + "小时前";
		}
		if (delta < 48L * ONE_HOUR) {
			return "昨天";
		}
		if (delta < 7L * ONE_DAY) {
			return toDays(delta) + "天前";
		}
		if (delta < 30L * ONE_DAY) {
			return "几周前";
		}

		if (delta < 12L * 4L * ONE_WEEK) {
			long months = toMonths(delta);
			return months <= 1 ? "1月前" : months + "月前";
		} else {
			long years = toYears(delta);
			return years <= 1 ? "1年前" : years + "年前";
		}
	}

	private static long toSeconds(long date) {
		return date / 1000L;
	}

	private static long toMinutes(long date) {
		return toSeconds(date) / 60L;
	}

	private static long toHours(long date) {
		return toMinutes(date) / 60L;
	}

	private static long toDays(long date) {
		return toHours(date) / 24L;
	}

	private static long toMonths(long date) {
		return toDays(date) / 30L;
	}

	private static long toYears(long date) {
		return toMonths(date) / 365L;
	}
}
