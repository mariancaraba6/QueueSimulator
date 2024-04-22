package Business.Logic;

import Model.Policies;
import Model.Task;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulationManager implements Runnable {
    public int numberOfClients;
    public int numberOfServers;
    public int timeLimit;
    public int minArrivalTime;
    public int maxArrivalTime;
    public int maxProcessingTime;
    public int minProcessingTime;
    public Policies.SelectionPolicy selectionPolicy;
    public String output;
    private Scheduler scheduler;
    private List<Task> generatedTasks;
    private float averageServiceTime;
    private float averageWaitingTime;
    private int peakHour = 0;
    private int peakNrClients = 0;
    private static CountDownLatch printFlag = new CountDownLatch(0);
    public static AtomicBoolean end = new AtomicBoolean(false);
    private static AtomicInteger currentTime =new AtomicInteger(0);

    public static int getCurrentTime() {
        return currentTime.get();
    }

    public static CountDownLatch getPrintFlag()
    {
        return printFlag;
    }

    public static void setPrintFlag(int q)
    {
        SimulationManager.printFlag = new CountDownLatch(q);
    }

    /**
     * @param numberOfClients
     * @param numberOfServers
     * @param timeLimit
     * @param minArrivalTime
     * @param maxArrivalTime
     * @param minProcessingTime
     * @param maxProcessingTime
     * @param selectionPolicy
     */
    public SimulationManager(int numberOfClients, int numberOfServers, int timeLimit, int minArrivalTime, int maxArrivalTime, int minProcessingTime, int maxProcessingTime, Policies.SelectionPolicy selectionPolicy) {
        this.numberOfClients = numberOfClients;
        this.numberOfServers = numberOfServers;
        this.timeLimit = timeLimit;
        this.minArrivalTime = minArrivalTime;
        this.maxArrivalTime = maxArrivalTime;
        this.maxProcessingTime = maxProcessingTime;
        this.minProcessingTime = minProcessingTime;
        this.selectionPolicy = selectionPolicy;
        this.scheduler = new Scheduler(numberOfServers);
        scheduler.changeStrategy(selectionPolicy);
        generateNRandomTasks();
    }

    private void generateNRandomTasks() {
        generatedTasks = new LinkedList<>();
        Random random = new Random();
        for (int i = 0; i < numberOfClients; i++) {
            int newArrivalTime = random.nextInt(maxArrivalTime - minArrivalTime+1) + minArrivalTime;
            int newServiceTime = random.nextInt(maxProcessingTime - minProcessingTime+1) + minProcessingTime;
            Task newTask = new Task(i + 1, newArrivalTime, newServiceTime);
            generatedTasks.add(newTask);
            averageServiceTime += newServiceTime;
        }
        averageServiceTime/=numberOfClients;
        Collections.sort(generatedTasks);
    }

    public String printTasks() {
        String waitingClients = new String();
        for (Task task : generatedTasks) {
            waitingClients += task.toString();
            waitingClients += "; ";
        }
        return waitingClients;
    }

    public void run() {
        output = new String();
        for(Thread thread : scheduler.getThreads()) {
            thread.start();
        }
        while (currentTime.get() < timeLimit && (!generatedTasks.isEmpty() || scheduler.numberOfClientsInLine() != 0)) {
            setPrintFlag(numberOfServers);
            while(printFlag.getCount()!=0)
            {
                try{
                    Thread.sleep(10);
                } catch (InterruptedException e)
                {
                    System.out.println(e.getMessage());
                }
            }
            if (!generatedTasks.isEmpty()) {
                Task currentTask = generatedTasks.getFirst();
                while (!generatedTasks.isEmpty() && currentTask.getArrivalTime() == currentTime.get()) {
                    averageWaitingTime += scheduler.dispatchTasks(currentTask);
                    generatedTasks.removeFirst();
                    if (!generatedTasks.isEmpty()) {
                        currentTask = generatedTasks.getFirst();
                    }
                }
                int currentNrOfClients = scheduler.numberOfClientsInLine();
                if (currentNrOfClients > peakNrClients) {
                    peakNrClients = currentNrOfClients;
                    peakHour = currentTime.get();
                }
            }
            output += "Time " + currentTime.get() + '\n';
            output += "Waiting clients: " + printTasks() + '\n';
            output += scheduler.toString() + '\n';
            currentTime.getAndIncrement();
        }
        averageWaitingTime/=numberOfClients;
        output += "Average waiting time: " + averageWaitingTime + "\nAverage service time: " +
                averageServiceTime + "\nPeak hour: " + peakHour + "\n";
        end.set(true);
    }

    public static void main(String[] args) {
        SimulationManager manager = new SimulationManager(5, 1,
                60, 2, 20, 20,
                30, Policies.SelectionPolicy.SHORTEST_TIME);
        Thread mainThread = new Thread(manager);
        mainThread.start();

        try {
            mainThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(manager.output);
    }
}