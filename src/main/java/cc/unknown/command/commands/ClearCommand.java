package cc.unknown.command.commands;

import cc.unknown.command.Command;

public class ClearCommand extends Command {

	public ClearCommand() {
		super("cls");
	}

	@Override
	public void onExecute(String alias, String[] args) {
		mc.ingameGUI.getChatGUI().clearChatMessages();
	}
}
