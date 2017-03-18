package tthcc.robot.polargraph;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import tthcc.robot.polargraph.constant.Constants;

import com.google.common.collect.Lists;

/**
 * This version of the TwoWaySerialComm example makes use of the
 * SerialPortEventListener to avoid polling.
 */
public class Controller implements SerialPortEventListener {

    private SimpleDateFormat sdf              = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private List<String>     svgPathList      = Lists.newArrayList();
    private String           svgFilename      = null;
    private String           progressFilename = null;
    private SerialPort       serialPort       = null;
    private InputStream      ins              = null;
    private OutputStream     outs             = null;
    private int              index            = 0;

    /**
     * @param portName
     * @throws Exception
     */
    private void connect(String portName) throws Exception {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if (portIdentifier.isCurrentlyOwned()) {
            System.out.println("Error: Port is currently in use");
        } else {
            CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
            if (commPort instanceof SerialPort) {
                serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);
                ins = serialPort.getInputStream();
                outs = serialPort.getOutputStream();
                serialPort.addEventListener(this);
                serialPort.notifyOnDataAvailable(true);
            } else {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }
    }

    /**
     * @param filename
     * @throws Exception
     */
    private void readSVG(String filename) throws Exception {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filename));
            String line = null;
            StringBuffer buf = new StringBuffer();
            String path = null;
            int bufSize = 0;
            int pathSize = 0;
            boolean viewBoxFlag = false;
            String dockX = null, dockY = null;
            while ((line = reader.readLine()) != null) {
                if (!viewBoxFlag && line.indexOf("viewBox=") > -1) {
                    String[] viewBox = line.split("\"")[1].split(" ");
                    dockX = viewBox[2];
                    dockY = viewBox[3];
                    viewBoxFlag = true;
                    continue;
                }
                if (line.startsWith("<path d=")) {
                    path = line.split("\"")[1];
                    pathSize = path.length();
                    if (bufSize + pathSize <= Constants.BUF_SIZE) {
                        buf.append(path);
                        bufSize += pathSize;
                    } else {
                        svgPathList.add(buf.toString());
                        buf.delete(0, bufSize);
                        buf.append(path);
                        bufSize = pathSize;
                    }
                }
            }
            if (buf.length() > 0) {
                svgPathList.add(buf.toString());
            }
            svgPathList.add(String.format(Constants.SVG_MOVE, dockX, dockY));
            svgPathList.add(Constants.CMD_DONE);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * @throws Exception
     */
    private void iamready() throws Exception {
        printController("-", Constants.CMD_I_AM_READY);
        outs.write(Constants.CMD_I_AM_READY.getBytes());
        outs.write(Constants.BUF_END);
    }

    /**
     * @param distance
     */
    private void setMotor(String distance) throws Exception {
        printController("-", Constants.CMD_MOTOR);
        outs.write(String.format("%s %s", Constants.CMD_MOTOR, distance).getBytes());
        outs.write(Constants.BUF_END);
    }

    /**
     * @param pen
     */
    private void setPen(String pen) throws Exception {
        printController("-", Constants.CMD_PEN);
        outs.write(String.format("%s %s", Constants.CMD_PEN, pen).getBytes());
        outs.write(Constants.BUF_END);
    }

    /**
     * @param paper
     */
    private void setPaper(String offsetX, String offsetY) throws Exception {
        printController("-", Constants.CMD_PAPER);
        outs.write(String.format("%s %s %s", Constants.CMD_PAPER, offsetX, offsetY).getBytes());
        outs.write(Constants.BUF_END);
    }

    /**
     * 
     */
    public void serialEvent(SerialPortEvent arg0) {
        byte[] buf = new byte[1024];
        try {
            int data = 0, iread = 0;
            while ((data = ins.read()) != -1) {
                buf[iread++] = (byte) data;
            }
            String line = null;
            String response = new String(buf, 0, iread);
            List<String> lines = Lists.newLinkedList();
            StringTokenizer token = new StringTokenizer(response, "\r\n");
            while (token.hasMoreTokens()) {
                line = token.nextToken();
                lines.add(line);
                printArduino(line);
            }
            for (String ln : lines) {
                if (ln.equals(Constants.CMD_SEND_PATH) && index < svgPathList.size()) {
                    String path = svgPathList.get(index);
                    printController(String.valueOf((index + 1)), path);
                    outs.write(path.getBytes());
                    outs.write(Constants.BUF_END);
                    saveProgress();
                    index++;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * @param msg
     */
    private void printArduino(String msg) {
        System.out.println(String.format(Constants.PRINT_ARDUINO, currentTime(), msg));
    }

    /**
     * @param index
     * @param msg
     */
    private void printController(String index, String msg) {
        System.out.println(String.format(Constants.PRINT_CONTROLLER, currentTime(), index, msg));
    }

    /**
     * @return
     */
    private String currentTime() {
        return sdf.format(Calendar.getInstance().getTime());
    }

    /**
     * 
     */
    private void saveProgress() {
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(progressFilename);
            fout.write((String.valueOf(index).getBytes()));
        } catch (Exception exp) {
            exp.printStackTrace();
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            }
        }
    }

    /**
     * 
     */
    private void loadProgress() {
        File file = new File(progressFilename);
        if (!file.exists()) {
            return;
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = reader.readLine();
            if (line != null) {
                index = Integer.valueOf(line);
                printController("-", "load progress, index=" + index);
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            }
        }
    }

    /**
     * @return
     */
    private Thread getShutdownHook() {
        return new Thread() {
            @Override
            public void run() {
                System.out.println("serialPort.removeEventListener");
                saveProgress();
                if (serialPort != null) {
                    serialPort.removeEventListener();
                }
            }
        };
    }

    public static void main(String[] args) {
        try {

            System.out.println("  file:" + args[0]);
            System.out.println("motor:" + args[1]);
            System.out.println("   pen:" + args[2]);
            System.out.println("paper:" + args[3] + " " + args[4]);

            Controller controller = new Controller();
            controller.svgFilename = args[0];
            Runtime.getRuntime().addShutdownHook(controller.getShutdownHook());
            controller.readSVG(controller.svgFilename);
            int idx = controller.svgFilename.lastIndexOf(".");
            controller.progressFilename = controller.svgFilename.substring(0, idx) + "_progress.txt";
            controller.loadProgress();
            controller.connect("COM4");
            Thread.sleep(3000);

            controller.setMotor(args[1]);
            Thread.sleep(2000);

            controller.setPen(args[2]);
            Thread.sleep(2000);

            controller.setPaper(args[3], args[4]);
            Thread.sleep(2000);

            controller.iamready();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
