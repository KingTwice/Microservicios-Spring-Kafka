package com.trxjster.accountcmd.infrastructure;

import com.trxjster.cqrscore.commands.BaseCommand;
import com.trxjster.cqrscore.commands.CommandHandlerMethod;
import com.trxjster.cqrscore.infrastructure.CommandDispatcher;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class AccountCommandDispatcher implements CommandDispatcher {

    private final Map<Class<? extends BaseCommand>, List<CommandHandlerMethod>> routes = new HashMap<>();

    @Override
    public <T extends BaseCommand> void registerHandler(Class<T> type, CommandHandlerMethod<T> handler) {
        var handlers = routes.computeIfAbsent(type, c -> new LinkedList<>());
        handlers.add(handler);
    }

    @Override
    public void send(BaseCommand command) {
        var handlers = routes.get(command.getClass());
        if(handlers == null || handlers.size() == 0){
            throw new RuntimeException("Command Handler was not registered.");
        }
        if(handlers.size() > 1)
            throw new RuntimeException("Cannot send a command with more than one handler.");

        handlers.get(0).handle(command);
    }
}
