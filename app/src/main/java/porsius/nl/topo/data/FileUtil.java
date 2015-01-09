package porsius.nl.topo.data;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by linda on 30/10/14.
 */
public class FileUtil {

    public static final String STORAGE_DIR = "Topo";
    public static final String FILE_PREFIX = "nl.porsius.topo";

    public static String readFile(String path)
    {
        StringBuilder text = new StringBuilder();
        String s = null;
        try {
            File sdcard = Environment.getExternalStorageDirectory();
            File file = new File(sdcard, path);

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);

            }
            s = text.toString();

        }
        catch (IOException e) {
            e.printStackTrace();


        }
        return s;
    }

    public static void write (String filename,String input, boolean append) throws IOException{
        try {
            String dirs = Environment.getExternalStorageDirectory().toString() + "/" + STORAGE_DIR;

            new File(dirs).mkdirs();

            File file = new File(dirs, filename);

            FileWriter writer = new FileWriter(file, append);
            writer.write(input);
            writer.close();

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }
    }

    public static void delete (String filename)
    {
        String dirs = Environment.getExternalStorageDirectory().toString() + "/" + STORAGE_DIR;

        File file = new File(dirs,filename);
        file.delete();
    }
    public static String[] getFiles() {
        String[] files = new String[]{};
        int count = 0;
        File sdCardRoot = Environment.getExternalStorageDirectory();
        //File yourDir = new File(sdCardRoot, "Documents");
        for (File f : sdCardRoot.listFiles()) {
            if (f.isFile()) {

                files[count]= f.getName();

                count++;
            }

        }

        return files;
    }


    public static List<String> getList(File parentDir, String pathToParentDir) {

        ArrayList<String> inFiles = new ArrayList<String>();
        String[] fileNames = parentDir.list();

        for (String fileName : fileNames) {
            if(fileName.equalsIgnoreCase(STORAGE_DIR))
                continue;
            else if (fileName.toLowerCase().contains(FILE_PREFIX)) {
                inFiles.add(pathToParentDir + fileName);
            } else {
                File file = new File(parentDir.getPath() + "/" + fileName);
                if (file.isDirectory()) {
                    inFiles.addAll(getList(file, pathToParentDir + fileName + "/"));
                    //getList(file, pathToParentDir + fileName + "/");
                }
            }
        }

        return inFiles;
    }
    public static String readTextFromResource(Context context, int resourceID)
    	{
        InputStream raw = context.getResources().openRawResource(resourceID);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            int i;
            try
           {
                   i = raw.read();
                   while (i != -1)
                        {
                            stream.write(i);
                           i = raw.read();
                       }
                   raw.close();
            	    }
            catch (IOException e)
           {
                   e.printStackTrace();
               }
          return stream.toString();
        }

}

