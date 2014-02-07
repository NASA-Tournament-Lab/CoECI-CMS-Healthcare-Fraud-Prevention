
INSERT INTO  role(id, name) VALUES('1', 'role1');
INSERT INTO  role(id, name) VALUES('2', 'role2');
INSERT INTO  role(id, name) VALUES('3', 'role3');

INSERT INTO  user(id, username, role_id, auto_retrieve_cached_data, created_date, password_hash, organization_name) VALUES('091f80d7-8ecb-429c-8f0b-caeaae18dcd8', 'user1', '1', 1, CURRENT_TIMESTAMP, 'L57Wnkszm+QfQVG3bMytHPkX6oePYUHxwZvabLqNaUafxkgtn0lopE0oUG2viaeZ', 'org1');
INSERT INTO  user(id, username, role_id, auto_retrieve_cached_data, created_by, created_date, password_hash, organization_name) VALUES('091f80d7-8ecb-429c-8f0b-caeaae18dcd9', 'user2', '2', 0, '091f80d7-8ecb-429c-8f0b-caeaae18dcd8', CURRENT_TIMESTAMP, 'eoVqUjsT/tBfopzrn5jSJQBQuqm/Y/jgij9RbcyIJDCy15MiW2sA3Mg2xiOK5cKm', 'org2');
INSERT INTO  user(id, username, role_id, auto_retrieve_cached_data, created_by, created_date, password_hash, organization_name) VALUES('091f80d7-8ecb-429c-8f0b-caeaae18dcda', 'user3', '3', 0, '091f80d7-8ecb-429c-8f0b-caeaae18dcd8', CURRENT_TIMESTAMP, 'sRwnKGaf/6v+ieLhNRpHDUbjM4AcjU5kgtcutmXsELxeZ5fJtc/D5RQD5I0eYSKc', 'org3');


INSERT INTO  user_stat(user_id, data_requests_responded) VALUES('091f80d7-8ecb-429c-8f0b-caeaae18dcd8', 1);
INSERT INTO  user_stat(user_id, data_requests_responded) VALUES('091f80d7-8ecb-429c-8f0b-caeaae18dcd9', 1);
INSERT INTO  user_stat(user_id, data_requests_responded) VALUES('091f80d7-8ecb-429c-8f0b-caeaae18dcda', 1);

  
  