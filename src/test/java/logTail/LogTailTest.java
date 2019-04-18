package logTail;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class LogTailTest {

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

    @Test
    void testEmptyLog() throws Exception {
        File tempFile = File.createTempFile("log-tail-test", "");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
        String regex = "^(?:[^\\s]+\\s){3}\\[([^\\]]+)\\].*$";
        LogTail logTail = new LogTail(tempFile.getAbsolutePath(), dateFormat, "27/Dec/2015:14:18:20 +0100", regex);
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
        logTail.tail(out);
        assertEquals(0, out.size());
        assertEquals("", out.toString("ASCII"));

    }

    @Test
    void testOlderLog() throws Exception {
        LogTail logTail = new LogTail("C:\\Users\\Alex\\IdeaProjects\\LogParser\\src\\main\\resources\\log.log",
                new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH),
                "25/Dec/2015:14:18:20 +0100",
                "^(?:[^\\s]+\\s){3}\\[([^\\]]+)\\].*$");

        ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
        logTail.tail(out);

    }

    @Test
    void testOfLineStart() throws Exception {
        RandomAccessFile fl = new RandomAccessFile("C:\\Users\\Alex\\IdeaProjects\\LogParser\\src\\main\\resources\\log.log", "r");
        long mid = 30;
        long left = 0L;

        int exp = (int) getLineStart(fl, mid, left);

        assertEquals(0, exp);
    }

    @Test
    void testOfLineStartLineTwo() throws Exception {
        RandomAccessFile fl = new RandomAccessFile("C:\\Users\\Alex\\IdeaProjects\\LogParser\\src\\main\\resources\\log.log", "r");
        long mid = 350;
        long left = 315;

        int exp = (int) getLineStart(fl, mid, left);


        assertEquals(315, exp);
    }

    @Test
    void testWithInputStream() throws Exception {
        String logContent =
                "178.115.130.130 - - [26/Dec/2015:18:50:13 +0100] \"GET /templates/_system/css/general.css HTTP/1.1\" 404 239 \"http://www.almhuette-raith.at/index.php?option=com_content&view=article&id=49&Itemid=55\" \"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36\" \"-\"\n" +
                        "178.115.130.130 - - [26/Dec/2015:18:50:13 +0100] \"GET /images/stories/raith/wohnung_1_web.jpg HTTP/1.1\" 200 80510 \"http://www.almhuette-raith.at/index.php?option=com_content&view=article&id=49&Itemid=55\" \"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36\" \"-\"\n" +
                        "178.115.130.130 - - [26/Dec/2015:18:50:39 +0100] \"GET /index.php?option=com_phocagallery&view=category&id=1&Itemid=53 HTTP/1.1\" 200 32583 \"http://www.almhuette-raith.at/index.php?option=com_content&view=article&id=49&Itemid=55\" \"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36\" \"-\"\n" +
                        "178.115.130.130 - - [26/Dec/2015:18:50:39 +0100] \"GET /components/com_phocagallery/assets/phocagallery.css HTTP/1.1\" 200 15063 \"http://www.almhuette-raith.at/index.php?option=com_phocagallery&view=category&id=1&Itemid=53\" \"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36\" \"-\"\n";
        InputStream is = new ByteArrayInputStream(logContent.getBytes(StandardCharsets.US_ASCII));
        LogTail logTail = new LogTail(is,
                new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH),
                "25/Dec/2018:14:18:20 +0100",
                "^(?:[^\\s]+\\s){3}\\[([^\\]]+)\\].*$");

        ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
        logTail.tail(out);

        assertEquals(0, out.size());
        assertEquals("", out.toString("ASCII"));
    }
}