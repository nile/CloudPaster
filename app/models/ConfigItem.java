package models;

import javax.persistence.Column;
import javax.persistence.Entity;

import play.db.jpa.Model;
@Entity
public class ConfigItem extends Model{
	public String key;
	@Column(name="value")
	public String value;
}
