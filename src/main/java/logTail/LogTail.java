package logTail;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.*;
import java.util.*;
import java.util.regex.*;

public class LogTail {
    private String filename;
    private DateFormat dateFormat; // = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
    private String regex; // = "^(?:[^\\s]+\\s){3}\\[([^\\]]+)\\].*$";
    private String targetDate;

    private LogTail(String[] args) {
        filename = args[0];
        dateFormat = new SimpleDateFormat(args[1], Locale.ENGLISH);
        targetDate = args[3];
        regex = args[2];
    }

    private void run() throws Exception {
        File fileToRead = new File(filename);

        if (fileToRead.exists()) {
            fileToRead = fileToRead.getCanonicalFile();
        }

        else {
            System.out.println("Something is wrong with the file.");
            throw new IOException();
        }

        Date staticDate =  dateFormat.parse(targetDate);

        try(RandomAccessFile file = new RandomAccessFile(fileToRead, "r")) {
            long fileSize = file.length();
            long left = 0L;
            long right = file.length();
            while (left < right) {
                long middle = left + (right - left) / 2;
                long lineStart = getLineStart(file, middle, left);
                long lineEnd = getLineEnd(file, middle, right);
                file.seek(lineStart);
                byte[] buffer = new byte[(int) (lineEnd - lineStart)];
                file.read(buffer);
                String line = new String(buffer, StandardCharsets.US_ASCII);
                Date date = strDateParser(line);
                if (date.compareTo(staticDate) < 0) {
                    left = lineEnd + 1;
                } else {
                    right = lineStart - 1;
                }
            }
            if (left < fileSize) {
                byte[] buffer = new byte[4096];
                file.seek(left);
                for (int i = 0; i >= 0; i = file.read(buffer)) {
                    System.out.print(new String(buffer, 0, i, StandardCharsets.US_ASCII));
                }
            }
        }

    }

    private long getLineEnd(RandomAccessFile file, long middle, long right) throws IOException {
        long pos = middle;
        file.seek(pos);
        while (pos < right) {
            if (file.readByte() == '\n') {
                return pos;
            }
            pos++;
        }
        return right;
    }

    private long getLineStart(RandomAccessFile file, long middle, long left) throws IOException {
        long pos = middle;
        while (pos >= left) {
            file.seek(pos);
            if (file.readByte() == '\n') {
                return pos;
            }
            pos--;
        }
        return left;
    }

    private Date strDateParser(String str) throws Exception {
        String dateFromStr;
        Pattern pattern = Pattern.compile(regex);
        try {
            Matcher matcher = pattern.matcher(str);
            dateFromStr = matcher.group();
        }
        catch (IllegalStateException e) {
            System.out.println("String does not match pattern.");
            throw e;
        }

        return dateFormat.parse(dateFromStr);
    }

    public static void main(String[] args) throws Exception {
        LogTail assigment = new LogTail(args);
        assigment.run();
    }
}
