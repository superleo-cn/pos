--Report 1
SELECT
    s.id,
    s.name               AS shop_name,
    u.username           AS username,
    f.name               AS food_name,
    f.name_zh            AS food_name_zh,
    t.cost_price         AS cost_price,
    t.retail_price       AS retail_price,
    t.quantity           AS quantity,
    t.total_cost_price   AS total_cost_price,
    t.total_discount     AS total_discount,
    t.total_package      AS total_package,
    t.free_of_charge     AS free_of_charge,
    t.total_retail_price AS total_retail_price,
    t.create_date        AS create_date
FROM
    tb_shop s,
    tb_user u,
    tb_food f,
    tb_transaction t
WHERE
    s.id = u.shop_id
AND s.id = f.shop_id
AND u.id = t.user_id
AND f.id = t.food_id
AND s.id = t.shop_id;

--Report 2
SELECT
    s.name AS shop_name,
    u.realname AS real_name,
    d.a_open_balance,
    d.b_expenses,
    d.c_cash_collected,
    d.d_daily_turnover,
    d.e_next_open_balance,
    f_bring_back_cash,
    d.create_date
FROM
    tb_shop s,
    tb_user u,
    tb_daily_summary d
WHERE
    s.id = u.shop_id
AND s.id = d.shop_id
AND u.id = d.user_id
AND d.create_date BETWEEN '2013-11-01' AND '2013-12-31';

--P & L we need 2 sqls, Net Profit (A-B-C)
--sql 1
SELECT
    shop_id,
     SUM(c_cash_collected) AS cash_collected  --get A Sales.
    SUM(b_expenses)       AS expenses,   -- get C Expenses
   
FROM
    tb_daily_summary
WHERE
    create_date BETWEEN '2013-11-01' AND '2013-21-31'
GROUP BY
    shop_id;
--sql 2
SELECT
    shop_id,
    SUM(total_cost_price) --get B Cost of Sales
FROM
    tb_transaction
WHERE
    create_date BETWEEN '2013-11-01' AND '2013-21-31'
GROUP BY
    shop_id    

-- report cashier closing
--select s.name AS shop_name,u.realname AS real_name,d.a_open_balance AS open_balance,d.c_cash_collected AS cash_in_drawer,d.b_expenses AS expenses,(d.c_cash_collected - d.e_next_open_balance) AS total_collection,d.d_daily_turnover AS daily_turnover,(d.b_expenses + d.c_cash_collected) AS total,d.create_date AS create_date,d.e_next_open_balance as next_open_balance from ((tb_shop s join tb_user u) join tb_daily_summary d) where ((s.id = u.shop_id) and (s.id = d.shop_id) and (u.id = d.user_id))
drop view report_cashier_closing;
create view report_cashier_closing as
select s.name AS shop_name,u.realname AS real_name,d.a_open_balance AS open_balance,d.c_cash_collected AS cash_in_drawer,d.b_expenses AS expenses,
d.f_bring_back_cash AS total_collection,
d.d_daily_turnover AS daily_turnover,
g.g_total_balance AS total,
d.create_date AS create_date,d.e_next_open_balance as next_open_balance 
from ((tb_shop s join tb_user u) join tb_daily_summary d) where ((s.id = u.shop_id) and (s.id = d.shop_id) and (u.id = d.user_id))
 
-- report export expenses details
create view report_expenses_details as
SELECT tct.create_date,tu.realname real_name,ts.name shop_name,
tc.name food_name,tct.price FROM tb_consume_transaction tct,
tb_consumption tc, tb_user tu,tb_shop ts where tct.shop_id=tc.shop_id and tct.consumption_id=tc.id and tct.user_id=tu.id and ts.id=tct.shop_id
order by create_date desc,realname,shop_name,food_name;

-- report export collection details
create view report_collection_details as
SELECT tct.create_date,tu.realname real_name,ts.name shop_name,
tc.price food_name,tct.total_price price FROM tb_cash_transaction tct,
tb_cash tc, tb_user tu,tb_shop ts where tct.shop_id=tc.shop_id and tct.cash_id=tc.id and tct.user_id=tu.id and ts.id=tct.shop_id
order by create_date desc,realname,shop_name,food_name;