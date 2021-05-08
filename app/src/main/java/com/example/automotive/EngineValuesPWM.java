package com.example.automotive;

import com.google.android.exoplayer2.util.Log;

public class EngineValuesPWM {

    //     INPUTS
    private final double nJoyX;              // Joystick X input                     (-128..+127)
    private final double nJoyY;              // Joystick Y input                     (-128..+127)

    // OUTPUTS
    private double nMotMixL;           // Motor (left)  mixed output           (-128..+127)
    private double nMotMixR;           // Motor (right) mixed output           (-128..+127)


    private double fPivYLimit = 32.0;

    // TEMP VARIABLES
    private double nMotPremixL;    // Motor (left)  premixed output        (-128..+127)
    private double nMotPremixR;    // Motor (right) premixed output        (-128..+127)
    private double nPivSpeed;      // Pivot Speed                          (-128..+127)
    private double fPivScale;      // Balance scale b/w drive and pivot    (   0..1   )

    public EngineValuesPWM(double x, double y) {
        this.nJoyX = x;
        this.nJoyY = y;
    }


    // Calculate Drive Turn output due to Joystick X input
    public void calulcate() {
        if (nJoyY >= 0) {
            // Forward
            nMotPremixL = (nJoyX >= 0) ? 127.0 : (127.0 + nJoyX);
            nMotPremixR = (nJoyX >= 0) ? (127.0 - nJoyX) : 127.0;
        } else {
            // Reverse
            nMotPremixL = (nJoyX >= 0) ? (127.0 - nJoyX) : 127.0;
            nMotPremixR = (nJoyX >= 0) ? 127.0 : (127.0 + nJoyX);
        }

// Scale Drive output due to Joystick Y input (throttle)
        nMotPremixL = nMotPremixL * nJoyY / 128.0;
        nMotPremixR = nMotPremixR * nJoyY / 128.0;

// Now calculate pivot amount
// - Strength of pivot (nPivSpeed) based on Joystick X input
// - Blending of pivot vs drive (fPivScale) based on Joystick Y input
        nPivSpeed = nJoyX;
        fPivScale = (Math.abs(nJoyY) > fPivYLimit) ? 0.0 : (1.0 - Math.abs(nJoyY) / fPivYLimit);

// Calculate final mix of Drive and Pivot
        nMotMixL = (1.0 - fPivScale) * nMotPremixL + fPivScale * (nPivSpeed);
        nMotMixR = (1.0 - fPivScale) * nMotPremixR + fPivScale * (-nPivSpeed);
    }

    public double getnMotMixL() {
        return nMotMixL;
    }

    public double getnMotMixR() {
        return nMotMixR;
    }

//
//                Log.i("Mix L:",String.valueOf(nMotMixL));
//                Log.i("Mix R:",String.valueOf(nMotMixR));
}
