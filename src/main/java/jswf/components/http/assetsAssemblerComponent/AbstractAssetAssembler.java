package jswf.components.http.assetsAssemblerComponent;

import org.eclipse.jetty.util.URIUtil;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

abstract public class AbstractAssetAssembler {

    protected Boolean loadMinifiedVersions = false;

    public Boolean getLoadMinifiedVersions() {
        return loadMinifiedVersions;
    }

    public AbstractAssetAssembler loadMinifiedVersions(Boolean loadMinifiedVersions) {
        this.loadMinifiedVersions = loadMinifiedVersions;

        return this;
    }

    public String process(String[] fileNames, String basePath, String resourcesPath) throws Exception {
        String fileExtension;

        String content = "";
        for (String filePath : fileNames) {
            fileExtension = getExtension();
            if (loadMinifiedVersions) {
                fileExtension = getMinifiedExtension();
            }

            String fullFilePath = URIUtil.addPaths(basePath, resourcesPath);
            fullFilePath = URIUtil.addPaths(fullFilePath, filePath + fileExtension);
            File file = new File(fullFilePath);

            if (file.exists()) {
                content += "\n/*** " + filePath + fileExtension + " ***/\n";

                Path fullPath = Paths.get(fullFilePath);
                content += new String(Files.readAllBytes(fullPath));
                content += "\n";
            } else {
                String previousFilePath = filePath + fileExtension;

                // Invert the extension since the previous files does not exist.
                if (loadMinifiedVersions) {
                    fileExtension = getExtension();
                } else {
                    fileExtension = getMinifiedExtension();
                }

                fullFilePath = URIUtil.addPaths(basePath, resourcesPath);
                fullFilePath = URIUtil.addPaths(fullFilePath, filePath + fileExtension);

                file = new File(fullFilePath);
                if (file.exists()) {
                    content += "/*** File: " + previousFilePath + " does not exist, loading alternative resource: " + filePath + fileExtension + " ***/\n";

                    Path fullPath = Paths.get(fullFilePath);

                    content += new String(Files.readAllBytes(fullPath));
                    content += "\n";
                } else {
                    content += "/*** Error: File [" + filePath + fileExtension +"] not found! ***/\n";
                }
            }
        }

        return content;
    }

    abstract public String getExtension();

    abstract public String getMinifiedExtension();

}
