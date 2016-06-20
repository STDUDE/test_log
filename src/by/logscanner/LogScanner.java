/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package by.logscanner;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Klimovich_AA
 */

public class LogScanner {
    private final Date date;
    private final String startupId;
    private final String requestNo;
    private final String logType;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
    private List<File> fileList;
    
    
    public LogScanner(String logType, String date, String startupId, String requestNo) throws ParseException {
        this.logType = logType;
        this.date = sdf.parse(date);
        this.startupId = startupId;
        this.requestNo = requestNo;
        this.fileList = new ArrayList<>();
    }
    
    public void init() throws IOException {
        ResourceBundle resBundle =  ResourceBundle.getBundle("config");
        String resPath = resBundle.getString("resPath"); 
        String destPath = resBundle.getString("destPath");
        
        System.out.println("logType: " + logType + ", date: " + sdf.format(date) + ", startupId: " + startupId + ", requestNo: " + requestNo + ", path: " + resPath);
        
        switch (logType) {
            case "apps":
            case "appsSheduler":
            case "sql":
            case "sqlSheduler":
                walk(resPath, destPath);
//                read();
//                write(destPath);
                break;
            default:
                walkGateway(resPath, destPath);
//                readGateway();
//                writeGateway(destPath);
                break;
        }
    }
    
    private void walk(String resPath, String destPath) throws IOException {
        File root = new File(resPath);
        File[] list = root.listFiles();
        
        if (list == null) return;

        for (File f : list) {
            if (f.isDirectory()) {
                walk(f.getAbsolutePath(), destPath);
            }
            else if(f.getName().equals(logType+".log")) {  
                write(destPath, read(f));
//                fileList.add(f);
            }
        }
    }
    
    private void walkGateway(String resPath, String destPath) throws IOException {
        File root = new File(resPath);
        File[] list = root.listFiles();

        if (list == null) return;

        for (File f : list) {
            if (f.isDirectory()) {
                walk(f.getAbsolutePath(), destPath);
            }
            else if(f.getName().contains(sdf.format(date)) && f.getName().contains("[" + startupId + "." + requestNo + "]")) {  
                
//                fileList.add(f);
            }
        }
    }
    
    private void readGateway() throws FileNotFoundException {
        for(File file : fileList){
            System.out.println();

            System.out.println("File:" + file.getAbsoluteFile() + " size: " + file.length());
            Scanner sc = new Scanner(new BufferedReader(new FileReader(file))); 
            String line = "";
            while (sc.hasNext()) {
                line += sc.nextLine();
            }
            System.out.println(line);
            sc.close();
        }
    }
    
    private String read(File file) throws FileNotFoundException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String line = "";
        
        Scanner sc = new Scanner(new BufferedReader(new FileReader(file))); 
        while (sc.hasNext()) {
            String temp = sc.nextLine();
            line += (temp.contains(sdf.format(date)) && temp.contains("[" + startupId + "." + requestNo + "]")) ? temp + "\n" : "" ;
        }           
        sc.close();
        
        System.out.println(line);
        return line;
    }

    private void writeGateway(String destPath, String data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void write(String destPath, String data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
