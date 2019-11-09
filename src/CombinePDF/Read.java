package CombinePDF;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Read {
    public static String txt(String path) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        FileReader fr = new FileReader(path);
        BufferedReader br = new BufferedReader(fr);

        int i;
        while ((i = br.read()) != -1) stringBuilder.append((char) i);

        br.close();
        fr.close();

        return stringBuilder.toString();
    }
}
