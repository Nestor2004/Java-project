package org.example;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.io.Serializable;

abstract class Client implements Serializable  {
    private String name;
    private String address;
    private String email;

    public Client(String name, String address, String email) {
        this.name = name;
        this.address = address;
        this.email = email;
    }
    public Client(){}

    public String getName() {
        return name;
    }
    public String getAddress() {
        return address;
    }
    public String getEmail() {
        return email;
    }

    // Геттери та сеттери для адреси та електронної пошти (потрібно додати)

    // Абстрактний метод для розрахунку вартості послуг/товарів
    public abstract double calculateCost();
}

class RetailClient extends Client implements Serializable {
    public double totalCost;
    public double taxRate;
    public RetailClient(String name, String address, String email,Double totalCost, Double taxRate) {
        super(name, address, email);
        this.totalCost=totalCost;
        this.taxRate=taxRate;
    }

    @Override
    public double calculateCost() {
        return  totalCost * (1 + taxRate / 100); // Логіка для розрахунку вартості для роздрібного покупця
    }
}
 class WholesaleClient extends Client implements Serializable {
    public double totalCost;
    public double discountRate;
    public WholesaleClient(String name, String address, String email,Double totalCost,Double discoubtRate) {
        super(name, address, email);
        this.totalCost=totalCost;
        this.discountRate=discoubtRate;
    }
     public WholesaleClient() {
         super();
     }

    @Override
    public double calculateCost() {
        return totalCost*(1-discountRate); // Логіка для розрахунку вартості для оптового покупця
    }
}

class PartnerCompany extends Client implements Serializable {
    public double serviceFee;
    public PartnerCompany(String name, String address, String email,Double ServiceFee) {
        super(name, address, email);
        this.serviceFee=ServiceFee;
    }

    @Override
    public double calculateCost() {
        return serviceFee; // Логіка для розрахунку вартості для компанії-партнера
    }
}

class Company {
    private static Company instance;
    private String name;
    private ArrayList<Client> clients;

    private Company(String name) {
        this.name = name;
        this.clients = new ArrayList<>();
    }

    public static Company getInstance(String name) {
        if (instance == null) {
            instance = new Company(name);
        }
        return instance;
    }

    public void addClient(Client client) {
        clients.add(client);
    }

    public void removeClient(Client client) {
        clients.remove(client);
    }

    public ArrayList<Client> getClients() {
        return clients;
    }

    public String getName() {
        return name;
    }
}

public class ClientGUI {
    private JFrame frame;
    private JTextArea textArea;

    private Company company;

    public ClientGUI(Company company) {
        this.company = company;

        frame = new JFrame("Client Information System");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        textArea = new JTextArea(20, 40);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        JButton loadButton = new JButton("Load Clients");
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayClients();
            }
        });

        panel.add(loadButton, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        frame.add(panel);
        frame.setVisible(true);
    }

    private void displayClients() {
        StringBuilder builder = new StringBuilder();
        builder.append("Company: ").append(company.getName()).append("\n\n");
        ArrayList<Client> clients = company.getClients();
        for (Client client : clients) {
            builder.append("Client: ").append(client.getName()).append("\n");
            builder.append("Cost: ").append(client.calculateCost()).append("\n\n");
        }
        textArea.setText(builder.toString());
    }

    public static void main(String[] args) {
        Company company = Company.getInstance("Your Company Name");
        new ClientGUI(company);
    }
}
