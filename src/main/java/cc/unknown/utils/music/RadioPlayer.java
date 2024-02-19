package cc.unknown.utils.music;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import cc.unknown.Haru;
import cc.unknown.mixin.interfaces.music.IPlayer;
import cc.unknown.module.impl.other.MusicPlayer;
import cc.unknown.utils.client.AdvancedTimer;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class RadioPlayer {
    private Thread thread;
    private Player player = null;
    private String current;
    private final AdvancedTimer timer = new AdvancedTimer(0);

    public void start(String url) throws JavaLayerException, IOException, NoSuchAlgorithmException, KeyManagementException, ReflectiveOperationException {
        if (this.timer.reached(20000L)) {
            try {
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[]{new TrustAllCertificates()}, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(10000);

                InputStream stream = new BufferedInputStream(connection.getInputStream());

                this.player = new Player(stream);
                this.thread = new Thread(() -> {
                    try {
                        this.player.play();
                    } catch (JavaLayerException e) {
                        e.printStackTrace();
                    }
                });
                this.thread.start();
                this.timer.reset();
                this.current = "" + url;
            } catch (IOException | JavaLayerException e) {
                e.printStackTrace();
            }
        }
    }	
    
    public void stop() {
    	new Thread(() -> {
	        if (this.thread != null) {
	            this.thread.interrupt();
	            this.thread = null;
	        }
	        if (this.player != null) {
	            this.player.close();
	            this.player = null;
	        }
    	}).start();
    }
    
    public void setVolume() {
    	MusicPlayer musicPlayer = (MusicPlayer) Haru.instance.getModuleManager().getModule(MusicPlayer.class);

    	if (this.thread != null) {
    		((IPlayer)this.player).setGain((-musicPlayer.volume.getInputToFloat()));
    	}
    }

    public String getCurrent() {
        return this.current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }
}