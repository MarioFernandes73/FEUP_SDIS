package messages;

import utils.Constants;

import java.util.HashMap;

public class MessageBuilder {

    private static HashMap<String, Class<?>> messageHashMap = Constants.messageHashMap;

    public static Message build(String[] args) {
        try {
            System.out.println("MESSAGE CREATED!!!");
            Message msg = (Message) messageHashMap.get(args[0]).getDeclaredConstructor(String[].class).newInstance((Object) args);
            System.out.println(msg.getHeader());
            return msg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
