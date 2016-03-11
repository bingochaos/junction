package cn.com.navia.sdk.locater.services;

public enum Status {

    INITED(1), STARTING(2), RUNGING(3), STOPING(4), STOPED(5), DESTROY(6);

    private int id;

    private Status(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}