create table cprole (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  constraint pk_cprole primary key (id))
;

create table configitem (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  val                       varchar(255),
  constraint pk_configitem primary key (id))
;

create table event (
  id                        bigint auto_increment not null,
  user_id                   bigint,
  action                    integer,
  target_id                 bigint,
  constraint ck_event_action check (action in (0,1)),
  constraint pk_event primary key (id))
;

create table favorite_tag (
  id                        bigint auto_increment not null,
  user_id                   bigint,
  tag_id                    bigint,
  constraint pk_favorite_tag primary key (id))
;

create table link (
  id                        bigint auto_increment not null,
  title                     varchar(255),
  url                       varchar(255),
  description               varchar(255),
  submitter_id              bigint,
  dateSubmitted             datetime,
  shorturl                  varchar(255),
  constraint pk_link primary key (id))
;

create table open_id_info (
  id                        bigint auto_increment not null,
  user_id                   bigint,
  openid                    varchar(255),
  provider                  varchar(255),
  constraint pk_open_id_info primary key (id))
;

create table paster (
  id                        bigint auto_increment not null,
  content                   varchar(255),
  wiki                      varchar(255),
  title                     varchar(255),
  parent_id                 bigint,
  best_id                   bigint,
  creator_id                bigint,
  created                   datetime,
  type                      integer,
  tagstext                  varchar(255),
  lastUser_id               bigint,
  updated                   datetime,
  state                     integer,
  voteup                    integer,
  votedown                  integer,
  viewCount                 integer,
  answerCount               integer,
  commentCount              integer,
  lastAnswerUser_id         bigint,
  lastAnswer_id             bigint,
  lastAnswered              datetime,
  constraint ck_paster_type check (type in (0,1,2)),
  constraint ck_paster_state check (state in (0,1)),
  constraint pk_paster primary key (id))
;

create table subscribe (
  id                        bigint auto_increment not null,
  user_id                   bigint,
  topic                     varchar(255),
  constraint pk_subscribe primary key (id))
;

create table tag (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  lastPaste                 datetime,
  constraint pk_tag primary key (id))
;

create table user (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  realname                  varchar(255),
  website                   varchar(255),
  location                  varchar(255),
  birthday                  datetime,
  about                     varchar(255),
  email                     varchar(255),
  createDate                datetime,
  constraint pk_user primary key (id))
;


create table paster_tag (
  paster_id                      bigint not null,
  tags_id                        bigint not null,
  constraint pk_paster_tag primary key (paster_id, tags_id))
;

create table user_cprole (
  user_id                        bigint not null,
  roles_id                       bigint not null,
  constraint pk_user_cprole primary key (user_id, roles_id))
;
alter table event add constraint fk_event_user_1 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_event_user_1 on event (user_id);
alter table event add constraint fk_event_target_2 foreign key (target_id) references paster (id) on delete restrict on update restrict;
create index ix_event_target_2 on event (target_id);
alter table favorite_tag add constraint fk_favorite_tag_user_3 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_favorite_tag_user_3 on favorite_tag (user_id);
alter table favorite_tag add constraint fk_favorite_tag_tag_4 foreign key (tag_id) references tag (id) on delete restrict on update restrict;
create index ix_favorite_tag_tag_4 on favorite_tag (tag_id);
alter table link add constraint fk_link_submitter_5 foreign key (submitter_id) references user (id) on delete restrict on update restrict;
create index ix_link_submitter_5 on link (submitter_id);
alter table open_id_info add constraint fk_open_id_info_user_6 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_open_id_info_user_6 on open_id_info (user_id);
alter table paster add constraint fk_paster_parent_7 foreign key (parent_id) references paster (id) on delete restrict on update restrict;
create index ix_paster_parent_7 on paster (parent_id);
alter table paster add constraint fk_paster_best_8 foreign key (best_id) references paster (id) on delete restrict on update restrict;
create index ix_paster_best_8 on paster (best_id);
alter table paster add constraint fk_paster_creator_9 foreign key (creator_id) references user (id) on delete restrict on update restrict;
create index ix_paster_creator_9 on paster (creator_id);
alter table paster add constraint fk_paster_lastUser_10 foreign key (lastUser_id) references user (id) on delete restrict on update restrict;
create index ix_paster_lastUser_10 on paster (lastUser_id);
alter table paster add constraint fk_paster_lastAnswerUser_11 foreign key (lastAnswerUser_id) references user (id) on delete restrict on update restrict;
create index ix_paster_lastAnswerUser_11 on paster (lastAnswerUser_id);
alter table paster add constraint fk_paster_lastAnswer_12 foreign key (lastAnswer_id) references paster (id) on delete restrict on update restrict;
create index ix_paster_lastAnswer_12 on paster (lastAnswer_id);
alter table subscribe add constraint fk_subscribe_user_13 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_subscribe_user_13 on subscribe (user_id);



alter table paster_tag add constraint fk_paster_tag_paster_01 foreign key (paster_id) references paster (id) on delete restrict on update restrict;

alter table paster_tag add constraint fk_paster_tag_tag_02 foreign key (tags_id) references tag (id) on delete restrict on update restrict;

alter table user_cprole add constraint fk_user_cprole_user_01 foreign key (user_id) references user (id) on delete restrict on update restrict;

alter table user_cprole add constraint fk_user_cprole_cprole_02 foreign key (roles_id) references cprole (id) on delete restrict on update restrict;
