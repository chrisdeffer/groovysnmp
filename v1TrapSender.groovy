@Grapes (
        @Grab(group='org.snmp4j', module='snmp4j', version='1.10.1')
)


import org.snmp4j.CommunityTarget
import org.snmp4j.PDU
import org.snmp4j.PDUv1
import org.snmp4j.Snmp
import org.snmp4j.mp.SnmpConstants
import org.snmp4j.smi.IpAddress
import org.snmp4j.smi.OID
import org.snmp4j.smi.OctetString
import org.snmp4j.smi.UdpAddress
import org.snmp4j.transport.DefaultUdpTransportMapping

/**
 * Created by munson.chris on 5/22/2015.
 */

def v1Trap = {
    String community = "public";
        // Sending Trap for sysLocation of RFC1213
    String Oid = ".1.3.6.1.2.1.1.8";
        //IP of Local Host
    String ipAddress = "127.0.0.1";
        //Ideally Port 162 should be used to send receive Trap, any other available Port can be used
    port = 1162;
    try {
        // Create Transport Mapping
        transport = new DefaultUdpTransportMapping();
        transport.listen();

        // Create Target
        cTarget = new CommunityTarget();
        cTarget.setCommunity(new OctetString(community));
        cTarget.setVersion(SnmpConstants.version1);
        cTarget.setAddress(new UdpAddress(ipAddress + "/" + port));
        cTarget.setTimeout(5000);
        cTarget.setRetries(2);

        pdu = new PDUv1();
        pdu.setType(PDU.V1TRAP);
        pdu.setEnterprise(new OID(Oid));
        pdu.setGenericTrap(PDUv1.ENTERPRISE_SPECIFIC);
        pdu.setSpecificTrap(1);
        pdu.setAgentAddress(new IpAddress(ipAddress));

        // Send the PDU
        snmp = new Snmp(transport);
        System.out.println("V1 Trap sent");
        //60.times {
        snmp.send(pdu, cTarget);
        //}
        snmp.close();
    } catch (Exception e) {
        e.printStackTrace();
    }

}

v1Trap.call()

