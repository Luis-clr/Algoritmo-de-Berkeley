public class Timekeeper extends Thread {
    private volatile int moment;
    private final String entity;
    private final int incrementInterval;
    private final int incrementStep;
    private boolean running = true;

    public Timekeeper(int startingPoint, String entity, int incrementInterval, int incrementStep) {
        this.moment = startingPoint;
        this.entity = entity;
        this.incrementInterval = incrementInterval; // Tempo em milissegundos entre incrementos
        this.incrementStep = incrementStep; // Quantos 'momentos' são adicionados a cada incremento
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(incrementInterval);
                incrementMoment();
            } catch (InterruptedException e) {
                System.out.println(entity + " - Interrupted.");
                running = false; // Encerra o loop se a thread for interrompida
            }
        }
    }

    private synchronized void incrementMoment() {
        this.moment += incrementStep;
        System.out.println(entity + " - " + this.moment + " units");
    }

    public synchronized int getCurrentMoment() {
        return this.moment;
    }

    public synchronized void adjustMoment(int newMoment) {
        this.moment = newMoment;
    }

    public void halt() {
        running = false;
        this.interrupt(); // Interrompe o sleep para sair do loop mais rapidamente
    }
}
