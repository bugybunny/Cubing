package sq1;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class JPanelSolution extends JPanel {
    MyCanvas canvas;

    float width = 1.0f;

    public JPanelSolution() {
        super(new BorderLayout());

        JPanel controlPanel = new JPanel(new GridLayout(3, 3));
        add(controlPanel, BorderLayout.NORTH);

        canvas = new MyCanvas();
        add(canvas, "Center");
    }

    class MyCanvas extends Canvas {
        private int sideLength = 300;
        int middle = sideLength / 2;
        int edgeLength = (int) (sideLength * 0.2);
        int cornerLength = (int) (sideLength * 0.4);

        @Override
        public void paint(Graphics g) {
            Graphics2D g2D = (Graphics2D) g;

            // g2D.rotate(0.5);

            BasicStroke stroke = new BasicStroke(width);
            g2D.setStroke(stroke);

            drawLayers(g2D);
        }

        public void drawLayers(Graphics2D g2D) {
            Color[] bottomCorners = new Color[] { Color.WHITE, Color.GRAY, Color.WHITE, Color.GRAY, Color.GRAY,
                    Color.GRAY, Color.WHITE, Color.WHITE };
            Color[] bottomSides = new Color[] { Color.ORANGE, Color.GRAY, Color.ORANGE, Color.GREEN, Color.GRAY,
                    Color.GRAY, Color.GRAY, Color.GRAY, Color.RED, Color.BLUE, Color.BLUE, Color.BLUE };
            int startX = 700;
            int startY = 600;
            drawLayer(g2D, startX, startY, bottomCorners, bottomSides);

            // slice position
            g2D.setStroke(new BasicStroke(1f));
            g2D.setColor(Color.RED);
            // g2D.drawLine(startX + sideLength - cornerLength, startY, startX +
            // cornerLength, startY + sideLength);
            drawSlice(g2D, startX + sideLength - cornerLength, startY, startX + cornerLength, startY + sideLength);

            startX = 700;
            startY = 200;
            Color[] topCorners = new Color[] { Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.WHITE, Color.GRAY,
                    Color.GRAY, Color.WHITE };
            Color[] topSides = new Color[] { Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.RED,
                    Color.GREEN, Color.GRAY, Color.GRAY, Color.GRAY, Color.GREEN, Color.GRAY };
            drawLayer(g2D, startX, startY, topCorners, topSides);

            // slice position
            g2D.setStroke(new BasicStroke(1f));
            g2D.setColor(Color.RED);
            // drawSlice(g2D, startX + cornerLength, startY, startX + sideLength
            // - cornerLength, startY + sideLength);
            g2D.drawLine(startX + cornerLength, startY, startX + sideLength - cornerLength, startY + sideLength);
        }

        private void drawSlice(Graphics2D g2D, int x1, int y1, int x2, int y2) {
            g2D.setStroke(new BasicStroke(1f));
            g2D.setColor(Color.RED);
            double gradient = (y2 - y1) / (x2 - x1);
            int length = 100;
            g2D.drawLine((int) (x1 - length * gradient), (int) (y1 + length * gradient), (int) (x2 + length * gradient),
                    (int) (y2 - length * gradient));
        }

        private void drawLayer(Graphics2D g2D, int startX, int startY, Color[] topcolors, Color[] sidecolors) {
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            List<Polygon> pieces = createPolygonsForLayer(startX, startY);

            g2D.setStroke(new BasicStroke(10.0f));
            int j = 0;
            for (int i = 0; i < pieces.size(); i++) {
                Polygon piece = pieces.get(i);
                g2D.setColor(topcolors[i]);
                g2D.fillPolygon(piece);

                g2D.setColor(sidecolors[j]);
                if (piece.xpoints.length == 3) {
                    g2D.drawLine(piece.xpoints[0], piece.ypoints[0], piece.xpoints[1], piece.ypoints[1]);
                    j++;
                } else if (piece.xpoints.length == 4) {
                    // first one is different
                    g2D.drawLine(piece.xpoints[0], piece.ypoints[0], piece.xpoints[1], piece.ypoints[1]);
                    if (i == 0) {
                        g2D.setColor(sidecolors[sidecolors.length - 1]);
                        g2D.drawLine(piece.xpoints[0], piece.ypoints[0], piece.xpoints[3], piece.ypoints[3]);
                    } else {
                        g2D.setColor(sidecolors[++j]);
                        g2D.drawLine(piece.xpoints[1], piece.ypoints[1], piece.xpoints[2], piece.ypoints[2]);
                    }
                    j++;
                }
            }

            g2D.setColor(Color.BLACK);

            // slices: top to bottom
            g2D.drawLine(startX + cornerLength, startY, startX + sideLength - cornerLength, startY + sideLength);
            g2D.drawLine(startX + sideLength - cornerLength, startY, startX + cornerLength, startY + sideLength);
            // slices: left to right
            g2D.drawLine(startX, startY + cornerLength, startX + sideLength, startY + sideLength - cornerLength);
            g2D.drawLine(startX, startY + sideLength - cornerLength, startX + sideLength, startY + cornerLength);
        }

        private List<Polygon> createPolygonsForLayer(int startX, int startY) {
            List<Polygon> pieces = new ArrayList<>(8);
            pieces.add(new Polygon(new int[] { startX, startX + cornerLength, startX + middle, startX },
                    new int[] { startY, startY, startY + middle, startY + cornerLength }, 4));

            pieces.add(new Polygon(
                    new int[] { startX + cornerLength, startX + cornerLength + edgeLength, startX + middle },
                    new int[] { startY, startY, startY + middle }, 3));

            pieces.add(new Polygon(
                    new int[] { startX + sideLength - cornerLength, startX + sideLength, startX + sideLength,
                            startX + middle },
                    new int[] { startY, startY, startY + cornerLength, startY + middle }, 4));

            pieces.add(new Polygon(new int[] { startX + sideLength, startX + sideLength, startX + middle },
                    new int[] { startY + cornerLength, startY + cornerLength + edgeLength, startY + middle }, 3));

            pieces.add(new Polygon(
                    new int[] { startX + sideLength, startX + sideLength, startX + sideLength - cornerLength,
                            startX + middle },
                    new int[] { startY + sideLength - cornerLength, startY + sideLength, startY + sideLength,
                            startY + middle },
                    4));

            pieces.add(new Polygon(
                    new int[] { startX + sideLength - cornerLength, startX + cornerLength, startX + middle },
                    new int[] { startY + sideLength, startY + sideLength, startY + middle }, 3));

            pieces.add(new Polygon(new int[] { startX + cornerLength, startX, startX, startX + middle }, new int[] {
                    startY + sideLength, startY + sideLength, startY + sideLength - cornerLength, startY + middle },
                    4));

            pieces.add(new Polygon(new int[] { startX, startX, startX + middle },
                    new int[] { startY + sideLength - cornerLength, startY + cornerLength, startY + middle }, 3));
            return pieces;
        }

    }

    public static void main(String[] a) {
        JFrame f = new JFrame();
        f.getContentPane().add(new JPanelSolution());
        f.setDefaultCloseOperation(1);
        f.setSize(1000, 1700);
        f.setVisible(true);
    }
}