# --- Sample dataset

# --- !Ups

insert into category (id,name) values (  1,'Find jobs');
insert into category (id,name) values (  2,'Java events');
insert into category (id,name) values (  3,'Family issues');
insert into category (id,name) values (  4,'Holiday');
insert into todo (id,text,deadline,created_at,done_at,category_id) values (  1,'Make a CRUD app',    '2017-05-05',null,null,1);
insert into todo (id,text,deadline,created_at,done_at,category_id) values (  2,'Java One Tokyo 2017','2017-05-17',null,null,2);
insert into todo (id,text,deadline,created_at,done_at,category_id) values (  3,'Enoshima trip',      '2017-05-06',null,null,3);
insert into todo (id,text,deadline,created_at,done_at,category_id) values (  4,'昭和の日',            '2017-04-29',null,'2017-04-29',4);
insert into todo (id,text,deadline,created_at,done_at,category_id) values (  5,'憲法記念日',          '2017-05-03',null,null,4);
insert into todo (id,text,deadline,created_at,done_at,category_id) values (  6,'みどりの日',          '2017-05-04',null,null,4);
insert into todo (id,text,deadline,created_at,done_at,category_id) values (  7,'こどもの日',          '2017-05-05',null,null,4);
insert into todo (id,text,deadline,created_at,done_at,category_id) values (  8,'海の日',              '2017-07-17',null,null,4);
insert into todo (id,text,deadline,created_at,done_at,category_id) values (  9,'山の日',              '2017-08-11',null,null,4);
insert into todo (id,text,deadline,created_at,done_at,category_id) values ( 10,'敬老の日',            '2017-09-18',null,null,4);
insert into todo (id,text,deadline,created_at,done_at,category_id) values ( 11,'秋分の日',            '2017-09-23',null,null,4);
insert into todo (id,text,deadline,created_at,done_at,category_id) values ( 12,'体育の日',            '2017-10-09',null,null,4);


# --- !Downs

delete from category;
delete from todo;
