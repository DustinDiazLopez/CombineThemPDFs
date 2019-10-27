Combine Them PDFs... yo
------
Application utilizing [JavaFX](https://en.wikipedia.org/wiki/JavaFX), [PDFBox](https://pdfbox.apache.org/) and [documents4j](https://documents4j.com/#/).
This [drag-and-drop](https://docs.oracle.com/javafx/2/drag_drop/jfxpub-drag_drop.htm) application takes in a list of [PDF](https://en.wikipedia.org/wiki/PDF) files and combines/merge them into one file. 
If a file is a Word document or [DOCX](https://www.howtogeek.com/304622/what-is-a-.docx-file-and-how-is-it-different-from-a-.doc-file-in-microsoft-word/) 
then it converts it to a [PDF](https://en.wikipedia.org/wiki/PDF) file on a temporary location relative to where the application is being ran and uses the 
new [PDF](https://en.wikipedia.org/wiki/PDF) file location instead of the [DOCX](https://www.howtogeek.com/304622/what-is-a-.docx-file-and-how-is-it-different-from-a-.doc-file-in-microsoft-word/)
location. Conversion from [DOCX](https://www.howtogeek.com/304622/what-is-a-.docx-file-and-how-is-it-different-from-a-.doc-file-in-microsoft-word/)
 to [PDF](https://en.wikipedia.org/wiki/PDF) does not work in [macOS](https://en.wikipedia.org/wiki/MacOS) 
 nor [Linux](https://en.wikipedia.org/wiki/Linux).
 
How to run the jar file
------
 
```
git clone https://github.com/DustinDiazLopez/CombineThemPDFs.git
```
```
cd CombineThemPDFs/out/artifacts/CombineThemPDFs_jar
```
```
java -jar CombineThemPDFs.jar
```

What I learned
------
- Merging [PDF](https://en.wikipedia.org/wiki/PDF) files with [PDFBox](https://pdfbox.apache.org/).
- Converting Word document or [DOCX](https://www.howtogeek.com/304622/what-is-a-.docx-file-and-how-is-it-different-from-a-.doc-file-in-microsoft-word/) 
files to [PDF](https://en.wikipedia.org/wiki/PDF) with [documents4j](https://documents4j.com/#/).
- Manual [JavaFX](https://en.wikipedia.org/wiki/JavaFX) styling and setup.
- [Drag and drop](https://docs.oracle.com/javafx/2/drag_drop/jfxpub-drag_drop.htm) functionality in [JavaFX](https://en.wikipedia.org/wiki/JavaFX).
- Extensive [String](https://en.wikipedia.org/wiki/String_(computer_science)) manipulation.
