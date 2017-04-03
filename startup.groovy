@Grab(group='org.snmp4j', module='snmp4j', version='1.10.1')
import org.snmp4j.smi.UdpAddress

def listenaddress = "10.5.165.138"
int port = 1162
traphost = new TrapReceiver()
traphost.listen(new UdpAddress(listenaddress + "/" + port))
