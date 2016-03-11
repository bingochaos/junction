package cn.com.navia.sdk.locater.services;

public enum MsgType {

    SERVER_SPECS(1),

    LOCAL_SUCCESS(2),

    DOWNLOAD_SUCCESS(3),
    DOWNLOAD_ERROR(4),

    LOCATION(5),

    ERR_SDK(6),

    INIT_SUCCESS(7),
    INIT_ERROR(8),

    START_SUCCESS(9),
    START_ERROR(10);

    private int id;

    private MsgType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}