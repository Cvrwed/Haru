package cc.unknown.module.impl.api;

public enum Category {
	Combat("Combat"),
	Player("Player"),
	Other("Other"),
	Visuals("Visuals"),
	Exploit("Exploit"),
	Settings("Settings");
	
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
