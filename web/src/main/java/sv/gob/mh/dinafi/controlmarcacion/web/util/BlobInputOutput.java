/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mh.dinafi.controlmarcacion.web.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import lombok.extern.java.Log;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author carlos.guerra
 */
@Log
public class BlobInputOutput {

    public static byte[] convertInputStreamToMinSizeBlob(InputStream is) throws IOException {
        BufferedImage sourceImage = ImageIO.read(is);
        Image thumbnail = sourceImage.getScaledInstance(300, 200, Image.SCALE_SMOOTH);
        BufferedImage bufferedThumbnail = new BufferedImage(thumbnail.getWidth(null),
                thumbnail.getHeight(null),
                BufferedImage.TYPE_INT_RGB);
        bufferedThumbnail.getGraphics().drawImage(thumbnail, 0, 0, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedThumbnail, "jpeg", baos);
        return baos.toByteArray();
    }

    public static byte[] convertInputStreamToBlob(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        return buffer.toByteArray();
    }

    public static byte[] convertFileContentToBlob(String filePath) throws IOException {
        // create file object
        File file = new File(filePath);
        // initialize a byte array of size of the file
        byte[] fileContent = new byte[(int) file.length()];
        FileInputStream inputStream = null;
        try {
            // create an input stream pointing to the file
            inputStream = new FileInputStream(file);
            // read the contents of file into byte array
            //inputStream.read(fileContent);
            int count;
            while ((count = inputStream.read(fileContent)) > 0) {
                // nothing
            }
        } catch (IOException e) {
            throw new IOException("Unable to convert file to byte array. "
                    + e.getMessage());
        } finally {
            // close input stream
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return fileContent;
    }

    public static StreamedContent obtenimg(Blob blob) {
        StreamedContent ret = null;
        try {
            byte[] initialArray = blob.getBytes(1, (int) blob.length());
            InputStream is = new ByteArrayInputStream(initialArray);
            ret = new DefaultStreamedContent(is);
        } catch (Exception ex) {
            log.log(Level.SEVERE, null, ex);
        }
        return ret;
    }
}
