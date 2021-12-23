select
	fam_movies.name as "Artist",
	group_concat(fam_movies.original_title,'; ') as "Movies Included in Query",
	sum(fam_movies.worldwide_gross_income) as "Worldwide Revenue Generated",
	sum(fam_movies.usa_gross_income) as "Domestic Revenue Generated",
	fam_names.family_names as "Family Members Included"
from (
	-- Get all movies involving artist or artist family member
	select distinct
		a.imdb_name_id,
		a.name,
		m.*
	from (

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

	) as x
	inner join title_principals tp on
		tp.imdb_name_id = x.fam_id
	inner join movies m on
		tp.imdb_title_id = m.imdb_title_id
	inner join artist a on
		x.imdb_name_id = a.imdb_name_id
	where
		m.currency = "USD"

) as fam_movies
inner join (
	-- Get the list of family members
	select 
		fam_ids.imdb_name_id,
		group_concat(fam_names.name,'; ') as "family_names"
	from (
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
	group by fam_ids.imdb_name_id
) fam_names
on fam_movies.imdb_name_id = fam_names.imdb_name_id
-- where
-- 	fam_movies.imdb_name_id = "nm0905154"
group by
	fam_movies.imdb_name_id
order by fam_movies.imdb_title_id

	
-- nm0000226 will smith
-- nm0000093 brad pitt
-- nm0905154 lana wachowski
