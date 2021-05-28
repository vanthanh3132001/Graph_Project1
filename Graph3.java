package Graph3;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Edge;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.camera.Camera;
import org.graphstream.ui.view.util.InteractiveElement;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Graph3 extends JFrame implements ActionListener {
    DFS g;
    private Graph graph;

    private JFrame frame;
    private JPanel graphPanel;
    private JPanel controlPanel;

    private JTextArea txtInput, txtChoose;
    private JLabel lHead, lTail, lChoose;
    JComboBox<String> comboBoxChoose;
    private JTextField tHead, tTail, tChoose;
    private JButton bFind, bReset, bBack, bSaveAsImage, bNext;


    private int countBack;

    private Viewer viewer;
    private ViewPanel viewPanel;
    private Camera camera;

    public static void main(String args[]) {
        EventQueue.invokeLater(new Graph3()::display);
    }

    public void readText(DFS g, Graph graph) {
        JFileChooser chooser = new JFileChooser();
        String currentDirectory = System.getProperty("user.dir");
        chooser.setCurrentDirectory(new File(currentDirectory));
        chooser.showSaveDialog(null);
        String path = chooser.getSelectedFile().getAbsolutePath();

        List<String> edges = new ArrayList<String>();
        try {
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String edge = myReader.nextLine();
                edges.add(edge);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        Set<String> nodes = new HashSet<String>();
        for(String edge : edges) {
            String [] e = edge.split(" ");
            if (!nodes.contains(e[0])) {
                graph.addNode(e[0]);
                nodes.add(e[0]);
            }
            if (!nodes.contains(e[1])) {
                graph.addNode(e[1]);
                nodes.add(e[1]);
            }
            Integer i1 = Integer.parseInt(e[0]);
            Integer i2 = Integer.parseInt(e[1]);
            g.addEd(i1, i2);
            String f = e[0] + e[1];
            graph.addEdge(f, e[0], e[1], true);
        }
    }

    public void graphSetup() {
        graph = new SingleGraph("Graphxyz", false, true);
        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.antialias");
        graph.setAttribute("ui.stylesheet",
                "graph {\n" +
                        "\tfill-color: white;\n" +
                        "}\n" +
                        "node {\n" +
                        "\tsize: 25px;\n" +
                        "\tshape: circle;\n" +
                        "\tfill-color: white;\n" +
                        "\tstroke-mode: plain;\n" +
                        "\tstroke-color: black;\n" +
                        "}\n" +
                        "node.marked {\n" +
                        "\tfill-color: yellow;\n" +
                        "}\n" +
                        "node.marked2 {\n" +
                        "\tfill-color: white;\n" +
                        "}\n" +
                        "edge {\n" +
                        "\tfill-color: black;\n" +
                        "\tshape: line;\n" +
                        "}\n" +
                        "edge.marked {\n" +
                        "\tfill-color: red;\n" +
                        "}\n" +
                        "edge.marked2 {\n" +
                        "\tfill-color: black;\n" +
                        "}\n"
        );
    }

    public void controlPanelConfig() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("TEAM4_PROJECT");

        lHead = new JLabel("Head");
        tHead = new JTextField(10);

        lTail = new JLabel("Tail");
        tTail = new JTextField(10);

        lChoose = new JLabel("Choose");
        comboBoxChoose = new JComboBox<String>();
        // comboBoxChoose.setSelectedIndex(0);
        tChoose = new JTextField(10);

        bFind = new JButton("Find");
        bSaveAsImage = new JButton("Save Graph as Image");
        bBack = new JButton("Back");
        bReset = new JButton("Reset Graph");
        bNext = new JButton("Next");


        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.add(lHead);
        controlPanel.add(tHead);
        controlPanel.add(lTail);
        controlPanel.add(tTail);
        controlPanel.add(lChoose);
        // controlPanel.add(tChoose);
        controlPanel.add(new JScrollPane(comboBoxChoose));
        controlPanel.add(bBack);
        controlPanel.add(bReset);
        controlPanel.add(bSaveAsImage);
        controlPanel.add(bFind);
        controlPanel.add(bNext);

        txtInput = new JTextArea(8, 16);
        txtInput.setText("All paths:\n");
        txtInput.setEditable(false);
        controlPanel.add(new JScrollPane(txtInput));

        txtChoose = new JTextArea(8, 16);
        txtChoose.setEditable(false);
        controlPanel.add(new JScrollPane(txtChoose));
        controlPanel.setBorder(BorderFactory.createLineBorder(Color.black, 2));

        frame.add(controlPanel);
    }

    public void graphPanelConfig() {
        graphPanel = new JPanel(new BorderLayout()) {
            public Dimension getPreferredSize() {
                Dimension dimFrame = frame.getContentPane().getSize();
                Dimension dimControlPanel = controlPanel.getSize();
                int frameWidth = (int) dimFrame.getWidth();
                int controlPanelWidth = (int) dimControlPanel.getWidth();
                int height = (int)  dimFrame.getHeight();
                return new Dimension(frameWidth - controlPanelWidth*2, height);
            }
        };
        graphPanel.setBorder(BorderFactory.createLineBorder(Color.black, 2));
        frame.add(graphPanel);

        for (Node node : graph) {
            node.setAttribute("ui.label", node.getId());
        }
        viewer = new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.enableAutoLayout();

        viewPanel = (ViewPanel) viewer.addDefaultView(false);
        viewPanel.removeMouseListener(viewPanel.getMouseListeners()[0]);
        viewPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        graphPanel.add(viewPanel);

        camera = viewPanel.getCamera();
        camera.setAutoFitView(true);

        frame.pack();
        frame.setLayout(new FlowLayout());
        frame.setVisible(true);
        frame.setSize(1000, 750);
        frame.setLocationRelativeTo(null);
    }

    private void bBackClick() {
        String[] List = new String[countBack];
        g.printAllPaths(Integer.parseInt(tHead.getText()), Integer.parseInt(tTail.getText()));
        for (int i = 0; i < countBack; i++) {
            List[i] = g.getValue().get(i) + "";
        };
        Node node = graph.getNode(List[countBack - 1]);
        node.setAttribute("ui.class", "marked2");

        Edge edge = graph.getEdge(List[countBack - 2] + List[countBack - 1]);
        edge.setAttribute("ui.class", "marked2");

        for (int i = 0; i < countBack; i++) {
            System.out.println(List[i]);
        };
        System.out.println("***");
        countBack--;

        g.clearString();
        g.clp();
    }

    private void bFindClick() {
        g.printAllPaths(Integer.parseInt(tHead.getText()), Integer.parseInt(tTail.getText()));
        txtInput.setText(g.getString() + "\nBest\n " + g.getValue());
        String text = txtInput.getText();
        txtInput.setCaretPosition(text != null ? text.length() : 0);

        String[] List = new String[g.getValue().size()];
        for (int i = 0; i < g.getValue().size(); i++) {
            List[i] = g.getValue().get(i) + "";
        };
        countBack = g.getValue().size();

        Timer timer = new Timer(1000, new ActionListener() {
            int cnt = 0;
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (cnt == List.length) {
                    ((Timer) actionEvent.getSource()).stop();
                    return;
                }
                graph.getNode(List[cnt]).setAttribute("ui.class", "marked");
                graph.getEdge(List[cnt] + List[cnt + 1]).setAttribute("ui.class", "marked");
                cnt++;
            }
        });
        timer.start();
        g.clp();
    }

    private void bResetClick() {
        for (int i = 0; i < graph.getNodeCount(); i++) {
            Node node = graph.getNode(i);
            node.setAttribute("ui.class", "marked2");
        }
        for (int i = 0; i < graph.getEdgeCount(); i++) {
            Edge edge = graph.getEdge(i);
            edge.setAttribute("ui.class", "marked2");
        }
    }

    private void bSaveAsImageClick() {
        BufferedImage bi = new BufferedImage(viewPanel.getWidth(), viewPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics x = bi.createGraphics();
        viewPanel.print(x);
        x.dispose();
        JFileChooser chooser = new JFileChooser();
        String currentDirectory = System.getProperty("user.dir");
        chooser.setSelectedFile(new File("Untitled.png"));
        chooser.setCurrentDirectory(new File(currentDirectory));
        chooser.showSaveDialog(null);
        String path = chooser.getSelectedFile().getAbsolutePath();
        File image = new File(path);
        try {
            ImageIO.write(bi, "gif", image);
            JOptionPane.showMessageDialog(null, "Images were written successfully.\nFile path: " + path);
        } catch (IOException e) {
            System.out.println("Exception occured :" + e.getMessage());
        };
    }

    private void bNextClick() {
        String selected = String.valueOf(comboBoxChoose.getSelectedItem());
        if(Integer.parseInt(selected) != Integer.parseInt(tTail.getText())) {
            ArrayList<String> temp = new ArrayList<String>();
            g.printPointPaths(Integer.parseInt(selected), Integer.parseInt(tTail.getText()));
            for (int p = 0; p < g.getfirstelement().size(); p++) {
                // int s = g.getfirstelement().get(p);
                temp.add(String.valueOf(g.getfirstelement().get(p)));
            }
            comboBoxChoose.setModel(new DefaultComboBoxModel<String>(temp.toArray(new String[temp.size()])));
            txtChoose.setText(String.join("\n", temp));
            String text1 = txtInput.getText();
            txtInput.setCaretPosition(text1 != null ? text1.length() : 0);
        }
        Node node = graph.getNode(selected);
        node.setAttribute("ui.class", "marked");
    }
    private void display() {
        System.setProperty("org.graphstream.ui", "swing");
        graphSetup();
        g = new DFS(100);
        readText(g, graph);
        controlPanelConfig();
        graphPanelConfig();

        bBack.addActionListener(l -> bBackClick());
        bFind.addActionListener(l -> bFindClick());
        bReset.addActionListener(l -> bResetClick());
        bSaveAsImage.addActionListener(l -> bSaveAsImageClick());
        bNext.addActionListener(l -> bNextClick());

        DocumentListener l = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                generalUpdate(e);
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                generalUpdate(e);
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                generalUpdate(e);
            }
            private void generalUpdate(DocumentEvent e) {
                Document doc = e.getDocument();
                try {
                    String nodeName = doc.getText(0, doc.getLength());
                    comboBoxChoose.setModel(new DefaultComboBoxModel<String>(new String[] {nodeName}));;
                } catch (BadLocationException ex) {

                }
            }
        };
        tHead.getDocument().addDocumentListener(l);

        viewPanel.addMouseMotionListener(new MouseMotionListener() {

            private int preX = -1;
            private int preY = -1;

            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                int currentX = mouseEvent.getX();
                int currentY = mouseEvent.getY();
                double mouseDraggedSpeed = 0.02;

                Point3 pointView = camera.getViewCenter();

                if (preX != -1 && preY != -1) {
                    if (preX < currentX) {
                        pointView.x -= mouseDraggedSpeed;
                    } else if (preX > currentX) {
                        pointView.x += mouseDraggedSpeed;
                    }

                    if (preY < currentY) {
                        pointView.y += mouseDraggedSpeed;
                    } else if (preY > currentY) {
                        pointView.y -= mouseDraggedSpeed;
                    }
                }
                camera.setViewCenter(pointView.x, pointView.y, pointView.z);

                preX = currentX;
                preY = currentY;
            }

            @Override
            public void mouseMoved(MouseEvent mouseEvent) {
                GraphicElement node = ((View) viewPanel).findGraphicElementAt(EnumSet.of(InteractiveElement.NODE), mouseEvent.getX(), mouseEvent.getY());
                if (node != null) {
                    viewPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                } else {
                    viewPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
            }
        });

        viewPanel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
                if (mouseWheelEvent.getWheelRotation() < 0) {
                    camera.setViewPercent(camera.getViewPercent() * 0.95);
                } else {
                    camera.setViewPercent(camera.getViewPercent() * 1.05);
                }
            }
        });

        viewPanel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                GraphicElement node = ((View) viewPanel).findGraphicElementAt(EnumSet.of(InteractiveElement.NODE), mouseEvent.getX(), mouseEvent.getY());
                if (node != null) {
                    System.out.println(node.getId());
                    graph.getNode(node.getId()).setAttribute("ui.class", "marked");
                }
            }
            @Override
            public void mousePressed(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}