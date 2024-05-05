package cc.unknown.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cc.unknown.Haru;
import cc.unknown.command.commands.*;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.network.ChatSendEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.setting.Setting;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.player.PlayerUtil;

public class CommandManager {
	private List<Command> commands = new ArrayList<>();
	private String prefix = ".";

	public CommandManager() {
		Haru.instance.getEventBus().register(this);
		add(
			new ConfigCommand(),
			new HelpCommand(),
			new BindCommand(),
			new CategoryCommand(),
			new ToggleCommand(),
			new FriendCommand(),
			new TransactionCommand(),
			new ClearCommand(),
			new GameCommand(),
			new PingCommand(),
			new SpyCommand()
			);
	}

	@EventLink
	public void onChatSend(ChatSendEvent e) {
	    try {
	        String message = e.getMessage();

	        if (message.startsWith(prefix)) {
	            e.setCancelled(true);
	            message = message.substring(1);

	            String[] arguments = message.split(" ");
	            String cmdName = arguments[0];
	            for (Command cmd : commands) {
	                if (cmd.name.equalsIgnoreCase(cmdName) || cmd.alias.equalsIgnoreCase(cmdName)) {
	                    String[] args = Arrays.copyOfRange(arguments, 1, arguments.length);
	                    cmd.onExecute(args);
	                    return;
	                }
	            }

	            for (final Module module : Haru.instance.getModuleManager().getModule()) {
	                if (module.getRegister().name().equalsIgnoreCase(cmdName)) {
	                    if (arguments.length > 1) {
	                        if (module.getSettingAlternative(arguments[1]) != null) {
	                            final Setting setting = module.getSettingAlternative(arguments[1]);

	                            try {
	                                if (setting instanceof BooleanValue) {
	                                    ((BooleanValue) setting).setEnabled(Boolean.parseBoolean(arguments[2]));
	                                } else if (setting instanceof SliderValue) {
	                                    ((SliderValue) setting).setValue(Double.parseDouble(arguments[2]));
	                                } else if (setting instanceof DoubleSliderValue) {
	                                    ((DoubleSliderValue) setting).setValueMin(Double.parseDouble(arguments[2]));
	                                    ((DoubleSliderValue) setting).setValueMax(Double.parseDouble(arguments[3]));
	                                } else if (setting instanceof ModeValue) {
	                                    ((ModeValue) setting).setMode(arguments[2]);
	                                }
	                            } catch (final NumberFormatException ignored) {
	                                return;
	                            } catch (final ArrayIndexOutOfBoundsException ignored) {
	                                return;
	                            }
	                        } else {
	                            PlayerUtil.send("§c'" + arguments[1] + "' setting doesn't exist");
	                            return;
	                        }
	                    }
	                }
	            }
	            //PlayerUtil.send("§c'" + cmdName + "' doesn't exist");
	        }
	    } catch (NullPointerException ignorethatshit) {
	    }
	}

	public Command getCommand(Class<? extends Command> clazz) {
		return commands.stream().filter(command -> command.getClass().equals(clazz)).findFirst().orElse(null);
	}
	
    private void add(Command... c) {
        commands.addAll(Arrays.asList(c));
    }

	public List<Command> getCommand() {
		return commands;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}
}