# --- First database schema

# --- !Ups

set ignorecase true;

create table category (
  id                        bigint not null,
  name                      varchar(255) not null,
  constraint pk_category primary key (id))
;
create table todo (
  id                        bigint not null,
  text                      varchar(255) not null,
  deadline                  timestamp,
  created_at                timestamp,
  done_at                   timestamp,
  category_id               bigint,
  constraint pk_todo primary key (id))
;
create sequence category_seq start with 1000;
create sequence todo_seq     start with 1000;
alter table todo add constraint fk_todo_category_1 foreign key (category_id) references category (id) on delete restrict on update restrict;
create index ix_todo_category_1 on todo (category_id);


# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;
drop table if exists category;
drop table if exists todo;
SET REFERENTIAL_INTEGRITY TRUE;
drop sequence if exists category_seq;
drop sequence if exists todo_seq;
