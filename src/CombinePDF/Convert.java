package CombinePDF;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

class Convert {
    public static String pathChar;

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

    //need to find another library
    public static void imageToPDF(String imagePath, String outputPath) {
        try (final PDDocument doc = new PDDocument()){

            PDPage page = new PDPage();
            doc.addPage(page);

            String image = new File(imagePath).getAbsolutePath();
            PDImageXObject pdImage = PDImageXObject.createFromFile(image, doc);

            PDPageContentStream contents = new PDPageContentStream(doc, page);
            PDRectangle mediaBox = page.getMediaBox();

            //float startX = (mediaBox.getWidth() - pdImage.getWidth()) / 2;
            float startY = (mediaBox.getHeight() - pdImage.getHeight()) / 2;
            float scale = .8f;
            contents.drawImage(pdImage, 20, startY, pdImage.getWidth() * scale, pdImage.getHeight() * scale);

            contents.close();

            doc.save(new File(outputPath));
        } catch (IOException e){
            System.err.println("Exception while trying to create pdf document - " + e);
        }
    }

    public static String PPTtoPDF(String pathToPPT) throws IOException {
        List<String> images = generateImagesOfSlides(pathToPPT);
        List<String> pdfList = new ArrayList<>();
        File tempDir = new File("TEMP");
        String out;

        if (tempDir.getAbsolutePath().contains("/")) pathChar = "/";
        else pathChar = "\\";

        //converts images to pdf
        for (String s : images) {
            out = Main.tempDir.getAbsolutePath() + pathChar + new File(s).getName() + ".pdf";
            imageToPDF(s, out);
            pdfList.add(out);
        }

        //merges pdfs into one file
        File[] slides = new File[pdfList.size()];

        for (int i = 0; i < pdfList.size(); i++) {
            slides[i] = new File(pdfList.get(i));
        }

        Main.merge(slides, false);
        Main.setLog(Main.last);

        //cleaning
        images.forEach(e -> {
            if (Main.deleteFile(new File(e).getAbsolutePath())) {
                Main.setLog("Deleted temp slide: " + new File(e).getName());
            } else {
                Main.setLog("Could not delete temp slide (image) or does not exists");
            }
        });

        pdfList.forEach(e -> {
            if (Main.deleteFile(new File(e).getAbsolutePath())) {
                Main.setLog("Deleted temp slide: " + new File(e).getName());
            } else {
                Main.setLog("Could not delete temp slide (pdf) or does not exists");
            }
        });

        //returns the file location
        return Main.last;
    }

    public static java.util.List<String> generateImagesOfSlides(String pathToPPT) throws IOException {
        File tempDir = new File("TEMP");
        List<String> slides = new ArrayList<>();

        if (tempDir.getAbsolutePath().contains("/")) pathChar = "/";
        else pathChar = "\\";

        FileInputStream is = new FileInputStream(pathToPPT);
        HSLFSlideShow ppt = new HSLFSlideShow(is);
        is.close();
        Dimension pgsize = ppt.getPageSize();
        int idx = 1;
        for (HSLFSlide slide : ppt.getSlides()) {
            BufferedImage img = new BufferedImage(pgsize.width, pgsize.height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = img.createGraphics();
            // clear the drawing area
            graphics.setPaint(Color.white);
            graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));
            // render
            slide.draw(graphics);
            // save the output
            String path = tempDir + pathChar + "slide-" + idx + ".png";
            FileOutputStream out = new FileOutputStream(path);
            slides.add(path);
            //Main.delete.add(path);
            javax.imageio.ImageIO.write(img, "png", out);
            out.close();
            idx++;
        }

        return slides;
    }
}
