SELECT
    s.id,
    s.name               AS shop_name,
    u.username           AS username,
    f.name               AS food_name,
    t.cost_price         AS cost_price,
    t.discount           AS discount,
    t.retail_price       AS retail_price,
    t.quantity           AS quantity,
    t.total_cost_price   AS total_cost_price,
    t.total_discount     AS total_discount,
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
AND s.id = t.shop_id