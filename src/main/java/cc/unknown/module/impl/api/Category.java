package cc.unknown.module.impl.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public enum Category {
	Combat("Combat"),
	Player("Player"),
	Move("Movement"),
	Other("Other"),
	Visuals("Visuals"),
	Exploit("Exploit");
	
	@Setter private String name;
}
