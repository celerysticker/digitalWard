package historyview;

import patientview.PatientJFrame;
import utility.Patient;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jfree.chart.ChartFactory; 
import org.jfree.chart.JFreeChart; 
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset; 
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author s1571163
 */
public class HistoryJFrame extends javax.swing.JFrame {
    private final Patient p;
    private final PatientJFrame patientframe;
    private final XYDataset dataset;
    private final XYItemRenderer renderer;

    /**
     * Creates new form HistoryJFrame
     * @param p
     * @param patientframe
     */
    public HistoryJFrame(Patient p, PatientJFrame patientframe) {
        initComponents();
        //set window in the center of the screen
        //Get the size of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        //Determine the new location of the window
        int w = this.getSize().width;
        int h = this.getSize().height;
        int x = (dim.width - w) / 2;
        int y = (dim.height - h) / 2;
        //Move the window
        this.setLocation(x, y);
        
        this.p = p;
        this.patientframe = patientframe;
        
        patientName.setText(p.getLastName() + ", " + p.getFirstName());

        dataset = getDataset();

        JFreeChart chart = ChartFactory.createXYLineChart(
            "", // Title
            "Time (GMT)", // x-axis Label
            "Value", // y-axis Label
            dataset, // Dataset
            PlotOrientation.VERTICAL,
            true, // Show Legend
            true, // Use tooltips
            false
        );
        chart.setBackgroundPaint(Color.white); //?
   
        jPanel_chart.setLayout(new java.awt.BorderLayout());
        ChartPanel cp = new ChartPanel(chart);
        
        XYPlot plot = chart.getXYPlot();
        renderer = plot.getRenderer();
        jPanel_chart.add(cp, BorderLayout.CENTER);
        jPanel_chart.validate();
        
        checkRespRate.setSelected(true);
        checkOxySat.setSelected(true);
        checkTemp.setSelected(true);
        checkSysBlood.setSelected(true);
        checkHeartRate.setSelected(true);
       
    }
    
    private XYDataset getDataset() {
        XYSeries[] series = new XYSeries[5];
        series[0] = new XYSeries("RESPIRATORY RATE");
        series[1] = new XYSeries("OXYGEN SATURATION");
        series[2] = new XYSeries("TEMPERATURE");
        series[3] = new XYSeries("SYSTOLIC BLOOD");
        series[4] = new XYSeries("HEART RATE");
        try {
            final Scanner scanner = new Scanner(new File(p.getDataFile()));
            scanner.useDelimiter(",");
            scanner.nextLine(); // skip first line
            int i = 0;
            while (scanner.hasNextLine()) {
                String[] arr = scanner.nextLine().split(",");
                int timestamp = Integer.parseInt(arr[0]);
                int br = Integer.parseInt(arr[1]);
                int spo2 = Integer.parseInt(arr[2]);
                float temp = Float.parseFloat(arr[3]);
                int systolic = Integer.parseInt(arr[4]);
                int hr = Integer.parseInt(arr[5]);
                series[0].add(timestamp, br);
                series[1].add(timestamp, spo2);
                series[2].add(timestamp, temp);
                series[3].add(timestamp, systolic);
                series[4].add(timestamp, hr);
            }
            scanner.close();
        }
        catch (FileNotFoundException e) {
            Logger.getLogger(HistoryJFrame.class.getName()).log(Level.SEVERE, null, e);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        for (int j = 0; j < series.length; ++j) {
            dataset.addSeries(series[j]);
        }
        
        return dataset;
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        iButton_patientView = new javax.swing.JButton();
        jPanel_chart = new javax.swing.JPanel();
        checkRespRate = new javax.swing.JCheckBox();
        checkOxySat = new javax.swing.JCheckBox();
        checkTemp = new javax.swing.JCheckBox();
        checkSysBlood = new javax.swing.JCheckBox();
        checkHeartRate = new javax.swing.JCheckBox();
        patientName = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        iButton_patientView.setText("< Back to Patient View");
        iButton_patientView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iButton_patientViewActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_chartLayout = new javax.swing.GroupLayout(jPanel_chart);
        jPanel_chart.setLayout(jPanel_chartLayout);
        jPanel_chartLayout.setHorizontalGroup(
            jPanel_chartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel_chartLayout.setVerticalGroup(
            jPanel_chartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 324, Short.MAX_VALUE)
        );

        checkRespRate.setText("RESPIRATORY RATE");
        checkRespRate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkRespRateActionPerformed(evt);
            }
        });

        checkOxySat.setText("OXYGEN SAT");
        checkOxySat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkOxySatActionPerformed(evt);
            }
        });

        checkTemp.setText("TEMPERATURE");
        checkTemp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkTempActionPerformed(evt);
            }
        });

        checkSysBlood.setText("SYSTOLIC BLOOD");
        checkSysBlood.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkSysBloodActionPerformed(evt);
            }
        });

        checkHeartRate.setText("HEART RATE");
        checkHeartRate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkHeartRateActionPerformed(evt);
            }
        });

        patientName.setFont(new java.awt.Font("MicrosoftSansSerif", 1, 18)); // NOI18N
        patientName.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        patientName.setText("Patient Name");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_chart, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(checkRespRate)
                            .addComponent(checkOxySat))
                        .addGap(63, 63, 63)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(checkTemp)
                                .addGap(76, 76, 76)
                                .addComponent(checkHeartRate))
                            .addComponent(checkSysBlood))
                        .addGap(0, 149, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(iButton_patientView)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(patientName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(iButton_patientView)
                    .addComponent(patientName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkRespRate)
                    .addComponent(checkTemp)
                    .addComponent(checkHeartRate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkOxySat)
                    .addComponent(checkSysBlood))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_chart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(97, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void iButton_patientViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iButton_patientViewActionPerformed
        // TODO add your handling code here:
        this.dispose();
        this.patientframe.setVisible(true);
    }//GEN-LAST:event_iButton_patientViewActionPerformed

    private void checkRespRateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkRespRateActionPerformed
        // TODO add your handling code here:
        renderer.setSeriesVisible(0, checkRespRate.isSelected());  
    }//GEN-LAST:event_checkRespRateActionPerformed

    private void checkOxySatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkOxySatActionPerformed
        renderer.setSeriesVisible(1, checkOxySat.isSelected());
    }//GEN-LAST:event_checkOxySatActionPerformed

    private void checkTempActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkTempActionPerformed
        renderer.setSeriesVisible(2, checkTemp.isSelected());
    }//GEN-LAST:event_checkTempActionPerformed

    private void checkSysBloodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkSysBloodActionPerformed
        renderer.setSeriesVisible(3, checkSysBlood.isSelected());
    }//GEN-LAST:event_checkSysBloodActionPerformed

    private void checkHeartRateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkHeartRateActionPerformed
        renderer.setSeriesVisible(4, checkHeartRate.isSelected());
    }//GEN-LAST:event_checkHeartRateActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(HistoryJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HistoryJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HistoryJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HistoryJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox checkHeartRate;
    private javax.swing.JCheckBox checkOxySat;
    private javax.swing.JCheckBox checkRespRate;
    private javax.swing.JCheckBox checkSysBlood;
    private javax.swing.JCheckBox checkTemp;
    private javax.swing.JButton iButton_patientView;
    private javax.swing.JPanel jPanel_chart;
    private javax.swing.JLabel patientName;
    // End of variables declaration//GEN-END:variables
}
