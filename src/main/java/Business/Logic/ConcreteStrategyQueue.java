package Business.Logic;

import Model.Server;
import Model.Task;

import java.util.List;

public class ConcreteStrategyQueue implements Strategy{
    public int addTask(List<Server> servers, Task t) {
        Server serverForTask = servers.get(0);
        int indexServerForTask = 0;
        int currentIndex = 0;
        for(Server iteratorServer: servers) {
            if(iteratorServer.countTasks() < serverForTask.countTasks()) {
                serverForTask = iteratorServer;
                indexServerForTask = currentIndex;
            }
            currentIndex++;
        }
        serverForTask.addTask(t);
        return indexServerForTask;
    }
}
