package gg.bayes.challenge.jpa.model;

import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table
@Data
public class MatchHero {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	private Match match;

	@ElementCollection
	private Map<Long, String> items;

	@ElementCollection
	private Map<String, Integer> spellCasts;

	@OneToMany(mappedBy = "hero", cascade = CascadeType.ALL)
	private Map<String, MatchHeroDamage> heroDamages;
}
