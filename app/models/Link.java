package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.modules.ebean.Model;

@Entity
@Table(name="link")
public class Link extends Model{
	public String title;
	public String url;
	public String description;
	@ManyToOne
	public User submitter;
	@Column(name="dateSubmitted")
	public Date dateSubmitted;
	public String shorturl;
}
