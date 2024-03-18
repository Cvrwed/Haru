package cc.unknown.command.commands;

import java.util.ArrayList;

import cc.unknown.command.Command;

public class ClearCommand extends Command {

	public ClearCommand() {
		super("cls");
	}

	@Override
	public void execute(String[] args) {
		mc.ingameGUI.getChatGUI().clearChatMessages();
	}

	@Override
	public ArrayList<String> autocomplete(int arg, String[] args) {
		return new ArrayList<>();
	}

}
