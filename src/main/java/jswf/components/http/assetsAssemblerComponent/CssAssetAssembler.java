package jswf.components.http.assetsAssemblerComponent;

public class CssAssetAssembler extends AbstractAssetAssembler {

    protected String extension = ".css";

    protected String minifiedExtension = ".min.css";

    @Override
    public String getExtension() {
        return extension;
    }

    @Override
    public String getMinifiedExtension() {
        return minifiedExtension;
    }

}
