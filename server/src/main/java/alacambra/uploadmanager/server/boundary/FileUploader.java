package alacambra.uploadmanager.server.boundary;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.validation.constraints.Max;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by alacambra on 1/11/15.
 */
@Path("file")
@Stateless
public class FileUploader {

    private final String UPLOADED_FILE_PATH = "/opt/uploader/files/";
    private Logger logger = Logger.getLogger(this.getClass().getName());

    @POST
    @Consumes("multipart/form-data")
    public void file(MultipartFormDataInput input){
        String fileName = "";

        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get("file");

        for (InputPart inputPart : inputParts) {

            try {

                MultivaluedMap<String, String> header = inputPart.getHeaders();
                fileName = getFileName(header);

                //convert the uploaded file to inputstream
                InputStream inputStream = inputPart.getBody(InputStream.class,null);
                byte [] bytes = IOUtils.toByteArray(inputStream);

                //constructs upload file path
                fileName = UPLOADED_FILE_PATH + fileName;
                writeFile(bytes, fileName);

            } catch (IOException e) {
                throw new WebApplicationException(e);
            }

        }
    }

    private String getFileName(MultivaluedMap<String, String> header) {

        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {

                String[] name = filename.split("=");
                String finalFileName = name[1].trim().replaceAll("\"", "");

                return finalFileName;
            }
        }
        return header.getFirst("Content-Disposition");
    }

    private void writeFile(byte[] content, String filename) throws IOException {

        File file = new File(filename);
        if (!file.exists()) {
            try{

                file.createNewFile();

            }catch (IOException e){
                logger.log(Level.SEVERE, "Error creating file " + filename + " --- " + e.getMessage());
                throw new RuntimeException("Error creating file " + filename, e);
            }
        }

        FileOutputStream fop = new FileOutputStream(file);

        fop.write(content);
        fop.flush();
        fop.close();
    }
}
