package CombinePDF;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;

import java.io.*;

class Convert {
    static void wordToPDF(String docPath, String pdfPath) {
        File inputWord = new File(docPath);
        File outputFile = new File(pdfPath);
        try {
            InputStream docxInputStream = new FileInputStream(inputWord);
            OutputStream outputStream = new FileOutputStream(outputFile);
            IConverter converter = LocalConverter.builder().build();
            converter.convert(docxInputStream).as(DocumentType.DOCX).to(outputStream).as(DocumentType.PDF).execute();
            outputStream.close();
            Main.setLog("Successful conversion!\n\"" + docPath + "\" -> \"" + pdfPath + "\"");
        } catch (Exception e) {
            e.printStackTrace();
            ConfirmBox.display("Error DOC to PDF", "Uh-Oh! An error occurred while converting your file to PDF.\n" +
                    "Stack trace:\n" + e.getMessage());
        }
    }

    //uses iText :[ need to find another library
    static void imageToPDF(String imagePath, String outputPath) {
        Document document = new Document();
        try {
            FileOutputStream fos = new FileOutputStream(outputPath);
            PdfWriter writer = PdfWriter.getInstance(document, fos);
            writer.open();
            document.open();
            document.add(Image.getInstance(imagePath));
            document.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
