package gg.bayes.challenge.parser;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class DotaLogEvent {

	private long timestamp;
	private String hero;
	private DotaLogEventType type;
	private String target;
	private int amount;

}
