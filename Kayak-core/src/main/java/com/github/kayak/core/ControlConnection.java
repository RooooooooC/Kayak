/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.kayak.core;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dschanoeh
 */
public class ControlConnection extends SocketcandConnection implements Runnable {

    private static final Logger logger = Logger.getLogger(ControlConnection.class.getName());

    private Socket socket;
    private PrintWriter output;
    private Thread thread;
    private InputStreamReader input;
    private Boolean connected = false;
    private StatisticsReceiver statisticsReceiver;

    public void setStatisticsReceiver(StatisticsReceiver receiver) {
        this.statisticsReceiver = receiver;
    }

    public StatisticsReceiver getStatisticsReceiver() {
        return statisticsReceiver;
    }

    public ControlConnection(BusURL url) {
        this.host = url.getHost();
        this.port = url.getPort();
        this.busName = url.getBus();
    }

    public void open() {
        InetSocketAddress address = new InetSocketAddress(host, port);

        try {
            socket = new Socket();
            socket.connect(address);
            socket.setSoTimeout(1000);

            input = new InputStreamReader(
                    socket.getInputStream());

            output = new PrintWriter(socket.getOutputStream(), true);

            String ret = getElement(input);
            if (!ret.equals("< hi >")) {
                logger.log(Level.SEVERE, "Did not receive greeting from host.");
            }

            output.print("< open " + busName + " >");
            output.flush();

            ret = getElement(input);
            if (!ret.equals("< ok >")) {
                logger.log(Level.SEVERE, "Could not open bus");
            }

            output.print("< controlmode >");
            output.flush();

            ret = getElement(input);
            if (!ret.equals("< ok >")) {
                logger.log(Level.SEVERE, "Could not switch to control mode.");
            }
            socket.setSoTimeout(100);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "IOException while creating the socket.", e);
            return;
        }

        connected = true;
    }

    public void close() {
        if (thread != null && thread.isAlive()) {
            try {
                thread.interrupt();
                thread.join();
            } catch (Exception e) {
            }
        }
        try {
            socket.close();
        } catch (IOException e) {
        }

        connected = false;
    }
    
    public Boolean isConnected() {
        return connected;
    }

    public void requestStatistics(int interval) {
        if(this.isConnected()) {
            output.write("< statistics " + Integer.toString(interval) + " >");
            output.flush();

            /* Start worker thread for frame reception */
            thread = new Thread(this);
            thread.start();
        } else {
            logger.log(Level.WARNING, "requested statistics while connection was not opened.");
        }
    }

    public void disableStatistics() {
        if(this.isConnected() && thread != null && thread.isAlive()) {
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException ex) {
            }
        }
    }


    @Override
    public void run() {
        while (true) {
            if (Thread.interrupted()) {
                break;
            }

            try {
                String frame = getElement(input);

                String[] fields = frame.split("\\s");

                if (fields[1].equals("stat")) {
                    try {
                        long rxBytes = Long.parseLong(fields[2]);
                        long rxPackets = Long.parseLong(fields[3]);
                        long txBytes = Long.parseLong(fields[4]);
                        long txPackets = Long.parseLong(fields[5]);

                        if(statisticsReceiver != null) {
                            statisticsReceiver.statisticsUpdated(rxBytes, rxPackets, txBytes, txPackets);
                        }

                    } catch (Exception ex) {
                        logger.log(Level.WARNING, "Could not properly deliver CAN frame", ex);
                    }
                } else if (fields[1].equals("error")) {
                    logger.log(Level.WARNING, "Received error from socketcand: " + frame);
                }
            }catch(InterruptedException ex) {
                logger.log(Level.WARNING, "Interrupted exception. Shutting down connection thread", ex);
                return;
            } catch (IOException ex) {
                /*
                 * A read from the socket may time out if there are very few frames.
                 * this will cause an IOException. This is ok so we will ignore these
                 * exceptions
                 */
            }
        }
    }

}