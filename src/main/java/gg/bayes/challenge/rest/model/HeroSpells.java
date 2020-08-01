package gg.bayes.challenge.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HeroSpells {
	private String spell;
	private Integer casts;
}
