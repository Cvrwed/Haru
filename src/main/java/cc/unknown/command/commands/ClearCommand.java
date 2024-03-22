package cc.unknown.command.commands;

import cc.unknown.command.Command;

public class ClearCommand extends Command {

	@Override
	public void onExecute(String[] args) {
		mc.ingameGUI.getChatGUI().clearChatMessages();
	}
	
	@Override
    public String getSyntax() {
        return ".cls";
    }

    @Override
    public String getDesc() {
        return "Clear the chat.";
    }

	@Override
	public String getName() {
		return "cls";
	}
}
