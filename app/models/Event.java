package models;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import models.Paster;
import models.User;
import play.db.jpa.Model;
@Entity
@Table(name="event")
public class Event extends Model{
	@OneToOne
	public User user;
	public Action action;
	@OneToOne
	public Paster target;
}
