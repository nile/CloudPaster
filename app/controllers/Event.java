package controllers;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import models.Paster;
import models.User;
import play.db.jpa.Model;
@Entity
public class Event extends Model{
	@OneToOne
	public User user;
	public Action action;
	@OneToOne
	public Paster target;
}
