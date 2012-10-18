package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import play.db.jpa.Model;
@Entity
@Table(name="configitem")
public class ConfigItem extends Model{        
	public String name;
	@Column(name="val")
	public String val;
	public static ConfigItem createOrSave(ConfigItem item) {
		ConfigItem configitem = ConfigItem.find("byName", item.name).first();
		if(configitem!=null) {
			configitem.val = item.val;
			configitem.save();
			return configitem;
		}else {
			item.save();
			return item;
		}
		
	}
}
