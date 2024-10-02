package cc.unknown.command.commands;

import cc.unknown.command.Command;

public class ClearCommand extends Command {

	public ClearCommand() {
		super("Clear", "Clear the chat.", "cls", ".cls");
	}

	@Override
	public void onExecute(String[] args) {
		clearChat();
	}
}
