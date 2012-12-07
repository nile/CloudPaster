/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.avaje.ebean.Query;

import play.modules.ebean.EbeanSupport;
import play.modules.ebean.Model;


/**
 *
 * @author nile
 */
@Entity
@Table(name="subscribe")
public class Subscribe extends Model{
	public static final String TOPOIC_ALL_QESTION="all_question",TOPIC_ANSWER_FOR_ME = "answer_for_me";
    @ManyToOne
    public User user;
    public String topic;
    public static Subscribe unsubscribe(User user,String topic){
        Query<Subscribe> q = Subscribe.find("user_id = ? and topic = ?", user.id,topic);
		Subscribe subscribe = q.findUnique();
        if(subscribe !=null){
            subscribe.delete();            
        }
        return subscribe;
    }
    public static Subscribe subscribe(User user,String topic){
        Query<Subscribe> q = Subscribe.find("user_id = ? and topic = ?", user.id,topic);
		Subscribe subscribe =  q.findUnique();
        if(subscribe == null){
            subscribe = new Subscribe();
            subscribe.user = user;
            subscribe.topic = topic;
            subscribe.save();
        }
        return subscribe;
    }
}
