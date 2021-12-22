select
	movies.imdb_title_id, movies.original_title, group_concat(artist.name) as Family, tp.imdb_name_id
from title_principals tp
inner join artist on tp.imdb_name_id = artist.imdb_name_id
inner join movies on tp.imdb_title_id = movies.imdb_title_id
	
where
-- 	tp.imdb_title_id = "tt1815862" and
-- 	movies.year = "2007" and
	tp.imdb_title_id in (
		select imdb_title_id from title_principals tp0
		where tp0.imdb_name_id = "nm0000226"
	)
	and
	(
		-- Which actors acted with a parent
		(
			tp.imdb_name_id in (
				select
					ca.child_imdb_id
				from title_principals tp1
				inner join children_artist ca on tp1.imdb_name_id = ca.imdb_name_id
				where
					tp1.imdb_title_id = tp.imdb_title_id
			)
		)
		or
		-- Which actors acted with child
		(
			tp.imdb_name_id in (
				select
					pa.parent_id
				from title_principals tp2
				inner join parents_artist pa on tp2.imdb_name_id = pa.imdb_name_id
				where
					tp2.imdb_title_id = tp.imdb_title_id
			)
		)
	)
	
group by
	tp.imdb_title_id
order by
	tp.imdb_title_id
limit 3

	
-- tt1815862 After Earth
-- nm0000226 Will Smith
-- nm1535523 Jaden Smith
-- nm0000586 Jada