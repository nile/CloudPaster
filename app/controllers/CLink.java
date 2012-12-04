package controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;

import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.Restrict;
import controllers.deadbolt.Restrictions;

import models.Link;
import play.Logger;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.modules.ebean.EbeanSupport;
import play.mvc.Controller;
import play.mvc.With;
import util.CryptoUtil;
@With({Deadbolt.class,GlobalUser.class})
public class CLink extends Controller{
	@Restrictions(@Restrict("user"))
	public static void submit(@Required String title,@Required String url, String desc) {
		if(StringUtils.isNotEmpty(params.get("dosubmit")))
			if(!Validation.hasErrors()) {
				Link link = new Link();
				link.title = title;
				link.url = url;
				link.description = desc;
				link.submitter = Auth.getLoginUser();
				link.dateSubmitted = new Date();
				link.save();
				link.shorturl = CryptoUtil.longToString(link.id);
				link.save();
				index(0);
			}
		if(StringUtils.isNotEmpty(params.get("docancel")))
			index(0);
		if(StringUtils.isNotEmpty(params.get("dofecthtitle")))
			try {
				params.put("title",Jsoup.parse(new URL(url), 15000).title());
			} catch (Exception e) {
				Logger.error(e, "fetch url %s title failed",url);
				flash.error("获取%s的标题失败。", url);
			}
		params.flash();
		render();
	}
	public static void index(int from) {
		long count = Link.count();
		Query<Link> q = Ebean.find(Link.class).order("dateSubmitted desc");
		List<Link> links = q.setFirstRow(from).setMaxRows(from+PAGE_SIZE).findList();
		long pagesize = PAGE_SIZE;
		render(links, count, from, pagesize);
	}
	public static void view(long id) {
		Link link = Link.findById(id);
		redirect(link.url);
	}
	final static int PAGE_SIZE = 30;
}
