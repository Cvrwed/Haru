package cc.unknown.command.commands;

import java.util.HashMap;
import java.util.Map;

import cc.unknown.command.Command;
import cc.unknown.utils.music.RadioPlayer;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.util.EnumChatFormatting;

public class MusicCommand extends Command {

    private static final Map<String, String> URLS = new HashMap<>();

    static {
        URLS.put("Casual", "https://streams.ilovemusic.de/iloveradio1.mp3");
        URLS.put("Dance", "https://streams.ilovemusic.de/iloveradio36.mp3");
        URLS.put("Chill Hop", "https://streams.ilovemusic.de/iloveradio17.mp3");
        URLS.put("Greatest Hits", "https://streams.ilovemusic.de/iloveradio16.mp3");
        URLS.put("The Sun", "https://streams.ilovemusic.de/iloveradio15.mp3");
    }

    private final RadioPlayer radioPlayer = new RadioPlayer();
    private volatile Thread musicThread;
    private boolean musicPlaying = false;

    public MusicCommand() {
        super("music");
    }

    @Override
    public void onExecute(String alias, String[] args) {
        if (args.length == 0) {
            toggleMusic();
        } else {
            if (args[0].equalsIgnoreCase("mode")) {
                if (args.length >= 2) {
                    playMusic(args[1]);
                } else {
                    PlayerUtil.send(EnumChatFormatting.RED + " Syntax Error. Usage: .music mode <Genre>");
                }
            } else if (args[0].equalsIgnoreCase("volume")) {
                if (args.length >= 2) {
                    try {
                        setVolume(Integer.parseInt(args[1]));
                    } catch (NumberFormatException e) {
                        PlayerUtil.send(EnumChatFormatting.RED + " Invalid Integer.");
                    }
                } else {
                    PlayerUtil.send(EnumChatFormatting.RED + " Syntax Error. Usage: .music volume <Volume>");
                }
            }
        }
    }
    
    private void toggleMusic() {
        if (musicPlaying) {
            stopMusic();
            musicPlaying = false;
            PlayerUtil.send(EnumChatFormatting.RED + " Music off.");
        } else {
            String defaultGenre = "Casual";
            playMusic(defaultGenre);
            musicPlaying = true;
            PlayerUtil.send(EnumChatFormatting.GREEN + " Music on.");
        }
    }

    private void playMusic(String genre) {
        String stationUrl = URLS.get(genre);
        if (stationUrl != null) {
            musicThread = new Thread(() -> {
                try {
                    radioPlayer.start(stationUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            musicThread.start();
        } else {
        	PlayerUtil.send(EnumChatFormatting.RED + " Invalid music genre.");
        }
    }

    private void stopMusic() {
        if (musicThread != null && musicThread.isAlive()) {
            musicThread.interrupt();
            try {
                musicThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        radioPlayer.stop();
        radioPlayer.setCurrent("");
        PlayerUtil.send(EnumChatFormatting.RED +  " Music stopped.");
    }
    
    private void setVolume(int volume) {
        radioPlayer.setVolume(volume);
        PlayerUtil.send(EnumChatFormatting.GREEN + " Volume set to " + volume);
    }
}
