package com.euronextclone.fix.client;

import com.euronextclone.fix.FixAdapter;
import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.fix42.ExecutionReport;
import quickfix.fix42.NewOrderSingle;


/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 7/25/12
 * Time: 8:28 PM
 */
public class FixClient extends FixAdapter {

    private static Logger logger = LoggerFactory.getLogger(FixClient.class);
    private final SocketInitiator socketInitiator;
    private SessionID sessionId;
    private EventHandler<ExecutionReport> executionReportEventHandler;

    public FixClient(final SessionSettings settings) throws ConfigError {

        MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
        SLF4JLogFactory logFactory = new SLF4JLogFactory(settings);
        MessageFactory messageFactory = new DefaultMessageFactory();
        socketInitiator = new SocketInitiator(this, messageStoreFactory, settings, logFactory, messageFactory);
    }

    @Override
    public void onCreate(SessionID sessionId) {
        logger.info("Session created: {}", sessionId);

        this.sessionId = sessionId;
        Session.lookupSession(sessionId).logon();
    }

    @Override
    public void onMessage(ExecutionReport message, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        try {
            executionReportEventHandler.onEvent(message, 0L, true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void start() throws ConfigError {
        socketInitiator.start();
    }

    public void stop() {
        socketInitiator.stop();
    }

    public void submitOrder(NewOrderSingle order) throws SessionNotFound {
        Session.sendToTarget(order, sessionId);
    }

    public void handleExecutions(EventHandler<ExecutionReport> handler) {
        executionReportEventHandler = handler;
    }
}
