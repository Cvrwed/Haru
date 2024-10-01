package cc.unknown.ui.auth;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class MicrosoftLogin {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static class LoginData {
        public String mcToken;
        public String newRefreshToken;
        public String uuid, username;

        public LoginData() {
        }

        public LoginData(final String mcToken, final String newRefreshToken, final String uuid, final String username) {
            this.mcToken = mcToken;
            this.newRefreshToken = newRefreshToken;
            this.uuid = uuid;
            this.username = username;
        }

        public boolean isGood() {
            return mcToken != null;
        }
    }

    private static final String CLIENT_ID = "ba89e6e0-8490-4a26-8746-f389a0d3ccc7", CLIENT_SECRET = "hlQ8Q~33jTRilP4yE-UtuOt9wG.ZFLqq6pErIa2B";
    private static final int PORT = 8247;

    private static HttpServer server;
    private static Consumer<String> callback;

    private static void browse(final String url) {
        String[] browsers = {
                "C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe " + url + " -incognito",
                "C:\\Program Files\\BraveSoftware\\Brave-Browser\\Application\\brave.exe " + url + " -incognito",
                "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe " + url + " -incognito",
                "open -na \"Google Chrome\" --args --incognito \"" + url + "\""
        };

        for (String browser : browsers) {
            try {
                Runtime.getRuntime().exec(browser);
                return;
            } catch (Exception ignored) {
            }
        }
    }

    private static void copy(final String url) {
        // set clipboard to url
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(url), null);
    }

    public static void getRefreshToken(final Consumer<String> callback) {
        MicrosoftLogin.callback = callback;

        startServer();
        copy("https://login.live.com/oauth20_authorize.srf?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&response_type=code&redirect_uri=http://localhost:" + PORT + "&scope=XboxLive.signin%20offline_access&prompt=select_account");
    }

    public static void getRefreshTokenAndOpenBrowser(final Consumer<String> callback) {
        MicrosoftLogin.callback = callback;

        startServer();
        browse("https://login.live.com/oauth20_authorize.srf?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&response_type=code&redirect_uri=http://localhost:" + PORT + "&scope=XboxLive.signin%20offline_access&prompt=select_account");
    }

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

        // Check game ownership
//        final GameOwnershipResponse gameOwnershipRes = gson.fromJson(
//                Browser.getBearerResponse("https://api.minecraftservices.com/entitlements/mcstore", mcRes.access_token),
//                GameOwnershipResponse.class);
//
//        if (gameOwnershipRes == null || !gameOwnershipRes.hasGameOwnership()) return new LoginData();

        // Profile
        final ProfileResponse profileRes = gson.fromJson(
                Browser.getBearerResponse("https://api.minecraftservices.com/minecraft/profile", mcRes.access_token),
                ProfileResponse.class);

        if (profileRes == null) return new LoginData();

        return new LoginData(mcRes.access_token, refreshToken, profileRes.id, profileRes.name);
    }

    private static void startServer() {
        if (server != null) return;

        try {
            server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);

            server.createContext("/", new Handler());
            server.setExecutor(executor);
            server.start();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private static void stopServer() {
        if (server == null) return;

        server.stop(0);
        server = null;

        callback = null;
    }

    private static class Handler implements HttpHandler {
        @Override
        public void handle(final HttpExchange req) throws IOException {
            if (req.getRequestMethod().equals("GET")) {
                // Login
                final List<NameValuePair> query = URLEncodedUtils.parse(req.getRequestURI(), StandardCharsets.UTF_8.name());

                boolean ok = false;

                for (final NameValuePair pair : query) {
                    if (pair.getName().equals("code")) {
                        handleCode(pair.getValue());

                        ok = true;
                        break;
                    }
                }

                if (!ok) writeText(req, "Cannot authenticate.");
                else writeText(req, "<html>You may now close this page, if you didn't specify an account then clear your cookies to select an individual one.</html>");
            }

            stopServer();
        }

        private void handleCode(final String code) {
            //System.out.println(code);
            final String response = Browser.postExternal("https://login.live.com/oauth20_token.srf",
                    "client_id=" + CLIENT_ID + "&code=" + code + "&client_secret=" + CLIENT_SECRET + "&grant_type=authorization_code&redirect_uri=http://localhost:" + PORT, false);
            final AuthTokenResponse res = gson.fromJson(
                    response,
                    AuthTokenResponse.class);

            if (res == null) callback.accept(null);
            else callback.accept(res.refresh_token);
        }

        private void writeText(final HttpExchange req, final String text) throws IOException {
            final OutputStream out = req.getResponseBody();

            req.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
            req.sendResponseHeaders(200, text.length());

            out.write(text.getBytes(StandardCharsets.UTF_8));
            out.flush();
            out.close();
        }
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

    private static class GameOwnershipResponse {
        @Expose
        @SerializedName("items")
        private Item[] items;

        private static class Item {
            @Expose
            @SerializedName("name")
            private String name;
        }

        private boolean hasGameOwnership() {
            boolean hasProduct = false;
            boolean hasGame = false;

            for (final Item item : items) {
                if (item.name.equals("product_minecraft")) hasProduct = true;
                else if (item.name.equals("game_minecraft")) hasGame = true;
            }

            return hasProduct && hasGame;
        }
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