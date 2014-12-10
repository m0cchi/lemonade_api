package example;

import protocol.Api;
import protocol.Config;

public class postMessage {

    public static void main(String[] args) {
        String token = "YOUR_TOKEN";
        Config config = new Config(token);
        Api api = new Api(config);
        api.postMessage(null, "message...", "LemonadeBot", null, null);
    }

}
