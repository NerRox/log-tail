package logTail;

import org.junit.jupiter.api.Test;

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
}