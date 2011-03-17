package controllers;

import play.mvc.Controller;
import ys.wikiparser.WikiParser;

public class Editor extends Controller {

	public static void preview(String data){
		renderHtml(WikiParser.renderXHTML(data));
	}
}