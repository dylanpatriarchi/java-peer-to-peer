import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class index{
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageField;
    private DatagramSocket socket;
    private InetAddress receiverAddress;
    private int receiverPort;

    public index(int port) {
        try {
            receiverAddress = InetAddress.getLocalHost();
            receiverPort = port;
            socket = new DatagramSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame = new JFrame("Peer-to-Peer Chat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        messageField = new JTextField();
        frame.add(messageField, BorderLayout.SOUTH);

        messageField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage(messageField.getText());
            }
        });

        frame.setVisible(true);
        startListening();
    }

    private void sendMessage(String message) {
        try {
            byte[] sendData = message.getBytes();
            DatagramPacket packet = new DatagramPacket(sendData, sendData.length, receiverAddress, receiverPort);
            socket.send(packet);
            chatArea.append("You: " + message + "\n");
            messageField.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startListening() {
        Thread listenerThread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        byte[] receiveData = new byte[1024];
                        DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
                        socket.receive(packet);
                        String message = new String(packet.getData(), 0, packet.getLength());
                        chatArea.append("Friend: " + message + "\n");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        listenerThread.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new index(12345));
    }
}
