/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
        Subscribe subscribe = null;/* Subscribe.find("user = ? and topic = ?", user,topic).first();
        if(subscribe !=null){
            subscribe.delete();            
        }*/
        return subscribe;
    }
    public static Subscribe subscribe(User user,String topic){
        Subscribe subscribe = null;/* Subscribe.find("user = ? and topic = ?", user,topic).first();
        if(subscribe == null){
            subscribe = new Subscribe();
            subscribe.user = user;
            subscribe.topic = topic;
            subscribe.save();
        }*/
        return subscribe;
    }
}
