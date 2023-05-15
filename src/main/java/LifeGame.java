import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Set;

public class LifeGame extends JFrame {
    private FieldPanel field = new FieldPanel();
    private Box controlPanel = Box.createHorizontalBox();
    private JButton startButton = new JButton("Start");
    private JButton stopButton = new JButton("Stop");
    private JButton nextButton = new JButton("Next");
    private JButton clearButton = new JButton("Clear");
    private Checkbox checkBoxGrid = new Checkbox("Grid");
    private Timer startTimer;
    private int speed = 150;
    private JButton loadButton = new JButton("Load");
    private JButton saveButton = new JButton("Save");
    private JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 10, 250, 150);
    private JSlider sizeSlider = new JSlider(JSlider.HORIZONTAL,10, 50, 20);

    private int generation = 0;
    private int unchangedGenerations = 0;

    public LifeGame() {
        setSize(900, 600);
        setLocation(10, 10);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());
        setTitle("0");

        add(field, BorderLayout.CENTER);

        add(controlPanel, BorderLayout.SOUTH);
        generateLoadButtons();
        controlPanel.add(Box.createHorizontalStrut(20));
        generateControlButtons();
        controlPanel.add(Box.createHorizontalStrut(10));
        generateCheckBox();
        controlPanel.add(Box.createHorizontalStrut(20));
        generateSliders();
    }

    private void generateLoadButtons() {
        controlPanel.add(loadButton);
        loadButton.addActionListener(e -> load());
        controlPanel.add(saveButton);
        saveButton.addActionListener(e -> save());
    }

    private void save() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save cells");
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file))) {
                outputStream.writeObject(field.getCells());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void load() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load cells");
        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
                Set<Cell> cellSet = (Set<Cell>) inputStream.readObject();
                field.setCells(cellSet);
                field.repaint();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    private void createGeneration() {
        int tmp = field.generate();
        ++generation;
        switch (tmp) {
            case 1:
                unchangedGenerations = 0;
                setTitle(String.valueOf(generation));
                break;
            case -1:
                stop();
                generation = 0;
                unchangedGenerations = 0;
                setTitle("Game over");
                field.clear();
                break;
            case 0:
                ++unchangedGenerations;
                if (unchangedGenerations > 50) {
                    stop();
                    generation = 0;
                    unchangedGenerations = 0;
                    setTitle("Game over");
                    field.clear();
                } else {
                    setTitle(String.valueOf(generation));
                }
                break;
        }
    }

    private void generateControlButtons() {
        startTimer = new Timer(speed, e -> createGeneration());
        controlPanel.add(startButton);
        startButton.addActionListener(e -> start());
        controlPanel.add(stopButton);
        stopButton.addActionListener(e -> stop());
        controlPanel.add(nextButton);
        nextButton.addActionListener(e -> createGeneration());
        controlPanel.add(clearButton);
        clearButton.addActionListener(e -> field.clear());
    }

    private void start() {
        if (!startTimer.isRunning()) {
            startTimer.start();
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
        }
    }

    private void stop() {
        if (startTimer.isRunning()) {
            startTimer.stop();
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            generation = 0;
            unchangedGenerations = 0;
            setTitle("0");
        }
    }


    private void generateCheckBox() {
        controlPanel.add(checkBoxGrid);
        checkBoxGrid.addItemListener(l -> {
            field.setGrid(!field.isGrid());
            field.repaint();
        });
        checkBoxGrid.setSize(new Dimension(50, 50));
        checkBoxGrid.setFont(new java.awt.Font("Arial", 1, 12));
        checkBoxGrid.setState(true);
    }
    private void generateSliders() {
        controlPanel.add(speedSlider);
        speedSlider.setMinorTickSpacing(10);
        speedSlider.setMajorTickSpacing(50);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.addChangeListener(l -> {
            speed = speedSlider.getValue();
            Timer newTimer = new Timer(speed, e -> createGeneration());
            startTimer.stop();
            startTimer = newTimer;
            startTimer.start();
        });

        controlPanel.add(sizeSlider);
        sizeSlider.setMinorTickSpacing(5);
        sizeSlider.setMajorTickSpacing(10);
        sizeSlider.setPaintTicks(true);
        sizeSlider.setPaintLabels(true);
        sizeSlider.addChangeListener(l -> field.setCellSize(sizeSlider.getValue()));
    }
    public JButton getNextButton() {
        return nextButton;
    }
}
