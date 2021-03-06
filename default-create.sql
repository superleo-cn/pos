create table tb_audit (
  id                        bigint auto_increment not null,
  user_id                   bigint,
  shop_id                   bigint,
  create_by                 varchar(255),
  modified_by               varchar(255),
  action                    varchar(255),
  create_date               datetime,
  modified_date             datetime,
  action_date               datetime,
  constraint pk_tb_audit primary key (id))
;

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
  name_zh                   varchar(255),
  status                    tinyint(1) default 0,
  shop_id                   bigint,
  position                  integer,
  create_by                 varchar(255),
  modified_by               varchar(255),
  create_date               datetime,
  modified_date             datetime,
  constraint pk_tb_consumption primary key (id))
;

create table tb_daily_summary (
  id                        bigint auto_increment not null,
  shop_id                   bigint,
  user_id                   bigint,
  a_open_balance            float,
  b_expenses                float,
  c_cash_collected          float,
  d_daily_turnover          float,
  e_next_open_balance       float,
  f_bring_back_cash         float,
  g_total_balance           float,
  middle_calculate_time     varchar(255),
  middle_calculate_balance  varchar(255),
  calculate_time            varchar(255),
  courier                   varchar(255),
  others                    varchar(255),
  create_by                 varchar(255),
  modified_by               varchar(255),
  create_date               datetime,
  modified_date             datetime,
  submit_date               datetime,
  constraint pk_tb_daily_summary primary key (id))
;

create table tb_food (
  id                        bigint auto_increment not null,
  sn                        varchar(255),
  name                      varchar(255),
  name_zh                   varchar(255),
  type                      varchar(255),
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

create table report_cashier_closing (
  create_date               datetime,
  shop_name                 varchar(255),
  real_name                 varchar(255),
  open_balance              double,
  expenses                  double,
  cash_in_drawer            double,
  next_open_balance         double,
  daily_turnover            double,
  total_collection          double,
  total                     double)
;

create table report_collection_details (
  create_date               datetime,
  shop_name                 varchar(255),
  real_name                 varchar(255),
  price                     double,
  food_name                 double)
;

create table report_expenses_details (
  create_date               datetime,
  shop_name                 varchar(255),
  real_name                 varchar(255),
  food_name                 varchar(255),
  price                     double)
;

create table report_transaction_detail (
  create_date               datetime,
  order_date                datetime,
  food_name                 varchar(255),
  food_name_zh              varchar(255),
  shop_name                 varchar(255),
  retail_price              double,
  cost_price                double,
  total_cost_price          double,
  total_discount            double,
  total_retail_price        double,
  total_package             double,
  quantity                  bigint,
  free_of_charge            varchar(255))
;

create table tb_shop (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  code                      varchar(255),
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
  transaction_id            varchar(255),
  user_id                   bigint,
  shop_id                   bigint,
  food_id                   bigint,
  cost_price                float,
  retail_price              float,
  quantity                  integer,
  total_cost_price          float,
  total_discount            float,
  total_package             float,
  total_retail_price        float,
  free_of_charge            tinyint(1) default 0,
  invoice                   varchar(255),
  create_by                 varchar(255),
  modified_by               varchar(255),
  create_date               datetime,
  modified_date             datetime,
  order_date                datetime,
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

create table tb_version (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  version_sn                varchar(255),
  version_no                bigint,
  create_by                 varchar(255),
  modified_by               varchar(255),
  description               varchar(255),
  create_date               datetime,
  modified_date             datetime,
  constraint pk_tb_version primary key (id))
;

alter table tb_audit add constraint fk_tb_audit_user_1 foreign key (user_id) references tb_user (id) on delete restrict on update restrict;
create index ix_tb_audit_user_1 on tb_audit (user_id);
alter table tb_audit add constraint fk_tb_audit_shop_2 foreign key (shop_id) references tb_shop (id) on delete restrict on update restrict;
create index ix_tb_audit_shop_2 on tb_audit (shop_id);
alter table tb_cash add constraint fk_tb_cash_shop_3 foreign key (shop_id) references tb_shop (id) on delete restrict on update restrict;
create index ix_tb_cash_shop_3 on tb_cash (shop_id);
alter table tb_cash_transaction add constraint fk_tb_cash_transaction_user_4 foreign key (user_id) references tb_user (id) on delete restrict on update restrict;
create index ix_tb_cash_transaction_user_4 on tb_cash_transaction (user_id);
alter table tb_cash_transaction add constraint fk_tb_cash_transaction_shop_5 foreign key (shop_id) references tb_shop (id) on delete restrict on update restrict;
create index ix_tb_cash_transaction_shop_5 on tb_cash_transaction (shop_id);
alter table tb_cash_transaction add constraint fk_tb_cash_transaction_cash_6 foreign key (cash_id) references tb_cash (id) on delete restrict on update restrict;
create index ix_tb_cash_transaction_cash_6 on tb_cash_transaction (cash_id);
alter table tb_consume_transaction add constraint fk_tb_consume_transaction_user_7 foreign key (user_id) references tb_user (id) on delete restrict on update restrict;
create index ix_tb_consume_transaction_user_7 on tb_consume_transaction (user_id);
alter table tb_consume_transaction add constraint fk_tb_consume_transaction_shop_8 foreign key (shop_id) references tb_shop (id) on delete restrict on update restrict;
create index ix_tb_consume_transaction_shop_8 on tb_consume_transaction (shop_id);
alter table tb_consume_transaction add constraint fk_tb_consume_transaction_cons_9 foreign key (consumption_id) references tb_consumption (id) on delete restrict on update restrict;
create index ix_tb_consume_transaction_cons_9 on tb_consume_transaction (consumption_id);
alter table tb_consumption add constraint fk_tb_consumption_shop_10 foreign key (shop_id) references tb_shop (id) on delete restrict on update restrict;
create index ix_tb_consumption_shop_10 on tb_consumption (shop_id);
alter table tb_daily_summary add constraint fk_tb_daily_summary_shop_11 foreign key (shop_id) references tb_shop (id) on delete restrict on update restrict;
create index ix_tb_daily_summary_shop_11 on tb_daily_summary (shop_id);
alter table tb_daily_summary add constraint fk_tb_daily_summary_user_12 foreign key (user_id) references tb_user (id) on delete restrict on update restrict;
create index ix_tb_daily_summary_user_12 on tb_daily_summary (user_id);
alter table tb_food add constraint fk_tb_food_shop_13 foreign key (shop_id) references tb_shop (id) on delete restrict on update restrict;
create index ix_tb_food_shop_13 on tb_food (shop_id);
alter table tb_transaction add constraint fk_tb_transaction_user_14 foreign key (user_id) references tb_user (id) on delete restrict on update restrict;
create index ix_tb_transaction_user_14 on tb_transaction (user_id);
alter table tb_transaction add constraint fk_tb_transaction_shop_15 foreign key (shop_id) references tb_shop (id) on delete restrict on update restrict;
create index ix_tb_transaction_shop_15 on tb_transaction (shop_id);
alter table tb_transaction add constraint fk_tb_transaction_food_16 foreign key (food_id) references tb_food (id) on delete restrict on update restrict;
create index ix_tb_transaction_food_16 on tb_transaction (food_id);
alter table tb_user add constraint fk_tb_user_shop_17 foreign key (shop_id) references tb_shop (id) on delete restrict on update restrict;
create index ix_tb_user_shop_17 on tb_user (shop_id);


