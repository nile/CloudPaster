
create table t_last_answer_view(parent_id bigint ,id bigint,creator_id bigint,created timestamp);

insert into t_last_answer_view
select parent_id,id,creator_id,created from Paster where id in (select max(id) from Paster 
where type = 1 and parent_id is not null 
group by parent_id) ;


update Paster join t_last_answer_view  on 
 t_last_answer_view.parent_id = Paster.id 
set 
Paster.lastAnswered = t_last_answer_view.created, 
Paster.lastAnswer_id = t_last_answer_view.id,
Paster.lastAnswerUser_id=t_last_answer_view.creator_id;

drop table t_last_answer_view;