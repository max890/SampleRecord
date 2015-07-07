package com.sample.javacv.samplerecord;

import android.util.Log;

import com.sample.javacv.samplerecord.models.FrameItem;

import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameRecorder;

import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;

public class FrameBufferHandler implements Runnable{

    private LinkedBlockingQueue<FrameItem> frameQueue;// = new LinkedList();
    private FFmpegFrameRecorder recorder;
    private Thread t;
    private boolean running = false;
    private Frame yuvImage = null;
    private static String TAG = "Buffer";

    public void putFrame(FrameItem data) throws InterruptedException {
        if (frameQueue.size() < 20) {
            frameQueue.put(data);
        }
        Log.d(TAG, "putFrame, count: " + frameQueue.size() );
    }

    private FrameItem getFrame() throws InterruptedException {
        Log.d(TAG, "getFrame, count: " + frameQueue.size() );
        return frameQueue.take();
    }


    public FrameBufferHandler(FFmpegFrameRecorder recorder, int imageWidth, int imageHeight) {
        this.recorder = recorder;
        this.frameQueue = new LinkedBlockingQueue<>();
        yuvImage = new Frame(imageWidth, imageHeight, Frame.DEPTH_UBYTE, 2);
    }

    @Override
    public void run() {
        while(!Thread.interrupted()) {
                try {
                    if (frameQueue != null && !frameQueue.isEmpty()) {
                        FrameItem item = getFrame();
                        if (item.getTime() > recorder.getTimestamp()) {
                            recorder.setTimestamp(item.getTime());
                        }
                        ((ByteBuffer) yuvImage.image[0].position(0)).put(item.getData());
                        recorder.record(yuvImage);
                    }
                } catch (FrameRecorder.Exception e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
        frameQueue.clear();
        if (t != null) {
            t.interrupt();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                t = null;
            }
        }
        running = false;
    }

    public boolean isRunning() {
        return running;
    }
}
