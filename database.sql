
create table user_data
(user_number varchar(4000),
 user_id varchar(20),
 password varchar(16),
 connection_status varchar(1),  
 email varchar(30),
 first_name varchar(15),
 last_name varchar(20),
 address varchar(30),
 city varchar(20),
 state varchar(2));
insert into user_data
  (user_number, user_id,password, connection_status, email,first_name, last_name, address, city, state)
  values ('0001', 'Stinky','dumbpassword','0','l@d.com','Luke', 'Duke',  '2130 Boars Nest', 
          'Hazard Co', 'Georgia');

