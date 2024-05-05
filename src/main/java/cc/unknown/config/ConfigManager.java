package cc.unknown.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import cc.unknown.Haru;
import cc.unknown.module.impl.Module;
import cc.unknown.utils.Loona;

public class ConfigManager implements Loona {
	public final File configDirectory = new File(mc.mcDataDir + File.separator + "Haru" + File.separator + "configs");

	private Config config;
	private final ArrayList<Config> configs = new ArrayList<>();

	public ConfigManager() {
		if (!configDirectory.isDirectory()) {
			configDirectory.mkdirs();
		}

		discoverConfigs();
		File defaultFile = new File(configDirectory, "default.haru");
		this.config = new Config(defaultFile);

		if (!defaultFile.exists()) {
			save();
		}

	}

	private boolean isOutdated(File file) {
		JsonParser jsonParser = new JsonParser();
		try (FileReader reader = new FileReader(file)) {
			JsonElement jsonElement = jsonParser.parse(reader);

			return !jsonElement.isJsonObject();
		} catch (IOException | JsonSyntaxException e) {
			e.printStackTrace();
			return true;
		}
	}

	public void discoverConfigs() {
		configs.clear();
		if (configDirectory.listFiles() == null || !(Objects.requireNonNull(configDirectory.listFiles()).length > 0))
			return;

		for (File file : Objects.requireNonNull(configDirectory.listFiles())) {
			if (file.getName().endsWith(".haru")) {
				if (!isOutdated(file)) {
					configs.add(new Config(new File(file.getPath())));
				}
			}
		}
	}

	public Config getConfig() {
		return config;
	}

	public void save() {
		JsonObject data = new JsonObject();
		JsonObject modules = new JsonObject();
		for (Module module : Haru.instance.getModuleManager().getModule()) {
			modules.add(module.getRegister().name(), module.getConfigAsJson());
		}
		data.add("modules", modules);

		config.save(data);
	}

	public void setConfig(Config config) {
		this.config = config;
		JsonObject data = Objects.requireNonNull(config.getData()).get("modules").getAsJsonObject();
		List<Module> knownModules = new ArrayList<>(Haru.instance.getModuleManager().getModule());
		for (Module module : knownModules) {
			if (data.has(module.getRegister().name())) {
				module.applyConfigFromJson(data.get(module.getRegister().name()).getAsJsonObject());
			} else {
				module.resetToDefaults();
			}
		}
	}

	public void loadConfigByName(String replace) {
		discoverConfigs();
		for (Config config : configs) {
			if (config.getName().equals(replace))
				setConfig(config);
		}
	}

	public ArrayList<Config> getConfigs() {
		discoverConfigs();
		return configs;
	}

	public void copyConfig(Config config, String s) {
		File file = new File(configDirectory, s);
		Config newConfig = new Config(file);
		newConfig.save(config.getData());
	}

	public void resetConfig() {
		for (Module module : Haru.instance.getModuleManager().getModule())
			module.resetToDefaults();
		save();
	}

	public void deleteConfig(Config config) {
		config.file.delete();
		if (config.getName().equals(this.config.getName())) {
			discoverConfigs();
			if (this.configs.size() < 2) {
				this.resetConfig();
				File defaultFile = new File(configDirectory, "default.haru");
				this.config = new Config(defaultFile);
				save();
			} else {
				this.config = this.configs.get(0);
			}

			this.save();
		}
	}
}
