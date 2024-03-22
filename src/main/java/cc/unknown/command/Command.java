package cc.unknown.command;

import cc.unknown.utils.interfaces.Loona;

public abstract class Command implements Loona {
    public abstract void onExecute(String[] args);
    public abstract String getName();
    public abstract String getSyntax();
    public abstract String getDesc();

}