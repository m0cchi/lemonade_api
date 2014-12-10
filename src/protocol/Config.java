package protocol;

import java.util.HashMap;

public class Config {
    private String token;
    private String userId;
    private String userName;
    private String teamId;
    private String teamName;
    private String teamUrl;
    @SuppressWarnings("serial")
    protected static HashMap<String, String> SlackURL = new HashMap<String, String>() {

        {
            put("test", "api.test");
            put("auth", "auth.test");
            put("channels.list", "channels.list");
            put("chat.postMessage", "chat.postMessage");
        }

        public String put(String key, String value) {
            String base = "https://slack.com/api/";
            super.put(key, base + value);
            return key;
        }
    };

    public Config(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public String getUserId() {
        return this.userId;
    }

    protected void setUserId(String userId) {
        this.userId = userId;
    }

    protected void setToken(String token) {
        this.token = token;
    }

    protected void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    protected void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    protected void setTeamUrl(String teamUrl) {
        this.teamUrl = teamUrl;
    }

    protected void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getTeamUrl() {
        return teamUrl;
    }

    @Override
    public String toString() {
        return "Config [token=" + token + ", userId=" + userId + ", userName="
                + userName + ", teamId=" + teamId + ", teamName=" + teamName
                + ", teamUrl=" + teamUrl + "]";
    }

}
