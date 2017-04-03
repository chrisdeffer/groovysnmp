// TrapReceiver
/********************************************************************************************************************************************
@Grapes(
        @Grab(group='org.snmp4j', module='snmp4j', version='1.10.1')
)

import java.io.IOException
import org.snmp4j.CommandResponder
import org.snmp4j.CommandResponderEvent
import org.snmp4j.CommunityTarget
import org.snmp4j.MessageDispatcher
import org.snmp4j.MessageDispatcherImpl
import org.snmp4j.MessageException
import org.snmp4j.PDU
import org.snmp4j.Snmp
import org.snmp4j.mp.MPv1
import org.snmp4j.mp.MPv2c
import org.snmp4j.mp.StateReference
import org.snmp4j.mp.StatusInformation
import org.snmp4j.security.Priv3DES
import org.snmp4j.security.SecurityProtocols
import org.snmp4j.smi.OctetString
import org.snmp4j.smi.TcpAddress
import org.snmp4j.smi.TransportIpAddress
import org.snmp4j.smi.UdpAddress
import org.snmp4j.transport.AbstractTransportMapping
import org.snmp4j.transport.DefaultTcpTransportMapping
import org.snmp4j.transport.DefaultUdpTransportMapping
import org.snmp4j.util.MultiThreadedMessageDispatcher
import org.snmp4j.util.ThreadPool
import org.apache.log4j.*
import groovy.util.logging.*

@Log4j
class TrapReceiver implements CommandResponder {

    public synchronized void listen(TransportIpAddress address) throws IOException {
        def config = new ConfigSlurper().parse(new File('config.rules').toURL())
        PropertyConfigurator.configure(config.toProperties())
        log.debug "TrapReceiver Started"
        AbstractTransportMapping transport
        if (address instanceof TcpAddress) {
            transport = new DefaultTcpTransportMapping((TcpAddress) address)
        } else {
            transport = new DefaultUdpTransportMapping((UdpAddress) address)
        }

        ThreadPool threadPool = ThreadPool.create("DispatcherPool", 10)
        MessageDispatcher mDispathcher = new MultiThreadedMessageDispatcher(
                threadPool, new MessageDispatcherImpl())

        // add message processing models
        mDispathcher.addMessageProcessingModel(new MPv1())
        mDispathcher.addMessageProcessingModel(new MPv2c())

        // add all security protocols
        SecurityProtocols.getInstance().addDefaultProtocols()
        SecurityProtocols.getInstance().addPrivacyProtocol(new Priv3DES())
        // Create Target
        CommunityTarget target = new CommunityTarget()
        target.setCommunity(new OctetString("public"))

        Snmp snmp = new Snmp(mDispathcher, transport)
        snmp.addCommandResponder(this)

        transport.listen()
        System.out.println("Listening on " + address)
  try {
            this.wait()
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt()
        }
    }

    public synchronized void processPdu(CommandResponderEvent cmdRespEvent) {
        //public synchronized void processPdu(CommandResponderEvent cmdRespEvent) {
        System.out.println("Received PDU...")
        PDU pdu = cmdRespEvent.getPDU()
        if (pdu != null) {
            println "RECIEVED PDU: "
            println pdu.toString()
            def mytype = pdu.getType()
            println "Trap Type is: ${mytype}"
            switch(mytype) {
                case "-92":
                    def forward_trap = new Forwarder()
                    forward_trap.v1Trap(pdu)
                    break
                case "-89":
                    def forward_trap = new ForwarderV2()
                    forward_trap.v2Trap(pdu)
                    break
                default:
                    println "no trap"
            }
        }
    }
}

