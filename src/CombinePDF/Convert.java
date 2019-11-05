package CombinePDF;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;

import java.io.*;

class Convert {
    static void toPDF(String docPath, String pdfPath) {
        File inputWord = new File(docPath);
        File outputFile = new File(pdfPath);
        try {
            InputStream docxInputStream = new FileInputStream(inputWord);
            OutputStream outputStream = new FileOutputStream(outputFile);
            IConverter converter = LocalConverter.builder().build();
            converter.convert(docxInputStream).as(DocumentType.DOCX).to(outputStream).as(DocumentType.PDF).execute();
            outputStream.close();
            System.out.println("\u001B[32m" + "success!" + "\u001B[0m");
        } catch (Exception e) {
            e.printStackTrace();
            ConfirmBox.display("Error DOC to PDF", "Uh-Oh! An error occured while converting your file to PDF.\n" +
                    "Stack trace:\n" + e.getMessage());
        }
    }
}
