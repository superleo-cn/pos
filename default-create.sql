create table tb_shop (
  id                        integer auto_increment not null,
  shop_name                 varchar(255),
  status                    tinyint(1) default 0,
  expiry_date               datetime,
  create_date               datetime,
  modified_date             datetime,
  constraint pk_tb_shop primary key (id))
;

create table tb_user (
  id                        integer auto_increment not null,
  username                  varchar(255),
  password                  varchar(255),
  realname                  varchar(255),
  usertype                  varchar(255),
  status                    tinyint(1) default 0,
  user_ip                   varchar(255),
  create_date               datetime,
  modified_date             datetime,
  last_login_date           datetime,
  shop_id                   integer,
  constraint pk_tb_user primary key (id))
;

alter table tb_user add constraint fk_tb_user_shop_1 foreign key (shop_id) references tb_shop (id) on delete restrict on update restrict;
create index ix_tb_user_shop_1 on tb_user (shop_id);


