import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FieldPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
    private Set<Cell> cells = new HashSet<>();
    private int cellSize = 20;
    private final List<Cell> validationCells = Arrays.asList(
            new Cell(-1, -1),
            new Cell(-1, 0),
            new Cell(-1, 1),
            new Cell(0, -1),
            new Cell(0, 1),
            new Cell(1, -1),
            new Cell(1, 0),
            new Cell(1, 1)
            );
    private Cell lastCell;
    private boolean isGrid = true;

    public FieldPanel() {
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }

    @Override
    public void paint(Graphics graphics) {
        graphics.setColor(this.getBackground());
        graphics.fillRect(0, 0, getWidth(), getHeight());
        graphics.setColor(Color.BLACK);
        for(Cell cell : cells) {
            graphics.fillOval(cell.getX()*cellSize, cell.getY()*cellSize, cellSize, cellSize);
        }
        if (isGrid) {
            paintGrid(graphics);
        }
    }

    private void paintGrid(Graphics graphics) {
        for (int i = 0; i < getWidth(); i+=cellSize) {
            graphics.drawLine(i, 0, i, getHeight());
        }
        for (int i = 0; i < getHeight(); i+=cellSize) {
            graphics.drawLine(0, i, getWidth(), i);
        }

    }

    public int generate() {
        int changes = 0;
        Set<Cell> newGen = new HashSet<>();
        for (Cell cell : cells) {
            int neighbours = countNeighbors(cell);
            if (neighbours == 2 || neighbours == 3) {
                if ((cell.getX() >= 0 && cell.getX() <= getWidth() - cellSize)
                        && (cell.getY() >= 0 && cell.getY() <= getHeight() - cellSize)) {
                    newGen.add(cell);
                }
            }
            for (Cell validationCell : validationCells) {
                Cell absoluteCell = new Cell(cell.getX() + validationCell.getX(), cell.getY() + validationCell.getY());
                neighbours = countNeighbors(absoluteCell);
                if (neighbours == 3) {
                    if ((cell.getX() >= 0 && cell.getX() <= getWidth() - cellSize)
                            && (cell.getY() >= 0 && cell.getY() <= getHeight() - cellSize)) {
                        newGen.add(absoluteCell);
                        ++changes;
                    }
                }
            }
        }
        if (changes == 0) {
            cells = newGen;
            repaint();
            return -1;
        } else if (cells.size() == newGen.size()) {
            cells = newGen;
            repaint();
            return 0;
        } else {
            cells = newGen;
            repaint();
            return 1;
        }
    }

    public int countNeighbors(Cell cell) {
        int neighbours = 0;
        for (Cell validationCell : validationCells) {
            Cell absolutCell = new Cell(cell.getX() + validationCell.getX(), cell.getY() + validationCell.getY());
            if (cells.contains(absolutCell)) {
                neighbours++;
            }
        }
        return neighbours;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Cell cell = new Cell(e.getX()/cellSize, e.getY()/cellSize);
        if (cells.contains(cell)) {
            cells.remove(cell);
        } else {
            cells.add(cell);
        }
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if ((e.getX() >= 0 && e.getX() <= getWidth()-cellSize) && (e.getY() >= 0 && e.getY() <= getHeight()-cellSize)) {
            Cell cell = new Cell(e.getX() / cellSize, e.getY() / cellSize);
            if (lastCell == null || !lastCell.equals(cell)) {
                if (cells.contains(cell)) {
                    cells.remove(cell);
                } else {
                    cells.add(cell);
                }
            }
            lastCell = cell;
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

    }

    public boolean isGrid() {
        return isGrid;
    }

    public void setGrid(boolean grid) {
        isGrid = grid;
    }

    public void clear() {
        cells.clear();
        repaint();
    }

    public Set<Cell> getCells() {
        return cells;
    }

    public void setCells(Set<Cell> cells) {
        this.cells = cells;
    }

    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
    }
}
