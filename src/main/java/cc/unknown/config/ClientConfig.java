package cc.unknown.config;

import static cc.unknown.ui.clickgui.EditHudPositionScreen.ArrayListX;
import static cc.unknown.ui.clickgui.EditHudPositionScreen.ArrayListY;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringJoiner;

import cc.unknown.Haru;
import cc.unknown.ui.clickgui.raven.impl.CategoryComp;
import cc.unknown.utils.Loona;
import cc.unknown.utils.client.FuckUtil;
import cc.unknown.utils.helpers.MathHelper;

public class ClientConfig implements Loona {
	private final File configFile;
	private final File configDir;
	private final String fileName = "config";
	private final String clickGuiPos = "clickgui:pos:";

	public ClientConfig() {
		configDir = new File(mc.mcDataDir, "Haru");
		if(!configDir.exists()) {
			configDir.mkdir();
		}
		
		configFile = new File(configDir, fileName);
		if(!configFile.exists()) {
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();	
			}
		}
	}
	
	public void saveConfig() {
		List<String> config = new ArrayList<>();
		config.add(clickGuiPos + getClickGuiPos());		
		config.add(ArrayListX + FuckUtil.instance.getArrayListX());
		config.add(ArrayListY + FuckUtil.instance.getArrayListY());
		
		config.add(FuckUtil.instance.WaifuX + FuckUtil.instance.getWaifuX());
		config.add(FuckUtil.instance.WaifuY + FuckUtil.instance.getWaifuY());

	    try (PrintWriter writer = new PrintWriter(configFile)) {
	        for (String line : config) {
	            writer.println(line);
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public void applyConfig() {
	    List<String> config = parseConfigFile();
	    Map<String, Action> cfg = new HashMap<>();
	    cfg.put(clickGuiPos, this::loadClickGuiCoords);
	    cfg.put(ArrayListX, hudX -> FuckUtil.instance.setArrayListX(Integer.parseInt(hudX)));
	    cfg.put(ArrayListY, hudY -> FuckUtil.instance.setArrayListY(Integer.parseInt(hudY)));
	    cfg.put(FuckUtil.instance.WaifuX, waifuX -> FuckUtil.instance.setWaifuX(Integer.parseInt(waifuX)));
	    cfg.put(FuckUtil.instance.WaifuY, waifuY -> FuckUtil.instance.setWaifuY(Integer.parseInt(waifuY)));

	    for (String line : config) {
	        for (Map.Entry<String, Action> entry : cfg.entrySet()) {
	            if (line.startsWith(entry.getKey())) {
	                entry.getValue().apply(line.replace(entry.getKey(), ""));
	                break;
	            }
	        }
	    }
	}
	
	private List<String> parseConfigFile() {
	    List<String> configFileContents = new ArrayList<>();

	    try (Scanner reader = new Scanner(configFile)) {
	        while (reader.hasNextLine()) {
	            configFileContents.add(reader.nextLine());
	        }
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }

	    return configFileContents;
	}

	private void loadClickGuiCoords(String decryptedString) {
		if (decryptedString == null || decryptedString.isEmpty()) {
			return;
		}

		for (String what : decryptedString.split("/")) {
			for (CategoryComp cat : Haru.instance.getHaruGui().getCategoryList()) {
				if (cat == null || cat.getCategory() == null) {
					continue;
				}

				if (what.startsWith(cat.getCategory().getName())) {
					try {
						List<String> cfg = MathHelper.StringListToList(what.split("~"));
						if (cfg.size() >= 4) {
							cat.setX(Integer.parseInt(cfg.get(1)));
							cat.setY(Integer.parseInt(cfg.get(2)));
							cat.setOpened(Boolean.parseBoolean(cfg.get(3)));
						}
					} catch (IndexOutOfBoundsException | IllegalArgumentException e) { }
				}
			}
		}
	}
	
	private String getClickGuiPos() {
	    StringJoiner posConfig = new StringJoiner("/");
	    
	    for (CategoryComp cat : Haru.instance.getHaruGui().getCategoryList()) {
	        posConfig.add(String.join("~", cat.getCategory().getName(), String.valueOf(cat.getX()), String.valueOf(cat.getY()), String.valueOf(cat.isOpen())));
	    }
	    return posConfig.toString();
	}
	
	@FunctionalInterface
	public interface Action {
	    void apply(String value);
	}

}