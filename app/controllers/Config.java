package controllers;

import java.util.List;

import com.avaje.ebean.Query;

import models.ConfigItem;
import play.modules.ebean.EbeanSupport;
import play.mvc.Controller;

public class Config extends Controller{
	public static void index() {
		Query<ConfigItem> query = ConfigItem.all();
		List<ConfigItem> configs = query.findList();
		render(configs);
	}
}
