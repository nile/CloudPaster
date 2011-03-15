package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;
import ys.wikiparser.WikiParser;

public class Editor extends Controller {

	public static void preview(String data){
		renderHtml(WikiParser.renderXHTML(data));
	}
}