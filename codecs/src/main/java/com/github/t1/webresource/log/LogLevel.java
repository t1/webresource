package com.github.t1.webresource.log;

import org.slf4j.Logger;

public enum LogLevel {
    OFF {
        @Override
        public boolean isEnabled(Logger logger) {
            return false;
        }

        @Override
        public void log(Logger logger, String message) {}

        @Override
        public void log(Logger logger, String message, Object... args) {}
    },
    TRACE {
        @Override
        public boolean isEnabled(Logger logger) {
            return logger.isTraceEnabled();
        }

        @Override
        public void log(Logger logger, String message) {
            logger.trace(message);
        }

        @Override
        public void log(Logger logger, String message, Object... args) {
            logger.trace(message, args);
        }
    },
    DEBUG {
        @Override
        public boolean isEnabled(Logger logger) {
            return logger.isDebugEnabled();
        }

        @Override
        public void log(Logger logger, String message) {
            logger.debug(message);
        }

        @Override
        public void log(Logger logger, String message, Object... args) {
            logger.debug(message, args);
        }
    },
    INFO {
        @Override
        public boolean isEnabled(Logger logger) {
            return logger.isInfoEnabled();
        }

        @Override
        public void log(Logger logger, String message) {
            logger.info(message);
        }

        @Override
        public void log(Logger logger, String message, Object... args) {
            logger.info(message, args);
        }
    },
    WARN {
        @Override
        public boolean isEnabled(Logger logger) {
            return logger.isWarnEnabled();
        }

        @Override
        public void log(Logger logger, String message) {
            logger.warn(message);
        }

        @Override
        public void log(Logger logger, String message, Object... args) {
            logger.warn(message, args);
        }
    },
    ERROR {
        @Override
        public boolean isEnabled(Logger logger) {
            return logger.isErrorEnabled();
        }

        @Override
        public void log(Logger logger, String message) {
            logger.error(message);
        }

        @Override
        public void log(Logger logger, String message, Object... args) {
            logger.error(message, args);
        }
    };

    public abstract boolean isEnabled(Logger logger);

    public abstract void log(Logger logger, String message);

    public abstract void log(Logger logger, String message, Object... args);
}
