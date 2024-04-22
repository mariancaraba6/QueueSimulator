package Model;

import Business.Logic.SimulationManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable{
    private BlockingQueue<Task> tasks;

    private AtomicInteger waitingPeriod;
    private int serverTime=0;

    public Server() {
        this.tasks = new LinkedBlockingQueue<>();
        this.waitingPeriod = new AtomicInteger();
    }
    public void addTask(Task newTask) {
        tasks.add(newTask);
        waitingPeriod.addAndGet(newTask.getServiceTime());
    }

    public void setWaitingPeriod(int waitingPeriod) {
        this.waitingPeriod.set(waitingPeriod);
    }

    public void run() {
        while (!SimulationManager.end.get()) {
                if(this.serverTime==SimulationManager.getCurrentTime() && SimulationManager.getPrintFlag().getCount()!=0) {
                    decrementServiceTime();
                    SimulationManager.getPrintFlag().countDown();
                    serverTime++;
                }
        }
    }

    public AtomicInteger getWaitingPeriod() {
        return waitingPeriod;
    }

    public int countTasks() {
        return tasks.size();
    }

    public BlockingQueue<Task> getTasks() {
        return tasks;
    }

    public void decrementServiceTime() {
        if(!tasks.isEmpty()) {
            waitingPeriod.getAndDecrement();
            Task head=tasks.peek();
            assert head != null;
            head.decrementServiceTime();
            if(head.getServiceTime() == 0){
                tasks.poll();
            }
        }
    }
    public String toString(int index) {
        String query = new String("Query " + index + ": ");
        if(tasks.isEmpty()) {
            query += "closed";
        } else {
            for(Task currentTask: tasks) {
                query += currentTask.toString() + "; ";
            }
        }
        query += "\n";
        return query;
    }

}