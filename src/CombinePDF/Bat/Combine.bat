@echo off

cd "C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\"

::git clean -df && git checkout -- . && git pull origin master

"C:\Program Files\Java\jdk1.8.0_202\bin\java.exe" "-javaagent:C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2018.3.4\lib\idea_rt.jar=54145:C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2018.3.4\bin" -Dfile.encoding=UTF-8 -classpath "C:\Program Files\Java\jdk1.8.0_202\jre\lib\charsets.jar;C:\Program Files\Java\jdk1.8.0_202\jre\lib\deploy.jar;C:\Program Files\Java\jdk1.8.0_202\jre\lib\ext\access-bridge-64.jar;C:\Program Files\Java\jdk1.8.0_202\jre\lib\ext\cldrdata.jar;C:\Program Files\Java\jdk1.8.0_202\jre\lib\ext\dnsns.jar;C:\Program Files\Java\jdk1.8.0_202\jre\lib\ext\jaccess.jar;C:\Program Files\Java\jdk1.8.0_202\jre\lib\ext\jfxrt.jar;C:\Program Files\Java\jdk1.8.0_202\jre\lib\ext\localedata.jar;C:\Program Files\Java\jdk1.8.0_202\jre\lib\ext\nashorn.jar;C:\Program Files\Java\jdk1.8.0_202\jre\lib\ext\sunec.jar;C:\Program Files\Java\jdk1.8.0_202\jre\lib\ext\sunjce_provider.jar;C:\Program Files\Java\jdk1.8.0_202\jre\lib\ext\sunmscapi.jar;C:\Program Files\Java\jdk1.8.0_202\jre\lib\ext\sunpkcs11.jar;C:\Program Files\Java\jdk1.8.0_202\jre\lib\ext\zipfs.jar;C:\Program Files\Java\jdk1.8.0_202\jre\lib\javaws.jar;C:\Program Files\Java\jdk1.8.0_202\jre\lib\jce.jar;C:\Program Files\Java\jdk1.8.0_202\jre\lib\jfr.jar;C:\Program Files\Java\jdk1.8.0_202\jre\lib\jfxswt.jar;C:\Program Files\Java\jdk1.8.0_202\jre\lib\jsse.jar;C:\Program Files\Java\jdk1.8.0_202\jre\lib\management-agent.jar;C:\Program Files\Java\jdk1.8.0_202\jre\lib\plugin.jar;C:\Program Files\Java\jdk1.8.0_202\jre\lib\resources.jar;C:\Program Files\Java\jdk1.8.0_202\jre\lib\rt.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\out\production\CombineThemPDFs;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\PDFBox\fontbox-2.0.16.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\PDFBox\pdfbox-2.0.16.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\PDFBox\pdfbox-app-2.0.16.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\PDFBox\pdfbox-tools-2.0.16.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\PDFBox\preflight-2.0.16.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\PDFBox\preflight-app-2.0.16.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\PDFBox\xmpbox-2.0.16.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\poi\lib\activation-1.1.1.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\poi\lib\commons-codec-1.12.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\poi\lib\commons-collections4-4.3.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\poi\lib\commons-compress-1.18.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\poi\lib\commons-logging-1.2.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\poi\lib\commons-math3-3.6.1.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\poi\lib\jaxb-api-2.3.0.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\poi\lib\jaxb-core-2.3.0.1.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\poi\lib\jaxb-impl-2.3.0.1.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\poi\lib\junit-4.12.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\poi\lib\log4j-1.2.17.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\poi\ooxml-lib\curvesapi-1.06.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\poi\ooxml-lib\xmlbeans-3.1.0.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\poi\poi-4.1.0.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\poi\poi-examples-4.1.0.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\poi\poi-excelant-4.1.0.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\poi\poi-ooxml-4.1.0.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\poi\poi-ooxml-schemas-4.1.0.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\poi\poi-scratchpad-4.1.0.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\documents4j\documents4j-client-standalone-1.0.3-shaded.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\documents4j\documents4j-server-standalone-1.0.3-shaded.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\iText\barcodes-7.1.9.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\iText\commons-codec-1.5.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\iText\dom4j-1.6.1.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\iText\forms-7.1.9.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\iText\fr.opensagres.xdocreport.itext.extension-1.0.6.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\iText\io-7.1.9.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\iText\itext-2.1.7.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\iText\kernel-7.1.9.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\iText\layout-7.1.9.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\iText\ooxml-schemas-1.1.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\iText\org.apache.poi.xwpf.converter.core-1.0.6.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\iText\org.apache.poi.xwpf.converter.pdf-1.0.6.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\iText\pdfa-7.1.9.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\iText\pdftest-7.1.9.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\iText\poi-3.10-FINAL.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\iText\poi-ooxml-3.10-FINAL.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\iText\sign-7.1.9.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\iText\stax-api-1.0.1.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\iText\styled-xml-parser-7.1.9.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\iText\svg-7.1.9.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\iText\xml-apis-1.0.b2.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\iText\xmlbeans-2.3.0.jar;C:\Users\dudia\OneDrive\Desktop\Just some stuff\Projects\CombineThemPDFs\src\libs\SQLite\sqlite-jdbc-3.28.0.jar" CombinePDF.Main

exit