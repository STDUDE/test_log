/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package by.logscanner;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import jdk.nashorn.internal.runtime.options.Options;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Klimovich_AA
 */

public class LogScanner {
    
    private final Date date;
    private final String startupId;
    private final String requestNo;
    private final String logType;
    
    String resPath;
    String destPath;
    
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
    String logData;
    
    private final List<FileData> fileData;
    
    
    class FileData {
        public String fileName;
        public byte[] content;
        
        public FileData(String fileName, byte[] content){
            this.fileName = fileName;
            this.content = content;
        }
    }
    
    public LogScanner(String logType, String date, String startupId, String requestNo) throws ParseException {
        this.logType = logType;
        this.date = sdf.parse(date);
        this.startupId = startupId;
        this.requestNo = requestNo;
        this.logData = "";
        this.fileData = new ArrayList<>();
    }
    
    public void init() throws IOException {
        
        ResourceBundle resBundle =  ResourceBundle.getBundle("config");
        resPath = resBundle.getString("resPath"); 
        destPath = resBundle.getString("destPath");
        
        System.out.println("logType: " + logType + ", date: " + sdf.format(date) + ", startupId: " + startupId + ", requestNo: " + requestNo + ", path: " + resPath);
        
        FileUtils.cleanDirectory(new File(destPath));
        
        long startTime = System.nanoTime();
        if (!isGateway()) {
            walk(resPath);
            fileData.add(new FileData(getDestFileName(), logData.getBytes(StandardCharsets.UTF_8)));
        }
        else {
            walkGateway(resPath);
        }
        
        write();
        
        long endTime = System.nanoTime();
        long elapsedTimeInMillis = TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS);
        System.out.println("Total elapsed time: " + elapsedTimeInMillis + " ms");
    }
    
    private void walk(String resPath) throws IOException {
        File root = new File(resPath);
        File[] list = root.listFiles();
        
        if (list == null) return;

        for (File f : list) {
            if (f.isDirectory()) {
                walk(f.getAbsolutePath());
            }
            else if (f.getName().equals(logType+".log")) {  
                logData += read(f);
            }
        }
    }
    
    private void walkGateway(String resPath) throws IOException {
        File root = new File(resPath);
        File[] list = root.listFiles();
        
        if (list == null) return;

        for (File f : list) {
            if (f.isDirectory()) {
                walkGateway(f.getAbsolutePath());
            }
            else if (f.getName().contains(sdf.format(date)) 
                    && f.getName().contains("[" + startupId + "." + requestNo + "]") 
                    && f.getAbsolutePath().contains(logType)) {  
                fileData.add(new FileData(f.getName(), readGateway(f)));
            }
        }
    }
    
    private byte[] readGateway(File file) throws FileNotFoundException, IOException {
        return FileUtils.readFileToByteArray(file);
    }
    
    private String read(File file) throws FileNotFoundException, IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        
        Stream<String> lines = Files.lines(file.toPath(), StandardCharsets.UTF_8 );
        String line = "";
        for ( String temp : (Iterable<String>) lines::iterator )
        {
            line  += (temp.contains(sdf.format(date)) && temp.contains("[" + startupId + "." + requestNo + "]")) ? temp + "\n" : "" ;
        }
        return line;
    }
    
    private String getDestFileName(){
        return logType + ".log";
    }
    
    public List<FileData> getFileData() {
        return fileData;
    }
    
    public void write() throws IOException {
        for (FileData item : fileData) {
            FileUtils.writeByteArrayToFile(new File(destPath + item.fileName), item.content);
        }
    }
    
    private boolean isGateway() {
        return !("apps".equals(logType) || "sql".equals(logType) || "appsSheduler".equals(logType) || "sqlSheduler".equals(logType));
    }
}
