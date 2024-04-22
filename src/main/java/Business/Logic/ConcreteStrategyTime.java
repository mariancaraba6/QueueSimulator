package Business.Logic;

import Model.Server;
import Model.Task;

import java.util.List;

public class ConcreteStrategyTime implements Strategy{
    public int addTask(List<Server> servers, Task t) {
        Server serverForTask = servers.get(0);
        int indexServerForTask = 0;
        int currentIndex = 0;
        for(Server iteratorServer: servers) {
            if(iteratorServer.getWaitingPeriod().get() < serverForTask.getWaitingPeriod().get()) {
                serverForTask = iteratorServer;
                indexServerForTask = currentIndex;
            }
            currentIndex++;
        }
        serverForTask.addTask(t);
        return indexServerForTask;
    }
}
