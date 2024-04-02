package org.example;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;


public class mainWindow extends JFrame {
    private JComboBox<String> clientTypeComboBox;
    private JTextField nameTextField;
    private JTextField addressTextField;
    private JTextField emailTextField;
    private JTextField additionalField1TextField;
    private JTextField additionalField2TextField;
    private JLabel additionalField1Label;
    private JLabel additionalField2Label;

    private ArrayList<Client> clients;
    private DefaultTableModel tableModel;
    private JTable clientTable;
    private JPanel panel1;

    // Змінні для зберігання додаткових полів при редагуванні
    private String editAdditionalField1;
    private String editAdditionalField2;
    private int editedRowIndex = -1;

    public mainWindow() {
        setTitle("Add Client");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        JLabel clientTypeLabel = new JLabel("Client Type:");
        clientTypeComboBox = new JComboBox<>(new String[]{"Retail", "Wholesale", "Partner"});
        JLabel nameLabel = new JLabel("Name:");
        nameTextField = new JTextField();
        JLabel addressLabel = new JLabel("Address:");
        addressTextField = new JTextField();
        JLabel emailLabel = new JLabel("Email:");
        emailTextField = new JTextField();
        additionalField1Label = new JLabel();
        additionalField1TextField = new JTextField();
        additionalField2Label = new JLabel();
        additionalField2TextField = new JTextField();

        clientTypeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedType = (String) clientTypeComboBox.getSelectedItem();
                switch (selectedType) {
                    case "Retail":
                        additionalField1Label.setText("Total Cost:");
                        additionalField2Label.setText("Tax Percentage (%):");
                        break;
                    case "Wholesale":
                        additionalField1Label.setText("Total Cost:");
                        additionalField2Label.setText("Discount Rate (%):");
                        break;
                    case "Partner":
                        additionalField1Label.setText("Service Fee:");
                        additionalField2Label.setText("");
                        break;
                }
            }
        });

        inputPanel.add(clientTypeLabel);
        inputPanel.add(clientTypeComboBox);
        inputPanel.add(nameLabel);
        inputPanel.add(nameTextField);
        inputPanel.add(addressLabel);
        inputPanel.add(addressTextField);
        inputPanel.add(emailLabel);
        inputPanel.add(emailTextField);
        inputPanel.add(additionalField1Label);
        inputPanel.add(additionalField1TextField);
        inputPanel.add(additionalField2Label);
        inputPanel.add(additionalField2TextField);

        panel.add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4));
        JButton addButton = new JButton("Add Client");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addClient();
            }
        });

        JButton editButton = new JButton("Edit Client");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editClient();
            }
        });

        JButton deleteButton = new JButton("Delete Client");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteClient();
            }
        });

        JButton calculateButton = new JButton("Calculate Cost");
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateCost();
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(calculateButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        tableModel = new DefaultTableModel();
        tableModel.addColumn("Name");
        tableModel.addColumn("Client Type");
        tableModel.addColumn("Address");
        tableModel.addColumn("Email");
        tableModel.addColumn("Total cost");

        // Додати стовпці для додаткових полів, але не відображати їх у таблиці


        clientTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(clientTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        add(panel);
        setVisible(true);

        clients = new ArrayList<>();
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(JLabel.CENTER);
        clientTable.setDefaultRenderer(Object.class, renderer);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveClientsToFile();
            }
        });

        // Завантажити клієнтів з файлу при запуску вікна
        loadClientsFromFile();
    }
    private void saveClientsToFile() {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("clients.txt"))) {
            outputStream.writeObject(clients);
            System.out.println(clients);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void loadClientsFromFile() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("clients.txt"))) {
            clients = (ArrayList<Client>) inputStream.readObject();
            for (Client client : clients) {
                String clientType = "";
                if (client instanceof RetailClient) {
                    clientType = "Retail";
                } else if (client instanceof WholesaleClient) {
                    clientType = "Wholesale";
                } else if (client instanceof PartnerCompany) {
                    clientType = "Partner";
                }
                Object[] rowData = {client.getName(), clientType, client.getAddress(), client.getEmail(), ""};
                tableModel.addRow(rowData);
            }
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private void addClient() {
        String clientType = (String) clientTypeComboBox.getSelectedItem();
        String name = nameTextField.getText();
        String address = addressTextField.getText();
        String email = emailTextField.getText();
        String additionalField1 = additionalField1TextField.getText();
        String additionalField2 = additionalField2TextField.getText();

        // Зберігати значення додаткових полів для майбутнього використання
        this.editAdditionalField1 = additionalField1;
        this.editAdditionalField2 = additionalField2;

        Client client = null;
        switch (clientType) {
            case "Retail":
                double totalCost = Double.parseDouble(additionalField1);
                double taxPercentage = Double.parseDouble(additionalField2);
                client = new RetailClient(name, address, email, totalCost, taxPercentage);
                break;
            case "Wholesale":
                double wholesaleTotalCost = Double.parseDouble(additionalField1);
                double discountRate = Double.parseDouble(additionalField2);
                client = new WholesaleClient(name, address, email, wholesaleTotalCost, discountRate);
                break;
            case "Partner":
                double serviceFee = Double.parseDouble(additionalField1);
                client = new PartnerCompany(name, address, email, serviceFee);
                break;
        }

        clients.add(client);
        Object[] rowData = {client.getName(), clientType, client.getAddress(), client.getEmail(), ""};
        tableModel.addRow(rowData);

        clearFields();
    }

    private void editClient() {
        int rowIndex = clientTable.getSelectedRow();
        if (rowIndex != -1) {
            // Отримати дані клієнта з обраного рядка
            String clientType = (String) clientTable.getValueAt(rowIndex, 1);
            String name = (String) clientTable.getValueAt(rowIndex, 0);
            String address = (String) clientTable.getValueAt(rowIndex, 2);
            String email = (String) clientTable.getValueAt(rowIndex, 3);

            // Отримати значення додаткових полів з відповідних стовпців у таблиці

            // Встановити значення в поля введення
            nameTextField.setText(name);
            addressTextField.setText(address);
            emailTextField.setText(email);
            clientTypeComboBox.setSelectedItem(clientType);

            // Зберегти індекс редагованого рядка для подальшого використання
            this.editedRowIndex = rowIndex;

            // Видалити рядок після редагування
            tableModel.removeRow(rowIndex);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a client to edit.");
        }
    }

    private void deleteClient() {
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow != -1) {
            // Видалити клієнта зі списку
            tableModel.removeRow(selectedRow);
            clients.remove(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a client to delete.");
        }
    }

    private void calculateCost() {
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow != -1) {
            Client client = clients.get(selectedRow);
            double cost = client.calculateCost();
            // Оновити стовпець "Total Cost" у таблиці з обчисленим значенням
            tableModel.setValueAt(cost, selectedRow, 4);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a client to calculate cost.");
        }
    }

    private void clearFields() {
        nameTextField.setText("");
        addressTextField.setText("");
        emailTextField.setText("");
        additionalField1TextField.setText("");
        additionalField2TextField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new mainWindow();
            }
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
