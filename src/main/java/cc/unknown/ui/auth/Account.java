package cc.unknown.ui.auth;

import cc.unknown.mixin.interfaces.IMinecraft;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

@AllArgsConstructor
@Getter
@Setter
public class Account {
    private AccountType type;
    private String name;
    private String uuid;
    private String accessToken;
    
    public boolean login() {
        Minecraft mc = Minecraft.getMinecraft();
        ((IMinecraft)mc).setSession(new Session(name, uuid, accessToken, "mojang"));
        return true;
    }

    public boolean isValid() {
        return name != null && uuid != null && accessToken != null && !name.isEmpty() && !uuid.isEmpty() && !accessToken.isEmpty();
    }
}
