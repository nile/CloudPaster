package models;

import javax.persistence.Column;
import javax.persistence.Entity;

import play.db.jpa.Model;
@Entity
public class ConfigItem extends Model{        
	public String name;
	@Column(name="val")
	public String val;
}
