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
    public int pressL1Fix;
    public int pressL2Fix;
    public int pressR1Fix;
    public int pressR2Fix;

    public int[] pressArrL;
    public int[] pressArrR;
    public int[] angleArrL;
    public int[] angleArrR;

    public int pressSumLU;
    public int pressSumLL; //left lower
    public int pressSumRU;
    public int pressSumRL; //right lower

    public int normalCnt;
    public int abnormalCnt;



    public boolean isConnectedLeft;
    public boolean isConnectedRight;

    public int linearGraphCount;

    private DataManager() {}

    private void init() {
        ahrsL = new AHRS();
        ahrsR = new AHRS();
        pressL1 = 0;
        pressL2 = 0;
        pressR1 = 0;
        pressR2 = 0;
        pressArrL = new int[FootProtocol.ARCHIVE_TIME];
        pressArrR = new int[FootProtocol.ARCHIVE_TIME];
        angleArrL = new int[FootProtocol.ANGLE_ARCHIVE_TIME];
        angleArrR = new int[FootProtocol.ANGLE_ARCHIVE_TIME];

        pressSumLU = 0;
        pressSumLL = 0;
        pressSumRU = 0;
        pressSumRL = 0;

        linearGraphCount = 0;

        normalCnt = 1;
        abnormalCnt = 1;

    }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
            instance.init();
        }
        return instance;
    }
}
