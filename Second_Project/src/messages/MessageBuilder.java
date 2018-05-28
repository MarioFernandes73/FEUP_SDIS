package messages;

import filesmanager.FilesManager;
import utils.Constants;

import java.util.HashMap;

public class MessageBuilder {

    private static HashMap<String, Class<?>> messageHashMap = Constants.messageHashMap;

    public static Message build(String[] args) {
        try {
            Message msg = (Message) messageHashMap.get(args[0]).getDeclaredConstructor(String[].class).newInstance((Object) args);
            FilesManager.addLog("New Message created!");
            FilesManager.addLog(msg.getHeader());
            return msg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
