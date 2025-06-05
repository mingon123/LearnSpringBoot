create table stodo(
 id number not null,
 todo clob not null,
 completed number(1) default 0 not null,
 created date default sysdate not null,
 constraint stodo_pk primary key (id)
);
create sequence stodo_seq;