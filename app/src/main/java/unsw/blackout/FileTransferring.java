package unsw.blackout;

public interface FileTransferring {
    public boolean hasReceiveBandwidth();

    public boolean hasSendBandwidth();

    public boolean maxStorageReached(int size);

    public boolean maxFilesReached();

    public int getSendBandwidth();

    public int getReceiveBandwidth();

    public int getSendingSpeed();

    public int getReceivingSpeed();

    public void incrementIncomingTransfers();

    public void incrementOutgoingTransfers();

    public void decrementIncomingTransfers();

    public void decrementOutgoingTransfers();

}
