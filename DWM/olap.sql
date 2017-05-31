-- Table creation code:

CREATE TABLE inventory(item varchar(50), colour varchar(50), quantity int);

INSERT INTO inventory VALUES('Table', 'Blue', 124);
INSERT INTO inventory VALUES('Table', 'Red', 223);
INSERT INTO inventory VALUES('Chair', 'Blue', 101);
INSERT INTO inventory VALUES('Chair', 'Red', 210);

CREATE TABLE cost(item varchar(50), cost int);

INSERT INTO cost VALUES('Table', 50);
INSERT INTO cost VALUES('Chair', 100);

-- CUBE:

SELECT item, colour, SUM(quantity) AS quantity_sum
FROM inventory
GROUP BY item, colour
WITH CUBE;

/* CUBE Output:

item                                               colour                                             quantity_sum 
-------------------------------------------------- -------------------------------------------------- ------------ 
Chair                                              Blue                                               101
Chair                                              Red                                                210
Chair                                              NULL                                               311
Table                                              Blue                                               124
Table                                              Red                                                223
Table                                              NULL                                               347
NULL                                               NULL                                               658
NULL                                               Blue                                               225
NULL                                               Red                                                433
*/

-- SLICE:

SELECT colour, COUNT(quantity) AS quantity_count
FROM inventory
GROUP BY colour;

/* SLICE Output:

colour                                             quantity_count 
-------------------------------------------------- -------------- 
Blue                                               2
Red                                                2
*/

-- DICE:

SELECT colour, COUNT(quantity) AS quantity_count
FROM inventory
GROUP BY item, colour
HAVING item='Table'
AND colour='Blue';

/* DICE Output:

colour                                             quantity_count 
-------------------------------------------------- -------------- 
Blue                                               1
*/

-- ROLLUP:

SELECT item, colour, SUM(quantity) AS quantity_sum
FROM inventory
GROUP BY item, colour
WITH ROLLUP;

/* ROLLUP Output:

item                                               colour                                             quantity_sum 
-------------------------------------------------- -------------------------------------------------- ------------ 
Chair                                              Blue                                               101
Chair                                              Red                                                210
Chair                                              NULL                                               311
Table                                              Blue                                               124
Table                                              Red                                                223
Table                                              NULL                                               347
NULL                                               NULL                                               658
*/

-- DRILL DOWN:

SELECT i.item, colour, cost, SUM(quantity) AS quantity_sum
FROM inventory i, cost c
WHERE i.item = c.item
GROUP BY i.item, colour, cost
ORDER BY i.item, colour, cost;

/* DRILL DOWN Output:

item                                       colour                                     cost        quantity_sum 
------------------------------------------ ------------------------------------------ ----------- ------------ 
Chair                                      Blue                                       100         101
Chair                                      Red                                        100         210
Table                                      Blue                                       50          124
Table                                      Red                                        50          223
*/