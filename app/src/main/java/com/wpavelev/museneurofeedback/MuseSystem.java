package com.wpavelev.museneurofeedback;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.choosemuse.libmuse.Accelerometer;
import com.choosemuse.libmuse.ConnectionState;
import com.choosemuse.libmuse.Eeg;
import com.choosemuse.libmuse.Muse;
import com.choosemuse.libmuse.MuseArtifactPacket;
import com.choosemuse.libmuse.MuseConnectionListener;
import com.choosemuse.libmuse.MuseConnectionPacket;
import com.choosemuse.libmuse.MuseDataListener;
import com.choosemuse.libmuse.MuseDataPacket;
import com.choosemuse.libmuse.MuseDataPacketType;
import com.choosemuse.libmuse.MuseListener;
import com.choosemuse.libmuse.MuseManagerAndroid;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Vlad on 29.05.2018.
 */

public class MuseSystem {

    Context context;


    private Logger logger;

    private final Handler handler = new Handler();

    private static final String TAG = "MuseSystem";

    private final double[] eegBuffer = new double[6];
    private boolean eegStale;
    private final double[] relativeAlphaBuffer = new double[6];
    private boolean relativeAlphaStale;
    private final double[] absoluteAlphaBuffer = new double[6];
    private boolean absoluteAlphaStale;
    private final double[] accelBuffer = new double[3];
    private boolean accelStale;

    private MuseManagerAndroid manager;
    private Muse muse;

    private ConnectionListener connectionListener;
    private DataListener dataListener;

    boolean connectionState = false;

    IMainActivity iMainActivity;

    public MuseSystem(Context context, WeakReference<MainActivity> weakActivity) {

        manager = MuseManagerAndroid.getInstance();
        manager.setContext(context);

        manager.setMuseListener(new MuseL(weakActivity));
        connectionListener = new ConnectionListener(weakActivity);
        dataListener = new DataListener(weakActivity);

        this.context = context;

        handler.post(tick);

    }



    public void stopListening() {
        manager.stopListening();
    }


    private final Runnable tick = new Runnable() {
        @Override
        public void run() {

            if (eegStale) {
                iMainActivity.setData(relativeAlphaBuffer, MuseDataPacketType.ALPHA_RELATIVE);
            }

            handler.postDelayed(tick, 1000 / Constants.FRAMERATE);
        }
    };


    private void getAccelValues(MuseDataPacket p) {
        accelBuffer[0] = p.getAccelerometerValue(Accelerometer.X);
        accelBuffer[1] = p.getAccelerometerValue(Accelerometer.Y);
        accelBuffer[2] = p.getAccelerometerValue(Accelerometer.Z);
    }

    public void disconnectMuse() {
        muse.disconnect();
    }

    public void refresh() {
        manager.stopListening();
        manager.startListening();
    }

 public String getName() {
        return muse.getName();

 }

    private void connectMuse(int id) {

        if (!connectionState) {
            manager.stopListening();

            List<Muse> availableMuses = manager.getMuses();

            if (availableMuses.size() < 1 ) {
                Log.w(TAG, "There is nothing to connectMuse to");
            } else {

                // Cache the Muse that the user has selected.
                muse = availableMuses.get(id);
                // Unregister all prior listeners and register our data listener to
                // receive the MuseDataPacketTypes we are interested in.  If you do
                // not register a listener for a particular data type, you will not
                // receive data packets of that type.
                muse.unregisterAllListeners();
                muse.registerConnectionListener(connectionListener);
                muse.registerDataListener(dataListener, MuseDataPacketType.EEG);
                muse.registerDataListener(dataListener, MuseDataPacketType.ALPHA_RELATIVE);
                muse.registerDataListener(dataListener, MuseDataPacketType.ALPHA_ABSOLUTE);
                muse.registerDataListener(dataListener, MuseDataPacketType.ACCELEROMETER);
                muse.registerDataListener(dataListener, MuseDataPacketType.BATTERY);
                muse.registerDataListener(dataListener, MuseDataPacketType.DRL_REF);
                muse.registerDataListener(dataListener, MuseDataPacketType.QUANTIZATION);

                // Initiate a connection to the headband and stream the data asynchronously.
                muse.runAsynchronously();

                connectionState = true;

            }


        } else {

            if (muse != null) {
                muse.disconnect();
            }


        }


    }

