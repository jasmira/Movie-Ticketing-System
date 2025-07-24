package org.movie.ticket.management.repository;

import org.movie.ticket.management.entity.Movie;
import org.movie.ticket.management.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {
    @Query("SELECT DISTINCT s.movie FROM Show s WHERE s.theater.city = :city")
    List<Movie> findDistinctMoviesByCity(@Param("city") String city);
}
