package com.wpavelev.museneurofeedback;


import com.choosemuse.libmuse.ConnectionState;
import com.choosemuse.libmuse.MuseDataPacketType;

public interface IMainActivity {

    void setData(double[] data, MuseDataPacketType type);

    void updateStatus(ConnectionState connectionState);



}
