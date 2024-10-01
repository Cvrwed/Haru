package cc.unknown.ui.auth;

import java.util.function.Consumer;

import cc.unknown.ui.auth.MicrosoftLogin.LoginData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MicrosoftAccount extends Account {
    private String refreshToken;

    public MicrosoftAccount(String name, String uuid, String accessToken, String refreshToken) {
        super(AccountType.MICROSOFT, name, uuid, accessToken);
        this.refreshToken = refreshToken;
    }

    public static MicrosoftAccount create() {
        MicrosoftAccount account = new MicrosoftAccount("", "", "", "");
        Consumer<String> consumer = (String refreshToken) -> {
            account.setRefreshToken(refreshToken);
            account.login();
        };

        MicrosoftLogin.getRefreshToken(consumer);
        return account;
    }

    @Override
    public boolean login() {
        if (refreshToken.isEmpty()) return super.login();

        MicrosoftLogin.LoginData loginData = MicrosoftLogin.login(refreshToken);
        if (!loginData.isGood()) {
            return false;
        }

        this.setName(loginData.username);
        this.setUuid(loginData.uuid);
        this.setAccessToken(loginData.mcToken);
        this.setRefreshToken(loginData.newRefreshToken);
        return super.login();
    }

    @Override
    public boolean isValid() {
        return super.isValid() && !refreshToken.isEmpty();
    }
}
