package utils;

import p.Address;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;

public class Utils {

    public static byte[] concatenateByteArrays(final byte[] array1, byte[] array2) {
        byte[] joinedArray = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    public static String getDirSeparator() {
        if(System.getProperty("os.name").split(" ")[0].equals("Windows")) {
            return "\\";
        }

        return "/";
    }

    public static HashMap<String, Address> createContacts(String contacts){
        String[] contactsArray = contacts.split("-");
        HashMap<String, Address> table = new HashMap<>();
        String currentId = "";
        for(int i = 0; i < contactsArray.length; i++){
            if(i % 2 == 0){
                currentId = contactsArray[i];
            } else {
                try {
                    table.put(currentId, new Address(contactsArray[i]));
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }

        return table;
    }

}
