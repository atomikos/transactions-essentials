package jms;

import java.io.IOException;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;

import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.atomikos.jms.extra.MessageDrivenContainer;
import com.atomikos.jms.extra.SingleThreadedJmsSenderTemplate;

public class JmsIntegrationTest {


	private static String BROKER_URL = "vm://localhost";

	@BeforeClass
	public static void startBroker() throws IOException, Exception {

		BrokerService broker = new BrokerService();
		broker.setUseJmx(false);
		broker.setPersistent(false);
		// configure the broker
		broker.addConnector(BROKER_URL);

		broker.start();

	}
	String textMessage = "Hello world !!!";
	@Before
	public void sendMessage() throws Exception {


		// create a reusable sender session
		SingleThreadedJmsSenderTemplate senderSession = JMSUtil
				.createSenderTemplate(BROKER_URL, "MyQueue");

		// our sender session is self-maintaining, so the following
		// can be repeated as many times as you like
		JMSUtil.sendMessageInTransaction(textMessage, senderSession);
		System.out.println("Sent message with text: " + textMessage);

		// when finished: close the sender session
		senderSession.close();
	}

	boolean messageReceived=false;
	@Test
	public void receiveMessage() throws JMSException, InterruptedException {

		MessageDrivenContainer container = JMSUtil
				.createMessageDrivenContainer(BROKER_URL,
						"MyQueue");


		container.setMessageListener(new MessageListener() {

			public void onMessage(Message msg) {
				// here we are if a message is received; a transaction
				// as been started before this method has been called.
				// this is done for us by the MessageDrivenContainer...

				if (msg instanceof TextMessage) {
					messageReceived=true;
					TextMessage tmsg = (TextMessage) msg;
					try {
						System.out.println("Transactional receive of message: "
								+ tmsg.getText());
						Assert.assertEquals(textMessage, tmsg.getText());
					} catch (JMSException error) {

						error.printStackTrace();
						// throw runtime exception to force rollback of
						// transaction
						throw new RuntimeException("Rollback due to error");
					}

				} else {
					// not a text message
					System.out.println("Transactional receive of message: "
							+ msg);
				}

			}
		});
		container.start();

		// here, we are certain that the server is listening
		System.out.println("Listening for incoming messages...");
		//wait a bit for the broker to receive message...
		Thread.sleep(1000);
		Assert.assertTrue(messageReceived);
		container.stop();
	}
	
	

}
