drop table user_data;
create table user_data
(
 user_id varchar(20),
 password varchar(16));
 
 
insert into user_data
  (user_id,password)
  values ('new','new');

insert into user_data
  ( user_id,password)
  values ('test','test');