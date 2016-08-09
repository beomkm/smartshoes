package kr.ac.koreatech.hilab.graduation;

public class AHRS {

    public float []q;
    private static final float BETA = 0.1f; // 2* proportional gain
    private static final float SAMPLE_FREQ = 512.0f;

    public AHRS(){
        //init quaternion
        q = new float[4];
        q[0] = 1.0f;
        q[1] = 0.0f;
        q[2] = 0.0f;
        q[3] = 0.0f;
    }
    /**
     *
     * @param data
     * 0. accX
     * 1. accY
     * 2. accZ
     * 3. gyroX
     * 4. gyroY
     * 5. gyroZ
     * 6. magX
     * 7. magY
     * 8. magZ
     * 9. pressure_1
     * @return
     */
    public void calcQuaternion(short []data){
        float recipNorm;
        float[] s = new float[4]; // step
        float[] qDot = new float[4]; // apply feedback step;
        float hx, hy;
        float _2q0mx, _2q0my, _2q0mz, _2q1mx,
                _2bx, _2bz, _4bx, _4bz,
                _2q0, _2q1, _2q2, _2q3,
                _2q0q2, _2q2q3, q0q0, q0q1, q0q2, q0q3, q1q1, q1q2, q1q3, q2q2, q2q3, q3q3;

        float ax = data[0]*2.0f/32768.0f; // 2 g full range for accelerometer
        float ay = data[1]*2.0f/32768.0f; //<>//
        float az = data[2]*2.0f/32768.0f;
        float gx = data[3]*250.0f/32768.0f; // 250 deg/s full range for gyroscope
        float gy = data[4]*250.0f/32768.0f;
        float gz = data[5]*250.0f/32768.0f;
        float mx = data[6]*10.0f*1229.0f/4096.0f + 18.0f; // milliGauss (1229 microTesla per 2^12 bits, 10 mG per microTesla)
        float my = data[7]*10.0f*1229.0f/4096.0f + 70.0f; // apply calibration offsets in mG that correspond to your environment and magnetometer
        float mz = data[8]*10.0f*1229.0f/4096.0f + 270.0f;

        // Rate of change of quaternion from gyroscope
        qDot[0] = 0.5f * (-q[1] * gx - q[2] * gy - q[3] * gz);
        qDot[1] = 0.5f * (q[0] * gx + q[2] * gz - q[3] * gy);
        qDot[2] = 0.5f * (q[0] * gy - q[1] * gz + q[3] * gx);
        qDot[3] = 0.5f * (q[0] * gz + q[1] * gy - q[2] * gx);

        // Normalise accelerometer measurement
        recipNorm = invSqrt(ax * ax + ay * ay + az * az);
        ax *= recipNorm;
        ay *= recipNorm;
        az *= recipNorm;

        // Normalise magnetometer measurement
        recipNorm = invSqrt(mx * mx + my * my + mz * mz);
        mx *= recipNorm;
        my *= recipNorm;
        mz *= recipNorm;


        // Auxiliary variables to avoid repeated arithmetic
        // 연산의 반복을 피하기 위한 보조값
        _2q0mx = 2.0f * q[0] * mx;
        _2q0my = 2.0f * q[0] * my;
        _2q0mz = 2.0f * q[0] * mz;
        _2q1mx = 2.0f * q[1] * mx;
        _2q0 = 2.0f * q[0];
        _2q1 = 2.0f * q[1];
        _2q2 = 2.0f * q[2];
        _2q3 = 2.0f * q[3];
        _2q0q2 = 2.0f * q[0] * q[2];
        _2q2q3 = 2.0f * q[2] * q[3];
        q0q0 = q[0] * q[0];
        q0q1 = q[0] * q[1];
        q0q2 = q[0] * q[2];
        q0q3 = q[0] * q[3];
        q1q1 = q[1] * q[1];
        q1q2 = q[1] * q[2];
        q1q3 = q[1] * q[3];
        q2q2 = q[2] * q[2];
        q2q3 = q[2] * q[3];
        q3q3 = q[3] * q[3];

        // Reference direction of Earth's magnetic field
        hx = mx * q0q0 - _2q0my * q[3] + _2q0mz * q[2] + mx * q1q1 + _2q1 * my * q[2] + _2q1 * mz * q[3] - mx * q2q2 - mx * q3q3;
        hy = _2q0mx * q[3] + my * q0q0 - _2q0mz * q[1] + _2q1mx * q[2] - my * q1q1 + my * q2q2 + _2q2 * mz * q[3] - my * q3q3;
        _2bx = (float) Math.sqrt(hx * hx + hy * hy);
        _2bz = -_2q0mx * q[2] + _2q0my * q[1] + mz * q0q0 + _2q1mx * q[3] - mz * q1q1 + _2q2 * my * q[3] - mz * q2q2 + mz * q3q3;
        _4bx = 2.0f * _2bx;
        _4bz = 2.0f * _2bz;

        // Gradient decent algorithm corrective step
        s[0] = -_2q2 * (2.0f * q1q3 - _2q0q2 - ax) + _2q1 * (2.0f * q0q1 + _2q2q3 - ay) - _2bz * q[2] * (_2bx * (0.5f - q2q2 - q3q3) + _2bz * (q1q3 - q0q2) - mx) + (-_2bx * q[3] + _2bz * q[1]) * (_2bx * (q1q2 - q0q3) + _2bz * (q0q1 + q2q3) - my) + _2bx * q[2] * (_2bx * (q0q2 + q1q3) + _2bz * (0.5f - q1q1 - q2q2) - mz);
        s[1] = _2q3 * (2.0f * q1q3 - _2q0q2 - ax) + _2q0 * (2.0f * q0q1 + _2q2q3 - ay) - 4.0f * q[1] * (1 - 2.0f * q1q1 - 2.0f * q2q2 - az) + _2bz * q[3] * (_2bx * (0.5f - q2q2 - q3q3) + _2bz * (q1q3 - q0q2) - mx) + (_2bx * q[2] + _2bz * q[0]) * (_2bx * (q1q2 - q0q3) + _2bz * (q0q1 + q2q3) - my) + (_2bx * q[3] - _4bz * q[1]) * (_2bx * (q0q2 + q1q3) + _2bz * (0.5f - q1q1 - q2q2) - mz);
        s[2] = -_2q0 * (2.0f * q1q3 - _2q0q2 - ax) + _2q3 * (2.0f * q0q1 + _2q2q3 - ay) - 4.0f * q[2] * (1 - 2.0f * q1q1 - 2.0f * q2q2 - az) + (-_4bx * q[2] - _2bz * q[0]) * (_2bx * (0.5f - q2q2 - q3q3) + _2bz * (q1q3 - q0q2) - mx) + (_2bx * q[1] + _2bz * q[3]) * (_2bx * (q1q2 - q0q3) + _2bz * (q0q1 + q2q3) - my) + (_2bx * q[0] - _4bz * q[2]) * (_2bx * (q0q2 + q1q3) + _2bz * (0.5f - q1q1 - q2q2) - mz);
        s[3] = _2q1 * (2.0f * q1q3 - _2q0q2 - ax) + _2q2 * (2.0f * q0q1 + _2q2q3 - ay) + (-_4bx * q[3] + _2bz * q[1]) * (_2bx * (0.5f - q2q2 - q3q3) + _2bz * (q1q3 - q0q2) - mx) + (-_2bx * q[0] + _2bz * q[2]) * (_2bx * (q1q2 - q0q3) + _2bz * (q0q1 + q2q3) - my) + _2bx * q[1] * (_2bx * (q0q2 + q1q3) + _2bz * (0.5f - q1q1 - q2q2) - mz);


        recipNorm = invSqrt(s[0] * s[0] + s[1] * s[1] + s[2] * s[2] + s[3] * s[3]); // normalise step magnitude
        s[0] *= recipNorm;
        s[1] *= recipNorm;
        s[2] *= recipNorm;
        s[3] *= recipNorm;

        // Apply feedback step
        qDot[0]-= AHRS.BETA * s[0];
        qDot[1] -= AHRS.BETA * s[1];
        qDot[2] -= AHRS.BETA * s[2];
        qDot[3] -= AHRS.BETA * s[3];

        // Integrate rate of change of quaternion to yield quaternion
        q[0] += qDot[0] * (1.0f / AHRS.SAMPLE_FREQ);
        q[1] += qDot[1] * (1.0f / AHRS.SAMPLE_FREQ);
        q[2] += qDot[2] * (1.0f / AHRS.SAMPLE_FREQ);
        q[3] += qDot[3] * (1.0f / AHRS.SAMPLE_FREQ);


        // Normalise quaternion
        recipNorm = invSqrt(q[0] * q[0] + q[1] * q[1] + q[2] * q[2] + q[3] * q[3]);
        q[0] *= recipNorm;
        q[1] *= recipNorm;
        q[2] *= recipNorm;
        q[3] *= recipNorm;
    }

    private static float invSqrt(float x){
        float xhalf = 0.5f*x;
        int i = Float.floatToIntBits(x);
        i = 0x5f3759df - (i>>1);
        x = Float.intBitsToFloat(i);
        x = x*(1.5f - xhalf*x*x);
        return x;
    }
}