package gg.bayes.challenge.jpa.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import gg.bayes.challenge.jpa.model.Match;

@Repository
public interface MatchRepository extends CrudRepository<Match, Long> {

}
