package kr.ac.koreatech.hilab.graduation;


import java.util.Queue;

/**
 * Created by byk on 2016-08-09.
 */
public class DataManager {

    private static DataManager instance;

    public AHRS ahrsL;
    public AHRS ahrsR;

    public int pressL1;
    public int pressL2;
    public int pressR1;
    public int pressR2;

    public int[] pressArrR;
    public int[] pressArrL;

    public boolean isConnectedLeft;
    public boolean isConnectedRight;

    private DataManager() {}

    private void init() {
        ahrsL = new AHRS();
        ahrsR = new AHRS();
        pressL1 = 0;
        pressL2 = 0;
        pressR1 = 0;
        pressR2 = 0;
        pressArrR = new int[600];
        pressArrL = new int[600];

    }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
            instance.init();
        }
        return instance;
    }
}
