create table tb_cash (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  status                    tinyint(1) default 0,
  shop_id                   bigint,
  create_date               datetime,
  modified_date             datetime,
  constraint pk_tb_cash primary key (id))
;

create table tb_consumption (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  status                    tinyint(1) default 0,
  shop_id                   bigint,
  create_date               datetime,
  modified_date             datetime,
  constraint pk_tb_consumption primary key (id))
;

create table tb_food (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  price                     float,
  picture                   varchar(255),
  status                    tinyint(1) default 0,
  shop_id                   bigint,
  create_date               datetime,
  modified_date             datetime,
  constraint pk_tb_food primary key (id))
;

create table tb_shop (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  status                    tinyint(1) default 0,
  expiry_date               datetime,
  create_date               datetime,
  modified_date             datetime,
  constraint pk_tb_shop primary key (id))
;

create table tb_user (
  id                        bigint auto_increment not null,
  username                  varchar(255),
  password                  varchar(255),
  realname                  varchar(255),
  usertype                  varchar(255),
  status                    tinyint(1) default 0,
  user_ip                   varchar(255),
  create_date               datetime,
  modified_date             datetime,
  last_login_date           datetime,
  shop_id                   bigint,
  constraint pk_tb_user primary key (id))
;

alter table tb_cash add constraint fk_tb_cash_shop_1 foreign key (shop_id) references tb_shop (id) on delete restrict on update restrict;
create index ix_tb_cash_shop_1 on tb_cash (shop_id);
alter table tb_consumption add constraint fk_tb_consumption_shop_2 foreign key (shop_id) references tb_shop (id) on delete restrict on update restrict;
create index ix_tb_consumption_shop_2 on tb_consumption (shop_id);
alter table tb_food add constraint fk_tb_food_shop_3 foreign key (shop_id) references tb_shop (id) on delete restrict on update restrict;
create index ix_tb_food_shop_3 on tb_food (shop_id);
alter table tb_user add constraint fk_tb_user_shop_4 foreign key (shop_id) references tb_shop (id) on delete restrict on update restrict;
create index ix_tb_user_shop_4 on tb_user (shop_id);


