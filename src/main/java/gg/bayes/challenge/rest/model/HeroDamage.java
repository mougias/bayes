package gg.bayes.challenge.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HeroDamage {
	private String target;
	@JsonProperty("damage_instances")
	private Integer damageInstances;
	@JsonProperty("total_damage")
	private Integer totalDamage;
}
