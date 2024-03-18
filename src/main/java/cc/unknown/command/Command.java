package cc.unknown.command;

import java.util.ArrayList;

import cc.unknown.utils.interfaces.Loona;

public abstract class Command implements Loona {

    private String name;

    protected Command(String name) {
        this.name = name;
    }

    public abstract void execute(String[] args);

    public abstract ArrayList<String> autocomplete(int arg, String[] args);

    public boolean match(String name) {
        return this.name.equalsIgnoreCase(name);
    }
    
    ArrayList<String> getNameAndAliases() {
    	ArrayList<String> l = new ArrayList<>();
        l.add(name);

        return l;
    }

	public String getName() {
		return name;
	}
}