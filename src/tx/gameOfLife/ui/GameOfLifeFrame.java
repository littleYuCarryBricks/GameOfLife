package tx.gameOfLife.ui;

import tx.gameOfLife.casegenerator.InitMatrix;
import tx.gameOfLife.model.CellMatrix;
import tx.gameOfLife.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class GameOfLifeFrame extends JFrame {

    private JButton openFileBtn = new JButton("选择文件");
    private JButton startGameBtn = new JButton("开始游戏");

    private JButton startGameBtnRandom = new JButton("随机开始游戏");
    private JButton MatrixRandom = new JButton("生成随机地图");

    private JLabel durationPromtLabel = new JLabel("动画间隔设置(ms为单位)");
    private JTextField durationTextField = new JTextField();

    private JLabel iterator = new JLabel("迭代次数");
    private JTextField iteratorTextField = new JTextField();



    //线程池优化
    ExecutorService impro = Executors.newCachedThreadPool();




    /**
     * 游戏是否开始的标志
     */
    private boolean isStart = false;

    /**
     * 游戏结束的标志
     */
    private boolean stop = false;

    private CellMatrix cellMatrix;
    private JPanel buttonPanel = new JPanel(new GridLayout(3, 2));

    private JPanel newbuttonPanel = new JPanel(new GridLayout(1, 2));
    private JPanel gridPanel = new JPanel();

    private JTextField[][] textMatrix;


    /**
     * 动画默认间隔200ms
     */
    private static final int DEFAULT_DURATION = 200;

    //动画间隔
    private int duration = DEFAULT_DURATION;

    private int count = 0;

    public GameOfLifeFrame() {
        setTitle("生命游戏");
        openFileBtn.addActionListener(new OpenFileActioner());
        startGameBtn.addActionListener(new StartGameActioner());
        startGameBtnRandom.addActionListener(new StartGameRandomActioner());
        MatrixRandom.addActionListener(new MatrixRandomActioner());

        buttonPanel.add(openFileBtn);
        buttonPanel.add(startGameBtn);
        buttonPanel.add(durationPromtLabel);
        buttonPanel.add(durationTextField);
        buttonPanel.add(iterator);
        buttonPanel.add(iteratorTextField);

        newbuttonPanel.add(MatrixRandom);
        newbuttonPanel.add(startGameBtnRandom);

        buttonPanel.setBackground(Color.WHITE);

        getContentPane().add("North", buttonPanel);
        getContentPane().add( "South",newbuttonPanel);

        this.setSize(1000, 1200);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    private class OpenFileActioner implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fcDlg = new JFileChooser(".");
            fcDlg.setDialogTitle("请选择初始配置文件");
            int returnVal = fcDlg.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {

                isStart = false;
                stop = true;
                startGameBtn.setText("开始游戏");

                String filepath = fcDlg.getSelectedFile().getPath();
                cellMatrix = Utils.initMatrixFromFile(filepath);
                initGridLayout();
                showMatrix();
                gridPanel.updateUI();
            }
        }


    }

    private void showMatrix() {

        int[][] matrix = cellMatrix.getMatrix();
        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix[0].length; x++) {
                if (matrix[y][x] == 1) {
                    textMatrix[y][x].setBackground(Color.BLACK);
                } else {
                    textMatrix[y][x].setBackground(Color.WHITE);
                }
            }
        }
    }

    /**
     * 创建显示的gridlayout布局
     */
    private void initGridLayout() {
        int rows = cellMatrix.getHeight();
        int cols = cellMatrix.getWidth();
        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(rows, cols));
        textMatrix = new JTextField[rows][cols];
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                JTextField text = new JTextField();
                textMatrix[y][x] = text;
                gridPanel.add(text);
            }
        }
        add("Center", gridPanel);
    }


    private class StartGameActioner implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!isStart) {

                //获取时间
                try {
                    duration = Integer.parseInt(durationTextField.getText().trim());
                } catch (NumberFormatException e1) {
                    duration = DEFAULT_DURATION;
                }

                //原代码线程：
//                new Thread(new GameControlTask()).start();
                impro.submit(new GameControlTaskRandom());

                isStart = true;
                stop = false;
                startGameBtn.setText("暂停游戏");
            } else {
                stop = true;
                isStart = false;
                startGameBtn.setText("开始游戏");
            }
        }
    }
    private class GameControlTask implements Runnable {

        @Override
        public void run() {

            while (!stop) {
                count ++;
                String strCount = Integer.toString(count);
                iteratorTextField.setText(strCount);
                cellMatrix.transform();
                showMatrix();
                if(count == /*cellMatrix.getTransfromNum()*/ 50)
                    stop = true;
                try {
                    TimeUnit.MILLISECONDS.sleep(duration);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

        }
    }

    private class GameControlTaskRandom implements Runnable {

        @Override
        public void run() {
            while (!stop) {
                count ++;
                String strCount = Integer.toString(count);
                iteratorTextField.setText(strCount);
                cellMatrix.transform();
                showMatrix();
                if(count ==  /*cellMatrix.getTransfromNum()*/ 50)
                    stop = true;
                try {
                    TimeUnit.MILLISECONDS.sleep(duration);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

        }
    }

    private class StartGameRandomActioner implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!isStart) {

                //获取时间
                try {
                    duration = Integer.parseInt(durationTextField.getText().trim());
                } catch (NumberFormatException e1) {
                    duration = cellMatrix.getDuration();
                }




                //原代码线程：
//                new Thread(new GameControlTaskRandom()).start();
   impro.submit(new GameControlTaskRandom());
                isStart = true;
                stop = false;
                startGameBtnRandom.setText("暂停游戏");
            } else {
                stop = true;
                isStart = false;
                startGameBtnRandom.setText("继续游戏");
            }
        }
    }
    private class MatrixRandomActioner implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            count = 0;
            cellMatrix = InitMatrix.initMatrixRandom();
            initGridLayout();
            showMatrix();
            gridPanel.updateUI();

        }
    }
}
