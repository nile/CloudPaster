package notifiers;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.mail.EmailAttachment;

import models.Paster;
import play.Logger;
import play.Play;
import play.mvc.Mailer;

public class Notifier extends Mailer {
 
   public static void welcome(String email) {
      setSubject("Welcome %s", email);
      addRecipient(email);
      setFrom("Me <me@me.com>");
      EmailAttachment emailAttachment = new EmailAttachment();
      emailAttachment.setPath("rule.pdf");
	addAttachment(emailAttachment);
      send(email);
   }
   public static void newquestion(List<Map> subscribeUsers,Paster paster){
        setSubject("CP更新通知："+paster.title);        
	setContentType("text/html");
	setCharset("UTF-8");
        for(Map u : subscribeUsers){
            Logger.info("send email to %s", u.get("email"));
            HashMap<String, Object> map = infos.get();
            map.remove("attachments");            
            map.remove("recipients");            
            addRecipient(u.get("email"));            
            String name = (String) u.get("name");
            send("Notifier/newquestion",name,paster);
        }
   }
   public static void anwser(Paster paster,Paster answer) {
	   setSubject("CP更新通知："+paster.title);
	   addRecipient(paster.creator.email);
	   setContentType("text/html");
	   setCharset("UTF-8");
           Logger.info("send email to %s", paster.creator.email);
	   send("Notifier/answer",paster,answer);
   }
   public static void paste(String email,Paster paster) {
	   setSubject("Paster 更新提醒");
	   addRecipient(email);
	   setContentType("text/html");
	   setCharset("UTF-8");
	   setFrom(Play.configuration.getProperty("mail.smtp.from"));
	   String username = email.substring(0,email.indexOf("@"));
	   send(username,paster);
   }
   
   public static void lostPassword(String email) {
      setFrom("Robot <robot@thecompany.com>");
      setSubject("Your password has been reset");
      addRecipient(email);
   }
 
}
