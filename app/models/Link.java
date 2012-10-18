package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.db.jpa.Model;
@Entity
@Table(name="link")
public class Link extends Model{
	public String title;
	public String url;
	public String description;
	@ManyToOne
	public User submitter;
	public Date dateSubmitted;
	public String shorturl;
}
