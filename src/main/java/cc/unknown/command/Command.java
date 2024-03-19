package cc.unknown.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cc.unknown.utils.interfaces.Loona;

public abstract class Command implements Loona {
	private String name;
	private String[] aliases;

	protected Command(String name, String... aliases) {
		this.name = name;
		this.aliases = aliases;
	}

	public abstract void onExecute(String alias, String[] args);

	boolean match(String name) {
		for (String alias : aliases) {
			if (alias.equalsIgnoreCase(name))
				return true;
		}
		return this.name.equalsIgnoreCase(name);
	}

	List<String> getNameAndAliases() {
		List<String> l = new ArrayList<>();
		l.add(name);
		l.addAll(Arrays.asList(aliases));

		return l;
	}

	public String getName() {
		return name;
	}

}