package configuration;

public class Archive {
    private String qrAETitle;
    private String qrIP;
    private Integer qrPort;

    private String qido;
    private String wado;

    private String moveAETitle;
    private String moveIP;
    private Integer movePort;

    private String storageAETitle;
    private String storageIP;
    private Integer storagePort;

    public Archive() {
    }

    public Archive(String qrAETitle, String qrIP, Integer qrPort, String qido, String wado, String moveAETitle,
                   String moveIP, Integer movePort, String storageAETitle, String storageIP, Integer storagePort) {
        this.qrAETitle = qrAETitle;
        this.qrIP = qrIP;
        this.qrPort = qrPort;
        this.qido = qido;
        this.wado = wado;
        this.moveAETitle = moveAETitle;
        this.moveIP = moveIP;
        this.movePort = movePort;
        this.storageAETitle = storageAETitle;
        this.storageIP = storageIP;
        this.storagePort = storagePort;
    }

    public String getQrAETitle() {
        return qrAETitle;
    }

    public void setQrAETitle(String qrAETitle) {
        this.qrAETitle = qrAETitle;
    }

    public String getQrIP() {
        return qrIP;
    }

    public void setQrIP(String qrIP) {
        this.qrIP = qrIP;
    }

    public Integer getQrPort() {
        return qrPort;
    }

    public void setQrPort(Integer qrPort) {
        this.qrPort = qrPort;
    }

    public String getQido() {
        return qido;
    }

    public void setQido(String qido) {
        this.qido = qido;
    }

    public String getWado() {
        return wado;
    }

    public void setWado(String wado) {
        this.wado = wado;
    }

    public String getMoveAETitle() {
        return moveAETitle;
    }

    public void setMoveAETitle(String moveAETitle) {
        this.moveAETitle = moveAETitle;
    }

    public String getMoveIP() {
        return moveIP;
    }

    public void setMoveIP(String moveIP) {
        this.moveIP = moveIP;
    }

    public Integer getMovePort() {
        return movePort;
    }

    public void setMovePort(Integer movePort) {
        this.movePort = movePort;
    }

    public String getStorageAETitle() {
        return storageAETitle;
    }

    public void setStorageAETitle(String storageAETitle) {
        this.storageAETitle = storageAETitle;
    }

    public String getStorageIP() {
        return storageIP;
    }

    public void setStorageIP(String storageIP) {
        this.storageIP = storageIP;
    }

    public Integer getStoragePort() {
        return storagePort;
    }

    public void setStoragePort(Integer storagePort) {
        this.storagePort = storagePort;
    }
}
