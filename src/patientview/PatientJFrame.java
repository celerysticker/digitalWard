/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package patientview;

import wardview.WardJFrame;
import utility.Patient;
import historyview.HistoryJFrame;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.Timer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


/**
 * Based on framework by Qi Zhou
 * @author Christina Lin
 */
public class PatientJFrame extends javax.swing.JFrame {

    private Timer timer;
    private int timerSeconds;
    private final Patient p;
    private final WardJFrame wardframe;
    public static final int RESPIRATORY_RATE = 0;
    public static final int SPO2 = 1;
    public static final int SYSTOLIC = 2;
    public static final int HEART_RATE = 3;
    public static final float TEMPERATURE = 0f;

    /**
     * Creates new form PatientJFrame
     * @param p
     * @param wardframe
     * @throws java.io.IOException
     */
    public PatientJFrame(Patient p, WardJFrame wardframe) throws IOException {
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
        this.wardframe = wardframe;
        
        //fill patient info fields
        patientNameField.setText(p.getFirstName() + " " + p.getLastName());
        wardNumField.setText("W001");
        bedNumField.setText(Integer.toString(p.getBedNumber()));
        dobField.setText(p.getDOB());
        genderField.setText(p.getGender());
        patientName.setText(p.getLastName() + ", " + p.getFirstName());
        
        //set focus on exit button
        jButton_exit.requestFocus();
        
        //open file
        final Scanner scanner = new Scanner(new File(p.getDataFile()));
        scanner.useDelimiter(",");
        scanner.nextLine(); // skip first line
        
        //set timer
        timerSeconds = 0;
        if (timer == null) {
            timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // do it every 1 second
                    Calendar cal = Calendar.getInstance();
                    cal.getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
                    jLabel_systemTime.setText(sdf.format(cal.getTime()));
                    // do it every 5 seconds
                    if (timerSeconds % 5 == 0) {
                        if (scanner.hasNextLine()) {
                            String[] arr = scanner.nextLine().split(",");
                            int timestamp = Integer.parseInt(arr[0]);
                            if (timestamp == timerSeconds) {
                                int br = Integer.parseInt(arr[1]);
                                int spo2 = Integer.parseInt(arr[2]);
                                float temp = Float.parseFloat(arr[3]);
                                int systolic = Integer.parseInt(arr[4]);
                                int hr = Integer.parseInt(arr[5]);
                                updatePatient(br, spo2, temp, systolic, hr);
                                displayData(br, spo2, temp, systolic, hr);
                            }
                        }  
                    }
                    timerSeconds++;
                }
            });
        }

        if (timer.isRunning() == false) {
            timer.start();
        }
    }
    
    private void updatePatient(int br, int spo2, float temp, int sys, int hr) {
        this.p.setAll(br, spo2, sys, temp, hr);
    }

    public void displayData(int br, int spo2, float temp, int sys, int hr) {
        // display value and color; update pSEWS
        respRateField.setText(Integer.toString(br));
        respRateField.setOpaque(true);
        if (br <= 8 || br >= 36) {
            respRateField.setBackground(Color.red);
        }
        else if (br >= 31 && br <= 35) {
            respRateField.setBackground(Color.orange);
        }
        else if (br >= 21 && br <= 30) {
            respRateField.setBackground(Color.orange);
        }
        else {
            respRateField.setBackground(Color.green);
        }
        
        oxySatField.setText(Integer.toString(spo2));
        oxySatField.setOpaque(true);
        if (spo2 < 85) {
            oxySatField.setBackground(Color.red);
        }
        else if (spo2 >= 85 && spo2 <= 89) {
            oxySatField.setBackground(Color.orange);
        }
        else if (spo2 >= 90 && spo2 <= 92) {
            oxySatField.setBackground(Color.orange);
        }
        else {
            oxySatField.setBackground(Color.green);
        }
        
        sysBloodField.setText(Integer.toString(sys));
        sysBloodField.setOpaque(true);
        if (sys <= 69) {
            sysBloodField.setBackground(Color.red);
        }
        else if ((sys >= 70 && sys <= 79) || sys >= 200) {
            sysBloodField.setBackground(Color.orange);
        }
        else if (sys >= 80 && sys <= 99) {
            sysBloodField.setBackground(Color.orange);
        }
        else {
            sysBloodField.setBackground(Color.green);
        }
        
        tempField.setText(String.format("%.1f", temp));
        tempField.setOpaque(true);
        if (temp < 34) {
            tempField.setBackground(Color.red);
        }
        else if ((temp >= 34 && temp <= 34.9) || temp >= 38.5) {
            tempField.setBackground(Color.orange);
        }
        else if ((temp >= 35 && temp <= 35.9) || (temp >= 38 && temp <= 38.4)) {
            tempField.setBackground(Color.orange);
        }
        else {
            tempField.setBackground(Color.green);
        }
        
        heartRateField.setText(Integer.toString(hr));
        heartRateField.setOpaque(true);
        if (hr < 29 || hr >= 130) {
            heartRateField.setBackground(Color.red);
        }
        else if ((hr >= 30 && hr <= 39) || (hr >= 110 && hr <= 129)) {
            heartRateField.setBackground(Color.orange);
        }
        else if ((hr >= 40 && hr <= 49) || (hr >= 100 && hr <= 109)) {
            heartRateField.setBackground(Color.orange);
        }
        else {
            heartRateField.setBackground(Color.green);
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel9 = new javax.swing.JLabel();
        jPanel_readings = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        respRateField = new javax.swing.JLabel();
        oxySatField = new javax.swing.JLabel();
        tempField = new javax.swing.JLabel();
        sysBloodField = new javax.swing.JLabel();
        heartRateField = new javax.swing.JLabel();
        jPanel_buttons = new javax.swing.JPanel();
        jButton_exit = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jButton_history = new javax.swing.JButton();
        jLabel_systemTime = new javax.swing.JLabel();
        jPanel_title = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        patientNameField = new javax.swing.JLabel();
        wardNumField = new javax.swing.JLabel();
        bedNumField = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        dobField = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        genderField = new javax.swing.JLabel();
        jButton_changeView = new javax.swing.JButton();
        patientName = new javax.swing.JLabel();

        jLabel9.setText("jLabel9");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel6.setText("RESPIRATORY RATE:");

        jLabel7.setText("OXYGEN SATURATION:");

        jLabel8.setText("TEMPERATURE:");

        jLabel10.setText("SYSTOLIC BLOOD:");

        jLabel11.setText("HEART RATE:");

        respRateField.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        respRateField.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        respRateField.setText("n/a");

        oxySatField.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        oxySatField.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        oxySatField.setText("n/a");

        tempField.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        tempField.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tempField.setText("n/a");

        sysBloodField.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        sysBloodField.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sysBloodField.setText("n/a");

        heartRateField.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        heartRateField.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        heartRateField.setText("n/a");

        org.jdesktop.layout.GroupLayout jPanel_buttonsLayout = new org.jdesktop.layout.GroupLayout(jPanel_buttons);
        jPanel_buttons.setLayout(jPanel_buttonsLayout);
        jPanel_buttonsLayout.setHorizontalGroup(
            jPanel_buttonsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );
        jPanel_buttonsLayout.setVerticalGroup(
            jPanel_buttonsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 40, Short.MAX_VALUE)
        );

        jButton_exit.setText("Exit");
        jButton_exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_exitActionPerformed(evt);
            }
        });

        jLabel12.setText("Vital Signs");

        jButton_history.setText("View History");
        jButton_history.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_historyActionPerformed(evt);
            }
        });

        jLabel_systemTime.setText("TIME");

        org.jdesktop.layout.GroupLayout jPanel_readingsLayout = new org.jdesktop.layout.GroupLayout(jPanel_readings);
        jPanel_readings.setLayout(jPanel_readingsLayout);
        jPanel_readingsLayout.setHorizontalGroup(
            jPanel_readingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel_buttons, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel_readingsLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel_readingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel_readingsLayout.createSequentialGroup()
                        .add(jPanel_readingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel_readingsLayout.createSequentialGroup()
                                .add(jPanel_readingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jLabel6)
                                    .add(respRateField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .add(18, 18, 18)
                                .add(jPanel_readingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jLabel7)
                                    .add(oxySatField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .add(18, 18, 18)
                                .add(jPanel_readingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jLabel8)
                                    .add(tempField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .add(23, 23, 23)
                                .add(jPanel_readingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jLabel10)
                                    .add(sysBloodField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jPanel_readingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jLabel11)
                                    .add(heartRateField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                            .add(jPanel_readingsLayout.createSequentialGroup()
                                .add(jLabel12)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jButton_history)))
                        .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(jPanel_readingsLayout.createSequentialGroup()
                        .add(jLabel_systemTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 249, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jButton_exit))))
        );
        jPanel_readingsLayout.setVerticalGroup(
            jPanel_readingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel_readingsLayout.createSequentialGroup()
                .add(17, 17, 17)
                .add(jPanel_readingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel12)
                    .add(jButton_history))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel_readingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6)
                    .add(jLabel7)
                    .add(jLabel8)
                    .add(jLabel10)
                    .add(jLabel11))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel_readingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(oxySatField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(respRateField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(tempField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(sysBloodField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(heartRateField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(117, 117, 117)
                .add(jPanel_buttons, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(59, 59, 59)
                .add(jPanel_readingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel_systemTime)
                    .add(jButton_exit)))
        );

        jLabel1.setText("PATIENT NAME:");

        jLabel2.setText("WARD NUMBER:");

        jLabel3.setText("BED NUMBER:");

        patientNameField.setText("jLabel");

        wardNumField.setText("jLabel");

        bedNumField.setText("jLabel");

        jLabel4.setText("DoB:");

        dobField.setText("jLabel");

        jLabel5.setText("GENDER:");

        genderField.setText("jLabel");

        jButton_changeView.setText("< Back to Ward View");
        jButton_changeView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_changeViewActionPerformed(evt);
            }
        });

        patientName.setFont(new java.awt.Font("MicrosoftSansSerif", 1, 18)); // NOI18N
        patientName.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        patientName.setText("Patient Name");

        org.jdesktop.layout.GroupLayout jPanel_titleLayout = new org.jdesktop.layout.GroupLayout(jPanel_title);
        jPanel_title.setLayout(jPanel_titleLayout);
        jPanel_titleLayout.setHorizontalGroup(
            jPanel_titleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel_titleLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel_titleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel_titleLayout.createSequentialGroup()
                        .add(jPanel_titleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel_titleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel3))
                            .add(jLabel4)
                            .add(jLabel5))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel_titleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(patientNameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
                            .add(wardNumField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(bedNumField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(dobField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(genderField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
                    .add(jPanel_titleLayout.createSequentialGroup()
                        .add(jButton_changeView)
                        .add(53, 53, 53)
                        .add(patientName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel_titleLayout.setVerticalGroup(
            jPanel_titleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel_titleLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel_titleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jButton_changeView)
                    .add(patientName))
                .add(18, 18, 18)
                .add(jPanel_titleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(patientNameField))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel_titleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(wardNumField))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel_titleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(bedNumField))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel_titleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel4)
                    .add(dobField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel_titleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(genderField))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel_readings, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel_title, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel_title, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel_readings, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_changeViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_changeViewActionPerformed
        this.dispose();
        this.wardframe.setVisible(true);
    }//GEN-LAST:event_jButton_changeViewActionPerformed

    private void jButton_exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_exitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jButton_exitActionPerformed

    private void jButton_historyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_historyActionPerformed
        //dispose current window
        this.setVisible(false);
        //open the history view
        HistoryJFrame historyframe = new HistoryJFrame(this.p, this);
        historyframe.setVisible(true);
    }//GEN-LAST:event_jButton_historyActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bedNumField;
    private javax.swing.JLabel dobField;
    private javax.swing.JLabel genderField;
    private javax.swing.JLabel heartRateField;
    private javax.swing.JButton jButton_changeView;
    private javax.swing.JButton jButton_exit;
    private javax.swing.JButton jButton_history;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel_systemTime;
    private javax.swing.JPanel jPanel_buttons;
    private javax.swing.JPanel jPanel_readings;
    private javax.swing.JPanel jPanel_title;
    private javax.swing.JLabel oxySatField;
    private javax.swing.JLabel patientName;
    private javax.swing.JLabel patientNameField;
    private javax.swing.JLabel respRateField;
    private javax.swing.JLabel sysBloodField;
    private javax.swing.JLabel tempField;
    private javax.swing.JLabel wardNumField;
    // End of variables declaration//GEN-END:variables
}
