package models;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;

import play.modules.ebean.Model;

@Entity
@Table(name="tag")
@Cacheable
public class Tag extends Model {
	public String name;
	@Column(name="lastPaste")
	public Date lastPaste;
	public Tag(String name) {
		this.name= name;
	}
	public static Tag findOrCreateByName(String name) {
	    Tag tag = findByName(name);
	    if(tag == null) {
	        tag = new Tag(name);
	        tag.save();
	    }
	    return tag;
	}
	public static Tag findByName(String name) {
	    Tag tag = Ebean.find(Tag.class).where("name = ?").setParameter(1, name).findUnique();
	    return tag;
	}
}
