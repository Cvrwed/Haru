package cc.unknown.ui;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;

import cc.unknown.mixin.interfaces.IMinecraft;
import cc.unknown.ui.auth.Browser;
import cc.unknown.ui.auth.MicrosoftAccount;
import cc.unknown.ui.auth.MicrosoftLogin;
import cc.unknown.utils.client.RenderUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;

public class AltLoginScreen extends GuiScreen {

	private GuiTextField email;
	private GuiTextField password;
	private static String[] cookie_string;
	private final Button[] buttons = { 
			new Button("Login"), 
			new Button("Cookie Login"), 
			new Button("Back") };
	private String status;

	@Override
	public void initGui() {
		ScaledResolution sr = new ScaledResolution(mc);

		int buttonHeight = 20;

		int totalHeight = buttonHeight * buttons.length;

		int y = Math.max(sr.getScaledHeight() / 2 - totalHeight / 2 - 50, 75);

		email = new GuiTextField(0, mc.fontRendererObj, sr.getScaledWidth() / 2 - 80, y, 160, 20);
		password = new GuiTextField(1, mc.fontRendererObj, sr.getScaledWidth() / 2 - 80, y + 30, 160, 20);

		for (Button button : buttons) {
			button.updateState(false);
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		RenderUtil.drawRect(0.0D, 0.0D, this.width, this.height, (new Color(0)).getRGB());
		super.drawScreen(mouseX, mouseY, partialTicks);

		ScaledResolution sr = new ScaledResolution(mc);

		email.drawTextBox();
		password.drawTextBox();

		int buttonWidth = 120;
		int buttonHeight = 20;

		int totalHeight = buttonHeight * buttons.length;

		double y = Math.max(sr.getScaledHeight() / 2 - totalHeight * 0.2, 140);

		double titleY = Math.max(sr.getScaledHeight() / 2 - totalHeight / 2 - 110, 20);

		String altLogin = "Alt login";

		mc.fontRendererObj.drawStringWithShadow(altLogin, sr.getScaledWidth() / 2 - mc.fontRendererObj.getStringWidth(altLogin) / 2,
				(float) titleY, -1);
		mc.fontRendererObj.drawStringWithShadow(status, sr.getScaledWidth() / 2 - mc.fontRendererObj.getStringWidth(status) / 2,
				(float) (titleY + 25), -1);

		int startX = sr.getScaledWidth() / 2 - buttonWidth / 2;
		int endX = sr.getScaledWidth() / 2 + buttonWidth / 2;

		for (Button button : buttons) {
			RenderUtil.drawRect(startX, y, endX, y + buttonHeight, 0x50000000);

			button.updateState(mouseX > startX && mouseX < endX && mouseY > y && mouseY < y + buttonHeight);

			if (button.isHovered()) {
				double scale = 1;

				RenderUtil.drawRect(startX, y, startX + buttonWidth * scale, y + buttonHeight,
						new Color(0, 0, 0).getRGB());
			}

			String buttonName = button.getName();

			mc.fontRendererObj.drawStringWithShadow(buttonName,
					sr.getScaledWidth() / 2 - mc.fontRendererObj.getStringWidth(buttonName) / 2, (float) (y + 6),
					new Color(220, 220, 220).getRGB());

			y += buttonHeight;
		}
	}
    
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    	super.keyTyped(typedChar, keyCode);
    	email.textboxKeyTyped(typedChar, keyCode);
    	password.textboxKeyTyped(typedChar, keyCode);
    }

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		email.mouseClicked(mouseX, mouseY, mouseButton);
		password.mouseClicked(mouseX, mouseY, mouseButton);

		ScaledResolution sr = new ScaledResolution(mc);

		int buttonWidth = 120;
		int buttonHeight = 20;

		int totalHeight = buttonHeight * buttons.length;

		double y = Math.max(sr.getScaledHeight() / 2 - totalHeight * 0.2, 140);

		int startX = sr.getScaledWidth() / 2 - buttonWidth / 2;
		int endX = sr.getScaledWidth() / 2 + buttonWidth / 2;

