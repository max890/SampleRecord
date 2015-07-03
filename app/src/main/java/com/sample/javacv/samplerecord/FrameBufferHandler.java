package com.sample.javacv.samplerecord;

import android.util.Log;

import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameRecorder;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class FrameBufferHandler implements Runnable{

    private Queue<Frame> frameQueue;// = new LinkedList();
    private FFmpegFrameRecorder recorder;
    private Thread t;
    private boolean running = false;
    private static String TAG = "Buffer";

    public void putFrame(Frame frame) {
        frameQueue.add(frame);
        Log.d(TAG, "putFrame, count: " + frameQueue.size() );
    }

    private Frame getFrame() {
        Log.d(TAG, "getFrame, count: " + frameQueue.size() );
        return frameQueue.poll();
    }


    public FrameBufferHandler (FFmpegFrameRecorder recorder) {
        this.recorder = recorder;
        this.frameQueue = new LinkedList();
    }

    @Override
    public void run() {
        while(frameQueue != null) {
            try {
                if (!frameQueue.isEmpty()) {
                    recorder.record(getFrame());
                }
            } catch (FrameRecorder.Exception e) {
                e.printStackTrace();
            }
//            try {
//                t.sleep(30);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }

    public void start() {

        if (t == null) {
            Log.d(TAG, " start buffer");
            t = new Thread(this);
            t.start();
        }

        running = true;
    }

    public void stop() {
        if (t != null) {
            t.interrupt();
            try {
                t.join();
            } catch (InterruptedException e) {}
            t = null;
            Log.d(TAG, " stop buffer");
        }
        running = false;
        frameQueue.clear();
        frameQueue = null;
    }

    public boolean isRunning() {
        return running;
    }
}
