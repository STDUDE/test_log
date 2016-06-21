/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package by.logscanner;

import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Klimovich_AA
 */
public class TestLog {
        /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            if(args.length != 0) {
                LogScanner lsc = new LogScanner(args[0], args[1], args[2], args[3]);
                System.out.println("logType: " + args[0] + ", date: " + args[1] + ", startupId: " + args[2] + ", requestNo: " + args[3]);
                long startTime = System.nanoTime();
                lsc.init();
                long endTime = System.nanoTime();
                long elapsedTimeInMillis = TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS);
                System.out.println("Total elapsed time: " + elapsedTimeInMillis + " ms");
            }
            else {
                System.err.println("No args");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ParseException ex) {
            System.err.println("Incorrect Date Format");
        }
        
    }
}
