// ForwarderV2.groovy
import org.snmp4j.CommunityTarget
import org.snmp4j.PDU
import org.snmp4j.PDU
import org.snmp4j.Snmp
import org.snmp4j.mp.SnmpConstants
import org.snmp4j.smi.IpAddress
import org.snmp4j.smi.OID
import org.snmp4j.smi.OctetString
import org.snmp4j.smi.UdpAddress
import org.snmp4j.transport.AbstractTransportMapping
import org.snmp4j.transport.DefaultUdpTransportMapping
import org.apache.log4j.*
import groovy.util.logging.*

@Log4j
class ForwarderV2 {

    def v2Trap(PDU myPdu) {
        def config = new ConfigSlurper().parse(new File('config.rules').toURL())
        PropertyConfigurator.configure(config.toProperties())
        log.debug "Forwarder received:"
        log.debug "${myPdu.toString()}"
        // Incoming Trap:
        /*
        TRAP[requestID=1750095147, errorStatus=Success(0), errorIndex=0, VBS[1.3.6.1.2.1.1.3.0 = Tue May 26 21:04:32 UTC 2015 1.3.6.1.6.3.1.1.4.1.0 = 1.3.6.1.2.1.1.8 1.3.6.1.6.3.18.1.3.0 = 192.168.2.5 1.3.6.1.2.1.1.8 = Major]]
        Trap Type is: -89
        */
        String ipAddress = config.hostcfg.forwardhost
        String community = "public"
        def port = config.hostcfg.forwardport

        try {
            // Create Transport Mapping
            def transport = new DefaultUdpTransportMapping()
            transport.listen()
            // Create Target
            def cTarget = new CommunityTarget()
            cTarget.setCommunity(new OctetString(community))
            cTarget.setVersion(SnmpConstants.version2c)
            cTarget.setAddress(new UdpAddress(ipAddress + "/" + port))
            cTarget.setTimeout(5000)
            cTarget.setRetries(2)
            // Send the PDU
            def snmp = new Snmp(transport)
            log.debug "Forwarding Trap to ${ipAddress}:${port}"
            snmp.send(myPdu,cTarget)
            snmp.close()
        } catch (Exception e) {
            e.printStackTrace()
        }
    }
 void sendTrapPost(trap) {
        def vb = trap.getProperties()
        vb.each { println it }
        myURL = new URL("https://sungardasdev.service-now.com/incident.do?SOAP")
        conn = soapURL.openConnection()
        // setRequestProperty -> put the cookie in from the previous session in here
        conn.setRequestProperty("Cookie","JSESSIONID=92C7FDA5F0A65E20EA1A5DC77830BE62")
        //String encoding = new sun.misc.BASE64Encoder().encode("soap.user:soa123".getBytes())
        //conn.setRequestProperty ("Authorization", "Basic " + encoding)
        conn.setRequestMethod("POST")
        conn.setRequestProperty("Content-Type", "application/xml")
        /*
        conn.getRequestProperties().each() {
            log.debug "Property: "+it

   }
        */
        conn.doOutput = true
        conn.headerFields.each { println it }

    }

    void sendTrapSocket() {

    }

    void sendTrapRest() {

    }

    void sendSnmpTrap() {

    }



}

