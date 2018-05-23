package messages;

import utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageBuilder {

    private static HashMap<String, Class<?>> messageHashMap = Constants.messageHashMap;

    public static Message build(ArrayList<String> args) {
        try {
            String[] constArgs = args.subList(1, args.size()).toArray(new String[args.size() - 1]);
            return (Message) messageHashMap.get(args.get(0)).getDeclaredConstructor(String[].class).newInstance((Object) constArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
