package Business.Logic;

import Model.Policies;
import Model.Server;
import Model.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Scheduler {
    private List<Server> servers;
    private List<Thread> threads;
    private int maxNoServers;
    private Strategy strategy;

    public Scheduler(int maxNoServers) {
        this.servers = new LinkedList<>();
        this.maxNoServers = maxNoServers;
        threads = new ArrayList<>();
        for(int i=0; i<maxNoServers; i++) {
            Server newServer = new Server();
            servers.add(newServer);
            Thread newThread = new Thread(newServer);
            threads.add(newThread);
        }
    }

    public void changeStrategy (Policies.SelectionPolicy policy) {
        if(policy == Policies.SelectionPolicy.SHORTEST_QUEUE) {
            strategy = new ConcreteStrategyQueue();
        } else if(policy == Policies.SelectionPolicy.SHORTEST_TIME) {
            strategy = new ConcreteStrategyQueue();
        }
    }

    public List<Thread> getThreads() {
        return threads;
    }

    public int numberOfClientsInLine() {
        int counter = 0;
        for(Server iterator: servers) {
            counter+=iterator.getTasks().size();
        }
        return counter;
    }

    /**
     * @param t
     * @return updated waiting period
     */
    public int dispatchTasks(Task t) {
        int serverNumber = strategy.addTask(servers, t);
        AtomicInteger waitingPeriod = servers.get(serverNumber).getWaitingPeriod();
        servers.get(serverNumber).setWaitingPeriod(waitingPeriod.get() + t.getServiceTime());
        return waitingPeriod.get() + t.getServiceTime();
    }


    public String toString() {
        int index = 1;
        String newString = new String();
        for(Server iterator: servers) {
            newString += iterator.toString(index);
            index++;
        }
        return newString;
    }
}
