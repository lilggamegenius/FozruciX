import com.LilG.Com.DataClasses.SaveDataStore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.xstream.XStream;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;

/**
 * Created by ggonz on 8/13/2016.
 */
public class jsonToXMLConverter {

    public static void main(String[] args) throws Exception {
        FileReader saveFile = new FileReader(new File(JOptionPane.showInputDialog("Enter file path")));
        OutputStream outputFile = new FileOutputStream(new File(JOptionPane.showInputDialog("Enter output file path")));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        SaveDataStore save = gson.fromJson(saveFile, SaveDataStore.class);
        XStream xstream = new XStream();
        xstream.toXML(save, outputFile);
    }
}