    /**
     * You will receive a callback to this method each time there is a change to the
     * connection state of one of the headbands.
     *
     * @param p    A packet containing the current and prior connection states
     * @param muse The headband whose state changed.
     */
    private void receiveMuseConnectionPacket(final MuseConnectionPacket p, final Muse muse) {

        final ConnectionState current = p.getCurrentConnectionState();


        // Update the UI with the change in connection state.
        handler.post(new Runnable() {
            @Override
            public void run() {

                iMainActivity.updateStatus(current);

            }
        });




    }

    /**
     * You will receive a callback to this method each time the headband sends a MuseDataPacket
     * that you have registered.  You can use different listeners for different packet types or
     * a single listener for all packet types as we have done here.
     *
     * @param p    The data packet containing the data from the headband (eg. EEG data)
     * @param muse The headband that sent the information.
     */
    public void receiveMuseDataPacket(final MuseDataPacket p, final Muse muse) {

        // valuesSize returns the number of data values contained in the packet.
        final long n = p.valuesSize();
        switch (p.packetType()) {
            case EEG:
                assert (eegBuffer.length >= n);
                getEegChannelValues(eegBuffer, p);
                eegStale = true;
                break;
            case ACCELEROMETER:
                assert (accelBuffer.length >= n);
                getAccelValues(p);
                accelStale = true;
                break;
            case ALPHA_RELATIVE:
                assert (relativeAlphaBuffer.length >= n);
                getEegChannelValues(relativeAlphaBuffer, p);
                relativeAlphaStale = true;
                break;

            case ALPHA_ABSOLUTE:
                assert (absoluteAlphaBuffer.length >= n);
                getEegChannelValues(absoluteAlphaBuffer, p);
                absoluteAlphaStale = true;
                break;

            case BATTERY:
            case DRL_REF:
            case QUANTIZATION:
            default:
                break;
        }
    }

    /**
     * You will receive a callback to this method each time an artifact packet is generated if you
     * have registered for the ARTIFACTS data type.  MuseArtifactPackets are generated when
     * eye blinks are detected, the jaw is clenched and when the headband is put on or removed.
     *
     * @param p    The artifact packet with the data from the headband.
     * @param muse The headband that sent the information.
     */
    private void receiveMuseArtifactPacket(final MuseArtifactPacket p, final Muse muse) {
    }



    private void getEegChannelValues(double[] buffer, MuseDataPacket p) {
        buffer[0] = p.getEegChannelValue(Eeg.EEG1);
        buffer[1] = p.getEegChannelValue(Eeg.EEG2);
        buffer[2] = p.getEegChannelValue(Eeg.EEG3);
        buffer[3] = p.getEegChannelValue(Eeg.EEG4);
        buffer[4] = p.getEegChannelValue(Eeg.AUX_LEFT);
        buffer[5] = p.getEegChannelValue(Eeg.AUX_RIGHT);
    }


    private Logger getLogger() {
        if (logger == null) {
            logger = new Logger(context);
        }
        return logger;


    }




//--------------Listener-----------------------------------------

    class MuseL extends MuseListener {
        final WeakReference<MainActivity> activityRef;

        MuseL(final WeakReference<MainActivity> activityRef) {
            this.activityRef = activityRef;
        }

        @Override
        public void museListChanged() {
            museListChanged();
        }
    }

    class ConnectionListener extends MuseConnectionListener {
        final WeakReference<MainActivity> activityRef;

        ConnectionListener(final WeakReference<MainActivity> activityRef) {
            this.activityRef = activityRef;
        }

        @Override
        public void receiveMuseConnectionPacket(final MuseConnectionPacket p, final Muse muse) {
            receiveMuseConnectionPacket(p, muse);
        }
    }

    class DataListener extends MuseDataListener {
        final WeakReference<MainActivity> activityRef;

        DataListener(final WeakReference<MainActivity> activityRef) {
            this.activityRef = activityRef;
        }

        @Override
        public void receiveMuseDataPacket(final MuseDataPacket p, final Muse muse) {
            receiveMuseDataPacket(p, muse);
        }

        @Override
        public void receiveMuseArtifactPacket(final MuseArtifactPacket p, final Muse muse) {
            receiveMuseArtifactPacket(p, muse);
        }
    }









}
