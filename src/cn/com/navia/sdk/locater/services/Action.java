package cn.com.navia.sdk.locater.services;

public enum Action {

    INIT(1), DESTROY(2), START(3), STOP(4), SHOW_LATEST_SPECS(5), DOWN_SPECT(6), LOAD_LOCAL_SPECS(7);

    private int id;

    private Action(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}