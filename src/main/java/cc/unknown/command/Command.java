package cc.unknown.command;

import java.util.ArrayList;
import java.util.Arrays;

import cc.unknown.utils.interfaces.Loona;

public abstract class Command implements Loona {

    private String name;
    private String[] aliases;

    protected Command(String name, String... aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    public abstract void execute(String alias, String[] args);

    public abstract ArrayList<String> autocomplete(int arg, String[] args);

    public boolean match(String name) {
        for (String alias : aliases) {
            if (alias.equalsIgnoreCase(name)) return true;
        }
        return this.name.equalsIgnoreCase(name);
    }
    
    ArrayList<String> getNameAndAliases() {
    	ArrayList<String> l = new ArrayList<>();
        l.add(name);
        l.addAll(Arrays.asList(aliases));

        return l;
    }

	public String getName() {
		return name;
	}

	public String[] getAliases() {
		return aliases;
	}
}