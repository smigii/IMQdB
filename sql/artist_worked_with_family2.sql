select m.original_title as "Movie", a1.name as "Artist", t1.title as Role, a2.name as "Family Member", t2.title as "Family Member Role", Relation as "Family Member Relation" from (
	-- Artists worked with parent
	select
		pa.imdb_name_id, tp.title_id, pa.parent_id as family_id, tp2.title_id as family_role_id, tp.imdb_title_id, "Parent" as "Relation"
	from parents_artist pa

	inner join title_principals tp on tp.imdb_name_id = pa.imdb_name_id
	inner join title_principals tp2 on tp2.imdb_name_id = pa.parent_id

	where
		tp.imdb_title_id = tp2.imdb_title_id
	
	union
	
	-- Artists worked with child
	select
		ca.imdb_name_id, tp.title_id, ca.child_imdb_id as family_id, tp2.title_id as family_role_id, tp.imdb_title_id, "Child" as "Relation"
	from children_artist ca

	inner join title_principals tp on tp.imdb_name_id = ca.imdb_name_id
	inner join title_principals tp2 on tp2.imdb_name_id = ca.child_imdb_id

	where
		tp.imdb_title_id = tp2.imdb_title_id
	
	union
	
	-- Artists worked with spouse
	select
		sa.imdb_name_id, tp.title_id, sa.spouse_id as family_id, tp2.title_id as family_role_id, tp.imdb_title_id, "Spouse" as "Relation"
	from spouses_artist sa

	inner join title_principals tp on tp.imdb_name_id = sa.imdb_name_id
	inner join title_principals tp2 on tp2.imdb_name_id = sa.spouse_id

	where
		tp.imdb_title_id = tp2.imdb_title_id
	
	union
	
	-- Artists worked with relative
	select
		ra.imdb_name_id, tp.title_id, ra.relative_id as family_id, tp2.title_id as family_role_id, tp.imdb_title_id, "Relative" as "Relation"
	from relatives_artist ra

	inner join title_principals tp on tp.imdb_name_id = ra.imdb_name_id
	inner join title_principals tp2 on tp2.imdb_name_id = ra.relative_id

	where
		tp.imdb_title_id = tp2.imdb_title_id
		
) as x

inner join artist a1 on x.imdb_name_id = a1.imdb_name_id
inner join artist a2 on x.family_id = a2.imdb_name_id
inner join titles t1 on x.title_id = t1.title_id
inner join titles t2 on x.family_role_id = t2.title_id
inner join movies m  on x.imdb_title_id = m.imdb_title_id

where
	m.year = 2010