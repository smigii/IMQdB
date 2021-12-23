select fam_ids.imdb_name_id, group_concat(fam_names.name) from (
	select a.imdb_name_id, a.imdb_name_id as fam_id from artist a
	
	union

	select a.imdb_name_id, sa.spouse_id as fam_id from artist a
	inner join spouses_artist sa on a.imdb_name_id = sa.imdb_name_id
	
	union
	
	select a.imdb_name_id, ca.child_imdb_id as fam_id from artist a
	inner join children_artist ca on a.imdb_name_id = ca.imdb_name_id

	union
	
	select a.imdb_name_id, pa.parent_id as fam_id from artist a
	inner join parents_artist pa on a.imdb_name_id = pa.imdb_name_id
	
	union
	
	select a.imdb_name_id, ra.relative_id as fam_id from artist a
	inner join relatives_artist ra on a.imdb_name_id = ra.imdb_name_id
) as fam_ids
inner join artist fam_names on fam_ids.fam_id = fam_names.imdb_name_id
-- where fam_ids.imdb_name_id = "nm0000226"
group by fam_ids.imdb_name_id