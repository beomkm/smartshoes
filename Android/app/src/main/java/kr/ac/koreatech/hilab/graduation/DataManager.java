package kr.ac.koreatech.hilab.graduation;

/**
 * Created by byk on 2016-08-09.
 */
public class DataManager {

    private static DataManager instance;
    public AHRS ahrs;

    private DataManager() {}

    private void init() {
        ahrs = new AHRS();
    }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
            instance.init();
        }
        return instance;
    }
}
