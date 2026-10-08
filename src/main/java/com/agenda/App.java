package com.agenda;

import com.agenda.util.DatabaseConnection;
import com.agenda.view.LoginView;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class App {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> DatabaseConnection.getInstance().fecharConexao()));

        SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));
    }
}
