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

import cc.unknown.mixin.interfaces.music.IPlayer;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class RadioPlayer {
	private Player player = null;
	private Thread thread;
	private String current;

	public void start(String url) {
		stop();

		new Thread(() -> {
			try {
				SSLContext sslContext = SSLContext.getInstance("TLS");
				sslContext.init(null, new TrustManager[] { new TrustAllCertificates() }, new SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

				HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
				connection.setRequestMethod("GET");
				connection.setReadTimeout(10000);
				connection.setConnectTimeout(10000);

				InputStream stream = new BufferedInputStream(connection.getInputStream());

				player = new Player(stream);
				thread = new Thread(() -> {
					try {
						player.play();
					} catch (JavaLayerException e) {
						e.printStackTrace();
					}
				});
				thread.start();
				current = url;
			} catch (IOException | JavaLayerException | KeyManagementException | NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}).start();
	}

	public void stop() {
		if (thread != null) {
			thread.interrupt();
			thread = null;
		}
		if (player != null) {
			player.close();
			player = null;
		}
	}

	public void setVolume(int v) {
		if (this.thread != null) {
			((IPlayer) this.player).setControl((float) (v * 0.8600000143051147 - 80.0));
		}
	}

	public String getCurrent() {
		return current;
	}

	public void setCurrent(String current) {
		this.current = current;
	}
}