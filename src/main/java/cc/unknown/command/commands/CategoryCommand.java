package cc.unknown.command.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import cc.unknown.command.Command;
import cc.unknown.command.Flips;
import cc.unknown.module.impl.api.Category;

@Flips(name = "Category", alias = "cat", desc = "Replace category name", syntax = ".category <old name> <new name>")
public class CategoryCommand extends Command {

    private Map<Category, String> originalNames = new HashMap<>();

    @Override
    public void onExecute(String[] args) {
        if (args.length == 0) {
            sendChat(getColor("Blue") + syntax);
            return;
        }

        if (args[0].equalsIgnoreCase("reset")) {
            resetCategory();
            return;
        }
        
        if (args.length != 2) {
            sendChat(getColor("Blue") + syntax);
            return;
        }
        
        String oldName = args[0];
        String newName = args[1];
        AtomicBoolean replaced = new AtomicBoolean(false);
        
        for (Category cat : Category.values()) {
            if (cat.getName().equalsIgnoreCase(oldName)) {
                originalNames.put(cat, cat.getName());
                cat.setName(newName);
                replaced.set(true);
                break;
            }
        }
        
        if (replaced.get()) {
            sendChat("Category " + args[0] + " replaced to " + args[1]);
        }
    }

    private void resetCategory() {
        for (Map.Entry<Category, String> entry : originalNames.entrySet()) {
            Category cat = entry.getKey();
            String originalName = entry.getValue();
            cat.setName(originalName);
        }
        originalNames.clear();
    }
}