import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TimeSyncClient {

    public static void main(String[] args) throws IOException {
        final int baseTime = Settings.initialClientTime;
        final int timeStep = Settings.timeStep;
        final int clientPortBase = Settings.clientPortBase;
        final int clientCount = Settings.clientCount;

        List<DatagramSocket> clientSockets = createClientSockets(clientPortBase, clientCount);

        int currentTime = baseTime;
        for (int i = 0; i < clientCount; i++) {
            new TimeClientWorker(i, clientSockets.get(i), currentTime).start();
            currentTime += timeStep;
        }
    }

    private static List<DatagramSocket> createClientSockets(int portBase, int count) throws SocketException {
        return IntStream.range(portBase, portBase + count)
                .mapToObj(port -> {
                    try {
                        return new DatagramSocket(port);
                    } catch (SocketException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }
}

