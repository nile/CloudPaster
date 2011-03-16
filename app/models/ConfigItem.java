package models;

import javax.persistence.Entity;

import play.db.jpa.Model;
@Entity
public class ConfigItem extends Model{
	public String key;
	public String value;
}
