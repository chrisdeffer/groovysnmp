//@Grab('log4j:log4j:1.2.17')
import org.snmp4j.CommunityTarget
import org.snmp4j.PDU;
import org.snmp4j.PDUv1
import org.snmp4j.Snmp
import org.snmp4j.mp.SnmpConstants
import org.snmp4j.smi.IpAddress
import org.snmp4j.smi.OID
import org.snmp4j.smi.OctetString
import org.snmp4j.smi.UdpAddress
import org.snmp4j.transport.AbstractTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping
import org.apache.log4j.*
import groovy.util.logging.*

@Log4j
class Forwarder {


    def v1Trap(PDUv1 myPdu) {
        def config = new ConfigSlurper().parse(new File('config.rules').toURL())
        PropertyConfigurator.configure(config.toProperties())
        log.debug "Forwarder received:"
        log.debug "${myPdu.toString()}"


        String Oid = myPdu.getEnterprise()
        def varbinds = myPdu.getVariableBindings()
        log.debug "Forwarder processing Incoming oid: ${Oid}"
        log.debug "Forwarder recieved varbinds: "
        varbinds.each { vb ->
                log.debug "${vb.toString()}"
        }
        def list = config.blacklist.oids
        if(list.contains(Oid)) {
                log.info "OID: ${Oid} in in blacklist, discarding"
        } else {
        }

        String ipAddress = config.hostcfg.forwardhost
        String community = "public"
        def port = config.hostcfg.forwardport
        // Incoming Trap:
        // V1TRAP[reqestID=0,timestamp=0:00:00.00,enterprise=1.3.6.1.2.1.15.7.0.1,genericTrap=6,specificTrap=0, VBS[1.3.6.1.2.1.15.7.0.1.0.23 = This is Test Value]]
        try {
            // Create Transport Mapping
            def transport = new DefaultUdpTransportMapping();
            transport.listen();
            // Create Target
            def cTarget = new CommunityTarget();
            cTarget.setCommunity(new OctetString(community));
            cTarget.setVersion(SnmpConstants.version1);
            cTarget.setAddress(new UdpAddress(ipAddress + "/" + port));
            cTarget.setTimeout(5000);
            cTarget.setRetries(2);
            // Send the PDU
            def snmp = new Snmp(transport);
            System.out.println("Forwarding Trap to ${ipAddress}:${port}");
            //snmp.send(pdu, cTarget);
            snmp.send(myPdu,cTarget)
            snmp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 void sendTrapPost(trap) {
        def vb = trap.getProperties()
        vb.each { println it }
        myURL = new URL("https://sungardasdev.service-now.com/incident.do?SOAP")
        conn = soapURL.openConnection()
        // setRequestProperty -> put the cookie in from the previous session in here
        conn.setRequestProperty("Cookie","JSESSIONID=92C7FDA5F0A65E20EA1A5DC77830BE62")
        //String encoding = new sun.misc.BASE64Encoder().encode("soap.user:soa123".getBytes());
        //conn.setRequestProperty ("Authorization", "Basic " + encoding);
        conn.setRequestMethod("POST")
        conn.setRequestProperty("Content-Type", "application/xml")
        /*
        conn.getRequestProperties().each() {
            println "Property: "+it
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

