package models;

public class BtDevice {

    private String address;
    private Long firstSeen;
    private Long lastSeen;

    public BtDevice() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getFirstSeen() {
        return firstSeen;
    }

    public void setFirstSeen(Long firstSeen) {
        this.firstSeen = firstSeen;
    }

    public Long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Long lastSeen) {
        this.lastSeen = lastSeen;
    }
}
