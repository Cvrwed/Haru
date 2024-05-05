package cc.unknown.command.commands;

import cc.unknown.command.Command;
import cc.unknown.command.Flips;

@Flips(name = "Clear", alias = "cls", desc = "Clear the chat.", syntax = ".cls")
public class ClearCommand extends Command {

	@Override
	public void onExecute(String[] args) {
		clearChat();
	}
}
