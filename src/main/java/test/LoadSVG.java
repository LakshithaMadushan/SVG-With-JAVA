package test;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGElement;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

public class LoadSVG {

    private Document svgDocument;
    private static String selectedBlock = "block-3";

    public static void main(String[] args) {
        ClassLoader classLoader = new LoadSVG().getClass().getClassLoader();
        URL filePath = (classLoader.getResource("block-map.svg"));

        try {
            LoadSVG svg = new LoadSVG(filePath.toString());

            SVGElement selectedSVGElement = svg.getSelectedElement(LoadSVG.selectedBlock);
            selectedSVGElement.setAttribute("fill", "#fdcb6e");

            svg.saveImagePNG(600, 600);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LoadSVG() {
    }

    private LoadSVG(String uri) throws IOException {
        setSVGDocument(createSVGDocument(uri));
    }

    private Document createSVGDocument(String uri) throws IOException {
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
        return factory.createDocument(uri);
    }

    private void setSVGDocument(Document document) {
        this.svgDocument = document;
    }

    private SVGElement getSelectedElement(String elementID) {
        return (SVGElement) this.svgDocument.getElementById(elementID);
    }

    private void saveImagePNG(double width, double height) throws IOException {
        PNGTranscoder t = new PNGTranscoder();
        t.addTranscodingHint(PNGTranscoder.KEY_WIDTH, (float) width);
        t.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float) height);

        t.addTranscodingHint(PNGTranscoder.KEY_FORCE_TRANSPARENT_WHITE, true);

        TranscoderInput input = new TranscoderInput(svgDocument);
        ByteArrayOutputStream ostream = new ByteArrayOutputStream(1000);
        TranscoderOutput output2 = new TranscoderOutput(ostream);

        try {
            t.transcode(input, output2);
        } catch (TranscoderException e) {
            e.printStackTrace();
        }

        BufferedImage imag = ImageIO.read(new ByteArrayInputStream(ostream.toByteArray()));
        ImageIO.write(imag, "png", new File(new Date().getTime() + ".png"));
        ostream.flush();
        ostream.close();
    }
}
