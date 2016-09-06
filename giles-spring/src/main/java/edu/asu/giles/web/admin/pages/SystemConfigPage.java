package edu.asu.giles.web.admin.pages;

public class SystemConfigPage {

    private String githubClientId;
    private String githubSecret;
    private String digilibScalerUrl;
    private String gilesUrl;
    private String pdfToImageDpi;
    private String pdfToImageType;
    private String pdfToImageFormat;
    private boolean pdfExtractText;
    private String jarsUrl;
    private String jarsFileUrl;
    private String tesseractBinFolder;
    private String tesseractDataFolder;
    private boolean ocrImagesFromPdfs;
    private int defaultPageSize;
    
    public String getGithubClientId() {
        return githubClientId;
    }
    public void setGithubClientId(String githubClientId) {
        this.githubClientId = githubClientId;
    }
    public String getGithubSecret() {
        return githubSecret;
    }
    public void setGithubSecret(String githubSecret) {
        this.githubSecret = githubSecret;
    }
    public String getDigilibScalerUrl() {
        return digilibScalerUrl;
    }
    public void setDigilibScalerUrl(String digilibScalerUrl) {
        this.digilibScalerUrl = digilibScalerUrl;
    }
    public String getGilesUrl() {
        return gilesUrl;
    }
    public void setGilesUrl(String gilesUrl) {
        this.gilesUrl = gilesUrl;
    }
    public String getPdfToImageDpi() {
        return pdfToImageDpi;
    }
    public void setPdfToImageDpi(String pdfToImageDpi) {
        this.pdfToImageDpi = pdfToImageDpi;
    }
    public String getPdfToImageType() {
        return pdfToImageType;
    }
    public void setPdfToImageType(String pdfToImageType) {
        this.pdfToImageType = pdfToImageType;
    }
    public boolean isPdfExtractText() {
        return pdfExtractText;
    }
    public void setPdfExtractText(boolean pdfExtractText) {
        this.pdfExtractText = pdfExtractText;
    }
    public String getJarsUrl() {
        return jarsUrl;
    }
    public void setJarsUrl(String jarsUrl) {
        this.jarsUrl = jarsUrl;
    }
    public String getJarsFileUrl() {
        return jarsFileUrl;
    }
    public void setJarsFileUrl(String jarsFileUrl) {
        this.jarsFileUrl = jarsFileUrl;
    }
    public String getTesseractBinFolder() {
        return tesseractBinFolder;
    }
    public void setTesseractBinFolder(String tesseractBinFolder) {
        this.tesseractBinFolder = tesseractBinFolder;
    }
    public String getTesseractDataFolder() {
        return tesseractDataFolder;
    }
    public void setTesseractDataFolder(String tesseractDataFolder) {
        this.tesseractDataFolder = tesseractDataFolder;
    }
    public boolean isOcrImagesFromPdfs() {
        return ocrImagesFromPdfs;
    }
    public void setOcrImagesFromPdfs(boolean ocrImagesFromPdfs) {
        this.ocrImagesFromPdfs = ocrImagesFromPdfs;
    }
    public int getDefaultPageSize() {
        return defaultPageSize;
    }
    public void setDefaultPageSize(int defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }
    public String getPdfToImageFormat() {
        return pdfToImageFormat;
    }
    public void setPdfToImageFormat(String pdfToImageFormat) {
        this.pdfToImageFormat = pdfToImageFormat;
    }
}
