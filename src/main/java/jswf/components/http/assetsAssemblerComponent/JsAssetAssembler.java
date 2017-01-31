package jswf.components.http.assetsAssemblerComponent;

public class JsAssetAssembler extends AbstractAssetAssembler {

    protected String extension = ".js";

    protected String minifiedExtension = ".min.js";

    @Override
    public String getExtension() {
        return extension;
    }

    @Override
    public String getMinifiedExtension() {
        return minifiedExtension;
    }
}
