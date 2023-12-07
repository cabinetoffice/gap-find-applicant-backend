package gov.cabinetoffice.gap.applybackend.testingextensions;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.read.ListAppender;
import ch.qos.logback.core.spi.FilterReply;

import java.util.List;
import java.util.stream.Collectors;

public class ErrorLogCapture {

    private ListAppender<ILoggingEvent> listAppender = new ListAppender<>();

    ErrorLogCapture() {
    }

    public String getFirstFormattedMessage() {
        return getFormattedMessageAt(0);
    }

    public String getLastFormattedMessage() {
        return getFormattedMessageAt(listAppender.list.size() - 1);
    }

    public String getFormattedMessageAt(int index) {
        return getLoggingEventAt(index).getFormattedMessage();
    }

    public LoggingEvent getLoggingEvent() {
        return getLoggingEventAt(0);
    }

    public LoggingEvent getLoggingEventAt(int index) {
        return (LoggingEvent) listAppender.list.get(index);
    }

    public List<LoggingEvent> getLoggingEvents() {
        return listAppender.list.stream().map(e -> (LoggingEvent) e).collect(Collectors.toList());
    }

    public void setLogFilter(Level logLevel) {
        listAppender.clearAllFilters();
        listAppender.addFilter(buildLevelFilter(logLevel));
    }

    public void clear() {
        listAppender.list.clear();
    }

    void start() {
        setLogFilter(Level.ERROR);
        listAppender.start();
    }

    void stop() {
        if (listAppender == null) {
            return;
        }

        listAppender.stop();
        listAppender.list.clear();
        listAppender = null;
    }

    ListAppender<ILoggingEvent> getListAppender() {
        return listAppender;
    }

    private Filter<ILoggingEvent> buildLevelFilter(Level logLevel) {
        LevelFilter levelFilter = new LevelFilter();
        levelFilter.setLevel(logLevel);
        levelFilter.setOnMismatch(FilterReply.DENY);
        levelFilter.start();

        return levelFilter;
    }

}