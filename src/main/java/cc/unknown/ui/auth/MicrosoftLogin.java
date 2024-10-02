package cc.unknown.ui.auth;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class MicrosoftLogin {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class LoginData {
        public String mcToken;
        public String newRefreshToken;
        public String uuid, username;

        public boolean isGood() {
            return mcToken != null;
        }
    }

    private static final String CLIENT_ID = "ba89e6e0-8490-4a26-8746-f389a0d3ccc7", CLIENT_SECRET = "hlQ8Q~33jTRilP4yE-UtuOt9wG.ZFLqq6pErIa2B";
    private static final int PORT = 8247;

    private static final Gson gson = new Gson();

    public static LoginData login(String refreshToken) {
        // Refresh access token
        final AuthTokenResponse res = gson.fromJson(
                Browser.postExternal("https://login.live.com/oauth20_token.srf", "client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&refresh_token=" + refreshToken + "&grant_type=refresh_token&redirect_uri=http://localhost:" + PORT + "&prompt=select_account", false),
                AuthTokenResponse.class
        );

        if (res == null) return new LoginData();

        final String accessToken = res.access_token;
        refreshToken = res.refresh_token;

        // XBL
        final XblXstsResponse xblRes = gson.fromJson(
                Browser.postExternal("https://user.auth.xboxlive.com/user/authenticate",
                        "{\"Properties\":{\"AuthMethod\":\"RPS\",\"SiteName\":\"user.auth.xboxlive.com\",\"RpsTicket\":\"d=" + accessToken + "\"},\"RelyingParty\":\"http://auth.xboxlive.com\",\"TokenType\":\"JWT\"}", true),
                XblXstsResponse.class);

        if (xblRes == null) return new LoginData();

        // XSTS
        final XblXstsResponse xstsRes = gson.fromJson(
                Browser.postExternal("https://xsts.auth.xboxlive.com/xsts/authorize",
                        "{\"Properties\":{\"SandboxId\":\"RETAIL\",\"UserTokens\":[\"" + xblRes.Token + "\"]},\"RelyingParty\":\"rp://api.minecraftservices.com/\",\"TokenType\":\"JWT\"}", true),
                XblXstsResponse.class);

        if (xstsRes == null) return new LoginData();

        // Minecraft
        final McResponse mcRes = gson.fromJson(
                Browser.postExternal("https://api.minecraftservices.com/authentication/login_with_xbox",
                        "{\"identityToken\":\"XBL3.0 x=" + xblRes.DisplayClaims.xui[0].uhs + ";" + xstsRes.Token + "\"}", true),
                McResponse.class);

        if (mcRes == null) return new LoginData();

        // Profile
        final ProfileResponse profileRes = gson.fromJson(
                Browser.getBearerResponse("https://api.minecraftservices.com/minecraft/profile", mcRes.access_token),
                ProfileResponse.class);

        if (profileRes == null) return new LoginData();

        return new LoginData(mcRes.access_token, refreshToken, profileRes.id, profileRes.name);
    }

    private static class AuthTokenResponse {
        @Expose
        @SerializedName("access_token")
        public String access_token;
        @Expose
        @SerializedName("refresh_token")
        public String refresh_token;
    }

    private static class XblXstsResponse {
        @Expose
        @SerializedName("Token")
        public String Token;
        @Expose
        @SerializedName("DisplayClaims")
        public DisplayClaims DisplayClaims;

        private static class DisplayClaims {
            @Expose
            @SerializedName("xui")
            private Claim[] xui;

            private static class Claim {
                @Expose
                @SerializedName("uhs")
                private String uhs;
            }
        }
    }

    public static class McResponse {
        @Expose
        @SerializedName("access_token")
        public String access_token;
    }


    public static class ProfileResponse {
        @Expose
        @SerializedName("id")
        public String id;
        @Expose
        @SerializedName("name")
        public String name;
    }
}