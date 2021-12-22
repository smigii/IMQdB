select a1.name, group_concat(a2.name) as "Parent(s)", m.original_title, m.year from parents_artist pa

inner join artist a1 on pa.imdb_name_id = a1.imdb_name_id
inner join artist a2 on pa.parent_id = a2.imdb_name_id

inner join title_principals tp on tp.imdb_name_id = pa.imdb_name_id
inner join title_principals tp2 on tp2.imdb_name_id = pa.imdb_name_id

inner join movies m on m.imdb_title_id = tp.imdb_title_id

where
	tp.imdb_title_id = "tt1815862" and
	tp.imdb_title_id = tp2.imdb_title_id

group by
	m.imdb_title_id