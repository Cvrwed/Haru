package cc.unknown.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class Config {
    public final File file;

    public Config(File pathToFile) {
        this.file = pathToFile;

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getName() {
        return file.getName().replace(".haru", "");
    }

    public JsonObject getData() {
        JsonParser jsonParser = new JsonParser();
        try (FileReader reader = new FileReader(file)) {
            JsonElement obj = jsonParser.parse(reader);
            return obj.isJsonNull() ? null : obj.getAsJsonObject();
        } catch (JsonSyntaxException | ClassCastException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save(JsonObject data) {
        data.addProperty("creationTime", getDate());
        
        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String formattedJson = gson.toJson(data);
            out.write(formattedJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date());
    }
}
