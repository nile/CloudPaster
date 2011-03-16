package controllers;

import java.util.List;

import models.ConfigItem;
import play.mvc.Controller;

public class Config extends Controller{
	public static void index() {
		List<Object> configs = ConfigItem.all().fetch();
		render(configs);
	}
}
