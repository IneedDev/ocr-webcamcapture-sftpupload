package pl.app.ocr.service;

import com.github.sarxos.webcam.*;
import com.github.sarxos.webcam.ds.ipcam.IpCamDeviceRegistry;
import com.github.sarxos.webcam.ds.ipcam.IpCamDriver;
import com.github.sarxos.webcam.ds.ipcam.IpCamMode;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

@Service
public class OcrService {

    private final UploaderService uploaderService;

    public OcrService(UploaderService uploaderService) {
        this.uploaderService = uploaderService;
    }

    @PostConstruct
    public void run() throws IOException, TesseractException {
        uploaderService.uploadFile();
//        readFileText("hello-world.jpeg");
//        readFileText("test.jpeg");
//        readFileText("detect.jpeg");
//        saveCapturedImageToFile();
//        webCamMotionDetector();
//        getMjpeFromIpCamera();
    }



    public void readFileText(String fileName) throws IOException, TesseractException {
        File image = new File("src/main/resources/images/"+fileName);
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("src/main/resources/tessdata");
        tesseract.setLanguage("eng");
        tesseract.setPageSegMode(1);
        tesseract.setOcrEngineMode(1);
        String result = tesseract.doOCR(image);
        System.out.println("Result for " + fileName + ": " + result);
    }

    private void getMjpeFromIpCamera() throws MalformedURLException {
        Webcam.setDriver(new IpCamDriver());

        IpCamDeviceRegistry.register("Lignano", "http://98.173.8.28:5300", IpCamMode.PUSH);

        WebcamPanel panel = new WebcamPanel(Webcam.getWebcams().get(0));
        panel.setFPSLimit(1);

        JFrame f = new JFrame("Live Views From Lignano Beach");
        f.add(panel);
        f.pack();
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        System.out.println(f);
    }

    private void webCamMotionDetector() throws IOException {
        Webcam webcam = Webcam.getDefault();
        webcam.close();
        WebcamMotionListener motionListener = new WebcamMotionListener() {
            @Override
            public void motionDetected(WebcamMotionEvent webcamMotionEvent) {
                //TODO send to sftp storage
                System.out.println("I Love puszek");
                try {
                    ImageIO.write(webcam.getImage(), "JPEG", new File("src/main/resources/images/detect.jpeg"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };

        WebcamMotionDetector detector = new WebcamMotionDetector(webcam);
        detector.setInterval(500);
        detector.addMotionListener(motionListener);
        detector.start();

        System.in.read();
    }

    private void saveCapturedImageToFile() throws IOException {
        Webcam webcam = Webcam.getDefault();
        webcam.open();
        webcam.getImage();
        ImageIO.write(webcam.getImage(), "JPEG", new File("src/main/resources/images/output.jpeg"));
        System.out.println(webcam.isOpen());
        webcam.close();
    }
}
