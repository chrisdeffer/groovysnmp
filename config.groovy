// config.rules
/***********************************************************************************************************************************************
 // Simple config that specifies trap host ip/port, blacklisted oid’s, Log4j properties
// config.rules
blacklist {
 oids = ["1.3.6.1.6.3.1.1.4.1.0", "1.3.6.1.2.1.15.7.0.1","1.3.6.1.2.1.1.3.0"]
}
log4j {
//
        appender.stdout = "org.apache.log4j.ConsoleAppender"
        appender."stdout.layout"="org.apache.log4j.PatternLayout"
//
        appender.scrlog = "org.apache.log4j.DailyRollingFileAppender"
        appender."scrlog.DatePattern"="'.'yyyy-MM-dd"
        appender."scrlog.Append"="true"
        appender."scrlog.File"="snmp_forwarder.log"
        appender."scrlog.layout"="org.apache.log4j.PatternLayout"
        appender."scrlog.layout.ConversionPattern"="%d %5p %c{1}:%L - %m%n"
        rootLogger="debug,scrlog,stdout"
}

hostcfg {
        forwardhost = "54.173.191.8"
        forwardport = "1162"
}
