create table tb_cash (
  id                        bigint auto_increment not null,
  price                     float,
  status                    tinyint(1) default 0,
  shop_id                   bigint,
  position                  integer,
  create_by                 varchar(255),
  modified_by               varchar(255),
  create_date               datetime,
  modified_date             datetime,
  constraint pk_tb_cash primary key (id))
;

create table tb_cash_transaction (
  id                        bigint auto_increment not null,
  user_id                   bigint,
  shop_id                   bigint,
  cash_id                   bigint,
  price                     float,
  quantity                  integer,
  total_price               float,
  create_by                 varchar(255),
  modified_by               varchar(255),
  create_date               datetime,
  modified_date             datetime,
  constraint pk_tb_cash_transaction primary key (id))
;

create table tb_consume_transaction (
  id                        bigint auto_increment not null,
  user_id                   bigint,
  shop_id                   bigint,
  consumption_id            bigint,
  price                     float,
  create_by                 varchar(255),
  modified_by               varchar(255),
  create_date               datetime,
  modified_date             datetime,
  constraint pk_tb_consume_transaction primary key (id))
;

create table tb_consumption (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  status                    tinyint(1) default 0,
  shop_id                   bigint,
  position                  integer,
  create_by                 varchar(255),
  modified_by               varchar(255),
  create_date               datetime,
  modified_date             datetime,
  constraint pk_tb_consumption primary key (id))
;

create table tb_food (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  cost_price                float,
  retail_price              float,
  picture                   varchar(255),
  status                    tinyint(1) default 0,
  shop_id                   bigint,
  position                  integer,
  create_by                 varchar(255),
  modified_by               varchar(255),
  create_date               datetime,
  modified_date             datetime,
  constraint pk_tb_food primary key (id))
;

create table tb_shop (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  status                    tinyint(1) default 0,
  expiry_date               datetime,
  create_by                 varchar(255),
  modified_by               varchar(255),
  create_date               datetime,
  modified_date             datetime,
  constraint pk_tb_shop primary key (id))
;

create table tb_transaction (
  id                        bigint auto_increment not null,
  user_id                   bigint,
  shop_id                   bigint,
  food_id                   bigint,
  cost_price                float,
  retail_price              float,
  discount                  float,
  quantity                  integer,
  total_cost_price          float,
  total_discount            float,
  total_retail_price        float,
  create_by                 varchar(255),
  modified_by               varchar(255),
  create_date               datetime,
  modified_date             datetime,
  constraint pk_tb_transaction primary key (id))
;

create table tb_user (
  id                        bigint auto_increment not null,
  username                  varchar(255),
  password                  varchar(255),
  realname                  varchar(255),
  usertype                  varchar(255),
  status                    tinyint(1) default 0,
  user_ip                   varchar(255),
  user_mac                  varchar(255),
  create_by                 varchar(255),
  modified_by               varchar(255),
  create_date               datetime,
  modified_date             datetime,
  last_login_date           datetime,
  shop_id                   bigint,
  constraint pk_tb_user primary key (id))
;

alter table tb_cash add constraint fk_tb_cash_shop_1 foreign key (shop_id) references tb_shop (id) on delete restrict on update restrict;
create index ix_tb_cash_shop_1 on tb_cash (shop_id);
alter table tb_cash_transaction add constraint fk_tb_cash_transaction_user_2 foreign key (user_id) references tb_user (id) on delete restrict on update restrict;
create index ix_tb_cash_transaction_user_2 on tb_cash_transaction (user_id);
alter table tb_cash_transaction add constraint fk_tb_cash_transaction_shop_3 foreign key (shop_id) references tb_shop (id) on delete restrict on update restrict;
create index ix_tb_cash_transaction_shop_3 on tb_cash_transaction (shop_id);
alter table tb_cash_transaction add constraint fk_tb_cash_transaction_cash_4 foreign key (cash_id) references tb_cash (id) on delete restrict on update restrict;
create index ix_tb_cash_transaction_cash_4 on tb_cash_transaction (cash_id);
alter table tb_consume_transaction add constraint fk_tb_consume_transaction_user_5 foreign key (user_id) references tb_user (id) on delete restrict on update restrict;
create index ix_tb_consume_transaction_user_5 on tb_consume_transaction (user_id);
alter table tb_consume_transaction add constraint fk_tb_consume_transaction_shop_6 foreign key (shop_id) references tb_shop (id) on delete restrict on update restrict;
create index ix_tb_consume_transaction_shop_6 on tb_consume_transaction (shop_id);
alter table tb_consume_transaction add constraint fk_tb_consume_transaction_cons_7 foreign key (consumption_id) references tb_consumption (id) on delete restrict on update restrict;
create index ix_tb_consume_transaction_cons_7 on tb_consume_transaction (consumption_id);
alter table tb_consumption add constraint fk_tb_consumption_shop_8 foreign key (shop_id) references tb_shop (id) on delete restrict on update restrict;
create index ix_tb_consumption_shop_8 on tb_consumption (shop_id);
alter table tb_food add constraint fk_tb_food_shop_9 foreign key (shop_id) references tb_shop (id) on delete restrict on update restrict;
create index ix_tb_food_shop_9 on tb_food (shop_id);
alter table tb_transaction add constraint fk_tb_transaction_user_10 foreign key (user_id) references tb_user (id) on delete restrict on update restrict;
create index ix_tb_transaction_user_10 on tb_transaction (user_id);
alter table tb_transaction add constraint fk_tb_transaction_shop_11 foreign key (shop_id) references tb_shop (id) on delete restrict on update restrict;
create index ix_tb_transaction_shop_11 on tb_transaction (shop_id);
alter table tb_transaction add constraint fk_tb_transaction_food_12 foreign key (food_id) references tb_food (id) on delete restrict on update restrict;
create index ix_tb_transaction_food_12 on tb_transaction (food_id);
alter table tb_user add constraint fk_tb_user_shop_13 foreign key (shop_id) references tb_shop (id) on delete restrict on update restrict;
create index ix_tb_user_shop_13 on tb_user (shop_id);


