package cc.unknown.ui.alt;

import java.awt.Color;
import java.io.IOException;
import java.util.Random;

import cc.unknown.mixin.interfaces.IMinecraft;
import cc.unknown.utils.client.RenderUtil;
import cc.unknown.utils.font.FontUtil;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import fr.litarvan.openauth.microsoft.model.response.MinecraftProfile;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;

public class AltLoginScreen extends GuiScreen {

    private GuiTextField email;
    private GuiTextField password;
    private final Button[] buttons = {new Button("Login"),  new Button("Random"), new Button("Back")};
    private String status;
    
    @Override
    public void initGui() {
        ScaledResolution sr = new ScaledResolution(mc);

        int buttonHeight = 20;

        int totalHeight = buttonHeight * buttons.length;

        int y = Math.max(sr.getScaledHeight() / 2 - totalHeight / 2 - 50, 75);

        email = new GuiTextField(0, mc.fontRendererObj, sr.getScaledWidth() / 2 - 80, y, 160, 20);
        password = new GuiTextField(1, mc.fontRendererObj, sr.getScaledWidth() / 2 - 80, y + 30, 160, 20);

        for(Button button : buttons) {
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

        FontUtil.two.drawStringWithShadow(altLogin, sr.getScaledWidth() / 2 - FontUtil.two.getStringWidth(altLogin) / 2, (float) titleY, -1);
        FontUtil.two.drawStringWithShadow(status, sr.getScaledWidth() / 2 - FontUtil.two.getStringWidth(status) / 2, (float) (titleY + 25), -1);

        int startX = sr.getScaledWidth() / 2 - buttonWidth / 2;
        int endX = sr.getScaledWidth() / 2 + buttonWidth / 2;

        for(Button button : buttons) {
            RenderUtil.drawRect(startX, y, endX, y + buttonHeight, 0x50000000);

            button.updateState(mouseX > startX && mouseX < endX && mouseY > y && mouseY < y + buttonHeight);

            if(button.isHovered()) {
                double scale = 1;

                RenderUtil.drawRect(startX, y, startX + buttonWidth * scale, y + buttonHeight, new Color(0, 0, 0).getRGB());
            }

            String buttonName = button.getName();

            FontUtil.two.drawStringWithShadow(buttonName, sr.getScaledWidth() / 2 - FontUtil.two.getStringWidth(buttonName) / 2, (float) (y + 6), new Color(220, 220, 220).getRGB());

            y += buttonHeight;
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        try {
            super.keyTyped(typedChar, keyCode);
        } catch (IOException e) {
            e.printStackTrace();
        }

        email.textboxKeyTyped(typedChar, keyCode);
        password.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        try {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        } catch (IOException e) {
            e.printStackTrace();
        }

        email.mouseClicked(mouseX, mouseY, mouseButton);
        password.mouseClicked(mouseX, mouseY, mouseButton);

        ScaledResolution sr = new ScaledResolution(mc);

        int buttonWidth = 120;
        int buttonHeight = 20;

        int totalHeight = buttonHeight * buttons.length;

        double y = Math.max(sr.getScaledHeight() / 2 - totalHeight * 0.2, 140);

        int startX = sr.getScaledWidth() / 2 - buttonWidth / 2;
        int endX = sr.getScaledWidth() / 2 + buttonWidth / 2;

        for(Button button : buttons) {
            if(mouseX > startX && mouseX < endX && mouseY > y && mouseY < y + buttonHeight) {
                switch (button.getName()) {
                    case "Login":
                        new Thread(() -> {
                            if(password.getText().isEmpty()) {
                            	((IMinecraft)mc).setSession(new Session(email.getText(), "none", "none", "mojang"));
                                status = "Logged into " + email.getText() + " - cracked account";
                            } else {
                                status = "Logging in...";

                                MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
                                MicrosoftAuthResult result = null;
                                
                        		try {
                        			result = authenticator.loginWithCredentials(email.getText(), password.getText());
                        			MinecraftProfile profile = result.getProfile();
                        			((IMinecraft)mc).setSession(new Session(profile.getName(), profile.getId(), result.getAccessToken(), "microsoft"));
                        			status = "Logged into " + mc.getSession().getUsername();
                        		} catch (MicrosoftAuthenticationException e) {
                                    e.printStackTrace();
                                    status = EnumChatFormatting.RED + "Login failed !";
                        		}
                            }
                        }).start();
                        break;
                    case "Random":
               			String chars = "abcdefghijklmnopqrstuvwxyz1234567890";
            			StringBuilder salt = new StringBuilder();
            			Random rnd = new Random();
            			int saltLength = getRandomInRange(8, 16);
        	    		while (salt.length() < saltLength) {
        	    			int index = (int)(rnd.nextFloat() * (float)chars.length());
            				salt.append(chars.charAt(index));
            			}
                    	((IMinecraft)mc).setSession(new Session(salt.toString(), "none", "none", "mojang"));
                    	status = "Logged into " + salt.toString() + " - cracked account";
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
    private int getRandomInRange(int min, int max) {
        return (int)(Math.random() * (double)(max - min) + (double)min);
    }


}
