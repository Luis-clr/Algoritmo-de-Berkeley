import java.io.IOException;
import java.net.*;

public class SyncWorker extends Thread {
    private int workerId;
    private int currentMoment;
    private DatagramSocket timeSocket;
    private TimeKeeper timeKeeper;

    public SyncWorker(int workerId, DatagramSocket timeSocket, int initialTime) {
        this.workerId = workerId;
        this.timeSocket = timeSocket;
        this.timeKeeper = new TimeKeeper(initialTime, "Worker-" + workerId);
        this.currentMoment = this.timeKeeper.getCurrentTime();
    }

    @Override
    public void run() {
        broadcastCurrentTime();
        updateLocalTime();
    }

    private void updateLocalTime() {
        try {
            DatagramPacket incomingPacket = new DatagramPacket(new byte[1024], 1024);
            while (true) {
                timeSocket.receive(incomingPacket);
                int updatedTime = Integer.parseInt(new String(incomingPacket.getData(), 0, incomingPacket.getLength()));
                timeKeeper.adjustTime(updatedTime);
                System.out.printf("Worker %d adjusted time to: %d%n", workerId, timeKeeper.getCurrentTime());
            }
        } catch (IOException e) {
            System.err.println("Error receiving updated time: " + e.getMessage());
        }
    }

    private void broadcastCurrentTime() {
        try {
            InetAddress serverAddress = InetAddress.getLocalHost();
            byte[] timeData = String.valueOf(currentMoment).getBytes();
            DatagramPacket timePacket = new DatagramPacket(timeData, timeData.length, serverAddress, 6000);
            System.out.printf("Worker %d broadcasting time: %d%n", workerId, currentMoment);
            timeSocket.send(timePacket);
        } catch (IOException e) {
            System.err.println("Error broadcasting time: " + e.getMessage());
        }
    }
}

class TimeKeeper {
    private int currentTime;
    private String identifier;

    public TimeKeeper(int currentTime, String identifier) {
        this.currentTime = currentTime;
        this.identifier = identifier;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public void adjustTime(int newTime) {
        this.currentTime = newTime;
    }
}
