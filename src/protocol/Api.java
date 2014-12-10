package protocol;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import json.Json;

public class Api {
    private Config config;

    @SuppressWarnings("unused")
    private Api() {
    }

    public Api(Config config) {
        this.config = config;

        @SuppressWarnings("rawtypes")
        Map json = Json.toJson(test());
        if (json != null && Boolean.parseBoolean((String) json.get("ok"))) {
            json = Json.toJson(auth());
            this.config.setTeamId((String) json.get("team_id"));
            this.config.setTeamName((String) json.get("team"));
            this.config.setUserId((String) json.get("user_id"));
            this.config.setUserName((String) json.get("user"));
            this.config.setTeamUrl((String) json.get("url"));
        }
    }

    public String auth() {
        String[] query = { "token=" + config.getToken() };
        return httpGet(Config.SlackURL.get("auth"), query);
    }

    public String test() {
        String[] query = { "token=" + config.getToken() };
        return httpGet(Config.SlackURL.get("test"), query);
    }

    public String getChannelsList() {
        String[] query = { "token=" + config.getToken() };
        return httpGet(Config.SlackURL.get("channels.list"), query);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public String getGereralId() {
        String id = "";
        String string = getChannelsList();
        Map map = Json.toJson(string);
        Map<String, Object> channels = (Map) map.get("channels");
        for (String e : channels.keySet()) {
            Map<String, Object> ch = (Map<String, Object>) channels.get(e);
            if (ch.get("name").equals("general")) {
                id = (String) ch.get("id");
                break;
            }
        }
        return id;
    }

    public String postMessage(String channelId, String text, String username,
            String parse, String iconEmoji) {
        String[] query = {
                "token=" + config.getToken(),
                "channel=" + (channelId != null ? channelId : getGereralId()),
                "text=" + (text != null ? text : "------------------------"),
                "username=" + (username != null ? username : "Tuna"),
                "parse=" + (parse != null ? parse : "none"),
                "icon_emoji="
                        + (iconEmoji != null ? iconEmoji : "%3Ahurtrealbad%3A") };
        return httpGet(Config.SlackURL.get("chat.postMessage"), query);
    }

    public static String httpGet(String api, String[] query) {
        HttpsURLConnection connection = null;
        String res = null;
        try {
            URL url = new URL(api
                    + (query != null ? "?" + String.join("&", query) : ""));
            StringBuilder sb = new StringBuilder();
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader isr = new InputStreamReader(
                        connection.getInputStream(), StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(isr);
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                res = sb.toString();
            } else {
                res = "{\"ok\":false}";
            }

        } catch (Exception e) {
            res = "{\"ok\":false}";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return res;
    }
}
