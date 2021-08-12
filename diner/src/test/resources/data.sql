insert into RESTO_ORDER(STATUS) values('PREPARING');

insert into resto_orderitem (order_item_type, item_preped, quantity, item_ref, order_id) values ('DRINK', false, 1, 'Cola', 1);
insert into resto_orderitem (order_item_type, item_preped, quantity, item_ref, order_id) values ('DISH', false, 1, 'Frites', 1);
