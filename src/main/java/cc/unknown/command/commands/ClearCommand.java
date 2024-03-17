package cc.unknown.command.commands;

import java.util.ArrayList;

import cc.unknown.command.Command;

public class ClearCommand extends Command {

	public ClearCommand() {
		super("clear", "cls");
	}

	@Override
	public void execute(String alias, String[] args) {
		mc.ingameGUI.getChatGUI().clearChatMessages();
	}

	@Override
	public ArrayList<String> autocomplete(int arg, String[] args) {
		return new ArrayList<>();
	}

}
