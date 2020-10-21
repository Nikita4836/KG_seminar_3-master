package com.company;

import com.company.line.DDALineDrawer;
import com.company.line.Line;
import com.company.line.LineDrawer;
import com.company.pixel.BufferedImagePixelDrawer;
import com.company.pixel.PixelDrawer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class DrawPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
   private ScreenConverter sc = new ScreenConverter(-2, 2, 4, 4, 800, 600);
    private Line xAxis = new Line(-1, 0, 1, 0);
    private Line yAxis = new Line(0, -1, 0, 1);

    private ArrayList<Line> allLines = new ArrayList<>();
    private Line currentNewLine = null;


    public DrawPanel (){
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
        this.addMouseWheelListener(this);
    }


    @Override
    public void paint(Graphics g) {
        sc.setScreenHeight(getHeight());
        sc.setScreenWidth(getWidth());
        BufferedImage bi = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);

        Graphics gr= bi.createGraphics();
        gr.setColor(Color.WHITE);
        gr.fillRect(0, 0, getWidth(), getHeight());
        gr.dispose();

        PixelDrawer pd = new BufferedImagePixelDrawer(bi);

        LineDrawer ld = new DDALineDrawer(pd);

        drawLine(ld, xAxis);
        drawLine(ld, yAxis);
        for (Line l : allLines)
            drawLine(ld, l);

        if (currentNewLine != null)
            drawLine(ld, currentNewLine);

        g.drawImage(bi, 0, 0, null);


    }
    private void drawLine(LineDrawer id, Line l){
        id.drawLine(sc.r2s(l.getP1()), sc.r2s(l.getP2()));
    }
    private ScreenPoint lastPostion = null;

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON3) {
            lastPostion = new ScreenPoint(e.getX(), e.getY());
        } else  if (e.getButton() == MouseEvent.BUTTON1){
            currentNewLine = new Line(sc.s2r(new ScreenPoint(e.getX(),e.getY())),
                    sc.s2r(new ScreenPoint(e.getX(), e.getY())));
            }

        repaint();

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON3) {
            lastPostion = null;
        } else if (e.getButton() == MouseEvent.BUTTON1){
            allLines.add(currentNewLine);
            currentNewLine = null;
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
        ScreenPoint currentPosition = new ScreenPoint(e.getX(), e.getY());
        if(lastPostion != null){
            ScreenPoint deltaScreen = new ScreenPoint(currentPosition.getX() - lastPostion.getX(),
                    currentPosition.getY() - lastPostion.getY());
            RealPoint deltaReal = sc.s2r(deltaScreen);
            RealPoint zeroReal = sc.s2r(new ScreenPoint(0, 0));
            RealPoint vector = new RealPoint(deltaReal.getX() - zeroReal.getX(), deltaReal.getY() - zeroReal.getY());
            sc.setCornerX(sc.getCornerX() - vector.getX());
            sc.setCornerY(sc.getCornerY() - vector.getY());
            lastPostion = currentPosition;
        }
        if (currentNewLine != null){
            currentNewLine.setP2(sc.s2r(currentPosition));
        }
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int clicks = e.getWheelRotation();
        double scale = 1;
        double coef = clicks < 0 ? 1.1 : 0.9;
        for (int i = 0; i < Math.abs(clicks); i++){
            scale *= coef;
        }

        sc.setRealWidth(sc.getRealWidth() * scale);
        sc.setRealHeight(sc.getRealHeight() * scale);

        repaint();
    }
}
