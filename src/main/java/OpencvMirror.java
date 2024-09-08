
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.highgui.ImageWindow;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class OpencvMirror {

    public static void main(String[] args){
        //装载c++动态库
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        VideoCapture vc = new VideoCapture(0);
        try{
            if(!vc.isOpened()) {
                System.out.println("在电脑上未发现已连接的摄像头！");
                return;
            }
            captureVideo(vc);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            System.out.println("释放摄像头！");
            vc.release();
        }
    }

    private static void captureVideo(VideoCapture vc) throws InterruptedException {
        final Mat image = new Mat();
        vc.read(image);
        final boolean[] b = {true};
        final JFrame f = HighGui.createJFrame("照镜子", HighGui.WINDOW_NORMAL);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        final JLabel lbl = new JLabel("照镜子");
        lbl.setPreferredSize(new Dimension(image.width(), image.height()));
        //窗口关闭事件
        f.addWindowListener(
                new WindowAdapter(){
                    public void windowClosing(WindowEvent e){
                        b[0]=false;
                    }
                });
        final ImageWindow window = new ImageWindow("照镜子", ImageWindow.WINDOW_NORMAL);
        window.setFrameLabelVisible(f, lbl);
        f.setLocationRelativeTo(null);
        HighGui.windows.put("照镜子", window);
        //启动显示线程
        new Thread(){
            public void run(){
                while(b[0]) {
                    if(!image.empty()) {
                        HighGui.imshow("照镜子", image);
                        HighGui.waitKey(33);
                    }
                }
                HighGui.destroyAllWindows();  //关闭所有窗口，不一定有效
            }
        }.start();
        while(b[0]) {//捕获
            vc.read(image);
            Thread.sleep(33);//33ms捕获一次，相当于帧率为30
        }
        System.out.println("结束照镜子");
    }

}
