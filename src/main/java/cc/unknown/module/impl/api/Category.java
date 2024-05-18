package cc.unknown.module.impl.api;

public enum Category {
	Combat("Combat"),
	Player("Player"),
	Move("Movement"),
	Other("Other"),
	Visuals("Visuals"),
	Exploit("Exploit");
	
	private String name;

	private Category(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
