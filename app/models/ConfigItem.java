package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
@Entity
@Table(name="configitem")
public class ConfigItem extends play.modules.ebean.Model{        
	public String name;
	@Column(name="val")
	public String val;
	public static ConfigItem createOrSave(ConfigItem item) {
		Query<ConfigItem> query = ConfigItem.find("byName", item.name);
		ConfigItem configitem = query.findUnique();
		if(configitem!=null) {
			configitem.val = item.val;
			configitem.save();
			return configitem;
		}else {
			item.save();
			return item;
		}
		
	}
	public static List<ConfigItem> listAll() {
		return Ebean.find(ConfigItem.class).findList();
	}
}
