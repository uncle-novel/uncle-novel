package com.unclezs.test;

import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.MediaKey;
import com.tulskiy.keymaster.common.Provider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;

public class JKeyMasterTest {
    public static final List<Integer> MODIFIERS = Arrays.asList(18, 17, 16, 157);

    public JKeyMasterTest() {
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        final Provider provider = Provider.getCurrentProvider(true);
        if (provider == null) {
            System.exit(1);
        }

        frame.add(new JLabel("Press hotkey combination:"), "North");
        final JTextField textField = new JTextField();
        textField.setFont(textField.getFont().deriveFont(1, 15.0F));
        textField.setEditable(false);
        textField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (JKeyMasterTest.MODIFIERS.contains(e.getKeyCode())) {
                    textField.setText("");
                } else {
                    textField.setText(KeyStroke.getKeyStrokeForEvent(e).toString().replaceAll("pressed ", ""));
                }
            }
        });
        frame.add(textField, "Center");
        JPanel box = new JPanel();
        JButton grab = new JButton("Grab");
        JButton ungrab = new JButton("Ungrab");
        JButton reset = new JButton("Reset All");
        box.add(grab);
        box.add(ungrab);
        HotKeyListener listener = (hotKey) -> {
            JOptionPane.showMessageDialog(frame, "Hooray: " + hotKey);
        };
        grab.addActionListener((e) -> {
            String text = textField.getText();
            if (text != null && text.length() > 0) {
                provider.register(KeyStroke.getKeyStroke(text), listener);
            }

        });
        ungrab.addActionListener((e) -> {
            String text = textField.getText();
            if (text != null && text.length() > 0) {
                provider.unregister(KeyStroke.getKeyStroke(text));
            }

        });
        reset.addActionListener((e) -> {
            provider.reset();
        });
        JButton grabMedia = new JButton("Grab media keys");
        grabMedia.addActionListener((e) -> {
            provider.register(MediaKey.MEDIA_NEXT_TRACK, listener);
            provider.register(MediaKey.MEDIA_PLAY_PAUSE, listener);
            provider.register(MediaKey.MEDIA_PREV_TRACK, listener);
            provider.register(MediaKey.MEDIA_STOP, listener);
        });
        box.add(grabMedia);
        box.add(reset);
        frame.add(box, "Last");
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                provider.reset();
                provider.stop();
                System.exit(0);
            }
        });
        frame.setSize(500, 150);
        frame.setLocationRelativeTo((Component) null);
        frame.setVisible(true);
    }
}
