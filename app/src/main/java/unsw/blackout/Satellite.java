package unsw.blackout;

import unsw.utils.Angle;

public abstract class Satellite extends Entity implements Orbit {
    public Satellite(String id, String type, double height, Angle position, int range) {
        super(id, type, position, height, range);
    }

    public abstract int getDefaultSendBandwidth();

    public abstract int getDefaultReceiveBandwidth();

    @Override
    public boolean supports(Entity dest) {
        return true;
    }

    @Override
    public int getSendBandwidth() {
        int numOutgoingTransfers = this.getNumOutgoingTransfers();
        if (numOutgoingTransfers == 0) {
            return getDefaultSendBandwidth();
        }
        return getDefaultSendBandwidth() / numOutgoingTransfers;
    }

    @Override
    public int getReceiveBandwidth() {
        int numIncomingTransfers = this.getNumIncomingTransfers();
        if (numIncomingTransfers == 0) {
            return getDefaultReceiveBandwidth();
        }
        return getDefaultReceiveBandwidth() / numIncomingTransfers;
    }

}
