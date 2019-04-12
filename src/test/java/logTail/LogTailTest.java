package logTail;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class LogTailTest {

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
}