UPDATE items_base SET interaction_type = 'totem_leg' WHERE item_name = 'totem_leg';
UPDATE items_base SET interaction_type = 'totem_head' WHERE item_name = 'totem_head';
UPDATE items_base SET interaction_type = 'totem_planet' WHERE item_name = 'totem_planet';
UPDATE items_base SET interaction_modes_count = '3' WHERE item_name = 'totem_planet';
UPDATE items_base SET interaction_modes_count = '12' WHERE item_name = 'totem_leg';
UPDATE items_base SET interaction_modes_count = '9' WHERE item_name = 'totem_head';
