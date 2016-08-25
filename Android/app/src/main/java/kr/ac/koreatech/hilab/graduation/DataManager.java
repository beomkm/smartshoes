package kr.ac.koreatech.hilab.graduation;


import java.util.Queue;

/**
 * Created by byk on 2016-08-09.
 */
public class DataManager {

    private static DataManager instance;

    public AHRS ahrsL;
    public AHRS ahrsR;

    public int press1L;
    public int press1R;
    public int press2L;
    public int press2R;

    public Queue<Dataset> dataQL;


    private DataManager() {}

    private void init() {
        ahrsL = new AHRS();
        ahrsR = new AHRS();
        press1R = 0;
        press2R = 0;


    }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
            instance.init();
        }
        return instance;
    }
}

class Dataset
{
    public int test1;
    public Dataset() {}
}