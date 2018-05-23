package messages;

import utils.Constants;

import java.util.HashMap;

public class MessageBuilder {

    private static HashMap<String, Class<?>> messageHashMap = Constants.messageHashMap;

    public static Message build(String[] args) {
        try {
            return (Message) messageHashMap.get(args[0]).getDeclaredConstructor(String[].class).newInstance((Object) args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
