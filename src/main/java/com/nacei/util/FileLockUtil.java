package com.nacei.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class FileLockUtil {

    private String fileName = "fileLock.lock";
    private String currentDirectory = System.getProperty("user.dir");
    private String fileSeparate = System.getProperty("file.separator");
    private File f = null;
    private RandomAccessFile raf = null;
    private FileChannel fc = null;
    private FileLock fl = null;
    private FileWriter fr = null;


    public boolean getFileLock() throws IOException {
        boolean rtn = true;
        try {
            if (!currentDirectory.endsWith(fileSeparate)) {
                currentDirectory = currentDirectory + fileSeparate;
            }
            System.out.println(currentDirectory);
            f = new File(currentDirectory + fileName);
            raf = new RandomAccessFile(f, "rw");
            fr = new FileWriter(f);
            fr.write("singleton");
            fr.close();
            fr = null;
            fc = raf.getChannel();
            fl = fc.tryLock();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        }
        return rtn;

    }

    public void releaseFileLock()  {
        try {
            if (fr != null)
                fr.close();
            if (fl != null)
                fl.release();
            if (fc != null)
                fc.close();
            if (raf != null)
                raf.close();
            if (f != null)
                f.delete();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}