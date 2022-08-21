module twitter4j {
    requires java.management;
    requires java.logging;
    requires org.slf4j;
    requires org.apache.logging.log4j;
    exports twitter4j;
    exports twitter4j.api;
    exports twitter4j.auth;
    exports twitter4j.conf;
    exports twitter4j.util;
}