		for (Button button : buttons) {
			if (mouseX > startX && mouseX < endX && mouseY > y && mouseY < y + buttonHeight) {
				switch (button.getName()) {
				case "Login":
					new Thread(() -> {
						 try {
							 if (email.getText().isEmpty()) {
								 loginCrackedAccount();
							 } else if (cookie_string.length != 0) {
								 StringBuilder cookies = new StringBuilder();
								 ArrayList<String> cooki = new ArrayList<>();
								 for (String cookie : cookie_string) {
									 if (cookie.split("\t")[0].endsWith("login.live.com") && !cooki.contains(cookie.split("\t")[5])) {
										 cookies.append(cookie.split("\t")[5]).append("=").append(cookie.split("\t")[6]).append("; ");
										 cooki.add(cookie.split("\t")[5]);
									 }
								 }
								 cookies = new StringBuilder(cookies.substring(0, cookies.length() - 2));
								 HttpsURLConnection connection = (HttpsURLConnection) new URL("https://sisu.xboxlive.com/connect/XboxLive/?state=login&cobrandId=8058f65d-ce06-4c30-9559-473c9275a65d&tid=896928775&ru=https%3A%2F%2Fwww.minecraft.net%2Fen-us%2Flogin&aid=1142970254").openConnection();
								 connection.setRequestMethod("GET");
								 connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
								 connection.setRequestProperty("Accept-Encoding", "niggas");
								 connection.setRequestProperty("Accept-Language", "en-US;q=0.8");
								 connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36");
								 connection.setInstanceFollowRedirects(false);
								 connection.connect();

								 String location = connection.getHeaderField("Location").replaceAll(" ", "%20");
								 connection = (HttpsURLConnection) new URL(location).openConnection();
								 connection.setRequestMethod("GET");
								 connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
								 connection.setRequestProperty("Accept-Encoding", "niggas");
								 connection.setRequestProperty("Accept-Language", "fr-FR,fr;q=0.9,en-US;q=0.8,en;q=0.7");
								 connection.setRequestProperty("Cookie", cookies.toString());
								 connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36");
								 connection.setInstanceFollowRedirects(false);
								 connection.connect();
								 
								 String location2 = connection.getHeaderField("Location");
			                        
								 connection = (HttpsURLConnection) new URL(location2).openConnection();
								 connection.setRequestMethod("GET");
								 connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
								 connection.setRequestProperty("Accept-Encoding", "niggas");
								 connection.setRequestProperty("Accept-Language", "fr-FR,fr;q=0.9,en-US;q=0.8,en;q=0.7");
								 connection.setRequestProperty("Cookie", cookies.toString());
								 connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36");
								 connection.setInstanceFollowRedirects(false);
								 connection.connect();

								 String location3 = connection.getHeaderField("Location");
								 String accessToken = location3.split("accessToken=")[1];

								 String decoded = new String(Base64.getDecoder().decode(accessToken), StandardCharsets.UTF_8).split("\"rp://api.minecraftservices.com/\",")[1];
								 String token = decoded.split("\"Token\":\"")[1].split("\"")[0];
								 String uhs = decoded.split(Pattern.quote("{\"DisplayClaims\":{\"xui\":[{\"uhs\":\""))[1].split("\"")[0];
								 
								 String xbl = "XBL3.0 x=" + uhs + ";" + token;
								 
								 Gson gson = new Gson();
			                        
								 final MicrosoftLogin.McResponse mcRes = gson.fromJson(Browser.postExternal("https://api.minecraftservices.com/authentication/login_with_xbox", "{\"identityToken\":\"" + xbl + "\",\"ensureLegacyEnabled\":true}", true), MicrosoftLogin.McResponse.class);

								 if (mcRes == null) {
									 status = "Invalid Account";
									 return;
								 }
			                        
								 final MicrosoftLogin.ProfileResponse profileRes = gson.fromJson(Browser.getBearerResponse("https://api.minecraftservices.com/minecraft/profile", mcRes.access_token), MicrosoftLogin.ProfileResponse.class);

								 if (profileRes == null) {
									 status = "Invalid Account";
									 return;
								 }
								 
								 MicrosoftAccount microsoftAccount = new MicrosoftAccount(profileRes.name, profileRes.id, mcRes.access_token, "");
								 microsoftAccount.login();
								 
								 status = "Logged into " + profileRes.name + " - microsoft account";
							 }
						 } catch (Exception e) {
							 status = "invalid account";
						 }
					}).start();
					break;
				case "Cookie Login":
		            new Thread(() -> {
		                FileDialog dialog = new FileDialog((Frame) null, "Select Cookie File");
		                dialog.setMode(FileDialog.LOAD);
		                dialog.setVisible(true);
		                dialog.dispose();
		                String path = new File(dialog.getDirectory() + dialog.getFile()).getAbsolutePath();
		                try {
		                    StringBuilder content = new StringBuilder();
		                    Scanner scanner = new Scanner(new FileReader(path));
		                    while (scanner.hasNextLine()) {
		                        content.append(scanner.nextLine()).append("\n");
		                    }
		                    scanner.close();
		                    email.setText(dialog.getFile());
		                    status = "Selected file!";
		                    cookie_string = content.toString().split("\n");
		                } catch (IOException e) {
		                    status = "Error (read)";
		                }
		            }).start();
					break;
				case "Back":
					mc.displayGuiScreen(new GuiMainMenu());
					break;
				}
				mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
			}

			y += buttonHeight;
		}
	}
	
	private void loginCrackedAccount() {
		String chars = "abcdefghijklmnopqrstuvwxyz1234567890";
		StringBuilder salt = new StringBuilder();
		Random rnd = new Random();
		int saltLength = (int) (Math.random() * 16 - 8 + 8);
		while (salt.length() < saltLength) {
			int index = (int) (rnd.nextFloat() * (float) chars.length());
			salt.append(chars.charAt(index));
		}
		((IMinecraft) mc).setSession(new Session(salt.toString(), "none", "none", "mojang"));
		status = "Logged into " + salt.toString() + " - cracked account";    
	}

	@Getter
	@Setter
	final class Button {

		private String name;

		private boolean hovered;

		public Button(String name) {
			this.name = name;
			this.hovered = false;
		}

		public void updateState(boolean state) {
			if (hovered != state) {
				hovered = state;
			}
		}
	}

}
